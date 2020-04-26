package com.example.pump;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.iid.FirebaseInstanceId;

import java.util.concurrent.ExecutionException;

//import com.google.android.gms.gcm.GoogleCloudMessaging;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {

    Button btnStopPump, btnStartPump, btnStopServer, btnAddPumpValve, btnAddPump, btnAddValve, btnCancelAdd;
    LinearLayout linearLayoutPumpController, linearLayoutAddPump, linearLayoutConnecting, linearLayoutBtnValve, linearLayoutBtnPump;
    TextView txtDebug, txtError;
    EditText editTextName, editTextLocation, editTextGPIO, editTextDescription;
    String SocketData;
    String response;
    Thread Thread1 = null;
    String SENDER_ID = "241875836417";
    //String SENDER_ID = "AIzaSyCwJgjDvb_pDDjm85BOAchzMT7XB9ofHEU";
    private String regid = "";
    private final static String TAG = "LaunchActivity";
    public static Handler UIHandler; //Invokes the UI Thread
    //FirebaseNotificationService notify = new FirebaseNotificationService(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtDebug = findViewById(R.id.TxtDebug);
        txtError = findViewById(R.id.TxtError);
        btnStopPump = findViewById(R.id.BtnStopPump);
        btnStartPump = findViewById(R.id.BtnStartPump);
        btnStopServer = findViewById(R.id.BtnStopServer);
        //notify.updateNgrokNotifications();

        linearLayoutPumpController = findViewById(R.id.LinearLayoutPumpController);
        linearLayoutAddPump = findViewById(R.id.LinearLayoutAddPump);
        linearLayoutConnecting = findViewById(R.id.LinearLayoutConnecting);
        linearLayoutBtnValve = findViewById(R.id.ScrollViewAlarmSensor);
        linearLayoutBtnPump = findViewById(R.id.LinearLayoutBtnPump);
        UIHandler = new Handler(Looper.getMainLooper());
        //populatePumpsValves();


        btnStopPump.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SocketData = "Stop Pump";
                SocketController socketController = new SocketController(MainActivity.this, SocketData);
                socketController.execute();
            }
        });

        btnStartPump.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SocketData = "Start Pump";
                SocketController socketController = new SocketController(MainActivity.this, SocketData);
                socketController.execute();

            }
        });


        sendApiKey();
        EstablishConn();

    }


    public void sendApiKey() {

        final String  androidID = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);

        new Thread(new Runnable() { //Running on a new thread
           public void run() {
        String token = androidID + "," + FirebaseInstanceId.getInstance().getToken() + "$setToken";
        final SocketController socketController = new SocketController(MainActivity.this, token);
        socketController.execute();
         }
         }).start();

    }



    /*
    public void notificationcall(){
        NotificationCompat.Builder notificationBuilder = (notificationCompat.Builder) new NotificationCompat.Builder(this)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .SetSmallIcon(R.drawable.stLsn)
                .SetLargeIcon(BitMapFactory.decodeResources(getResources(), R.drawable.stLsm))
                .setContentTitle("Notification from Pumps")
                .setContentText("Hello and welcome!");

        NotificationManager notificationManager = (NotificationManager ) getSystemServer(MainActivity.this:NOTIFICATION_SERVICE);
        notificationManager.notify(1, notificationBuilder.build());
    }
    */
    private void addPumpValveLayout(){
        linearLayoutPumpController.setVisibility(View.GONE);
        linearLayoutConnecting.setVisibility(View.GONE);
        linearLayoutAddPump.setVisibility(View.VISIBLE);
    }

    private void EstablishConn(){


        linearLayoutPumpController.setVisibility(View.GONE);
        linearLayoutConnecting.setVisibility(View.VISIBLE);
        linearLayoutAddPump.setVisibility(View.GONE);


        //------------------------------------------------------------------
        Intent HomePage = new Intent(this,Home.class);
        //HomePage.putExtra("GoogleSignInAccount",account); //use to parse information to new page
        finish(); //Closes this activity
        startActivity(HomePage);
        //------------------------------------------------------------------
        finish(); //Closes this activity






        //String processData = "";
        //SocketData = "ping";
        //SocketController socketController = new SocketController(SocketData);
        //try{
        //    processData = socketController.execute().get();
        //    if(processData.equals("success")){
                //communicationLayout();
                //Opens the main activity page
                //------------------------------------------------------------------
        //        Intent HomePage = new Intent(this,Home.class);
                //HomePage.putExtra("GoogleSignInAccount",account); //use to parse information to new page
        //        finish(); //Closes this activity
        //        startActivity(HomePage);
                //------------------------------------------------------------------
        //    }
        //}catch (ExecutionException e){

        //}catch (InterruptedException i){

        //}


    }

    private void communicationLayout(){

        linearLayoutPumpController.setVisibility(View.VISIBLE);
        linearLayoutConnecting.setVisibility(View.GONE);
        linearLayoutAddPump.setVisibility(View.GONE);
        txtDebug.setText(response);
        populatePumpsValves();
    }

    @Override
    public void onClick(View v) {


        SocketData = v.getId() +"$RUN";
        SocketController socketController = new SocketController(MainActivity.this,SocketData);
        try{
            String processData = socketController.execute().get();

        }catch (ExecutionException e){

        }catch (InterruptedException i){

        }
    }

    @Override
    public boolean onLongClick(View v) {

        SocketData = v.getId() +"$RUN";
        SocketController socketController = new SocketController(MainActivity.this,SocketData);
        try{
            String processData = socketController.execute().get();

        }catch (ExecutionException e){

        }catch (InterruptedException i){

        }
        return true;
    }



    private void populatePumpsValves() { //Adds buttons corresponding to the pumps/ Valves added to the server

        linearLayoutBtnPump.removeAllViews();
        linearLayoutBtnValve.removeAllViews();
        String[] differentButtons;
        //Gets the different pumps
        SocketData = "getPumps";
        SocketController socketController = new SocketController(MainActivity.this,SocketData);

        String processData = "";

        try{
           processData = socketController.execute().get();

        }catch (ExecutionException e){

        }catch (InterruptedException i){

        }

        LayoutInflater layoutInflaterPump = LayoutInflater.from(this);//Pump
        LayoutInflater layoutInflaterValve = LayoutInflater.from(this);//Valve

        if(processData.equals("No Data")) {
            //No Data
        }else{
        differentButtons = processData.split("#");

        for (int i = 0; i < differentButtons.length; i++) {

            String[] buttonInfo = differentButtons[i].split(",");

            View view = layoutInflaterPump.inflate(R.layout.all_pumps, linearLayoutBtnPump, false); //_____________________________________________________________Pump
            //TextView txtStatusPump = view.findViewById(R.id.TxtStatusPump);
            Button btnPump = view.findViewById(R.id.BtnPump);
            btnPump.setId(Integer.parseInt(buttonInfo[0]));
            btnPump.setOnClickListener(this);
            btnPump.setOnLongClickListener(this);
            //txtStatusPump.setText("OFF");
            btnPump.setText("Pump: " + (buttonInfo[1]));
            linearLayoutBtnPump.addView(view);                                                             //_____________________________________________________________Pump
        }
    }

        SocketData = "getValves";                                                                      //_____________________________________________________________Valves
        socketController = new SocketController(MainActivity.this,SocketData);
        try{
            processData = socketController.execute().get();

        }catch (ExecutionException e){

        }catch (InterruptedException i){

        }
        if(processData.equals("No Data")) {
        //No Data
        }else{
        differentButtons = processData.split("#");


            for (int i = 0; i < differentButtons.length; i++) {

                String[] buttonInfo = differentButtons[i].split(",");

                View view = layoutInflaterValve.inflate(R.layout.all_valves, linearLayoutBtnValve, false);
                //TextView txtStatusValve = view.findViewById(R.id.TxtStatusValve);
                Button btnValve = view.findViewById(R.id.BtnValve);
                btnValve.setId(Integer.parseInt(buttonInfo[0]));
                btnValve.setOnClickListener(this);
                btnValve.setOnLongClickListener(this);
                //txtStatusValve.setText("OFF");
                btnValve.setText("Valve: " + (buttonInfo[1]));
                linearLayoutBtnValve.addView(view);                                                             //_____________________________________________________________Valves
            }
        }
    }


}

