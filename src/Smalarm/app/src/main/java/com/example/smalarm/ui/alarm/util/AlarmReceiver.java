package com.example.smalarm.ui.alarm.util;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.example.smalarm.ui.alarm.calendar.CalendarActivity;
import com.example.smalarm.ui.alarm.calendar.GpsTracker;

import org.threeten.bp.LocalDateTime;
import org.threeten.bp.format.DateTimeFormatter;

import java.io.IOException;

public class AlarmReceiver extends BroadcastReceiver {
    Context context;
    Intent alarmService;

    @Override
    public void onReceive(Context context, Intent intent) {

        this.context = context;
        int alarmIdx = intent.getIntExtra("idx", 0);
        boolean smart = intent.getBooleanExtra("smart", false);

//        alarmIdx = context.getSharedPreferences("alarms", Context.MODE_PRIVATE).getInt("count", 0);

        if (!smart) {   // 스마트알람이 꺼져있으면 설정된 시간에 바로 알람 울림
            alarmService = new Intent(context, AlarmService.class);
            alarmService.putExtra("idx", alarmIdx);
            alarmService.putExtra("command", "alarm on");
            alarmService.putExtra("sound", "alarm.mp3");

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(alarmService);
            } else {
                context.startService(alarmService);
            }
        } else {    // 스마트알람이 켜져있으면 서버로 알람설정시간, 현재위치, 목적지 전송
            ContentValues values = new ContentValues();
            String url = "http://13.125.23.94:5000/req";
            String location = intent.getStringExtra("location");
            String start = intent.getStringExtra("startTime");

//            DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
            values.put("start", start);

//            String address = ((CalendarActivity) context).getCurrentAddress(latitude, longitude);
//            values.put("current",address);
//            values.put("destination", location);
//            GpsTracker gpsTracker = new GpsTracker(context);
//            double latitude = gpsTracker.getLatitude();
//            double longitude = gpsTracker.getLongitude();
            values.put("clat", intent.getDoubleExtra("clat", 0.0));
            values.put("clng", intent.getDoubleExtra("clng", 0.0));

            Geocoder geocoder = new Geocoder(context);
            Address addr;
            try {
                addr = geocoder.getFromLocationName(location, 1).get(0);
                double dlat = addr.getLatitude();
                double dlng = addr.getLongitude();
                values.put("dlat", dlat);
                values.put("dlng", dlng);
            } catch (IOException e) {
                e.printStackTrace();
            }

            Log.i("values", String.valueOf(values));
            Log.i("url : ", url);
            NetworkTask networkTask = new NetworkTask(url, values, context, 1);
            networkTask.execute();
        }
    }
}