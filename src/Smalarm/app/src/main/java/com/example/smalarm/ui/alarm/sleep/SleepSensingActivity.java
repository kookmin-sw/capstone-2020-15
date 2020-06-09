
package com.example.smalarm.ui.alarm.sleep;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smalarm.MainActivity;
import com.example.smalarm.R;
import com.example.smalarm.WelcomeActivity;

public class SleepSensingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep_sensing);



        Button endButton = findViewById(R.id.endButton);
        endButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopService();
            }
        });



    }

    public void stopService() {

        Intent serviceIntent = new Intent(this, SleepSensingService.class);
        stopService(serviceIntent);

        //RequestActivity로 전달할 인텐트 생성
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();


    }



}

