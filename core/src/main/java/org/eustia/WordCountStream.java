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

import org.ansj.domain.Result;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.NlpAnalysis;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.JsonNode;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.streaming.util.serialization.JSONKeyValueDeserializationSchema;
import org.apache.flink.util.Collector;
import org.eustia.dao.WordCountConnect;
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
        final StreamExecutionEnvironment streamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment();
        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", "127.0.0.1:9092");
        properties.setProperty("zookeeper.connect", "127.0.0.1:2181");
        properties.setProperty("group.id", "kafka.test");
        FlinkKafkaConsumer<ObjectNode> kafkaConsumer = new FlinkKafkaConsumer<>("kafka-test-topic",
                new JSONKeyValueDeserializationSchema(true), properties);
        kafkaConsumer.setStartFromGroupOffsets();
        DataStream<ObjectNode> wordStream = streamExecutionEnvironment.addSource(kafkaConsumer);
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
                    public void flatMap(Tuple2<Integer, String> integerStringTuple2, Collector<Tuple2<Tuple2<Integer, String>, Integer>> collector) throws Exception {
                        Tuple2<Tuple2<Integer, String>, Integer> tuple = new Tuple2<>(integerStringTuple2, 1);
                        collector.collect(tuple);
                    }
                })
                .keyBy(0)
                .sum(1)
                .addSink(new RichSinkFunction<Tuple2<Tuple2<Integer, String>, Integer>>() {
                    @Override
                    public void invoke(Tuple2<Tuple2<Integer, String>, Integer> value, Context context) throws Exception {
                        WordCountConnect wordCountConnect = new WordCountConnect();
                        WordCountInfo wordCountInfo = new WordCountInfo();
                        SqlInfo<WordCountInfo> sqlInfo = new SqlInfo<>();
                        Tuple2<Integer, String> key = value.getField(0);

                        wordCountInfo.setTimeStamp((int) key.getField(0));
                        wordCountInfo.setWord((String) key.getField(1));
                        wordCountInfo.setCount((int) value.getField(1));

                        sqlInfo.setModel(wordCountInfo);
                        wordCountConnect.insertDuplicateData(sqlInfo);
                    }
                });

        try {
            streamExecutionEnvironment.execute("test");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
