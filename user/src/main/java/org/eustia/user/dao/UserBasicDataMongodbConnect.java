package org.eustia.user.dao;
/*
 * @package: org.eustia.user.dao
 * @program: UserBasicDataMongodbConnect
 * @description
 *
 * @author:  rinne
 * @e-mail:  minami.rinne.me@gmail.com
 * @date: 2020/03/11 午後 05:07
 */

import com.mongodb.MongoException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.JsonNode;
import org.bson.Document;
import org.eustia.common.dao.AbstractMongodbConnect;
import org.eustia.common.dao.impl.MongodbOperation;
import org.eustia.common.model.MongodbSqlInfo;
import org.eustia.user.model.UserBasicInfo;

import java.util.Hashtable;

/**
 * @classname: UserBasicDataMongodbConnect
 * @description: %{description}
 * @author: rinne
 * @date: 2020/03/11 午後 05:07
 * @Version 1.0
 */

public class UserBasicDataMongodbConnect extends AbstractMongodbConnect<UserBasicInfo, Object> implements MongodbOperation<UserBasicInfo, Object> {
    @Override
    public MongoDatabase getDatabase(MongodbSqlInfo<UserBasicInfo, Object> mongodbSqlInfo) throws MongoException {
        return super.getDatabase(mongodbSqlInfo);
    }

    @Override
    public MongoCollection<Document> getCollection(MongodbSqlInfo<UserBasicInfo, Object> mongodbSqlInfo) throws MongoException {
        return super.getCollection(mongodbSqlInfo);
    }

    @Override
    public void insertData(MongodbSqlInfo<UserBasicInfo, Object> mongodbSqlInfo) throws MongoException {
        mongodbSqlInfo.setDatabase("UserInfo");
        mongodbSqlInfo.setCollectionName("BasicData");
        Hashtable<String, Object> hashtable = new Hashtable<>();
        JsonNode data = mongodbSqlInfo.getModel().getData().get("value");
        hashtable.put("_id", data.get("data").get("card").get("mid").toString());
        hashtable.put("data", data.toString());
        mongodbSqlInfo.setFile(hashtable);
        super.insertData(mongodbSqlInfo);
    }

    @Override
    public void insertManyData(MongodbSqlInfo<UserBasicInfo, Object> mongodbSqlInfo) throws MongoException {
        super.insertManyData(mongodbSqlInfo);
    }

    @Override
    public FindIterable<Document> findAll(MongodbSqlInfo<UserBasicInfo, Object> mongodbSqlInfo) throws MongoException {
        return super.findAll(mongodbSqlInfo);
    }

    @Override
    public MongoCursor<Document> find(MongodbSqlInfo<UserBasicInfo, Object> mongodbSqlInfo) throws MongoException {
        return super.find(mongodbSqlInfo);
    }

    @Override
    public void updateData(MongodbSqlInfo<UserBasicInfo, Object> mongodbSqlInfo) throws MongoException {
        mongodbSqlInfo.setDatabase("UserInfo");
        mongodbSqlInfo.setCollectionName("BasicData");
        Hashtable<String, Object> hashtable = new Hashtable<>();
        Hashtable<String, Object> newHashtable = new Hashtable<>();
        JsonNode data = mongodbSqlInfo.getModel().getData().get("value");

        hashtable.put("_id", data.get("data").get("card").get("mid").toString());
        newHashtable.put("data", data.toString());

        mongodbSqlInfo.setFile(hashtable);
        mongodbSqlInfo.setUpdateFile(newHashtable);
        super.updateData(mongodbSqlInfo);
    }

    @Override
    public void updateManyData(MongodbSqlInfo<UserBasicInfo, Object> mongodbSqlInfo) throws MongoException {
        super.updateManyData(mongodbSqlInfo);
    }

    @Override
    public void deleteData(MongodbSqlInfo<UserBasicInfo, Object> mongodbSqlInfo) throws MongoException {
        super.deleteData(mongodbSqlInfo);
    }

    @Override
    public void deleteManyData(MongodbSqlInfo<UserBasicInfo, Object> mongodbSqlInfo) throws MongoException {
        super.deleteManyData(mongodbSqlInfo);
    }
}
