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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.ansj.domain.Result;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.NlpAnalysis;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.RuntimeContext;
import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.JsonNode;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.apache.flink.streaming.api.functions.windowing.ProcessAllWindowFunction;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.GlobalWindow;
import org.apache.flink.streaming.connectors.elasticsearch.ElasticsearchSinkFunction;
import org.apache.flink.streaming.connectors.elasticsearch.RequestIndexer;
import org.apache.flink.streaming.connectors.elasticsearch7.ElasticsearchSink;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.util.Collector;
import org.apache.http.HttpHost;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.Requests;
import org.eustia.common.model.EmotionalWordModel;
import org.eustia.common.model.SqlInfo;
import org.eustia.common.model.WordCountModel;
import org.eustia.common.time.TimeCheckpoint;
import org.eustia.wordcount.dao.EmotionalAnalysisConnect;
import org.eustia.wordcount.dao.WordCountConnect;
import org.eustia.wordcount.model.EmotionalAnalysisInfo;
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
    static TreeSet<String> blackListWord = new TreeSet<>();

    static {
        InputStream negativeWordFile = Thread.currentThread().getContextClassLoader().getResourceAsStream("ntusd-negative.txt");
        InputStream positiveWordFile = Thread.currentThread().getContextClassLoader().getResourceAsStream("ntusd-positive.txt");
        InputStream blackListWordFile = Thread.currentThread().getContextClassLoader().getResourceAsStream("blacklist.txt");

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

        if (blackListWordFile != null ) {
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(blackListWordFile, StandardCharsets.UTF_8))) {
                String word;
                while ((word = bufferedReader.readLine()) != null) {
                    blackListWord.add(word);
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

        List<HttpHost> esHosts = new ArrayList<>();
        esHosts.add(new HttpHost("10.16.95.21", 9200, "http"));

        FlinkKafkaConsumer<String> kafkaConsumer = new FlinkKafkaConsumer<>(
                "Word_Count",
                new DeserializationSchema<String>() {
                    @Override
                    public String deserialize(byte[] bytes) {
                        return new String(bytes);
                    }

                    @Override
                    public boolean isEndOfStream(String s) {
                        return false;
                    }

                    @Override
                    public TypeInformation<String> getProducedType() {
                        return TypeInformation.of(String.class);
                    }
                },
                properties
        );
        kafkaConsumer.setStartFromEarliest();

        DataStream<String> wordStream = streamExecutionEnvironment.addSource(kafkaConsumer);

//        wordStream.process(new ProcessFunction<String, Object>() {
//            @Override
//            public void processElement(String s, Context context, Collector<Object> collector) throws Exception {
//
//            }
//        });

//        wordStream.addSink(new ElasticsearchSink.Builder<>(
//                esHosts,
//                new ElasticsearchSinkFunction<String>() {
//                    @Override
//                    public void process(String s, RuntimeContext runtimeContext, RequestIndexer requestIndexer) {
//                        JSONObject objectNode = JSON.parseObject(s);
//                        HashMap<String, String> json = new HashMap<>(1);
//                        json.put("data", objectNode.toString());
//
//                        IndexRequest request = Requests.indexRequest()
//                                .index("word_count")
//                                .id(objectNode.get("av").toString())
//                                .source(json);
//                        requestIndexer.add(request);
//                    }
//                }).build()
//        );

        DataStream<WordCountModel> cleanData = wordStream
                .process(new ProcessFunction<String, JsonNode>() {
                    @Override
                    public void processElement(String s, Context context, Collector<JsonNode> collector) throws Exception {
                        ObjectMapper objectMapper = new ObjectMapper();
                        JsonNode jsonNode = objectMapper.readTree(s);
                        collector.collect(jsonNode);
                    }
                })
                .flatMap(new FlatMapFunction<JsonNode, WordCountModel>() {
                     @Override
                     public void flatMap(JsonNode value, Collector<WordCountModel> collector) {
                         JsonNode bulletScreen = value.get("bullet_screen");
                         String av = value.get("av").toString().replaceAll("\"", "");

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
                                         WordCountModel model = new WordCountModel();
                                         model.setWord(word);
                                         model.setTimestamp(times);
                                         model.setCount(1);
                                         model.setAv(av);
                                         collector.collect(model);
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
                                 if (word.length() <= 1 || blackListWord.contains(word)) {
                                     continue;
                                 }
                                 WordCountModel model = new WordCountModel();
                                 model.setWord(word);
                                 model.setTimestamp(times);
                                 model.setCount(1);
                                 model.setAv(av);
                                 collector.collect(model);
                             }
                         }
                     }
                });

        cleanData
                .keyBy(new KeySelector<WordCountModel, Object>() {
                    @Override
                    public Object getKey(WordCountModel wordCountModel) {
                        return wordCountModel.getKey();
                    }
                })
//                .timeWindow(Time.seconds(300))
                .sum("count")
                .process(new ProcessFunction<WordCountModel, Object>() {
                    @Override
                    public void processElement(WordCountModel wordCountModel, Context context, Collector<Object> collector) throws Exception {
                        String s = "aaaaaaa";
//                       ArrayList<WordCountModel> arrayList = new ArrayList<>();

//                        for (WordCountModel x : elements) {
//                            int count  = x.getCount();
//
//                            if (count > needCount) {
//                                arrayList.add(x);
//                            }
//                        }
                        collector.collect(s);
                    }
                });
//                .countWindowAll(200)
//                .process(new ProcessAllWindowFunction<WordCountModel, ArrayList<WordCountModel>, GlobalWindow>() {
//                    @Override
//                    public void process(Context context, Iterable<WordCountModel> elements, Collector<ArrayList<WordCountModel>> out) {
//
//                        ArrayList<WordCountModel> arrayList = new ArrayList<>();
//
//                        for (WordCountModel x : elements) {
//                            int count  = x.getCount();
//
//                            if (count > needCount) {
//                                arrayList.add(x);
//                            }
//                        }
//                        out.collect(arrayList);
//                    }
//                });
//                .addSink(new RichSinkFunction<ArrayList<WordCountModel>>() {
//                    private TimeCheckpoint timeCheckpoint;
//                    private WordCountConnect wordCountConnect;
//                    private SqlInfo<WordCountInfo> sqlInfo;
//
//                    @Override
//                    public void open(Configuration parameters) throws Exception {
//                        this.timeCheckpoint = new TimeCheckpoint();
//                        this.wordCountConnect = new WordCountConnect();
//                        this.sqlInfo = new SqlInfo<>();
//                        super.open(parameters);
//                    }
//
//                    @Override
//                    public void invoke(ArrayList<WordCountModel> value, Context context) throws Exception {
//                        ArrayList<ArrayList<Object>> arrayLists = new ArrayList<>();
//
//                        for (WordCountModel wordCountModel : value) {
//                            ArrayList<Object> a = new ArrayList<>();
//                            a.add(wordCountModel.getTimestamp());
//                            a.add(wordCountModel.getWord());
//                            a.add(wordCountModel.getCount());
//                            arrayLists.add(a);
//                        }
//
//                        sqlInfo.setManyDataList(arrayLists);
//
//                        if (this.timeCheckpoint.isDay()) {
//                            sqlInfo.setTable(this.timeCheckpoint.getTimeFormat());
//                            try {
//                                this.wordCountConnect.createTable(sqlInfo);
//                            } catch (Exception e) {
//                                System.out.println(e);
//                            }
//                        }
//
//                        sqlInfo.setTable(this.timeCheckpoint.getTimeFormat());
//                        this.wordCountConnect.insertManyDuplicateUpdateData(sqlInfo);
//                    }
//                });

        // 情绪统计
        cleanData.flatMap(new FlatMapFunction<WordCountModel, EmotionalWordModel>() {

            private String emotionalCheckpoint(String word) {
                if (negativeWord.contains(word)) {
                    return "negative";
                } else if (positiveWord.contains(word)) {
                    return "positive";
                } else {
                    return "unknown";
                }
            }

            @Override
            public void flatMap(WordCountModel valueModel, Collector<EmotionalWordModel> collector) {
                EmotionalWordModel emotionalWordModel = new EmotionalWordModel();

                emotionalWordModel.setKey(valueModel.getKey());
                emotionalWordModel.setAv(valueModel.getAv());
                emotionalWordModel.setEmotionalWord(this.emotionalCheckpoint(valueModel.getWord()));
                emotionalWordModel.setTimestamp(valueModel.getTimestamp());
                emotionalWordModel.setCount(valueModel.getCount());
                collector.collect(emotionalWordModel);
            }
        })
                .keyBy(new KeySelector<EmotionalWordModel, Object>() {
                    @Override
                    public Object getKey(EmotionalWordModel emotionalWordModel) {
                        return emotionalWordModel.getKey();
                    }
                })
//                .timeWindow(Time.seconds(300))
                .sum("count")
                .process(new ProcessFunction<EmotionalWordModel, Object>() {

                    @Override
                    public void processElement(EmotionalWordModel emotionalWordModel, Context context, Collector<Object> collector) throws Exception {
                        String s = new String();
                        collector.collect(s);
                    }
                });
//                .countWindowAll(200)
//                .process(new ProcessAllWindowFunction<EmotionalWordModel, ArrayList<EmotionalWordModel>, GlobalWindow>() {
//                    @Override
//                    public void process(Context context, Iterable<EmotionalWordModel> iterable, Collector<ArrayList<EmotionalWordModel>> collector) {
//                        ArrayList<EmotionalWordModel> arrayList = new ArrayList<>();
//                        for (EmotionalWordModel e : iterable) {
//                            arrayList.add(e);
//                        }
//                        collector.collect(arrayList);
//                     }
//                });
//                .addSink(new RichSinkFunction<ArrayList<EmotionalWordModel>>() {
//                    private TimeCheckpoint timeCheckpoint;
//                    private EmotionalAnalysisConnect emotionalAnalysisConnect;
//                    private SqlInfo<EmotionalAnalysisInfo> sqlInfo;
//
//                    @Override
//                    public void open(Configuration parameters) throws Exception {
//                        this.timeCheckpoint = new TimeCheckpoint();
//                        this.emotionalAnalysisConnect = new EmotionalAnalysisConnect();
//                        this.sqlInfo = new SqlInfo<>();
//                        super.open(parameters);
//                    }
//
//                    @Override
//                    public void invoke(ArrayList<EmotionalWordModel> value, Context context) throws Exception {
//                        ArrayList<ArrayList<Object>> arrayLists = new ArrayList<>();
//
//                        for (EmotionalWordModel emotionalWordModel : value) {
//                            ArrayList<Object> a = new ArrayList<>();
//                            a.add(emotionalWordModel.getTimestamp());
//                            a.add(emotionalWordModel.getAv());
//                            a.add(emotionalWordModel.getEmotionalWord());
//                            a.add(emotionalWordModel.getCount());
//                            arrayLists.add(a);
//                        }
//
//                        sqlInfo.setManyDataList(arrayLists);
//
//                        if (this.timeCheckpoint.isDay()) {
//                            sqlInfo.setTable(this.timeCheckpoint.getTimeFormat());
//                            try {
//                                emotionalAnalysisConnect.createTable(this.sqlInfo);
//                            } catch (Exception e) {
//                                System.out.println(e);
//                            }
//                        }
//
//                        sqlInfo.setTable(this.timeCheckpoint.getTimeFormat());
//                        this.emotionalAnalysisConnect.insertManyDuplicateUpdateData(sqlInfo);
//                    }
//                });

        try {
            streamExecutionEnvironment.execute("WordCount");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
