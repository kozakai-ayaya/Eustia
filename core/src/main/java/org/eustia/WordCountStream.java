package org.eustia;
/*
 * @package: org.eustia
 * @program: test
 * @description
 *
 * @author:  rinne
 * @e-mail:  minami.rinne.me@gmail.com
 * @date: 2020/02/04 午前 12:17
 */

import com.mongodb.MongoException;
import org.ansj.domain.Result;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.NlpAnalysis;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.JsonNode;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.streaming.util.serialization.JSONKeyValueDeserializationSchema;
import org.apache.flink.util.Collector;
import org.eustia.common.TimeSetting;
import org.eustia.dao.BasicDataMongodbConnect;
import org.eustia.dao.WordCountConnect;
import org.eustia.model.BasicDataInfo;
import org.eustia.model.MongodbSqlInfo;
import org.eustia.model.SqlInfo;
import org.eustia.model.WordCountInfo;

import java.util.*;

/**
 * @classname: WordCountStream
 * @description: %{description}
 * @author: rinne
 * @date: 2020/02/04 午前 12:17
 * @Version 1.0
 */

public class WordCountStream {
    public static void main(final String[] args) {
        final int needCount = 10;

        StreamExecutionEnvironment streamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment();
        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", "localhost:9092");
        properties.setProperty("zookeeper.connect", "localhost:2181");
        properties.setProperty("group.id", "kafka.test");
        FlinkKafkaConsumer<ObjectNode> kafkaConsumer = new FlinkKafkaConsumer<>("Word_Count",
                new JSONKeyValueDeserializationSchema(true), properties);
        kafkaConsumer.setStartFromGroupOffsets();
        DataStream<ObjectNode> wordStream = streamExecutionEnvironment.addSource(kafkaConsumer);

        wordStream.addSink(new RichSinkFunction<ObjectNode>() {
            BasicDataMongodbConnect basicDataMongodbConnect;
            @Override
            public void open(Configuration parameters) throws Exception {
                basicDataMongodbConnect = new BasicDataMongodbConnect();
            }

            @Override
            public void invoke(ObjectNode value, Context context) throws Exception {
                BasicDataInfo basicDataInfo = new BasicDataInfo();
                MongodbSqlInfo<BasicDataInfo, Object> mongodbSqlInfo = new MongodbSqlInfo<>();
                basicDataInfo.setDate(value);
                mongodbSqlInfo.setModel(basicDataInfo);
                try {
                    basicDataMongodbConnect.insertData(mongodbSqlInfo);
                } catch (MongoException e) {
                    System.out.println(e);
                    basicDataMongodbConnect.updateData(mongodbSqlInfo);
                }
            }
        });

        wordStream.process(new ProcessFunction<ObjectNode, Tuple2<Integer, String>>() {
            @Override
            public void processElement(ObjectNode jsonNodes, Context context, Collector<Tuple2<Integer, String>> collector) {

                JsonNode value = jsonNodes.get("value");
                JsonNode bulletScreen = value.get("bullet_screen");

                for (JsonNode bulletInfo : bulletScreen) {
                    JsonNode bullet = bulletInfo.get("bullet");
                    Iterator<Map.Entry<String, JsonNode>> field = bullet.fields();

                    while (field.hasNext()) {
                        Map.Entry<String, JsonNode> message = field.next();
                        int times = Integer.parseInt(message.getKey()) / (24 * 60 * 60);
                        Result parse = NlpAnalysis.parse(message.getValue().toString().replaceAll("[\\pP\\pS\\pZ]", ""));

                        for (Term words : parse) {
                            String word = words.toString().split("/")[0];
                            if (word.length() <= 1) {
                                continue;
                            }
                            Tuple2<Integer, String> text = new Tuple2<>(times, word);
                            collector.collect(text);
                        }
                    }
                }

                JsonNode replies = value.get("replies");
                for (JsonNode repliesInfo : replies) {
                    JsonNode repliesMessage = repliesInfo.get("message");
                    int times = Integer.parseInt(repliesInfo.get("time").toString()) / (24 * 60 * 60);
                    Result parse = NlpAnalysis.parse(repliesMessage.toString().replaceAll("[\\pP\\pS\\pZ]", ""));

                    for (Term words : parse) {
                        String word = words.toString().split("/")[0];
                        if (word.length() <= 1) {
                            continue;
                        }
                        Tuple2<Integer, String> text = new Tuple2<>(times, word);
                        collector.collect(text);
                    }
                }
            }
        })
                .flatMap(new FlatMapFunction<Tuple2<Integer, String>, Tuple2<Tuple2<Integer, String>, Integer>>() {
                    @Override
                    public void flatMap(Tuple2<Integer, String> integerStringTuple2, Collector<Tuple2<Tuple2<Integer, String>, Integer>> collector) {
                        Tuple2<Tuple2<Integer, String>, Integer> tuple = new Tuple2<>(integerStringTuple2, 1);
                        collector.collect(tuple);
                    }
                })
                .keyBy(0)
                .timeWindow(Time.seconds(1))
                .sum(1)
                .process(new ProcessFunction<Tuple2<Tuple2<Integer, String>, Integer>, Tuple2<Tuple2<Integer, String>, Integer>>() {

                    @Override
                    public void processElement(Tuple2<Tuple2<Integer, String>, Integer> value, Context ctx,
                                               Collector<Tuple2<Tuple2<Integer, String>, Integer>> out) {
                        if ((int) value.getField(1) >= needCount) {
                            out.collect(value);
                        }
                    }
                })
                .addSink(new RichSinkFunction<Tuple2<Tuple2<Integer, String>, Integer>>() {
                    private TimeSetting timeSetting;

                    @Override
                    public void open(Configuration parameters) throws Exception {
                        super.open(parameters);
                        this.timeSetting = new TimeSetting();
                    }

                    @Override
                    public void invoke(Tuple2<Tuple2<Integer, String>, Integer> value, Context context) throws Exception {
                        WordCountConnect wordCountConnect = new WordCountConnect();
                        WordCountInfo wordCountInfo = new WordCountInfo();
                        SqlInfo<WordCountInfo> sqlInfo = new SqlInfo<>();
                        Tuple2<Integer, String> key = value.getField(0);

                        wordCountInfo.setTimeStamp(key.getField(0));
                        wordCountInfo.setWord(key.getField(1));
                        wordCountInfo.setCount(value.getField(1));

                        if (this.timeSetting.isHour()) {
                            if (this.timeSetting.isDay()) {
                                sqlInfo.setTable(this.timeSetting.getTimeFormat() + "_" + "0");
                            } else {
                                sqlInfo.setTable(this.timeSetting.getTimeFormat() + "_" + this.timeSetting.getFlag());
                            }
                            try {
                                wordCountConnect.createTable(sqlInfo);
                            } catch (Exception e) {
                                System.out.println(e);
                            }
                        }

                        sqlInfo.setTable(timeSetting.getTimeFormat() + "_" + timeSetting.getFlag());
                        sqlInfo.setModel(wordCountInfo);
                        wordCountConnect.insertDuplicateUpdateData(sqlInfo);
                    }
                });

        try {
            streamExecutionEnvironment.execute("WordCount");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
