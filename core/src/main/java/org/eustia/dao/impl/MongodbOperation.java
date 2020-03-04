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

import com.mongodb.MongoException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
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

public interface MongodbOperation<T, ValueT> {
    MongoDatabase getDatabase(MongodbSqlInfo<T, ValueT> mongodbSqlInfo) throws MongoException;

    MongoCollection<Document> getCollection(MongodbSqlInfo<T, ValueT> mongodbSqlInfo) throws MongoException;

    FindIterable<Document> findAll(MongodbSqlInfo<T, ValueT> mongodbSqlInfo) throws MongoException;

    MongoCursor<Document> find(MongodbSqlInfo<T, ValueT> mongodbSqlInfo) throws MongoException;

    void insertData(MongodbSqlInfo<T, ValueT> mongodbSqlInfo) throws MongoException;

    void insertManyData(MongodbSqlInfo<T, ValueT> mongodbSqlInfo) throws MongoException;

    void updateData(MongodbSqlInfo<T, ValueT> mongodbSqlInfo) throws MongoException;

    void updateManyData(MongodbSqlInfo<T, ValueT> mongodbSqlInfo) throws MongoException;

    void deleteData(MongodbSqlInfo<T, ValueT> mongodbSqlInfo) throws MongoException;

    void deleteManyData(MongodbSqlInfo<T, ValueT> mongodbSqlInfo) throws MongoException;
}
