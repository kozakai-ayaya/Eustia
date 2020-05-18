package org.eustia.common.dao.impl;
/*
 * @package: org.eustia.common.dao.impl
 * @program: DataBaseOperation
 * @description
 *
 * @author:  rinne
 * @e-mail:  minami.rinne.me@gmail.com
 * @date: 2020/02/22 午後 06:46
 */

import org.eustia.common.model.SqlInfo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * @interface name: DataBaseOperation
 * @description: %{description}
 * @author: rinne
 * @date: 2020/02/22 午後 06:46
 * @Version 1.0
 */

public interface DataBaseOperation<T> {
    ArrayList<ArrayList<Object>> getResult(SqlInfo<T> sqlInfo) throws SQLException;

    ArrayList<ArrayList<Object>> getAllResult(SqlInfo<T> sqlInfo) throws SQLException;

    void createTable(SqlInfo<T> sqlInfo) throws SQLException;

    void insertData(SqlInfo<T> sqlInfo) throws SQLException;

    void insertManyData(SqlInfo<T> sqlInfo) throws SQLException;

    void insertDuplicateUpdateData(SqlInfo<T> sqlInfo) throws SQLException;

    void insertManyDuplicateUpdateData(SqlInfo<T> sqlInfo) throws SQLException;

    void insertDuplicateReplaceData(SqlInfo<T> sqlInfo) throws SQLException;

    void insertManyDuplicateReplaceData(SqlInfo<T> sqlInfo) throws SQLException;

    void updateData(SqlInfo<T> sqlInfo) throws SQLException;

    void deleteData(SqlInfo<T> sqlInfo) throws SQLException;

    long sumData(SqlInfo<T> sqlInfo) throws SQLException;

    long sumWhereData(SqlInfo<T> sqlInfo) throws SQLException;

    long countData(SqlInfo<T> sqlInfo) throws SQLException;

    ArrayList<String> getTable(SqlInfo<T> sqlInfo) throws SQLException;

    ArrayList<T> getSearch(ArrayList<ArrayList<Object>> result) throws SQLException;

    ArrayList<ArrayList<Object>> getSearch(SqlInfo<T> sqlInfo) throws SQLException;
}
