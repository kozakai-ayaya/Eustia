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

public class MongodbSqlInfo<T> {
    private String database;
    private String collectionName;
    private String id;
    private Hashtable<String, T> file;
    private Hashtable<String, T> updateFile;
    private ArrayList<Hashtable<String, T>> manyFile;
    private ArrayList<Hashtable<String, T>> updateManyFile;

    public String getCollectionName() {
        return collectionName;
    }

    public String getDatabase() {
        return database;
    }

    public String getId() {
        return id;
    }

    public Hashtable<String, T> getFile() {
        return file;
    }

    public ArrayList<Hashtable<String, T>> getManyFile() {
        return manyFile;
    }

    public Hashtable<String, T> getUpdateFile() {
        return updateFile;
    }

    public ArrayList<Hashtable<String, T>> getUpdateManyFile() {
        return updateManyFile;
    }

    public void setUpdateFile(Hashtable<String, T> updateFile) {
        this.updateFile = updateFile;
    }

    public void setUpdateManyFile(ArrayList<Hashtable<String, T>> updateManyFile) {
        this.updateManyFile = updateManyFile;
    }

    public void setManyFile(ArrayList<Hashtable<String, T>> manyFile) {
        this.manyFile = manyFile;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setCollectionName(String collectionName) {
        this.collectionName = collectionName;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public void setFile(Hashtable<String, T> file) {
        this.file = file;
    }
}
