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
    private T file;

    public String getCollectionName() {
        return collectionName;
    }

    public String getDatabase() {
        return database;
    }

    public String getId() {
        return id;
    }

    public T getFile() {
        return file;
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

    public void setFile(T file) {
        this.file = file;
    }
}
