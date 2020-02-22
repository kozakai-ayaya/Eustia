package org.eustia.dao;
/*
 * @package: org.eustia.dao
 * @program: AbstractDataBaseConnect
 * @description
 *
 * @author:  rinne
 * @e-mail:  minami.rinne.me@gmail.com
 * @date: 2020/02/22 午後 08:54
 */

import org.eustia.common.HikariCpConnect;
import org.eustia.dao.impl.DataBaseOperation;
import org.eustia.model.SqlInfo;
import org.eustia.model.WordCountInfo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @classname: AbstractDataBaseConnect
 * @description: %{description}
 * @author: rinne
 * @date: 2020/02/22 午後 08:54
 * @Version 1.0
 */

public class AbstractDataBaseConnect<T> implements DataBaseOperation<T> {

    public AbstractDataBaseConnect() {

    }

    @Override
    public ArrayList<ArrayList<Object>> getResult(SqlInfo<T> sqlInfo) throws SQLException {
        try (Connection connection = HikariCpConnect.syncPool.getConnection()) {
            ArrayList<ArrayList<Object>> resultList = new ArrayList<>();

            String sql = "SELECT * FROM " + sqlInfo.getTable() + " WHERE " + sqlInfo.getKey() + " = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setObject(1, sqlInfo.getValue());
            ResultSet resultSet = preparedStatement.executeQuery();

            int count = resultSet.getMetaData().getColumnCount();
            while (resultSet.next()) {
                ArrayList<Object> arrayList = new ArrayList<>();
                for (int i = 1; i <= count; i ++) {

                    arrayList.add(resultSet.getObject(i));
                }
                resultList.add(arrayList);
            }
            return resultList;
        }
    }

    @Override
    public ArrayList<ArrayList<Object>> getAllResult(SqlInfo<T> sqlInfo) throws SQLException {
        try (Connection connection = HikariCpConnect.syncPool.getConnection()) {
            ArrayList<ArrayList<Object>> resultList = new ArrayList<>();
            String sql = "SELECT * FROM " + sqlInfo.getTable();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();

            int count = resultSet.getMetaData().getColumnCount();
            while (resultSet.next()) {
                ArrayList<Object> arrayList = new ArrayList<>();
                for (int i = 1; i <= count; i ++) {
                    arrayList.add(resultSet.getObject(i));
                }
                resultList.add(arrayList);
            }
            return resultList;
        }
    }

    @Override
    public void insertData(SqlInfo<T> sqlInfo) throws SQLException {
        try (Connection connection = HikariCpConnect.syncPool.getConnection()) {
            String sql = "INSERT INTO " + sqlInfo.getTable() +  " " + sqlInfo.getKey() + " VALUES " + sqlInfo.getValue();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ArrayList<Object> list = sqlInfo.getList();
            for (int i = 1, value = 0; value < list.size(); i++, value++) {
                preparedStatement.setObject(i, list.get(value));
            }
            preparedStatement.executeUpdate();
        }
    }

    @Override
    public void insertManyData(SqlInfo<T> sqlInfo) throws SQLException {
        try (Connection connection = HikariCpConnect.syncPool.getConnection()) {
            String sql = "INSERT INTO " + sqlInfo.getTable() +  " " + sqlInfo.getKey() + " VALUES " + sqlInfo.getValue();
            connection.setAutoCommit(false);
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            for (ArrayList<Object> info : sqlInfo.getManyDataList()) {
                for (int i = 1, value = 0; value < info.size(); i++, value++) {
                    preparedStatement.setObject(i, info.get(value));
                }
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
            connection.commit();
        }
    }

    @Override
    public void updateData(SqlInfo<T> sqlInfo) throws SQLException {
        try (Connection connection = HikariCpConnect.syncPool.getConnection()) {
            StringBuilder key = new StringBuilder();
            HashMap<String, Object> hashMap = sqlInfo.getUpdateMap();
            ArrayList<Object> list = new ArrayList<>();

            for (String a : hashMap.keySet()) {
                key.append(a).append(" = ?, ");
                list.add(hashMap.get(a));
            }

            key.deleteCharAt(key.length() - 2);

            String sql = "UPDATE " + sqlInfo.getTable() + " SET " + key.toString() + " WHERE " + sqlInfo.getKey() + " = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            for (int i = 1, value = 0; value < list.size(); i++, value++) {
                preparedStatement.setObject(i, list.get(value));
                if (i == list.size()) {
                    preparedStatement.setObject(i + 1, sqlInfo.getValue());
                }
            }
            preparedStatement.executeUpdate();
        }
    }

    @Override
    public void deleteData(SqlInfo<T> sqlInfo) throws SQLException {
        try (Connection connection = HikariCpConnect.syncPool.getConnection()) {
            String sql = "DELETE FROM " + sqlInfo.getTable() + " WHERE " + sqlInfo.getKey() + " = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setObject(1, sqlInfo.getValue());
            preparedStatement.execute();
        }
    }

    @Override
    public ArrayList<T> getSearch(ArrayList<ArrayList<Object>> result) {
        return null;
    }

    public static void main(String[] args) throws SQLException {
        new HikariCpConnect();
        WordCountInfo wordCountInfo = new WordCountInfo();
        SqlInfo<WordCountInfo> sqlInfo = new SqlInfo<>();
        AbstractDataBaseConnect<WordCountInfo> abstractDataBaseConnect = new AbstractDataBaseConnect<>();
        ArrayList<ArrayList<Object>> a = new ArrayList<>();
        for (int i = 0; i < 5; i ++) {
            ArrayList<Object> arrayList = new ArrayList<>();
            arrayList.add(1 + i);
            arrayList.add(10 + i);
            arrayList.add(20 + i);
            a.add(arrayList);
        }
        sqlInfo.setManyDataList(a);
        sqlInfo.setTable("hot_word");
        sqlInfo.setKey("(times_stamp, word, count)");
        sqlInfo.setValue("(?, ?, ?)");
        abstractDataBaseConnect.insertManyData(sqlInfo);
    }
}
