package org.eustia.dao;
/*
 * @package: org.eustia.dao
 * @program: AbstractMongodbConnect
 * @description
 *
 * @author:  rinne
 * @e-mail:  minami.rinne.me@gmail.com
 * @date: 2020/03/03 午後 07:57
 */

import com.mongodb.MongoException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.eustia.common.MongodbConnect;
import org.eustia.dao.impl.MongodbOperation;
import org.eustia.model.MongodbSqlInfo;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * @classname: AbstractMongodbConnect
 * @description: %{description}
 * @author: rinne
 * @date: 2020/03/03 午後 07:57
 * @Version 1.0
 */

public class AbstractMongodbConnect<T> implements MongodbOperation<T> {
    @Override
    public MongoDatabase getDatabase(MongodbSqlInfo<T> mongodbSqlInfo) throws MongoException {
        return MongodbConnect.mongoClient.getDatabase(mongodbSqlInfo.getDatabase());
    }

    @Override
    public MongoCollection<Document> getCollection(MongodbSqlInfo<T> mongodbSqlInfo) throws MongoException {
        return this.getDatabase(mongodbSqlInfo).getCollection(mongodbSqlInfo.getCollectionName());
    }

    @Override
    public void insertData(MongodbSqlInfo<T> mongodbSqlInfo) throws MongoException {
        Document document = new Document();
        for (Map.Entry<String, T> map : mongodbSqlInfo.getFile().entrySet()) {
            document.put(map.getKey(), map.getValue());
        }
        this.getCollection(mongodbSqlInfo).insertOne(document);
    }

    @Override
    public void insertManyData(MongodbSqlInfo<T> mongodbSqlInfo) throws MongoException {
        List<Document> list = new ArrayList<>();
        for (Hashtable<String, T> hashtable : mongodbSqlInfo.getManyFile()) {
            Document document = new Document();
            for (Map.Entry<String, T> map : hashtable.entrySet()) {
                document.put(map.getKey(), map.getValue());
            }
            list.add(document);
        }
        this.getCollection(mongodbSqlInfo).insertMany(list);
    }

    @Override
    public FindIterable<Document> findAll(MongodbSqlInfo<T> mongodbSqlInfo) throws MongoException {
        return this.getCollection(mongodbSqlInfo).find();
    }

    @Override
    public MongoCursor<Document> find(MongodbSqlInfo<T> mongodbSqlInfo) throws MongoException {
        String key = "";
        T value = null;
        for (Map.Entry<String, T> map : mongodbSqlInfo.getFile().entrySet()) {
            key = map.getKey();
            value = map.getValue();
        }
        return this.getCollection(mongodbSqlInfo).find(Filters.eq(key, value)).iterator();
    }

    @Override
    public void updateData(MongodbSqlInfo<T> mongodbSqlInfo) throws MongoException {
        String key = "";
        String updateKey = "";
        T value = null;
        T updateValue = null;
        for (Map.Entry<String, T> map : mongodbSqlInfo.getFile().entrySet()) {
            key = map.getKey();
            value = map.getValue();
        }

        for (Map.Entry<String, T> map : mongodbSqlInfo.getUpdateFile().entrySet()) {
            updateKey = map.getKey();
            updateValue = map.getValue();
        }
        this.getCollection(mongodbSqlInfo).updateOne(Filters.eq(key, value),
                                                     new Document("$set", new Document(updateKey, updateValue)));
    }

    @Override
    public void updateManyData(MongodbSqlInfo<T> mongodbSqlInfo) throws MongoException {
        String key = "";
        T value = null;
        List<Document> list = new ArrayList<>();

        for (Hashtable<String, T> file : mongodbSqlInfo.getUpdateManyFile()) {
            String updateKey = "";
            T updateValue = null;
            for (Map.Entry<String, T> map : mongodbSqlInfo.getUpdateFile().entrySet()) {
                updateKey = map.getKey();
                updateValue = map.getValue();
            }
            list.add(new Document(updateKey , updateValue));
        }

        for (Map.Entry<String, T> map : mongodbSqlInfo.getFile().entrySet()) {
            key = map.getKey();
            value = map.getValue();
        }

        this.getCollection(mongodbSqlInfo).updateMany(Filters.eq(key,value), list);
    }
}
