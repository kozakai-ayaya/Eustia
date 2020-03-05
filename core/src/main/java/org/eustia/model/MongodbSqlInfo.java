package org.eustia.model;
/*
 * @package: org.eustia.model
 * @program: MongodbSqlInfo
 * @description
 *
 * @author:  rinne
 * @e-mail:  minami.rinne.me@gmail.com
 * @date: 2020/03/03 午後 05:00
 */

import java.util.ArrayList;
import java.util.Hashtable;

/**
 * @classname: MongodbSqlInfo
 * @description: %{description}
 * @author: rinne
 * @date: 2020/03/03 午後 05:00
 * @Version 1.0
 */

public class MongodbSqlInfo<T, ValueT> {
    private T model;
    private String database;
    private String collectionName;
    private ValueT data;
    private Hashtable<String, ValueT> file;
    private Hashtable<String, ValueT> updateFile;
    private ArrayList<Hashtable<String, ValueT>> manyFile;
    private ArrayList<Hashtable<String, ValueT>> updateManyFile;

    public String getCollectionName() {
        return collectionName;
    }

    public String getDatabase() {
        return database;
    }

    public Hashtable<String, ValueT> getFile() {
        return file;
    }

    public ArrayList<Hashtable<String, ValueT>> getManyFile() {
        return manyFile;
    }

    public Hashtable<String, ValueT> getUpdateFile() {
        return updateFile;
    }

    public ArrayList<Hashtable<String, ValueT>> getUpdateManyFile() {
        return updateManyFile;
    }

    public T getModel() {
        return model;
    }

    public ValueT getData() {
        return data;
    }

    public void setData(ValueT data) {
        this.data = data;
    }

    public void setModel(T model) {
        this.model = model;
    }

    public void setUpdateFile(Hashtable<String, ValueT> updateFile) {
        this.updateFile = updateFile;
    }

    public void setUpdateManyFile(ArrayList<Hashtable<String, ValueT>> updateManyFile) {
        this.updateManyFile = updateManyFile;
    }

    public void setManyFile(ArrayList<Hashtable<String, ValueT>> manyFile) {
        this.manyFile = manyFile;
    }

    public void setCollectionName(String collectionName) {
        this.collectionName = collectionName;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public void setFile(Hashtable<String, ValueT> file) {
        this.file = file;
    }
}
