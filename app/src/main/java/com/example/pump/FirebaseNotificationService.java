package com.example.pump;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.Map;

import static androidx.constraintlayout.widget.Constraints.TAG;
import static com.example.pump.App.CHANNEL_1_ID;

public class FirebaseNotificationService extends FirebaseMessagingService {
    private NotificationManagerCompat notificationManager;
    Context context = this;
    public FirebaseNotificationService(Context context) {
    this.context = context;
    }

    public FirebaseNotificationService() {

    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        // ...
        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            Map<String, String> remoteData  = remoteMessage.getData();
            if(remoteData.get("Ngrok") !=null && remoteData.get("Bluetooth") !=null){
                updateNgrokNotifications(remoteData.get("Ngrok"), remoteData.get("Bluetooth"));
            }else if(remoteData.get("Alarm") !=null){
                AlarmNotify(remoteData.get("Alarm"));
                //updateNgrokNotifications(remoteData.get("Ngrok"));
            }



            if (/* Check if data needs to be processed by long running job */ true) {
                //String title = remoteMessage.getNotification().getTitle();
                //String body = remoteMessage.getNotification().getBody();

                // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
                //scheduleJob();
            } else {
                // Handle message within 10 seconds
                //handleNow();
            }

        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            //Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());

        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    public void updateNgrokNotifications(String externalConnection, String Mac){
    notificationManager = NotificationManagerCompat.from(context);
    SQLManager sqlManager = new SQLManager(context);
        String name = sqlManager.getControllerNameByMac(Mac);
        Intent UpdateConnectionAndOpen = new Intent(this, NotificationActions.class);
        UpdateConnectionAndOpen.putExtra("UpdateNgrokAndOpen", "1");
        UpdateConnectionAndOpen.putExtra("notificationId", "1");
        UpdateConnectionAndOpen.putExtra("Ngrok", externalConnection);
        UpdateConnectionAndOpen.putExtra("Bluetooth", Mac);
        PendingIntent contentIntent = PendingIntent.getBroadcast(this, 0, UpdateConnectionAndOpen, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent broadcastIntent = new Intent(this, NotificationActions.class);
        broadcastIntent.putExtra("notificationId", "1");
        broadcastIntent.putExtra("Ngrok", externalConnection);
        broadcastIntent.putExtra("Bluetooth", Mac);
        PendingIntent actionIntent = PendingIntent.getBroadcast(this, 0, broadcastIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(context, CHANNEL_1_ID)
                .setSmallIcon(R.drawable.ic_cloud)
                .setContentTitle(name + ": Ngrok Connection Update")
                .setContentText("New Connection: " + externalConnection)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setColor(Color.BLUE)
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .setOnlyAlertOnce(true)
                .addAction(R.drawable.ic_cloud, "Update Connection", actionIntent)
                .build();

            notificationManager.notify(1, notification);
    }

    public void AlarmNotify(String externalConnection){
        JSONObject alarmDetail = null;
        String NotificationMessage = "";
        long[] pattern = {500,500,500,500,500,500,500,500,500};

        Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getPackageName() + "/raw/alarm");
        //Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALL);
        try{
            alarmDetail = new JSONObject(externalConnection);
        }catch (Exception e){

        }

        Iterator<String> iter = alarmDetail.keys(); //This should be the iterator you want.
        while(iter.hasNext()){
            String key = iter.next();
            key = key;
            try{
                JSONObject alarm = alarmDetail.getJSONObject(key);

                NotificationMessage = NotificationMessage + alarm.getString("Name") + ": Went off at: " + alarm.getString("Time") + "   ";

            }catch (Exception e){
                Log.d(TAG, "Message data payload error: " + e.toString());

            }

        }
        notificationManager = NotificationManagerCompat.from(context);
        Notification notification = new NotificationCompat.Builder(context, CHANNEL_1_ID)
                .setSmallIcon(R.drawable.ic_notifications_active_24dp)
                .setContentTitle("Alarm")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setColor(Color.BLUE)
                //.setContentIntent(contentIntent)
                .setVibrate(pattern)
                .setAutoCancel(true)
                .setOnlyAlertOnce(true)
                .setSound(alarmSound)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(NotificationMessage))
                //.addAction(R.drawable.ic_cloud, "Update Connection", actionIntent)
                .build();


        Ringtone r = RingtoneManager.getRingtone(context, alarmSound);
        r.play();
        notification.sound = (RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        notificationManager.notify(2, notification);
    }

}
