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
        return getDatabase(mongodbSqlInfo).getCollection(mongodbSqlInfo.getCollectionName());
    }

    @Override
    public void insertData(MongodbSqlInfo<T> mongodbSqlInfo) throws MongoException {
        Document document = new Document();
        for (Map.Entry<String, T> map : mongodbSqlInfo.getFile().entrySet()) {
            document.put(map.getKey(), map.getValue());
        }
        getCollection(mongodbSqlInfo).insertOne(document);
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
        getCollection(mongodbSqlInfo).insertMany(list);
    }

    @Override
    public FindIterable<Document> findAll(MongodbSqlInfo<T> mongodbSqlInfo) throws MongoException {
        return getCollection(mongodbSqlInfo).find();
    }

    @Override
    public MongoCursor<Document> find(MongodbSqlInfo<T> mongodbSqlInfo) throws MongoException {
        String key = "";
        T value = null;
        for (Map.Entry<String, T> map : mongodbSqlInfo.getFile().entrySet()) {
            key = map.getKey();
            value = map.getValue();
        }
        return getCollection(mongodbSqlInfo).find(Filters.eq(key, value)).iterator();
    }

    @Override
    public void updata(MongodbSqlInfo<T> mongodbSqlInfo) throws MongoException {

    }

    @Override
    public void updateManyData(MongodbSqlInfo<T> mongodbSqlInfo) throws MongoException {

    }
}
