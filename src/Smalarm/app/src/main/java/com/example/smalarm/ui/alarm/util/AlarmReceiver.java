package com.example.smalarm.ui.alarm.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class AlarmReceiver extends BroadcastReceiver {
    Context context;

    @Override
    public void onReceive(Context context, Intent intent) {

        this.context = context;
        int alarmIdx = intent.getIntExtra("idx", 0);

        Intent alarmService = new Intent(context, AlarmService.class);
        alarmService.putExtra("idx", alarmIdx);
        alarmService.putExtra("command", "alarm on");
        alarmService.putExtra("sound", "alarm.mp3");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(alarmService);
        } else {
            context.startService(alarmService);
        }
//        Intent alarmIntent = new Intent("android.intent.action.sec");
//        alarmIntent.setClass(context, AlarmOffActivity.class);
//        alarmIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
//                Intent.FLAG_ACTIVITY_SINGLE_TOP);
//        context.startActivity(alarmIntent);

//        final int no = intent.getIntExtra("no", -1);
//        WritingInfo wi = new WritingInfo();
//
//        if (no == -1) {
//            Log.i("Error", "AlarmReceiver intent error");
//            return;
//        }
//
//        final String[] week = {"일", "월", "화", "수", "목", "금", "토"};
//        ArrayList<AlarmInfoVO> alarmInfo = wi.selectAlarmInfo(context, no);
//        Calendar calendar = Calendar.getInstance();
//        String todayDayOfWeek = week[calendar.get(Calendar.DAY_OF_WEEK) - 1];
//        String[] alarmDayOfWeek = alarmInfo.get(0).getDayOfWeek().split("/");
//        int useOrNot = alarmInfo.get(0).getUseOrNot();
//
//        boolean weekCheck = false;
//
//        for (int i = 0; i < alarmDayOfWeek.length; i++) {
//            if (alarmDayOfWeek[i].equals(todayDayOfWeek)) {
//                weekCheck = true;
//                break;
//            }
//        }
//
//        //위에서 오늘의 요일과 저장해놓은 요일과 비교를 한 다음 오늘이 알람이 울려야 할 요일이면 Activity를 호출한다.
//        if (!weekCheck) {
//            return;
//        } else if (weekCheck) {
//            if (useOrNot == 1) {
//                Intent activityIntent = new Intent(context, AlarmUserViewActivty.class);
//                activityIntent.putExtra("no", alarmInfo.get(0).getListNo());
//                activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                context.startActivity(activityIntent);
//            }
//        }
    }
}