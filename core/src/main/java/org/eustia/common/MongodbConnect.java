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
import com.mongodb.MongoClientURI;

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
        mongoClient = new MongoClient("localhost", 27017);
    }
}
