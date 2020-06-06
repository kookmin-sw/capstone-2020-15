package com.example.smalarm.ui.alarm;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smalarm.MainActivity;
import com.example.smalarm.R;
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

                Intent intent = new Intent(v.getContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
