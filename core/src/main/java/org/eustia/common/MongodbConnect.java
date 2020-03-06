package org.eustia.common;
/*
 * @package: org.eustia.common
 * @program: MongodbConnect
 * @description
 *
 * @author:  rinne
 * @e-mail:  minami.rinne.me@gmail.com
 * @date: 2020/03/03 午前 02:45
 */

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ServerAddress;

/**
 * @classname: MongodbConnect
 * @description: %{description}
 * @author: rinne
 * @date: 2020/03/03 午前 02:45
 * @Version 1.0
 */

public class MongodbConnect {
    public static MongoClient mongoClient;

    static {
        MongoClientOptions option = MongoClientOptions.builder().connectTimeout(60000).build();
        mongoClient = new MongoClient(new ServerAddress("localhost",27017), option);
    }
}
