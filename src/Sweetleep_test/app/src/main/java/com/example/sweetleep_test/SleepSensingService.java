package com.example.sweetleep_test;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

import com.example.sweetleep_test.ui.dashboard.DashboardActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeMap;

public class SleepSensingService extends Service implements SensorEventListener {

    public static final String CHANNEL_ID = "ForegroundServiceChannel";

    // 센서매니저
    private SensorManager sensorManager;
    private Sensor accelerormeterSensor;

    // 움직임이 있을때 구간별 움직임을 담는 해쉬맵 (15분간격)
    HashMap<Integer,Integer> countHash = new HashMap<>();

    // 그래프 그리기위한 어레이리스트 (time : x축 , motionCounter : y축)
    ArrayList<Integer> time = new ArrayList<>();
    ArrayList<Integer> motionCounter = new ArrayList<>();

    // 모션카운터가 0인 구간처리를 위한 어레이리스트
    ArrayList<Integer> preTime = new ArrayList<>();


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


    public SleepSensingService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerormeterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        String input = intent.getStringExtra("inputExtra");
        createNotificationChannel();
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("수면측정중")
                .setContentText(input)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1, notification);

        //do heavy work on a background thread
        processCommand();

        //stopSelf();

        return START_NOT_STICKY;

    }

    private void processCommand() {

        // 센서 이벤트 시작
        sensorManager.registerListener(SleepSensingService.this, accelerormeterSensor,
                SensorManager.SENSOR_DELAY_GAME);

    }

    @Override
    public void onDestroy() {

        // 센서 이벤트 종료
        sensorManager.unregisterListener(SleepSensingService.this);

        // 그래프를 위한 time, motionCounter 설정
        setValue();

        // InfoActivity 로 전달하기 위한 intent 생성
        Intent infoIntent = new Intent(getApplicationContext(), DashboardActivity.class);
        infoIntent.putExtra("preTime", preTime);
        infoIntent.putExtra("time", time);
        infoIntent.putExtra("motionCounter", motionCounter);

        startActivity(infoIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));

        super.onDestroy();
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

    // 중간에 비는 값 보정 메소드
    private void setValue(){

        // 해쉬맵 키값별 오름차순으로 순회하는 이터레이터 설정
        TreeMap<Integer, Integer> tm = new TreeMap<Integer, Integer>(countHash);
        Iterator<Integer> keyiterator = tm.keySet().iterator();

        // 이터레이터로 순회하면서 preTime 어레이리스트에 저장
        while(keyiterator.hasNext()){
            int i = keyiterator.next();
            preTime.add(i);
        }

        // time , motionCounter 값 채워줌 중간공백 없이
        int start = preTime.get(0);
        int target = preTime.get(0);
        int last = preTime.get(preTime.size() - 1);
        int n = 1;

        // 15(1)분 주기로 더해가면서 빈값 채우는 루프
        while(target <= last){

            time.add(target);

            if(countHash.get(target) == null){ // 만약 해당 주기에 count값이 없으면
                motionCounter.add(0);
            }
            else{
                motionCounter.add(countHash.get(target));
            }

            target = start + (15*n);

            n++;
        }

    }

}
