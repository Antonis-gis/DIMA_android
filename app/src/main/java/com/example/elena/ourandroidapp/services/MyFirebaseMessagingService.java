package com.example.elena.ourandroidapp.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;

import com.example.elena.ourandroidapp.R;
import com.example.elena.ourandroidapp.activities.MainActivity;
import com.example.elena.ourandroidapp.model.Poll;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by elena on 25/11/17.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "FCMMessagingService";
    private LocalBroadcastManager broadcaster;
    @Override
    public void onCreate() {
        broadcaster = LocalBroadcastManager.getInstance(this);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
/*
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            // In this case the XMPP Server sends a payload data
            String message = remoteMessage.getData().get("message");
            Intent intent = new Intent("MyData");
            intent.putExtra("message", remoteMessage.getData().get("message"));
            broadcaster.sendBroadcast(intent);
            Log.d(TAG, "Message received: " + message);

            showBasicNotification(message);
            //showInboxStyleNotification(message);
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }
        */
        if (remoteMessage.getData().size() > 0) {
            final String message = remoteMessage.getData().get("poll_id");
            DatabaseService mPollService = DatabaseService.getInstance();
            DatabaseService.Callback callback = new DatabaseService.Callback() {//we need somehow find right poll and update its view
                @Override
                public void onLoad(String poll_id) {

                    Intent intent = new Intent("NewPollReceived");
                    intent.putExtra("poll_id", message);
                    broadcaster.sendBroadcast(intent);
                }

                @Override
                public void onFailure() {

                }
            };

            mPollService.retrievePollToGlobalContainer(message, callback);
        }




    }

    private void showBasicNotification(String message) {
        Intent i = new Intent(this,MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,i,PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setAutoCancel(true)
                .setContentTitle("Basic Notification")
                .setContentText(message)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent);

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        manager.notify(0,builder.build());

    }

}
