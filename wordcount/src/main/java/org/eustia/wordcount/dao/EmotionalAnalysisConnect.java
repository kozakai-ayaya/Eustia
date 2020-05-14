package org.eustia.wordcount.dao;
/*
 * @package: org.eustia.wordcount.dao
 * @program: EmotionalAnalysisConnect
 * @description
 *
 * @author:  rinne
 * @e-mail:  minami.rinne.me@gmail.com
 * @date: 2020/05/14 午前 03:41
 */

import org.eustia.common.dao.AbstractDataBaseConnect;
import org.eustia.common.dao.impl.DataBaseOperation;
import org.eustia.common.model.SqlInfo;
import org.eustia.wordcount.model.EmotionalAnalysisInfo;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * @classname: EmotionalAnalysisConnect
 * @description: %{description}
 * @author: rinne
 * @date: 2020/05/14 午前 03:41
 * @Version 1.0
 */

public class EmotionalAnalysisConnect extends AbstractDataBaseConnect<EmotionalAnalysisInfo> implements DataBaseOperation<EmotionalAnalysisInfo> {

    @Override
    public ArrayList<ArrayList<Object>> getResult(SqlInfo<EmotionalAnalysisInfo> sqlInfo) throws SQLException {
        return super.getResult(sqlInfo);
    }

    @Override
    public ArrayList<ArrayList<Object>> getAllResult(SqlInfo<EmotionalAnalysisInfo> sqlInfo) throws SQLException {
        return super.getAllResult(sqlInfo);
    }

    @Override
    public void createTable(SqlInfo<EmotionalAnalysisInfo> sqlInfo) throws SQLException {
        sqlInfo.setTable("emotional_word" + sqlInfo.getTable());
        sqlInfo.setValue("(times_stamp bigint(20) NOT NULL, " +
                         "av varchar(255) NOT NULL, " +
                         "emotional varchar(255) NOT NULL, " +
                         "count int(11) DEFAULT NULL, " +
                         "PRIMARY KEY (times_stamp, av, emotional), " +
                         "KEY times_stamp (times_stamp), " +
                         "KEY av (av), " +
                         "KEY emotional (emotional), " +
                         "KEY av_2 (av, emotional)) ENGINE=InnoDB");
        super.createTable(sqlInfo);
    }

    @Override
    public void insertData(SqlInfo<EmotionalAnalysisInfo> sqlInfo) throws SQLException {
        super.insertData(sqlInfo);
    }

    @Override
    public void insertManyData(SqlInfo<EmotionalAnalysisInfo> sqlInfo) throws SQLException {
        super.insertManyData(sqlInfo);
    }

    @Override
    public void insertDuplicateUpdateData(SqlInfo<EmotionalAnalysisInfo> sqlInfo) throws SQLException {
        sqlInfo.setTable("emotional_word" + sqlInfo.getTable());
        sqlInfo.setKey("(times_stamp, av, emotional, count)");
        sqlInfo.setValue("(?, ?, ?, ?)");
        sqlInfo.setUpdateKey("count");
        sqlInfo.setOperation("count + ?");

        EmotionalAnalysisInfo emotionalAnalysisInfo = sqlInfo.getModel();
        ArrayList<Object> list = new ArrayList<>();
        list.add(emotionalAnalysisInfo.getTime());
        list.add(emotionalAnalysisInfo.getAvNumber());
        list.add(emotionalAnalysisInfo.getEmotional());
        list.add(emotionalAnalysisInfo.getCount());
        list.add(emotionalAnalysisInfo.getCount());

        sqlInfo.setList(list);
        super.insertDuplicateUpdateData(sqlInfo);
    }

    @Override
    public void insertManyDuplicateUpdateData(SqlInfo<EmotionalAnalysisInfo> sqlInfo) throws SQLException {
        super.insertManyDuplicateUpdateData(sqlInfo);
    }

    @Override
    public void insertDuplicateReplaceData(SqlInfo<EmotionalAnalysisInfo> sqlInfo) throws SQLException {
        super.insertDuplicateReplaceData(sqlInfo);
    }

    @Override
    public void insertManyDuplicateReplaceData(SqlInfo<EmotionalAnalysisInfo> sqlInfo) throws SQLException {
        super.insertManyDuplicateReplaceData(sqlInfo);
    }

    @Override
    public void updateData(SqlInfo<EmotionalAnalysisInfo> sqlInfo) throws SQLException {
        super.updateData(sqlInfo);
    }

    @Override
    public void deleteData(SqlInfo<EmotionalAnalysisInfo> sqlInfo) throws SQLException {
        super.deleteData(sqlInfo);
    }

    @Override
    public ArrayList<EmotionalAnalysisInfo> getSearch(ArrayList<ArrayList<Object>> result) {
        return super.getSearch(result);
    }
}
