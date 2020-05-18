package org.eustia.increment.model;
/*
 * @package: org.eustia.increment.model
 * @program: IncrementInfo
 * @description
 *
 * @author:  rinne
 * @e-mail:  minami.rinne.me@gmail.com
 * @date: 2020/05/18 午前 02:09
 */

/**
 * @classname: IncrementInfo
 * @description: %{description}
 * @author: rinne
 * @date: 2020/05/18 午前 02:09
 * @Version 1.0
 */

public class IncrementInfo {
    private String table;
    private String word;
    private Long count;

    public void setTable(String table) {
        this.table = table;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public void setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getTable() {
        return table;
    }

    public String getWord() {
        return word;
    }

    public Long getCount() {
        return count;
    }

    public Long getTimeStamp() {
        return timeStamp;
    }

    private Long timeStamp;
}
