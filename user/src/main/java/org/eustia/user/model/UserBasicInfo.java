package org.eustia.user.model;
/*
 * @package: org.eustia.user.model
 * @program: UserBasicInfo
 * @description
 *
 * @author:  rinne
 * @e-mail:  minami.rinne.me@gmail.com
 * @date: 2020/03/11 午前 09:55
 */

import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.JsonNode;

/**
 * @classname: UserBasicInfo
 * @description: %{description}
 * @author: rinne
 * @date: 2020/03/11 午前 09:55
 * @Version 1.0
 */

public class UserBasicInfo {
    private String id;
    private JsonNode data;

    public String getId() {
        return id;
    }

    public JsonNode getData() {
        return data;
    }

    public void setData(JsonNode data) {
        this.data = data;
    }

    public void setId(String id) {
        this.id = id;
    }
}
