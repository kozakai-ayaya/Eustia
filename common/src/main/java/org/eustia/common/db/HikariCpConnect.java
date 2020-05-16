package org.eustia.common.db;
/*
 * @package: org.eustia.common.db
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

import static java.nio.charset.StandardCharsets.*;

/**
 * @classname: HikariCpConnect
 * @description: %{description}
 * @author: rinne
 * @date: 2020/02/22 午後 08:55
 * @Version 1.0
 */

public class HikariCpConnect {
    public static HikariDataSource syncPool;

    static {
        InputStream jsonFile = Thread.currentThread().getContextClassLoader().getResourceAsStream("word_count_db.json");
        if (jsonFile != null) {
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(jsonFile))) {
                StringBuilder stringBuilder = new StringBuilder();

                int i;
                while ((i = bufferedReader.read()) != -1) {
                    stringBuilder.append((char) i);
                }

                String jsonString = stringBuilder.toString();
                JSONObject jsonObject = JSON.parseObject(jsonString);

                HikariConfig hikariConfig = new HikariConfig();
                final String className = "com.mysql.cj.jdbc.Driver";
                hikariConfig.setDriverClassName(className);
                hikariConfig.setJdbcUrl(jsonObject.getString("url"));
                hikariConfig.setUsername(jsonObject.getString("user"));
                hikariConfig.setPassword(jsonObject.getString("pwd"));
                hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
                hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
                hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
                syncPool = new HikariDataSource(hikariConfig);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
