package com.example.sweetleep_test.ui.home.alarm;

public class AlarmData {
    public String title;
    public String timeDigit;
    public String timeUnit;
    public int[] repeat; // {0:일, 1:월, 2:화, 3:수, 4:목, 5:금, 6:토}
    public boolean state = true; // true:on, false:off

    public AlarmData(String title, String timeDigit, int[] repeat) {
        this.title = title;
        this.timeDigit = timeDigit;
        this.timeUnit = Integer.parseInt(timeDigit.substring(0,1)) > 12? "AM":"PM";
        this.repeat = repeat;
        this.state = true;
    }
}
