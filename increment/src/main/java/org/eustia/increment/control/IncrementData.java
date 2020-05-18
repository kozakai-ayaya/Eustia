package org.eustia.increment.control;
/*
 * @package: org.eustia.increment.control
 * @program: SumData
 * @description
 *
 * @author:  rinne
 * @e-mail:  minami.rinne.me@gmail.com
 * @date: 2020/05/18 午前 01:31
 */

import org.eustia.common.model.SqlInfo;
import org.eustia.common.time.TimeCheckpoint;
import org.eustia.increment.dao.IncrementConnect;
import org.eustia.increment.model.IncrementInfo;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @classname: IncrementData
 * @description: %{description}
 * @author: rinne
 * @date: 2020/05/18 午前 01:31
 * @Version 1.0
 */

public class IncrementData {
    TimeCheckpoint timeCheckpoint = new TimeCheckpoint();
    IncrementConnect incrementConnect = new IncrementConnect();

    public IncrementData() throws SQLException {
        try {
            insertSumData();
            increment();
            TimeUnit.MINUTES.sleep(5);
        } catch (InterruptedException throwables) {
            throwables.printStackTrace();
        }
    }

    private void insertSumData() throws SQLException {
        SqlInfo<IncrementInfo> sqlInfo = new SqlInfo<>();
        HashMap<String, Long> map = new HashMap<>();
        SqlInfo<IncrementInfo> hotWord = new SqlInfo<>();
        SqlInfo<IncrementInfo> emotionalWord = new SqlInfo<>();
        ArrayList<ArrayList<Object>> arrayLists = new ArrayList<>();

        if (this.timeCheckpoint.isSecondDay()) {
            String hotWordTableName = "hot_word" + timeCheckpoint.getTimeFormat();
            String emotionalWordTableName = "emotional_word" + timeCheckpoint.getTimeFormat();

            hotWord.setTable("hot_word%");
            emotionalWord.setTable("emotional_word%");

            ArrayList<String> hotWordTable = this.incrementConnect.getTable(hotWord);
            ArrayList<String> emotionalWordTable = this.incrementConnect.getTable(emotionalWord);

            if (hotWordTable.contains(hotWordTableName) && emotionalWordTable.contains(emotionalWordTableName)) {
                sqlInfo.setTable("increment_count" + this.timeCheckpoint.getTimeFormat());
                hotWord.setTable("hot_word" + this.timeCheckpoint.getTimeFormat());
                emotionalWord.setTable("emotional_word" + this.timeCheckpoint.getTimeFormat());
                this.incrementConnect.createTable(sqlInfo);
            } else {
                sqlInfo.setTable("increment_count" + this.timeCheckpoint.getLastDayTimeFormat());
                hotWord.setTable("hot_word" + this.timeCheckpoint.getLastDayTimeFormat());
                emotionalWord.setTable("emotional_word" + this.timeCheckpoint.getLastDayTimeFormat());
            }

        } else {
            sqlInfo.setTable("increment_count" + this.timeCheckpoint.getTimeFormat());
            hotWord.setTable("hot_word" + timeCheckpoint.getTimeFormat());
            emotionalWord.setTable("emotional_word" + timeCheckpoint.getTimeFormat());
        }

        hotWord.setSumKey("count");
        map.put("hotword", this.incrementConnect.sumData(hotWord));

        emotionalWord.setSumKey("count");
        emotionalWord.setKey("emotional");
        map.put("positive", this.incrementConnect.sumWhereData(emotionalWord, "positive"));
        map.put("negative", this.incrementConnect.sumWhereData(emotionalWord, "negative"));
        map.put("unknown", this.incrementConnect.sumWhereData(emotionalWord, "unknown"));
        Long time = (System.currentTimeMillis()  / (5 * 60)) * (5 * 60 * 1000);

        for (Map.Entry<String, Long> x : map.entrySet()) {
            ArrayList<Object> data = new ArrayList<>();
            data.add(time);
            data.add(x.getKey());
            data.add(x.getValue());
            arrayLists.add(data);
        }
        sqlInfo.setManyDataList(arrayLists);
        this.incrementConnect.insertManyData(sqlInfo);
    }

    private void increment() throws SQLException {
        int four = 4;
        SqlInfo<IncrementInfo> sqlInfo = new SqlInfo<>();
        SqlInfo<IncrementInfo> tableSql = new SqlInfo<>();
        String incrementCountTableName = "increment_count" + this.timeCheckpoint.getTimeFormat();
        tableSql.setTable("increment_count%");
        ArrayList<String> table = this.incrementConnect.getTable(tableSql);
        String lastDayTableName = "increment_count" + this.timeCheckpoint.getLastDayTimeFormat();

        if (table.contains(incrementCountTableName)) {
            sqlInfo.setCountKey("*");
            sqlInfo.setTable(incrementCountTableName);
            long dataCount = this.incrementConnect.countData(sqlInfo);

            HashMap<String, Long> newData = new HashMap<>(4);
            HashMap<String, Long> oldData = new HashMap<>(4);
            HashMap<String, Long> increment = new HashMap<>(4);
            ArrayList<ArrayList<Object>> arrayLists = new ArrayList<>();

            if (table.contains(lastDayTableName) && dataCount < four) {
                sqlInfo.setTable(lastDayTableName);
                sqlInfo.setKey("*");
                sqlInfo.setOperation(" time_stamp >= " + (System.currentTimeMillis() - (30 * 60 * 1000)) + " ORDER BY time_stamp DESC LIMIT 8");
                ArrayList<ArrayList<Object>> result = this.incrementConnect.getSearch(sqlInfo);

                int i = result.size() / 2;
                int flag = 0;
                for (ArrayList<Object> x : result) {
                    if (flag < i) {
                        newData.put((String) x.get(1),(Long) x.get(2));
                        flag ++;
                    } else {
                        oldData.put((String) x.get(1),(Long) x.get(2));
                    }
                }

            } else if (table.contains(lastDayTableName) && dataCount == four) {

                sqlInfo.setTable(lastDayTableName);
                sqlInfo.setKey("*");
                sqlInfo.setOperation(" time_stamp >= " + (System.currentTimeMillis() - (30 * 60 * 1000)) + " ORDER BY time_stamp DESC LIMIT 4");
                ArrayList<ArrayList<Object>> result = this.incrementConnect.getSearch(sqlInfo);

                for (ArrayList<Object> x : result) {
                    oldData.put((String) x.get(1),(Long) x.get(2));
                }

                sqlInfo.setTable(incrementCountTableName);
                sqlInfo.setKey("*");
                sqlInfo.setOperation(" time_stamp >= " + (System.currentTimeMillis() - (30 * 60 * 1000)) + " ORDER BY time_stamp DESC LIMIT 4");
                ArrayList<ArrayList<Object>> newResult = this.incrementConnect.getSearch(sqlInfo);

                for (ArrayList<Object> x : newResult) {
                    newData.put((String) x.get(1),(Long) x.get(2));
                }
            } else if (!table.contains(lastDayTableName) && dataCount == four) {
                sqlInfo.setTable(incrementCountTableName);
                sqlInfo.setKey("*");
                sqlInfo.setOperation(" time_stamp >= " + (System.currentTimeMillis() - (30 * 60 * 1000)) + " ORDER BY time_stamp DESC LIMIT 4");
                ArrayList<ArrayList<Object>> result = this.incrementConnect.getSearch(sqlInfo);

                for (ArrayList<Object> x : result) {
                    newData.put((String) x.get(1),(Long) x.get(2));
                }

                oldData.put("hotword", 0L);
                oldData.put("positive", 0L);
                oldData.put("negative", 0L);
                oldData.put("unknown", 0L);
            } else if (dataCount > four) {
                sqlInfo.setTable(incrementCountTableName);
                sqlInfo.setKey("*");
                sqlInfo.setOperation(" time_stamp >= " + (System.currentTimeMillis() - (30 * 60 * 1000)) + " ORDER BY time_stamp DESC LIMIT 8");
                ArrayList<ArrayList<Object>> result = this.incrementConnect.getSearch(sqlInfo);

                int i = result.size() / 2;
                int flag = 0;
                for (ArrayList<Object> x : result) {
                    if (flag < i) {
                        newData.put((String) x.get(1),(Long) x.get(2));
                        flag ++;
                    } else {
                        oldData.put((String) x.get(1),(Long) x.get(2));
                    }
                }

            }

            increment.put("hotword", (newData.get("hotword") - oldData.get("hotword")));
            increment.put("positive", (newData.get("positive") - oldData.get("positive")));
            increment.put("negative", (newData.get("negative") - oldData.get("negative")));
            increment.put("unknown", (newData.get("unknown") - oldData.get("unknown")));
            Long time = (System.currentTimeMillis()  / (5 * 60)) * (5 * 60 * 1000);

            for (Map.Entry<String, Long> x : increment.entrySet()) {
                ArrayList<Object> data = new ArrayList<>();
                data.add(time);
                data.add(x.getKey());
                data.add(x.getValue());
                arrayLists.add(data);
            }

            sqlInfo.setTable("increment" + this.timeCheckpoint.getTimeFormat());
            sqlInfo.setManyDataList(arrayLists);
            if (!table.contains(sqlInfo.getTable())) {
                this.incrementConnect.createTable(sqlInfo);
            }
            this.incrementConnect.insertManyData(sqlInfo);
        } else if (!table.contains(incrementCountTableName) && table.contains(lastDayTableName)) {
            HashMap<String, Long> newData = new HashMap<>(4);
            HashMap<String, Long> oldData = new HashMap<>(4);
            HashMap<String, Long> increment = new HashMap<>(4);
            ArrayList<ArrayList<Object>> arrayLists = new ArrayList<>();

            sqlInfo.setTable("increment_count" + this.timeCheckpoint.getLastDayTimeFormat());
            sqlInfo.setKey("*");
            sqlInfo.setOperation(" time_stamp >= " + (System.currentTimeMillis() - (30 * 60 * 1000)) + " ORDER BY time_stamp DESC LIMIT 8");
            ArrayList<ArrayList<Object>> result = this.incrementConnect.getSearch(sqlInfo);

            int i = result.size() / 2;
            int flag = 0;
            for (ArrayList<Object> x : result) {
                if (flag < i) {
                    newData.put((String) x.get(1),(Long) x.get(2));
                    flag ++;
                } else {
                    oldData.put((String) x.get(1),(Long) x.get(2));
                }
            }

            increment.put("hotword", (newData.get("hotword") - oldData.get("hotword")));
            increment.put("positive", (newData.get("positive") - oldData.get("positive")));
            increment.put("negative", (newData.get("negative") - oldData.get("negative")));
            increment.put("unknown", (newData.get("unknown") - oldData.get("unknown")));
            Long time = (System.currentTimeMillis()  / (5 * 60)) * (5 * 60 * 1000);

            for (Map.Entry<String, Long> x : increment.entrySet()) {
                ArrayList<Object> data = new ArrayList<>();
                data.add(time);
                data.add(x.getKey());
                data.add(x.getValue());
                arrayLists.add(data);
            }

            sqlInfo.setTable("increment" + this.timeCheckpoint.getLastDayTimeFormat());
            sqlInfo.setManyDataList(arrayLists);
            if (!table.contains(sqlInfo.getTable())) {
                this.incrementConnect.createTable(sqlInfo);
            }
            this.incrementConnect.insertManyData(sqlInfo);
        }
    }
}
