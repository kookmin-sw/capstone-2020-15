package com.example.serviceevent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button startButton = findViewById(R.id.startButton);
        Button endButton = findViewById(R.id.endButton);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startService();
            }
        });

        endButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               stopService();

            }
        });

    }
    public void startService() {

        Intent serviceIntent = new Intent(this, MyService.class);
        serviceIntent.putExtra("inputExtra", "수면 중 뒤척임 감지중입니다.");

        if (Build.VERSION.SDK_INT >= 26) {
            ContextCompat.startForegroundService(this, serviceIntent);
        }
        else {
            startService(serviceIntent);
        }
    }

    public void stopService() {

        Intent serviceIntent = new Intent(this, MyService.class);

        if (Build.VERSION.SDK_INT >= 26) {

            stopService(serviceIntent);
        }
        else{

            stopService(serviceIntent);
        }
    }
}
