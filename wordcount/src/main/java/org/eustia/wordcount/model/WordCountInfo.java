package org.eustia.wordcount.model;
/*
 * @package: org.eustia.wordcount.model
 * @program: WordCountInfo
 * @description
 *
 * @author:  rinne
 * @e-mail:  minami.rinne.me@gmail.com
 * @date: 2020/02/22 午後 09:21
 */

/**
 * @classname: WordCountInfo
 * @description: %{description}
 * @author: rinne
 * @date: 2020/02/22 午後 09:21
 * @Version 1.0
 */

public class WordCountInfo {
    private int timeStamp;
    private String word;
    private int count;

    public String getWord() {
        return word;
    }

    public int getCount() {
        return count;
    }

    public int getTimeStamp() {
        return timeStamp;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setTimeStamp(int timeStamp) {
        this.timeStamp = timeStamp;
    }

    public void setWord(String word) {
        this.word = word;
    }
}
