package org.eustia.model;
/*
 * @package: org.eustia.model
 * @program: SqlInfo
 * @description
 *
 * @author:  rinne
 * @e-mail:  minami.rinne.me@gmail.com
 * @date: 2020/02/22 午後 08:18
 */

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @classname: SqlInfo
 * @description: %{description}
 * @author: rinne
 * @date: 2020/02/22 午後 08:18
 * @Version 1.0
 */

public class SqlInfo<T> {
    private String table;
    private String key;
    private String operation;
    private Object value;
    private T model;
    private ArrayList<Object> list;
    private HashMap<String, Object> updateMap;
    private String updateKey;
    private ArrayList<ArrayList<Object>> manyDataList;

    public SqlInfo() {
        this.table = null;
        this.key = null;
        this.value = null;
        this.list = null;
        this.model = null;
        this.updateMap = null;
        this.updateKey = null;
        this.manyDataList = null;
    }

    public SqlInfo(String key, Object value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getTable() {
        return table;
    }

    public Object getValue() {
        return value;
    }

    public T getModel() {
        return model;
    }

    public ArrayList<Object> getList() {
        return this.list;
    }

    public ArrayList<ArrayList<Object>> getManyDataList() {
        return manyDataList;
    }

    public void setManyDataList(ArrayList<ArrayList<Object>> manyDataList) {
        this.manyDataList = manyDataList;
    }


    public void setUpdateKey(String updateKey) {
        this.updateKey = updateKey;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getOperation() {
        return operation;
    }

    public String getUpdateKey() {
        return updateKey;
    }

    public HashMap<String, Object> getUpdateMap() {
        return updateMap;
    }

    public void setList(ArrayList<Object> list) {
        this.list = list;
    }

    public void setUpdateMap(HashMap<String, Object> updateMap) {
        this.updateMap = updateMap;
    }

    public void setModel(T model) {
        this.model = model;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setTable(String table) {
        this.table = table;
    }
}
