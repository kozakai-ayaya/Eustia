package org.eustia.dao;
/*
 * @package: org.eustia.dao.impl
 * @program: WordCountConnect
 * @description
 *
 * @author:  rinne
 * @e-mail:  minami.rinne.me@gmail.com
 * @date: 2020/02/22 午後 06:47
 */

import org.eustia.dao.impl.DataBaseOperation;
import org.eustia.model.SqlInfo;
import org.eustia.model.WordCountInfo;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * @classname: WordCountConnect
 * @description: %{description}
 * @author: rinne
 * @date: 2020/02/22 午後 06:47
 * @Version 1.0
 */

public class WordCountConnect extends AbstractDataBaseConnect<WordCountInfo> implements DataBaseOperation<WordCountInfo> {
    @Override
    public ArrayList<ArrayList<Object>> getResult(SqlInfo<WordCountInfo> sqlInfo) throws SQLException {
        sqlInfo.setTable("hot_word");
        return super.getResult(sqlInfo);
    }

    @Override
    public ArrayList<ArrayList<Object>> getAllResult(SqlInfo<WordCountInfo> sqlInfo) throws SQLException {
        sqlInfo.setTable("hot_word");
        return super.getResult(sqlInfo);
    }

    @Override
    public void insertData(SqlInfo<WordCountInfo> sqlInfo) throws SQLException {
        sqlInfo.setTable("hot_word");
        sqlInfo.setKey("time_stamp, word, count");
        sqlInfo.setValue("(?, ?, ?)");

        WordCountInfo wordCountInfo = sqlInfo.getModel();
        ArrayList<Object> list = new ArrayList<>();
        list.add(wordCountInfo.getTimeStamp());
        list.add(wordCountInfo.getWord());
        list.add(wordCountInfo.getCount());

        sqlInfo.setList(list);
        super.insertData(sqlInfo);
    }

    @Override
    public void updateData(SqlInfo<WordCountInfo> sqlInfo) throws SQLException {

    }

    @Override
    public void deleteData(SqlInfo<WordCountInfo> sqlInfo) throws SQLException {

    }

    @Override
    public ArrayList<WordCountInfo> getSearch(ArrayList<ArrayList<Object>> result) {
        return null;
    }
}
