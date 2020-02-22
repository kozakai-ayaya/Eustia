package org.eustia.common;
/*
 * @package: org.eustia.common
 * @program: HikariCpConnect
 * @description
 *
 * @author:  rinne
 * @e-mail:  minami.rinne.me@gmail.com
 * @date: 2020/02/22 午後 08:55
 */

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

/**
 * @classname: HikariCpConnect
 * @description: %{description}
 * @author: rinne
 * @date: 2020/02/22 午後 08:55
 * @Version 1.0
 */

public class HikariCpConnect {
    private final HashMap<String, String> databaseInfo = new HashMap<>();
    public static HikariDataSource syncPool;

    public HikariCpConnect() {
        readDatabaseInfo();

        HikariConfig hikariConfig = new HikariConfig();
        final String className = "com.mysql.cj.jdbc.Driver";
        hikariConfig.setDriverClassName(className);
        hikariConfig.setJdbcUrl(this.databaseInfo.get("url"));
        hikariConfig.setUsername(this.databaseInfo.get("user"));
        hikariConfig.setPassword(this.databaseInfo.get("pwd"));
        hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
        hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        syncPool = new HikariDataSource(hikariConfig);
    }

    private void readDatabaseInfo() {
        File jsonFile = new File("src/main/resources/word_count_db.json");

        try (Reader reader = new InputStreamReader(new FileInputStream(jsonFile), StandardCharsets.UTF_8)) {

            StringBuilder stringBuilder = new StringBuilder();

            int i;
            while ((i = reader.read()) != -1) {
                stringBuilder.append((char) i);
            }

            String jsonString = stringBuilder.toString();
            JSONObject jsonObject = JSON.parseObject(jsonString);
            this.databaseInfo.put("driver", jsonObject.getString("driver"));
            this.databaseInfo.put("url", jsonObject.getString("url"));
            this.databaseInfo.put("user", jsonObject.getString("user"));
            this.databaseInfo.put("pwd", jsonObject.getString("pwd"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
