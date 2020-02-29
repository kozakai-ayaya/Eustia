package org.eustia.dao;
/*
 * @package: org.eustia.dao
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
        return super.getResult(sqlInfo);
    }

    @Override
    public ArrayList<ArrayList<Object>> getAllResult(SqlInfo<WordCountInfo> sqlInfo) throws SQLException {
        return super.getResult(sqlInfo);
    }

    @Override
    public void createTable(SqlInfo<WordCountInfo> sqlInfo) throws SQLException {
        sqlInfo.setTable("hot_word" + sqlInfo.getTable());
        sqlInfo.setValue("  `times_stamp` int(11) NOT NULL," +
                         "  `word` varchar(255) NOT NULL," +
                         "  `count` int(11) DEFAULT NULL," +
                         "  PRIMARY KEY (`times_stamp`,`word`)" +
                         ") ENGINE=InnoDB");
        super.createTable(sqlInfo);
    }

    @Override
    public void insertData(SqlInfo<WordCountInfo> sqlInfo) throws SQLException {
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
    public void insertManyData(SqlInfo<WordCountInfo> sqlInfo) throws SQLException {
        sqlInfo.setKey("(times_stamp, word, count)");
        sqlInfo.setValue("(?, ?, ?)");

        super.insertManyData(sqlInfo);
    }

    @Override
    public void insertDuplicateUpdateData(SqlInfo<WordCountInfo> sqlInfo) throws SQLException {
        sqlInfo.setTable("hot_word" + sqlInfo.getTable());
        sqlInfo.setKey("(times_stamp, word, count)");
        sqlInfo.setValue("(?, ?, ?)");
        sqlInfo.setUpdateKey("count");
        sqlInfo.setOperation("count + ?");

        WordCountInfo wordCountInfo = sqlInfo.getModel();
        ArrayList<Object> list = new ArrayList<>();
        list.add(wordCountInfo.getTimeStamp());
        list.add(wordCountInfo.getWord());
        list.add(wordCountInfo.getCount());
        list.add(wordCountInfo.getCount());

        sqlInfo.setList(list);
        super.insertDuplicateUpdateData(sqlInfo);
    }

    @Override
    public void insertManyDuplicateUpdateData(SqlInfo<WordCountInfo> sqlInfo) throws SQLException {
        sqlInfo.setKey("(times_stamp, word, count)");
        sqlInfo.setValue("(?, ?, ?)");
        sqlInfo.setUpdateKey("count");
        sqlInfo.setOperation("count + ?");

        ArrayList<ArrayList<Object>> newDataList = new ArrayList<>();
        for (ArrayList<Object> arrayList : sqlInfo.getManyDataList()) {
            arrayList.add(arrayList.size());
            newDataList.add(arrayList);
        }
        sqlInfo.setManyDataList(newDataList);

        super.insertManyDuplicateUpdateData(sqlInfo);
    }

    @Override
    public void insertDuplicateReplaceData(SqlInfo<WordCountInfo> sqlInfo) throws SQLException {
        super.insertDuplicateReplaceData(sqlInfo);
    }

    @Override
    public void insertManyDuplicateReplaceData(SqlInfo<WordCountInfo> sqlInfo) throws SQLException {
        super.insertManyDuplicateReplaceData(sqlInfo);
    }

    @Override
    public void updateData(SqlInfo<WordCountInfo> sqlInfo) throws SQLException {
        super.updateData(sqlInfo);
    }

    @Override
    public void deleteData(SqlInfo<WordCountInfo> sqlInfo) throws SQLException {
        super.deleteData(sqlInfo);
    }

    @Override
    public ArrayList<WordCountInfo> getSearch(ArrayList<ArrayList<Object>> result) {
        ArrayList<WordCountInfo> arrayList = new ArrayList<>();
        for (ArrayList<Object> o : result) {
            WordCountInfo wordCountInfo = new WordCountInfo();
            wordCountInfo.setTimeStamp((int) o.get(0));
            wordCountInfo.setWord((String) o.get(1));
            wordCountInfo.setCount((int) o.get(2));
            arrayList.add(wordCountInfo);
        }
        return arrayList;
    }
}
