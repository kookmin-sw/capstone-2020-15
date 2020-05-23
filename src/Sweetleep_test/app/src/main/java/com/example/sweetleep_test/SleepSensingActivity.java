
package com.example.sweetleep_test;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

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

        }

    }

