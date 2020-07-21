package org.eustia.common.model;
/*
 * @package: org.eustia.common.model
 * @program: WordCountModel
 * @description
 *
 * @author:  rinne
 * @e-mail:  minami.rinne.me@gmail.com
 * @date: 2020/7/15 3:31 下午
 */

import javafx.util.Pair;

/**
 * @classname: WordCountModel
 * @description: %{description}
 * @author: rinne
 * @date: 2020/7/15 3:31 下午
 * @Version 1.0
 */

public class WordCountModel {
    private String word;
    private int count;
    private Long timestamp;
    private String key;
    private String av;

    public WordCountModel() {}

    public void setCount(int count) {
        this.count = count;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
        if (this.word != null) {
            this.key = this.timestamp + "&" + this.word;
        }
    }

    public void setWord(String word) {
        this.word = word;
        if (this.timestamp != null) {
            this.key = this.timestamp + "&" + this.word;
        }
    }

    public void setAv(String av) {
        this.av = av;
    }

    public String getAv() {
        return av;
    }

    public int getCount() {
        return count;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public String getWord() {
        return word;
    }

    public String getKey() {
        return key;
    }
}
