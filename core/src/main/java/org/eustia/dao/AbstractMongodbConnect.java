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
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.eustia.common.MongodbConnect;
import org.eustia.dao.impl.MongodbOperation;
import org.eustia.model.MongodbSqlInfo;

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

    }

    @Override
    public void insertManyData(MongodbSqlInfo<T> mongodbSqlInfo) throws MongoException {

    }
}
