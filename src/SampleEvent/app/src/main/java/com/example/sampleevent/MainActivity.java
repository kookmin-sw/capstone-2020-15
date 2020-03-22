package com.example.sampleevent;

import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;


import java.util.Calendar;
import java.util.HashMap;


public class MainActivity extends AppCompatActivity implements SensorEventListener{


    // 움직임 측정시 사용하는 변수들
    private long lastTime;
    private float speed;
    private float lastX;
    private float lastY;
    private float lastZ;
    private float x, y, z;
    private static final int SHAKE_THRESHOLD = 30; // 민감도 변수
    private static final int DATA_X = SensorManager.DATA_X;
    private static final int DATA_Y = SensorManager.DATA_Y;
    private static final int DATA_Z = SensorManager.DATA_Z;

    // 센서매니저
    private SensorManager sensorManager;
    private Sensor accelerormeterSensor;

    private TextView txtCount;
    //int count = 0;

    // 움직임이 있을때 구간별 움직임을 담는 해쉬맵 (15분간격)
    HashMap<Integer,Integer> countHash = new HashMap<>();

    LinearLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerormeterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        /*--
        txtCount = findViewById(R.id.motionCount);

        // 현재까지 움직임 횟수
        Button countButton = (Button) findViewById(R.id.countBtn);
        countButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtCount.setText(String.valueOf(count));
            }
        });

         */

        // 측정시작
        layout=(LinearLayout)findViewById(R.id.onoff);
        Button startButton = (Button) findViewById(R.id.startBtn);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sensorManager.registerListener(MainActivity.this, accelerormeterSensor,
                        SensorManager.SENSOR_DELAY_GAME);

                layout.setBackgroundColor(getResources().getColor(R.color.green));
                AlertDialog.Builder alert_confirm = new AlertDialog.Builder(MainActivity.this);
                alert_confirm.setMessage("측정을 시작합니다");
                alert_confirm.setPositiveButton("확인", null);
                AlertDialog alert = alert_confirm.create();
                alert.show();
            }
        });

        // 측정중단
        Button stopButton = (Button) findViewById(R.id.stopBtn);
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sensorManager.unregisterListener(MainActivity.this);

                layout.setBackgroundColor(getResources().getColor(R.color.red));
                AlertDialog.Builder alert_confirm = new AlertDialog.Builder(MainActivity.this);
                alert_confirm.setMessage("측정을 중단합니다");
                alert_confirm.setPositiveButton("확인", null);
                AlertDialog alert = alert_confirm.create();
                alert.show();
            }
        });

        // 구간대별 움직임을 보여주는 액티비티로 이동
        Button endButton = (Button) findViewById(R.id.endBtn);
        endButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), TimeList.class);
                intent.putExtra("hashmap", countHash);
                startActivity(intent);
            }
        });

    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            // 움직인 속도를 구하는 과정
            long currentTime = System.currentTimeMillis();
            long gabOfTime = (currentTime - lastTime);
            if (gabOfTime > 100) {
                lastTime = currentTime;
                x = event.values[SensorManager.DATA_X];
                y = event.values[SensorManager.DATA_Y];
                z = event.values[SensorManager.DATA_Z];

                speed = Math.abs(x + y + z - lastX - lastY - lastZ) / gabOfTime * 10000;

                // 속도가 설정값보다 크면 움직인 걸로 조건에 걸림 ( 너무 큰 움직임은 측정 안함 )
                if ( (speed > SHAKE_THRESHOLD) ){

                    // 15분 단위로 할때
                    Calendar cal = Calendar.getInstance();
                    int minute = cal.get(Calendar.MINUTE);
                    int time = 0;

                    if(0 <= minute && minute < 15){ // 0 ~ 14 분
                        time = (cal.get(Calendar.HOUR_OF_DAY) * 60) + 15;
                    }
                    else if (15 <= minute && minute < 30){ // 15 ~ 29분
                        time = (cal.get(Calendar.HOUR_OF_DAY) * 60) + 30;
                    }
                    else if (30 <= minute && minute < 45){ // 30분 ~ 44분
                        time = (cal.get(Calendar.HOUR_OF_DAY) * 60) + 45;
                    }
                    else if (45 <= minute && minute < 60){ // 45분 ~ 59분
                        time = (cal.get(Calendar.HOUR_OF_DAY) * 60) + 60;
                    }

                    // 해쉬맵에 저장
                    this.setMotionCounter(time);
                }
                lastX = event.values[DATA_X];
                lastY = event.values[DATA_Y];
                lastZ = event.values[DATA_Z];

            }

        }

    }

    // 해당 구간별 움직임을 담는 메소드
    public void setMotionCounter(int time){
        if(countHash.get(time) == null){
            countHash.put(time, 1);
        }
        else {
            int temp = countHash.get(time);
            if(temp < 40) {
                countHash.put(time, temp + 1);
            }
        }
    }

}
