package com.example.smalarm.ui.alarm;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smalarm.MainActivity;
import com.example.smalarm.R;
import com.example.smalarm.ui.alarm.sleep.SleepSensingActivity;
import com.example.smalarm.ui.alarm.sleep.SleepSensingService;
import com.example.smalarm.ui.alarm.util.AlarmService;

public class AlarmOffActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_off);

//        TextClock tClock = findViewById(R.id.textClock);
        Button btn = findViewById(R.id.offButton);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent alarmService = new Intent(v.getContext(), AlarmService.class);
//                alarmService.putExtra("command","alarm off");
//                alarmService.putExtra("sound","alarm.mp3");
//                startService(alarmService);
                stopService(alarmService);

                System.out.println(isSleepServiceRunning(SleepSensingService.class));

                // 만약 수면측정중이라면 SleepSensingAcitivity로 이동
                if(isSleepServiceRunning(SleepSensingService.class)){
                    Intent intent = new Intent(v.getContext(), SleepSensingActivity.class);
                    startActivity(intent);
                    finish();

                }
                // 아니라면 MainAcitivity로 이동
                else{
                    Intent intent = new Intent(v.getContext(), MainActivity.class);
                    startActivity(intent);
                    finish();

                }

            }
        });
    }

    private boolean isSleepServiceRunning(Class<?> serviceClass){
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)){
            if (serviceClass.getName().equals(service.service.getClassName())){
                return true;
            }
        }
        return false;
    }




}
