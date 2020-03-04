package org.eustia.model;
/*
 * @package: org.eustia.model
 * @program: BasicDataInfo
 * @description
 *
 * @author:  rinne
 * @e-mail:  minami.rinne.me@gmail.com
 * @date: 2020/03/04 午後 05:09
 */

import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.JsonNode;

/**
 * @classname: BasicDataInfo
 * @description: %{description}
 * @author: rinne
 * @date: 2020/03/04 午後 05:09
 * @Version 1.0
 */

public class BasicDataInfo {
    private String id;
    private JsonNode date;

    public String getId() {
        return id;
    }

    public JsonNode getDate() {
        return date;
    }

    public void setDate(JsonNode date) {
        this.date = date;
    }

    public void setId(String id) {
        this.id = id;
    }
}
