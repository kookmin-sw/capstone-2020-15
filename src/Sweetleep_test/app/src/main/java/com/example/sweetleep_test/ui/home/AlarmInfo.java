package com.example.sweetleep_test.ui.home;

import java.util.ArrayList;

public class AlarmInfo {
    public String title;
    public String time;
    public int[] selected; // {0:일, 1:월, 2:화, 3:수, 4:목, 5:금, 6:토}

    public AlarmInfo(String title, String time, int[] selected) {
        this.title = title;
        this.time = time;
        this.selected = selected;
    }

}
