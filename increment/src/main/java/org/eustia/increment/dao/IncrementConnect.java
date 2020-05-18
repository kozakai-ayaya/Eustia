package org.eustia.increment.dao;
/*
 * @package: org.eustia.increment.dao
 * @program: IncrementConnect
 * @description
 *
 * @author:  rinne
 * @e-mail:  minami.rinne.me@gmail.com
 * @date: 2020/05/18 午前 01:57
 */

import org.eustia.common.dao.AbstractDataBaseConnect;
import org.eustia.common.dao.impl.DataBaseOperation;
import org.eustia.common.model.SqlInfo;
import org.eustia.increment.model.IncrementInfo;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * @classname: IncrementConnect
 * @description: %{description}
 * @author: rinne
 * @date: 2020/05/18 午前 01:57
 * @Version 1.0
 */

public class IncrementConnect extends AbstractDataBaseConnect<IncrementInfo> implements DataBaseOperation<IncrementInfo> {
    public IncrementConnect() {
        super();
    }

    @Override
    public ArrayList<ArrayList<Object>> getResult(SqlInfo<IncrementInfo> sqlInfo) throws SQLException {
        return super.getResult(sqlInfo);
    }

    @Override
    public ArrayList<ArrayList<Object>> getAllResult(SqlInfo<IncrementInfo> sqlInfo) throws SQLException {
        return super.getAllResult(sqlInfo);
    }

    @Override
    public void createTable(SqlInfo<IncrementInfo> sqlInfo) throws SQLException {
        sqlInfo.setValue("(time_stamp bigint(20) NOT NULL, " +
                         "word varchar(255) NOT NULL, " +
                         "count bigint(20) DEFAULT NULL, " +
                         "PRIMARY KEY (time_stamp, word), " +
                         "KEY time_stamp (time_stamp) " +
                         ") ENGINE=InnoDB");
        super.createTable(sqlInfo);
    }

    @Override
    public void insertData(SqlInfo<IncrementInfo> sqlInfo) throws SQLException {
        super.insertData(sqlInfo);
    }

    @Override
    public void insertManyData(SqlInfo<IncrementInfo> sqlInfo) throws SQLException {
        sqlInfo.setKey("(time_stamp, word, count)");
        sqlInfo.setValue("(?, ?, ?)");
        super.insertManyData(sqlInfo);
    }

    @Override
    public void insertDuplicateUpdateData(SqlInfo<IncrementInfo> sqlInfo) throws SQLException {
        super.insertDuplicateUpdateData(sqlInfo);
    }

    @Override
    public void insertManyDuplicateUpdateData(SqlInfo<IncrementInfo> sqlInfo) throws SQLException {
        super.insertManyDuplicateUpdateData(sqlInfo);
    }

    @Override
    public void insertDuplicateReplaceData(SqlInfo<IncrementInfo> sqlInfo) throws SQLException {
        super.insertDuplicateReplaceData(sqlInfo);
    }

    @Override
    public void insertManyDuplicateReplaceData(SqlInfo<IncrementInfo> sqlInfo) throws SQLException {
        super.insertManyDuplicateReplaceData(sqlInfo);
    }

    @Override
    public void updateData(SqlInfo<IncrementInfo> sqlInfo) throws SQLException {
        super.updateData(sqlInfo);
    }

    @Override
    public void deleteData(SqlInfo<IncrementInfo> sqlInfo) throws SQLException {
        super.deleteData(sqlInfo);
    }

    @Override
    public long sumData(SqlInfo<IncrementInfo> sqlInfo) throws SQLException {
        return super.sumData(sqlInfo);
    }

    @Override
    public long sumWhereData(SqlInfo<IncrementInfo> sqlInfo) throws SQLException {
        return super.sumWhereData(sqlInfo);
    }

    public long sumWhereData(SqlInfo<IncrementInfo> sqlInfo, String value) throws SQLException {
        sqlInfo.setValue(value);
        return sumWhereData(sqlInfo);
    }

    @Override
    public ArrayList<String> getTable(SqlInfo<IncrementInfo> sqlInfo) throws SQLException {
        return super.getTable(sqlInfo);
    }

    @Override
    public long countData(SqlInfo<IncrementInfo> sqlInfo) throws SQLException {
        return super.countData(sqlInfo);
    }

    @Override
    public ArrayList<IncrementInfo> getSearch(ArrayList<ArrayList<Object>> result) throws SQLException {
        return super.getSearch(result);
    }

    @Override
    public ArrayList<ArrayList<Object>> getSearch(SqlInfo<IncrementInfo> sqlInfo) throws SQLException {
        return super.getSearch(sqlInfo);
    }
}
