package com.example.smalarm.ui.alarm.util;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.example.smalarm.MainActivity;
import com.example.smalarm.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;
import java.util.Map;

public class FirebaseAlarmService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMsgService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) { // Handle FCM Message
        Log.e(TAG, remoteMessage.getFrom()); // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Message data payload: " + remoteMessage.getData());
            handleNow();
        } // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.e(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            String getMessage = remoteMessage.getNotification().getBody();
            if (TextUtils.isEmpty(getMessage)) {
                Log.e(TAG, "ERR: Message data is empty...");
            } else {
                Map<String, String> mapMessage = new HashMap<>();
                assert getMessage != null;
                mapMessage.put("key", getMessage); // Broadcast Data Sending Test

                sendNotification(mapMessage);

                Intent intent = new Intent("alert_data");
                intent.putExtra("msg", getMessage);
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
            }
        }
    }

    private void sendNotification(Map<String, String> data) {
        int noti_id = 1;
        String getMessage = "";
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("notification_id", 0); // Push로 받은 데이터를 그대로 다시 intent에 넣어준다.
        if (data != null && data.size() > 0) {
            for (String key : data.keySet()) {
                getMessage = data.get(key);
                intent.putExtra(key, getMessage);
            }
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        String channelId = getString(R.string.app_name);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Smalarm ")
                .setContentText(getMessage)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE); // Notification 채널을 설정합니다.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "Channel human readable title", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }
        notificationManager.notify(noti_id, notificationBuilder.build());
    }


    private void handleNow() {
        Log.d(TAG, "Short lived task is done.");
    }

    /**
     * 새로운 토큰이 생성되는 경우
     **/
    @Override
    public void onNewToken(String refreshedToken) {
        super.onNewToken(refreshedToken);
        Log.e(TAG, "Refreshed token: " + refreshedToken);
        sendRegistrationToServer(refreshedToken);
    }

    private void sendRegistrationToServer(String token) {
        Log.e(TAG, "here ! sendRegistrationToServer! token is " + token);
    }
}
