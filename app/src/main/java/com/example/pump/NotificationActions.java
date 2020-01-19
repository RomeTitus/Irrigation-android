package com.example.pump;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class NotificationActions extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        String message = intent.getStringExtra("Ngrok");
        String Mac = intent.getStringExtra("Bluetooth");
        if(message!= null){
            SQLManager database = new SQLManager(context);
           //tcp://0.tcp.eu.ngrok.io:16829
            String port = message.substring(24);
            port = port.replace("\n", "");

            database.updateExternalPathWithMac("0.tcp.eu.ngrok.io",port, Mac);

            Toast.makeText(context, "Updated!", Toast.LENGTH_SHORT).show();
            if(intent.getStringExtra("UpdateNgrokAndOpen") != null && intent.getStringExtra("UpdateNgrokAndOpen").equals("1")){
                Intent activityIntent = new Intent(context, select_controller.class);
                context.startActivity(activityIntent);
            }
        }
        String ID = intent.getStringExtra("notificationId");
        if(ID!= null){
            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            manager.cancel(Integer.parseInt(ID));
        }

    }
}
