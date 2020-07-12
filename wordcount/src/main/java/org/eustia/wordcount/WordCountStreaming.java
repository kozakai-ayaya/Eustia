package org.eustia.wordcount;
/*
 * @package: org.eustia.wordcount
 * @program: WordCountStreaming
 * @description
 *
 * @author:  rinne
 * @e-mail:  minami.rinne.me@gmail.com
 * @date: 2020/07/03 午後 08:51
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
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.apache.flink.streaming.api.functions.windowing.ProcessAllWindowFunction;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.GlobalWindow;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.streaming.util.serialization.JSONKeyValueDeserializationSchema;
import org.apache.flink.util.Collector;
import org.eustia.common.model.MongodbSqlInfo;
import org.eustia.common.model.SqlInfo;
import org.eustia.common.time.TimeCheckpoint;
import org.eustia.wordcount.dao.BasicDataMongodbConnect;
import org.eustia.wordcount.dao.WordCountConnect;
import org.eustia.wordcount.model.BasicDataInfo;
import org.eustia.wordcount.model.WordCountInfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @classname: WordCountStreaming
 * @description: %{description}
 * @author: rinne
 * @date: 2020/07/03 午後 08:51
 * @Version 1.0
 */

public class WordCountStreaming {
    static TreeSet<String> negativeWord = new TreeSet<>();
    static TreeSet<String> positiveWord = new TreeSet<>();

    static {
        InputStream negativeWordFile = Thread.currentThread().getContextClassLoader().getResourceAsStream("ntusd-negative.txt");
        InputStream positiveWordFile = Thread.currentThread().getContextClassLoader().getResourceAsStream("ntusd-positive.txt");

        if (negativeWordFile != null) {
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(negativeWordFile, StandardCharsets.UTF_8))) {
                String word;
                while ((word = bufferedReader.readLine()) != null) {
                    negativeWord.add(word );
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (positiveWordFile != null) {
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(positiveWordFile, StandardCharsets.UTF_8))) {
                String word;
                while ((word = bufferedReader.readLine()) != null) {
                    positiveWord.add(word);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        final int needCount = 10;
        StreamExecutionEnvironment streamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment();
        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", "10.16.95.21:9092");
        properties.setProperty("zookeeper.connect", "10.16.95.21:2181");
        properties.setProperty("group.id", "WordCount.stream");
        FlinkKafkaConsumer<ObjectNode> kafkaConsumer = new FlinkKafkaConsumer<>("Word_Count",
                new JSONKeyValueDeserializationSchema(true), properties);
        kafkaConsumer.setStartFromGroupOffsets();
        DataStream<ObjectNode> wordStream = streamExecutionEnvironment.addSource(kafkaConsumer);

        // 原数据提交到MongoDB
        wordStream.addSink(new RichSinkFunction<ObjectNode>() {
            BasicDataMongodbConnect basicDataMongodbConnect;
            @Override
            public void open(Configuration parameters) throws Exception {
                basicDataMongodbConnect = new BasicDataMongodbConnect();
            }

            @Override
            public void invoke(ObjectNode value, Context context) throws Exception {
                try {
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
                } catch (MongoException err) {
                    System.out.println(err);
                }
            }
        });

        wordStream.flatMap(new FlatMapFunction<ObjectNode, Tuple2<String, Integer>>() {
            @Override
            public void flatMap(ObjectNode jsonNodes, Collector<Tuple2<String, Integer>> collector) throws Exception {
                JsonNode value = jsonNodes.get("value");
                JsonNode bulletScreen = value.get("bullet_screen");

                // 弹幕
                for (JsonNode bulletInfo : bulletScreen) {
                    JsonNode bullet = bulletInfo.get("bullet");
                    Iterator<Map.Entry<String, JsonNode>> field = bullet.fields();

                    while (field.hasNext()) {
                        Map.Entry<String, JsonNode> message = field.next();
                        Iterator<Map.Entry<String, JsonNode>> messageField = message.getValue().fields();
                        while (messageField.hasNext()) {
                            Map.Entry<String, JsonNode> messageInfo = messageField.next();

                            // 时间戳求余放大
                            long times = (Long.parseLong(messageInfo.getKey()) / (5 * 60)) * (5 * 60 * 1000);
                            String text = messageInfo.getValue().toString();

                            // 特殊弹幕过滤
                            if (text.charAt(1) == '[' && text.charAt(text.length() - 2) == ']') {
                                continue;
                            }

                            // 过滤特殊字符
                            Result parse = NlpAnalysis.parse(messageInfo.getValue().toString().replaceAll("[\\pP\\pS\\pZ]", ""));
                            for (Term words : parse) {
                                String word = words.toString().split("/")[0];
                                if (word.length() <= 1) {
                                    continue;
                                }
                                String keyInfo = times + "," + word;
                                Tuple2<String, Integer> wordTuple = new Tuple2<>(keyInfo, 1);
                                collector.collect(wordTuple);
                            }
                        }
                    }
                }

                // 评论区
                JsonNode replies = value.get("replies");
                for (JsonNode repliesInfo : replies) {
                    JsonNode repliesMessage = repliesInfo.get("message");
                    long times = (Long.parseLong(repliesInfo.get("time").toString()) / (5 * 60)) * (5 * 60 * 1000);
                    Result parse = NlpAnalysis.parse(repliesMessage.toString().replaceAll("[\\pP\\pS\\pZ]", ""));

                    for (Term words : parse) {
                        String word = words.toString().split("/")[0];
                        if (word.length() <= 1) {
                            continue;
                        }
                        String keyInfo = times + "," + word;
                        Tuple2<String, Integer> text = new Tuple2<>(keyInfo, 1);
                        collector.collect(text);
                    }
                }
            }
        })
                .keyBy(0)
                .timeWindow(Time.seconds(300))
                .sum(1)
                .countWindowAll(200)
                .process(new ProcessAllWindowFunction<Tuple2<String, Integer>, ArrayList<ArrayList<Object>>, GlobalWindow>() {
                    @Override
                    public void process(Context context, Iterable<Tuple2<String, Integer>> elements, Collector<ArrayList<ArrayList<Object>>> out) throws Exception {

                        ArrayList<ArrayList<Object>> arrayList = new ArrayList<>();

                        for (Tuple2<String, Integer> x : elements) {
                            String key = x.getField(0);

                            int count  = Integer.parseInt(x.getField(1));


                            if (count < needCount) {
                                continue;
                            }

                            ArrayList<Object> listInfo = new ArrayList<>();
                            System.out.println(x);
                            listInfo.add(Long.parseLong(key.split(",")[0]));
                            listInfo.add(key.split(",")[1]);
                            listInfo.add(count);
                            arrayList.add(listInfo);
                        }

                        out.collect(arrayList);
                    }
                })
                .addSink(new RichSinkFunction<ArrayList<ArrayList<Object>>>() {
                    private TimeCheckpoint timeCheckpoint;

                    @Override
                    public void open(Configuration parameters) throws Exception {
                        super.open(parameters);
                    }

                    @Override
                    public void invoke(ArrayList<ArrayList<Object>> value, Context context) throws Exception {
                        WordCountConnect wordCountConnect = new WordCountConnect();
                        WordCountInfo wordCountInfo = new WordCountInfo();
                        SqlInfo<WordCountInfo> sqlInfo = new SqlInfo<>();

                        sqlInfo.setManyDataList(value);

                        if (this.timeCheckpoint.isDay()) {
                            sqlInfo.setTable(this.timeCheckpoint.getTimeFormat());
                            try {
                                wordCountConnect.createTable(sqlInfo);
                            } catch (Exception e) {
                                System.out.println(e);
                            }
                        }

                        sqlInfo.setTable(this.timeCheckpoint.getTimeFormat());
                        sqlInfo.setModel(wordCountInfo);
                        wordCountConnect.insertManyDuplicateUpdateData(sqlInfo);
                    }
                });
        
//        wordStream.flatMap(new FlatMapFunction<ObjectNode, Object>() {
//        })
        
        try {
            streamExecutionEnvironment.execute("WordCount");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
