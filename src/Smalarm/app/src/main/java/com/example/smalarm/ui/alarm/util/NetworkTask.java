package com.example.smalarm.ui.alarm.util;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.example.smalarm.ui.alarm.calendar.CalendarActivity;
import com.example.smalarm.ui.alarm.calendar.GpsTracker;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.threeten.bp.LocalDateTime;
import org.threeten.bp.format.DateTimeFormatter;

import java.io.IOException;

public class NetworkTask extends AsyncTask<Void, Void, String> {

    String url;
    ContentValues values;
    Context context;
    int mode; // 1: startService(alarm) 2: location

    public NetworkTask(String url, ContentValues values, Context context, int mode){
        this.url = url;
        this.values = values;
        this.context = context;
        this.mode = mode;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //progress bar를 보여주는 등등의 행위
    }

    @Override
    protected String doInBackground(Void... params) {
        String result;
        RequestHttpConnection requestHttpURLConnection = new RequestHttpConnection();
        result = requestHttpURLConnection.request(url, values);
        return result; // 결과가 여기에 담깁니다. 아래 onPostExecute()의 파라미터로 전달됩니다.
    }

    @Override
    protected void onPostExecute(String result) {
        // 통신이 완료되면 호출됩니다.
        // 결과에 따른 UI 수정 등은 여기서 합니다.

        switch (mode) {
            case 1:
                System.out.println(result);
                JsonObject jsonObjectAlt = JsonParser.parseString(result).getAsJsonObject();
                int msg = jsonObjectAlt.get("msg").getAsInt();

                if(msg == 1) {
                    Intent alarmService = new Intent(context, AlarmService.class);
                    alarmService.putExtra("command", "alarm on");
                    alarmService.putExtra("sound", "alarm.mp3");

                    Toast.makeText(context.getApplicationContext(), result, Toast.LENGTH_LONG).show();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        context.startForegroundService(alarmService);
                    } else {
                        context.startService(alarmService);
                    }
                } else if (msg == 0) {

                }
                break;
            case 2:
                Toast.makeText(context.getApplicationContext(), result, Toast.LENGTH_LONG).show();

                break;
        }

    }
}