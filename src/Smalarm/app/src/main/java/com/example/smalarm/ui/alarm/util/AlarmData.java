package com.example.smalarm.ui.alarm.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class AlarmData implements Serializable {
    private static final AtomicInteger count = new AtomicInteger(0);
    private final int ID;

    public String title;
    public String timeDigit;
    public String timeUnit;
    public List<Integer> repeat; // {0:일, 1:월, 2:화, 3:수, 4:목, 5:금, 6:토}
    public boolean state = true; // true:on, false:off

    public AlarmData(int id, String title, String timeDigit, String timeUnit, List<Integer> repeat) {
        this.ID =  id; // count.incrementAndGet();
        this.title = title;
        this.timeDigit = timeDigit;
        this.timeUnit = timeUnit;
//        this.timeUnit = Integer.parseInt(timeDigit.substring(0,1)) > 12? "AM":"PM";
        this.repeat = repeat;
        this.state = true;
    }

    public int getID() {
        return ID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTimeDigit() {
        return timeDigit;
    }

    public void setTimeDigit(String timeDigit) {
        this.timeDigit = timeDigit;
    }

    public String getTimeUnit() {
        return timeUnit;
    }

    public void setTimeUnit(String timeUnit) {
        this.timeUnit = timeUnit;
    }
}
