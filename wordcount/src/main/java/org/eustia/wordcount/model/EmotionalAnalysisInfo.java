package org.eustia.wordcount.model;
/*
 * @package: org.eustia.wordcount.model
 * @program: EmotionalAnalysisInfo
 * @description
 *
 * @author:  rinne
 * @e-mail:  minami.rinne.me@gmail.com
 * @date: 2020/05/14 午前 03:43
 */

/**
 * @classname: EmotionalAnalysisInfo
 * @description: %{description}
 * @author: rinne
 * @date: 2020/05/14 午前 03:43
 * @Version 1.0
 */

public class EmotionalAnalysisInfo {
    private long time;
    private String avNumber;
    private String emotional;
    private int count;

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getAvNumber() {
        return avNumber;
    }

    public void setAvNumber(String avNumber) {
        this.avNumber = avNumber;
    }

    public String getEmotional() {
        return emotional;
    }

    public void setEmotional(String emotional) {
        this.emotional = emotional;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
