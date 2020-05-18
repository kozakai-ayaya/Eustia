package org.eustia.common.time;
/*
 * @package: org.eustia.common
 * @program: org.eustia.TimeSetting
 * @description
 *
 * @author:  rinne
 * @e-mail:  minami.rinne.me@gmail.com
 * @date: 2020/03/01 午後 07:34
 */

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

/**
 * @classname: org.eustia.Checkpoint
 * @description: %{description}
 * @author: rinne
 * @date: 2020/03/01 午後 07:34
 * @Version 1.0
 */

public class TimeCheckpoint {
    private final static int HOUR = 1000 * 60 * 60;
    private final static int DAY = 1000 * 60 * 60 * 24;
    private int flag = 0;
    private Timestamp timestamp;

    public TimeCheckpoint() {
        this.timestamp = new Timestamp(0);
    }

    public boolean isHour() {
        Timestamp nowTime = new Timestamp(System.currentTimeMillis());

        if (this.timestamp.equals(new Timestamp(0))) {
            return true;
        } else if (nowTime.getTime() - this.timestamp.getTime() >= HOUR) {
            this.timestamp = nowTime;
            this.flag ++;
            return true;
        } else {
            return false;
        }
    }

    public boolean isDay() {
        Timestamp nowTime = new Timestamp(System.currentTimeMillis());

        if (nowTime.getTime() - this.timestamp.getTime() >= DAY) {
            timestamp = nowTime;
            this.flag = 0;
            return true;
        } else {
            return false;
        }
    }

    public boolean isSecondDay() {
        Timestamp nowTime = new Timestamp(System.currentTimeMillis());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");

        if (!simpleDateFormat.format(this.timestamp).equals(simpleDateFormat.format(nowTime))) {
            this.timestamp = nowTime;
            return true;
        } else {
            return false;
        }
    }

    public String getLastDayTimeFormat() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        return simpleDateFormat.format( new Timestamp(System.currentTimeMillis() - (24 * 60 * 60 * 1000)));
    }

    public String getTimeFormat() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        return simpleDateFormat.format(this.timestamp);
    }

    public String getFlag() {
        return String.valueOf(this.flag);
    }
}
