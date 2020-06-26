package org.eustia.common.dao;
/*
 * @package: org.eustia.common.dao
 * @program: AbstractDataBaseConnect
 * @description
 *
 * @author:  rinne
 * @e-mail:  minami.rinne.me@gmail.com
 * @date: 2020/02/22 午後 08:54
 */

import org.eustia.common.dao.impl.DataBaseOperation;
import org.eustia.common.db.HikariCpConnect;
import org.eustia.common.model.SqlInfo;

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
    public void createTable(SqlInfo<T> sqlInfo) throws SQLException {
        try (Connection connection = HikariCpConnect.syncPool.getConnection()) {
            String sql = "CREATE TABLE IF NOT EXISTS " + sqlInfo.getTable() + " " + sqlInfo.getValue() ;
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            //System.out.println(preparedStatement.toString());
            preparedStatement.execute();
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
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            for (ArrayList<Object> info : sqlInfo.getManyDataList()) {
                for (int i = 1, value = 0; value < info.size(); i++, value++) {
                    preparedStatement.setObject(i, info.get(value));
                }
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
        }
    }

    @Override
    public void insertDuplicateUpdateData(SqlInfo<T>  sqlInfo) throws SQLException {
        try (Connection connection = HikariCpConnect.syncPool.getConnection()) {
            String sql = "INSERT INTO " + sqlInfo.getTable() + " " + sqlInfo.getKey() + " VALUES " + sqlInfo.getValue()
                    + " ON DUPLICATE KEY UPDATE " + sqlInfo.getUpdateKey() + " = " + sqlInfo.getOperation();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            ArrayList<Object> list = sqlInfo.getList();
            for (int i = 1, value = 0; value < list.size(); i++, value++) {
                preparedStatement.setObject(i, list.get(value));
            }
            //System.out.println(preparedStatement.toString());
            try {
                preparedStatement.execute();
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }

    @Override
    public void insertManyDuplicateUpdateData(SqlInfo<T> sqlInfo) throws SQLException {
        try (Connection connection = HikariCpConnect.syncPool.getConnection()) {
            String sql = "INSERT INTO " + sqlInfo.getTable() + " " + sqlInfo.getKey() + " VALUES " + sqlInfo.getValue()
                    + " ON DUPLICATE KEY UPDATE " + sqlInfo.getUpdateKey() + " = " + sqlInfo.getOperation();

            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            for (ArrayList<Object> info : sqlInfo.getManyDataList()) {
                for (int i = 1, value = 0; value < info.size(); i++, value++) {
                    preparedStatement.setObject(i, info.get(value));
                }
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
        }
    }

    @Override
    public void insertDuplicateReplaceData(SqlInfo<T> sqlInfo) throws SQLException {
        try (Connection connection = HikariCpConnect.syncPool.getConnection()) {
            String sql = "INSERT INTO " + sqlInfo.getTable() + " " + sqlInfo.getKey() + " VALUES " + sqlInfo.getValue()
                    + " ON DUPLICATE KEY REPLACE " + sqlInfo.getUpdateKey() + " = " + sqlInfo.getOperation();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            ArrayList<Object> list = sqlInfo.getList();
            for (int i = 1, value = 0; value < list.size(); i++, value++) {
                preparedStatement.setObject(i, list.get(value));
            }
            //System.out.println(preparedStatement.toString());
            try {
                preparedStatement.execute();
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }

    @Override
    public void insertManyDuplicateReplaceData(SqlInfo<T> sqlInfo) throws SQLException {
        try (Connection connection = HikariCpConnect.syncPool.getConnection()) {
            String sql = "INSERT INTO " + sqlInfo.getTable() + " " + sqlInfo.getKey() + " VALUES " + sqlInfo.getValue()
                    + " ON DUPLICATE KEY REPLACE " + sqlInfo.getUpdateKey() + " = " + sqlInfo.getOperation();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            for (ArrayList<Object> info : sqlInfo.getManyDataList()) {
                for (int i = 1, value = 0; value < info.size(); i++, value++) {
                    preparedStatement.setObject(i, info.get(value));
                }
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
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
    public long sumData(SqlInfo<T> sqlInfo) throws SQLException {
        try (Connection connection = HikariCpConnect.syncPool.getConnection()) {
            String sql = "SELECT SUM(" + sqlInfo.getSumKey() + ") FROM " + sqlInfo.getTable();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            
            long result = 0;
            while (resultSet.next()) {
                result = resultSet.getInt(1);
            }
            return result;
        }
    }

    @Override
    public long sumWhereData(SqlInfo<T> sqlInfo) throws SQLException {
        try (Connection connection = HikariCpConnect.syncPool.getConnection()) {
            String sql = "SELECT SUM(" + sqlInfo.getSumKey() + ") FROM " + sqlInfo.getTable() + " WHERE " + sqlInfo.getKey() + "= ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setObject(1, sqlInfo.getValue());
            ResultSet resultSet = preparedStatement.executeQuery();
            long result = 0;
            while (resultSet.next()) {
                result = resultSet.getInt(1);
            }
            return result;
        }
    }

    @Override
    public ArrayList<String> getTable(SqlInfo<T> sqlInfo) throws SQLException {
        try (Connection connection = HikariCpConnect.syncPool.getConnection()) {
            ArrayList<String> arrayList = new ArrayList<>();
            ResultSet resultSet = connection.getMetaData().getTables(null, null, sqlInfo.getTable(), new String[] { "TABLE" });
            while (resultSet.next()) {
                arrayList.add(resultSet.getString("TABLE_NAME"));
            }
            return arrayList;
        }
    }

    @Override
    public long countData(SqlInfo<T> sqlInfo) throws SQLException {
        try (Connection connection = HikariCpConnect.syncPool.getConnection()) {
            String sql = "SELECT COUNT(" + sqlInfo.getCountKey() + ") FROM " + sqlInfo.getTable();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            long result = 0;
            while (resultSet.next()) {
                result = resultSet.getInt(1);
            }
            return result;
        }
    }

    @Override
    public ArrayList<T> getSearch(ArrayList<ArrayList<Object>> result) throws SQLException {
        return null;
    }

    @Override
    public ArrayList<ArrayList<Object>> getSearch(SqlInfo<T> sqlInfo) throws SQLException {
        try (Connection connection = HikariCpConnect.syncPool.getConnection()) {
            ArrayList<ArrayList<Object>> resultList = new ArrayList<>();
            String sql = "SELECT " + sqlInfo.getKey() + " FROM " + sqlInfo.getTable() + " WHERE " + sqlInfo.getOperation();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery(sql);

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

}
