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
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.streaming.util.serialization.JSONKeyValueDeserializationSchema;
import org.apache.flink.util.Collector;

import java.util.*;

/**
 * @classname: WordCountStream
 * @description: %{description}
 * @author: rinne
 * @date: 2020/02/04 午前 12:17
 * @Version 1.0
 */

public class WordCountStream {
    public static void main(String[] args) {
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
                        int times = Integer.parseInt(message.getKey()) / (5 * 60);
                        Result parse = NlpAnalysis.parse(message.getValue().toString().replaceAll("[\\pP\\pS\\pZ]", ""));

                        for (Term word : parse) {
                            Tuple2<Integer, String> text = new Tuple2<>(times, word.toString().split("/")[0]);
                            collector.collect(text);
                        }
                    }
                }

                JsonNode replies = value.get("replies");
                for (JsonNode repliesInfo : replies) {
                    JsonNode repliesMessage = repliesInfo.get("message");
                    int times = Integer.parseInt(repliesInfo.get("time").toString()) / (5 * 60);
                    Result parse = NlpAnalysis.parse(repliesMessage.toString().replaceAll("[\\pP\\pS\\pZ]", ""));

                    for (Term word : parse) {
                        Tuple2<Integer, String> text = new Tuple2<>(times, word.toString().split("/")[0]);
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
                .timeWindow(Time.minutes(2))
                .sum(1)
                .process(new ProcessFunction<Tuple2<Tuple2<Integer, String>, Integer>, Object>() {
                    @Override
                    public void processElement(Tuple2<Tuple2<Integer, String>, Integer> value, Context ctx, Collector<Object> out) throws Exception {

                    }
                });

        try {
            streamExecutionEnvironment.execute("test");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
