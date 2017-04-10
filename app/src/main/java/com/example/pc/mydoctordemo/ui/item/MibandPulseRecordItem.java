package com.example.pc.mydoctordemo.ui.item;

/**
 * Created by PC on 2017-04-08.
 */

public class MibandPulseRecordItem {
    private String month;
    private String day;
    private String time;
    private String minute;
    private String when;
    private String pulse;

    public MibandPulseRecordItem(String month, String day, String time, String minute, String when, String pulse){
        this.month = month;
        this.day = day;
        this.time = time;
        this.minute = minute;
        this.when = when;
        this.pulse = pulse;
    }

    public String getPulse() {
        return pulse;
    }

    public String getMonth() {
        return month;
    }

    public String getDay() {
        return day;
    }

    public String getTime() {
        return time;
    }

    public String getMinute() {
        return minute;
    }

    public String getWhen() {
        return when;
    }
}
