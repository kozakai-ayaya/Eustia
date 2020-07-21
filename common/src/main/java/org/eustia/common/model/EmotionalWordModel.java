package org.eustia.common.model;
/*
 * @package: org.eustia.common.model
 * @program: EmotionalWordModel
 * @description
 *
 * @author:  rinne
 * @e-mail:  minami.rinne.me@gmail.com
 * @date: 2020/7/15 3:48 下午
 */

import javafx.util.Pair;

/**
 * @classname: EmotionalWordModel
 * @description: %{description}
 * @author: rinne
 * @date: 2020/7/15 3:48 下午
 * @Version 1.0
 */

public class EmotionalWordModel {

    private String emotionalWord;
    private Long timestamp;
    private int count;
    private String key;
    private String av;

    public EmotionalWordModel() {}

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
        if (this.key != null) {
            this.key = this.timestamp + "&" + this.emotionalWord;
        }
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setEmotionalWord(String emotionalWord) {
        this.emotionalWord = emotionalWord;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setAv(String av) {
        this.av = av;
    }

    public String getAv() {
        return av;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public int getCount() {
        return count;
    }

    public String getEmotionalWord() {
        return emotionalWord;
    }

    public String getKey() {
        return key;
    }
}
