package com.example.rizzchat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    private static final String CHANNEL_ID = "channel_id";
    private static final CharSequence CHANNEL_NAME = "channel_name";
    private static final int NOTIFICATION_ID = 1;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        // Check if message contains data payload
        if (remoteMessage.getData().size() > 0) {
            // Handle data payload
            String userName = remoteMessage.getData().get("userName");
            String message = remoteMessage.getData().get("message");
            Log.d(TAG, "Data Payload: userName = " + userName + ", message = " + message);

            // Display notification for data payload
            displayNotification(userName, message);
        }

        // Check if message contains notification payload
        if (remoteMessage.getNotification() != null) {
            // Handle notification payload
            String title = remoteMessage.getNotification().getTitle();
            String body = remoteMessage.getNotification().getBody();
            Log.d(TAG, "Notification Payload: title = " + title + ", body = " + body);

            // Display notification for notification payload
            displayNotification(title, body);
        }
    }

    private void displayNotification(String title, String body) {
        NotificationManager notificationManager = getSystemService(NotificationManager.class);

        // Create notification channel (required for Android 8.0 and above)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        // Create intent for notification tap action
        Intent intent = new Intent(this, ChatActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Build notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification_icon)
                .setContentTitle(title)
                .setContentText(body)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        // Display notification
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }
}
