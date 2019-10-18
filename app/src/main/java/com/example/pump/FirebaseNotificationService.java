package com.example.pump;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

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
            if(remoteData.get("Ngrok") !=null){
                updateNgrokNotifications(remoteData.get("Ngrok"));
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

    public void updateNgrokNotifications(String externalConnection){
    notificationManager = NotificationManagerCompat.from(context);

        Intent UpdateConnectionAndOpen = new Intent(this, NotificationActions.class);
        UpdateConnectionAndOpen.putExtra("UpdateNgrokAndOpen", "1");
        UpdateConnectionAndOpen.putExtra("notificationId", "1");
        UpdateConnectionAndOpen.putExtra("Ngrok", externalConnection);
        PendingIntent contentIntent = PendingIntent.getBroadcast(this, 0, UpdateConnectionAndOpen, PendingIntent.FLAG_UPDATE_CURRENT);

    //Intent activityIntent = new Intent(context, Add_Controller.class);
     //   PendingIntent contentIntent = PendingIntent.getActivity(context,
      //          0, activityIntent, 0);

        Intent broadcastIntent = new Intent(this, NotificationActions.class);
        broadcastIntent.putExtra("notificationId", "1");
        broadcastIntent.putExtra("Ngrok", externalConnection);
        PendingIntent actionIntent = PendingIntent.getBroadcast(this, 0, broadcastIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        //PendingIntent actionIntent = PendingIntent.getBroadcast(context,
         //       0, saveConnection, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(context, CHANNEL_1_ID)
                .setSmallIcon(R.drawable.ic_cloud)
                .setContentTitle("Ngrok Connection Update")
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

    /*


        Notification notification = new NotificationCompat.Builder(this, CHANNEL_1_ID)
                .setSmallIcon(R.drawable.ic_cloud)
                .setContentTitle("Im a Title")
                .setContentText("Bla, bla, bla")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .build();

        notificationManager.notify(1, notification);
*/

    }


}
