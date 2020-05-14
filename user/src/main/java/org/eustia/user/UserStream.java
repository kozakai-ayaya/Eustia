package org.eustia.user;
/*
 * @package: org.eustia.user
 * @program: UserStream
 * @description
 *
 * @author:  rinne
 * @e-mail:  minami.rinne.me@gmail.com
 * @date: 2020/03/11 午前 08:31
 */

import com.mongodb.MongoException;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.streaming.util.serialization.JSONKeyValueDeserializationSchema;
import org.eustia.common.model.MongodbSqlInfo;
import org.eustia.user.dao.UserBasicDataMongodbConnect;
import org.eustia.user.model.UserBasicInfo;

import java.util.Properties;

/**
 * @classname: UserStream
 * @description: %{description}
 * @author: rinne
 * @date: 2020/03/11 午前 08:31
 * @Version 1.0
 */

public class UserStream {
    public static void main(String[] args) {
        StreamExecutionEnvironment streamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment();
        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", "localhost:9092");
        properties.setProperty("zookeeper.connect", "localhost:2181");
        properties.setProperty("group.id", "UserInfo.Stream");
        FlinkKafkaConsumer<ObjectNode> kafkaConsumer = new FlinkKafkaConsumer<>("User_Info",
                new JSONKeyValueDeserializationSchema(true), properties);
        kafkaConsumer.setStartFromGroupOffsets();
        DataStream<ObjectNode> userStream= streamExecutionEnvironment.addSource(kafkaConsumer);

        userStream.addSink(new RichSinkFunction<ObjectNode>() {
            UserBasicDataMongodbConnect userBasicDataMongodbConnect;

            @Override
            public void open(Configuration parameters) throws Exception {
                userBasicDataMongodbConnect = new UserBasicDataMongodbConnect();
            }

            @Override
            public void invoke(ObjectNode value, Context context) throws Exception {
                UserBasicInfo userBasicInfo = new UserBasicInfo();
                MongodbSqlInfo<UserBasicInfo, Object> mongodbSqlInfo = new MongodbSqlInfo<>();
                userBasicInfo.setData(value);
                mongodbSqlInfo.setModel(userBasicInfo);
                try {
                    userBasicDataMongodbConnect.insertData(mongodbSqlInfo);
                } catch (MongoException e) {
                    System.out.println(e);
                    userBasicDataMongodbConnect.updateData(mongodbSqlInfo);
                }
            }
        });

        try {
            streamExecutionEnvironment.execute("UserStream");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
