package org.eustia.dao.impl;
/*
 * @package: org.eustia.dao.impl
 * @program: MongodbOperation
 * @description
 *
 * @author:  rinne
 * @e-mail:  minami.rinne.me@gmail.com
 * @date: 2020/03/03 午後 04:40
 */

import com.mongodb.DBCollection;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.eustia.model.MongodbSqlInfo;

/**
 * @interface name: MongodbOperation
 * @description: %{description}
 * @author: rinne
 * @date: 2020/03/03 午後 04:40
 * @Version 1.0
 */

public interface MongodbOperation<T> {
    MongoDatabase getDatabase(MongodbSqlInfo<T> mongodbSqlInfo) throws MongoException;

    MongoCollection<Document> getCollection(MongodbSqlInfo<T> mongodbSqlInfo) throws MongoException;

    void insertData(MongodbSqlInfo<T> mongodbSqlInfo) throws MongoException;

    void insertManyData(MongodbSqlInfo<T> mongodbSqlInfo) throws MongoException;
}
