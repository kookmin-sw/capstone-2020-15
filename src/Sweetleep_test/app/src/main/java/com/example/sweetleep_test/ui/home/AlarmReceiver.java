package com.example.sweetleep_test.ui.home;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.sweetleep_test.RingtonePlayingService;

public class AlarmReceiver extends BroadcastReceiver {

    Context context;

    @Override
    public void onReceive(Context context, Intent intent) {

        this.context = context;
        // intent로부터 전달받은 string
        String get_yout_string = intent.getExtras().getString("state");

        // RingtonePlayingService 서비스 intent 생성
        Intent service_intent = new Intent(context, RingtonePlayingService.class);

        // RingtonePlayinService로 extra string값 보내기
        service_intent.putExtra("state", get_yout_string);
        // start the ringtone service

        this.context.startForegroundService(service_intent);


//        // Fetch extra longs from MainActivity intent
//        // Tells which value user selects from spinner
//        int get_sound_choice = intent.getExtras().getInt("sound_choice");
//        //Log.e("Sound choice is ", get_sound_choice.toString());
//
//        // Pass extra integer from receiver to RingtonePlayingService
//        service_intent.putExtra("sound_choice", get_sound_choice);

    }
}
