package com.example.smalarm.ui.alarm.util;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.example.smalarm.MainActivity;
import com.example.smalarm.R;
import com.example.smalarm.ui.alarm.AlarmOffActivity;

public class AlarmService extends Service {
    private static final String TAG = "AlarmService";
    MediaPlayer mp = new MediaPlayer();

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent == null)
            return Service.START_STICKY;
        else
            processCommand(intent);

        return super.onStartCommand(intent, flags, startId);
    }

    private void processCommand(Intent intent) {
        String sound = intent.getStringExtra("sound");
        String command = intent.getStringExtra("command");
        String name = intent.getStringExtra("name");

        Intent showIntent = new Intent(getApplicationContext(), MainActivity.class);

        if(command != null) {
            if (command.equals("alarm on")) {
                createNotificationMessage();
                showIntent.putExtra("command", "show");

                switch (sound) {
                    case "alarm.mp3":
                        mp = MediaPlayer.create(this, R.raw.alarm);
                        mp.start();
                        System.out.println("음악 실행중 ...");
                        break;
                    case "alarm2.mp3":
                        break;
                    default:
                        break;
                }
            } else if (command.equals("alarm off")) {
                showIntent.putExtra("command", "");

                mp.stop();
                mp.release();
                stopSelf();
            }
        }

        showIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_SINGLE_TOP |
                Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(showIntent);
    }

    private void createNotificationMessage() {
        Intent notificationIntent = new Intent(this, AlarmOffActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 200,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "alarm");

        //OREO API 26 이상에서는 채널 필요
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            builder.setSmallIcon(R.drawable.ic_launcher_foreground); //mipmap 사용시 Oreo 이상에서 시스템 UI 에러남

            String chanelId = "alarm";
            String channelName = "알람 시작";
            String description = "알람음 재생중";
            int importance = NotificationManager.IMPORTANCE_HIGH; // 소리와 알림메시지를 같이 보여줌

            NotificationChannel channel = new NotificationChannel(chanelId, channelName, importance);
            channel.setDescription(description);

            // 노티피케이션 채널을 시스템에 등록
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        } else
            builder.setSmallIcon(R.mipmap.ic_launcher);

        builder.setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setTicker("{Time to watch some cool stuff!}")
                .setContentTitle("상태바 드래그시 보이는 타이틀")
                .setContentText("상태바 드래그시 보이는 서브타이틀")
                .setContentInfo("INFO")
                .setContentIntent(pendingIntent);


        if (notificationManager != null) {
//            Intent alarmIntent = new Intent("android.intent.action.sec");
//
//            alarmIntent.setClass(context, AlarmOffActivity.class);
//            alarmIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
//                    Intent.FLAG_ACTIVITY_SINGLE_TOP |
//                    Intent.FLAG_ACTIVITY_CLEAR_TOP);

            startForeground(200, builder.build());
            notificationManager.notify(200, builder.build());

            // 액티비티를 띄운다
//            context.startActivity(alarmIntent);

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mp.stop();
        mp.release();
        Toast.makeText(this, "destory service", Toast.LENGTH_SHORT).show();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
