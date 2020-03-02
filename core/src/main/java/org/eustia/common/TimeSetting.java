package org.eustia.common;
/*
 * @package: org.eustia.common
 * @program: TimeSetting
 * @description
 *
 * @author:  rinne
 * @e-mail:  minami.rinne.me@gmail.com
 * @date: 2020/03/01 午後 07:34
 */

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

/**
 * @classname: TimeSetting
 * @description: %{description}
 * @author: rinne
 * @date: 2020/03/01 午後 07:34
 * @Version 1.0
 */

public class TimeSetting {
    private int flag = 0;
    private Timestamp timestamp;

    public TimeSetting() {
        this.timestamp = new Timestamp(0);
    }

    public boolean isHour() {
        Timestamp nowTime = new Timestamp(System.currentTimeMillis());
        int hour = 1000 * 60;

        if (this.timestamp.equals(new Timestamp(0))) {
            return true;
        } else if (nowTime.getTime() - this.timestamp.getTime() >= hour) {
            this.timestamp = nowTime;
            this.flag ++;
            return true;
        } else {
            return false;
        }
    }

    public boolean isDay() {
        Timestamp nowTime = new Timestamp(System.currentTimeMillis());

        int day = 1000 * 60 * 60 * 24;
        if (nowTime.getTime() - this.timestamp.getTime() >= day) {
            timestamp = nowTime;
            this.flag = 0;
            return true;
        } else {
            return false;
        }
    }

    public String getTimeFormat() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        return simpleDateFormat.format(this.timestamp);
    }

    public String getFlag() {
        return String.valueOf(this.flag);
    }
}
