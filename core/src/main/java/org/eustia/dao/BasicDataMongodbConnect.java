package org.eustia.dao;
/*
 * @package: org.eustia.dao
 * @program: BasicDataMongodbConnect
 * @description
 *
 * @author:  rinne
 * @e-mail:  minami.rinne.me@gmail.com
 * @date: 2020/03/04 午後 02:13
 */

import com.mongodb.*;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.JsonNode;
import org.bson.Document;
import org.eustia.dao.impl.MongodbOperation;
import org.eustia.model.BasicDataInfo;
import org.eustia.model.MongodbSqlInfo;

import java.util.Hashtable;

/**
 * @classname: BasicDataMongodbConnect
 * @description: %{description}
 * @author: rinne
 * @date: 2020/03/04 午後 02:13
 * @Version 1.0
 */

public class BasicDataMongodbConnect extends AbstractMongodbConnect<BasicDataInfo, Object> implements MongodbOperation<BasicDataInfo, Object> {
    @Override
    public MongoDatabase getDatabase(MongodbSqlInfo<BasicDataInfo, Object> mongodbSqlInfo) throws MongoException {
        mongodbSqlInfo.setDatabase("WordCount");
        return super.getDatabase(mongodbSqlInfo);
    }

    @Override
    public MongoCollection<Document> getCollection(MongodbSqlInfo<BasicDataInfo, Object> mongodbSqlInfo) throws MongoException {
        mongodbSqlInfo.setDatabase("WordCount");
        mongodbSqlInfo.setCollectionName("BasicData");
        return super.getCollection(mongodbSqlInfo);
    }

    @Override
    public void insertData(MongodbSqlInfo<BasicDataInfo, Object> mongodbSqlInfo) throws MongoException {
        mongodbSqlInfo.setDatabase("WordCount");
        mongodbSqlInfo.setCollectionName("BasicData");
        Hashtable<String, Object> hashtable = new Hashtable<>();
        JsonNode data = mongodbSqlInfo.getModel().getDate();
        hashtable.put("_id", data.get("value").get("av").toString());
        hashtable.put("data", data.toString());
        mongodbSqlInfo.setFile(hashtable);
        super.insertData(mongodbSqlInfo);
    }

    @Override
    public void insertManyData(MongodbSqlInfo<BasicDataInfo, Object> mongodbSqlInfo) throws MongoException {
        mongodbSqlInfo.setDatabase("WordCount");
        mongodbSqlInfo.setCollectionName("BasicData");
        super.insertManyData(mongodbSqlInfo);
    }

    @Override
    public FindIterable<Document> findAll(MongodbSqlInfo<BasicDataInfo, Object> mongodbSqlInfo) throws MongoException {
        mongodbSqlInfo.setDatabase("WordCount");
        mongodbSqlInfo.setCollectionName("BasicData");
        return super.findAll(mongodbSqlInfo);
    }

    @Override
    public MongoCursor<Document> find(MongodbSqlInfo<BasicDataInfo, Object> mongodbSqlInfo) throws MongoException {
        mongodbSqlInfo.setDatabase("WordCount");
        mongodbSqlInfo.setCollectionName("BasicData");
        return super.find(mongodbSqlInfo);
    }

    @Override
    public void updateData(MongodbSqlInfo<BasicDataInfo, Object> mongodbSqlInfo) throws MongoException {
        mongodbSqlInfo.setDatabase("WordCount");
        mongodbSqlInfo.setCollectionName("BasicData");
        Hashtable<String, Object> hashtable = new Hashtable<>();
        Hashtable<String, Object> newHashtable = new Hashtable<>();
        JsonNode data = mongodbSqlInfo.getModel().getDate();

        hashtable.put("_id", data.get("value").get("av").toString());
        newHashtable.put("data", data.toString());

        mongodbSqlInfo.setFile(hashtable);
        mongodbSqlInfo.setUpdateFile(newHashtable);
        super.updateData(mongodbSqlInfo);
    }

    @Override
    public void updateManyData(MongodbSqlInfo<BasicDataInfo, Object> mongodbSqlInfo) throws MongoException {
        mongodbSqlInfo.setDatabase("WordCount");
        mongodbSqlInfo.setCollectionName("BasicData");
        super.updateManyData(mongodbSqlInfo);
    }

    @Override
    public void deleteData(MongodbSqlInfo<BasicDataInfo, Object> mongodbSqlInfo) throws MongoException {
        mongodbSqlInfo.setDatabase("WordCount");
        mongodbSqlInfo.setCollectionName("BasicData");
        super.deleteData(mongodbSqlInfo);
    }

    @Override
    public void deleteManyData(MongodbSqlInfo<BasicDataInfo, Object> mongodbSqlInfo) throws MongoException {
        mongodbSqlInfo.setDatabase("WordCount");
        mongodbSqlInfo.setCollectionName("BasicData");
        super.deleteManyData(mongodbSqlInfo);
    }
}
