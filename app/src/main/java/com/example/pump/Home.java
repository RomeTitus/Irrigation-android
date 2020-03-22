package com.example.pump;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.concurrent.ExecutionException;

public class Home extends AppCompatActivity {

    private ViewPager Pager;
    private SlideAdapter myadapter;
    private LinearLayout status;
    private TextView txtViewPing, TxtPiName;
    private ImageView imageViewSignal;
    private BottomNavigationView bottomNavigationView;
    private static Handler UIHandler = new Handler();

    public static void runOnUI(Runnable runnable) { //Runs code to invoke the main thread
        UIHandler.post(runnable);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        TxtPiName = findViewById(R.id.TxtPiName);
        TxtPiName.setText("");

        SQLManager sqlManager = new SQLManager(this);
        Cursor path = sqlManager.getPath();
        if (path.getCount() < 1) {
            //------------------------------------------------------------------
            Intent Controller = new Intent(this, AddPiController.class);
            startActivity(Controller);
            //------------------------------------------------------------------
        } else {

            final String androidID = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
            new Thread(new Runnable() { //Running on a new thread
                public void run() {
                    SQLManager sqlManagerMac = new SQLManager(Home.this);
                    Cursor ControllerMac = sqlManagerMac.getSelectedMac();
                    if (ControllerMac.getCount() > 0) {
                        ControllerMac.moveToNext();
                        String mac = ControllerMac.getString(0);


                        String processData = "Data Empty";
                        final SocketController socketControllerExternal = new SocketController(Home.this, "getMAC");
                        try {
                            processData = socketControllerExternal.execute().get();
                            if (!processData.equals("Server Not Running")) {

                                if (mac.equals(processData)) {

                                    FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener( Home.this,  new OnSuccessListener<InstanceIdResult>() {
                                        @Override
                                        public void onSuccess(InstanceIdResult instanceIdResult) {
                                            String mToken = instanceIdResult.getToken();
                                            Log.e("Token",mToken);
                                            String token = androidID + "," + mToken + "$setToken";
                                            final SocketController socketController = new SocketController(Home.this, token);
                                            socketController.execute();
                                        }
                                    });



                                    /*


                                    SQLManager sqlManagerName = new SQLManager(Home.this);
                                    Cursor CursorName = sqlManagerMac.getControllerName();
                                    if (CursorName.getCount() > 0) {
                                        try{
                                            CursorName.moveToNext();
                                            String Name = CursorName.getString(0);

                                            TxtPiName.setText(Name);
                                        }catch (Exception e){

                                        }

                                    }

                                } else {
                                    TxtPiName.setText("Unknown Controller");
                                    */

                                }

                            }


                        } catch (ExecutionException e) {

                        } catch (InterruptedException i) {

                        }
                    }


                }
            }).start();


        bottomNavigationView = findViewById(R.id.bottomNavigation);

        Pager = findViewById(R.id.viewPager); //Creates Fragment
        txtViewPing = findViewById(R.id.TxtViewPing);
        imageViewSignal = findViewById(R.id.ImageViewSignal);
        imageViewSignal.setBackgroundResource(R.drawable.ic_action_no_signal);
        myadapter = new SlideAdapter(this);
        Pager.setAdapter(myadapter);
        Pager.setCurrentItem(0);

        bottomNavigationView.setSelectedItemId(R.id.scheduleNav);


        myadapter.setCustomObjectListener(new SlideAdapter.MyCustomObjectListener() {
            @Override
            public void onObjectReady(String title) {
                /*
                int time = Integer.parseInt(title);
                if (time == 0) {
                    imageViewSignal.setBackgroundResource(R.drawable.ic_action_no_signal);
                    txtViewPing.setText("No Connection!!!");
                } else if (time < 250) {
                    imageViewSignal.setBackgroundResource(R.drawable.ic_action_4_bar);
                    txtViewPing.setText(title + "ms");

                } else if (time < 500) {
                    imageViewSignal.setBackgroundResource(R.drawable.ic_action_3_bar);
                    txtViewPing.setText(title + "ms");

                } else if (time < 1000) {
                    imageViewSignal.setBackgroundResource(R.drawable.ic_action_2_bar);
                    txtViewPing.setText(title + "ms");

                } else {
                    imageViewSignal.setBackgroundResource(R.drawable.ic_action_1_bar);
                    txtViewPing.setText(title + "ms");

                }

                */
            }
        });

        Pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                if (i == 0) {
                    bottomNavigationView.setSelectedItemId(R.id.scheduleNav);
                } else if (i == 1) {
                    bottomNavigationView.setSelectedItemId(R.id.manualNav);
                } else if (i == 2) {
                    bottomNavigationView.setSelectedItemId(R.id.settingNav);
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.scheduleNav:
                        Pager.setCurrentItem(0);
                        break;
                    case R.id.manualNav:
                        Pager.setCurrentItem(1);
                        break;
                    case R.id.settingNav:
                        Pager.setCurrentItem(2);
                        break;

                }


                return true;
            }
        });

        ConnectionStatus();
        controllerName();
        }
    }


    private void ConnectionStatus() {


        new Thread(new Runnable() { //Running on a new thread
            public void run() { //used to ge the pumps that are in today's schedule
                while (true) {
                    String PingTime;
                    try {
                        SocketController socketControllerManual = new SocketController(Home.this, "ping");
                        socketControllerManual.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR).get();
                        //socketControllerManual.execute().get();
                        PingTime = socketControllerManual.getPingTime();
                    } catch (ExecutionException e) {
                        PingTime = "0";
                    } catch (InterruptedException i) {
                        PingTime = "0";
                    }


                    final String finalPingTime = PingTime;
                    runOnUI(new Runnable() { //used to speak to main thread
                        @Override
                        public void run() {


                            int time = Integer.parseInt(finalPingTime);
                            if (time == 0) {
                                imageViewSignal.setBackgroundResource(R.drawable.ic_action_no_signal);
                                txtViewPing.setText("No Connection!!!");
                            } else if (time < 250) {
                                imageViewSignal.setBackgroundResource(R.drawable.ic_action_4_bar);
                                txtViewPing.setText(time + "ms");

                            } else if (time < 500) {
                                imageViewSignal.setBackgroundResource(R.drawable.ic_action_3_bar);
                                txtViewPing.setText(time + "ms");

                            } else if (time < 1000) {
                                imageViewSignal.setBackgroundResource(R.drawable.ic_action_2_bar);
                                txtViewPing.setText(time + "ms");

                            } else {
                                imageViewSignal.setBackgroundResource(R.drawable.ic_action_1_bar);
                                txtViewPing.setText(time + "ms");

                            }

                        }
                    });

                    SystemClock.sleep(1000);
                }
            }


        }).start();


    }

    private void controllerName() {


        new Thread(new Runnable() { //Running on a new thread
            public void run() { //used to ge the pumps that are in today's schedule
                while (true) {
                    SQLManager sqlManagerName = new SQLManager(Home.this);
                    SQLManager sqlManagerMac = new SQLManager(Home.this);
                    final Cursor CursorName = sqlManagerName.getControllerName();
                    String processData = "Data Empty";
                    String mac = "";
                    Cursor ControllerMac = sqlManagerMac.getSelectedMac();

                    if (ControllerMac.getCount() > 0) {
                        ControllerMac.moveToNext();
                        mac = ControllerMac.getString(0);


                        final SocketController socketControllerExternal = new SocketController(Home.this, "getMAC");
                        try {
                            processData = socketControllerExternal.execute().get();

                        } catch (ExecutionException e) {

                        } catch (InterruptedException i) {

                        }

                    }
                        final String finalProcessData = processData;
                    final String finalMac = mac;
                    runOnUI(new Runnable() { //used to speak to main thread
                        @Override
                        public void run() {
                            try{
                                if (CursorName.getCount() > 0) {
                                    if (finalMac.equals(finalProcessData)) {
                                        CursorName.moveToNext();
                                        String Name = CursorName.getString(0);

                                        TxtPiName.setText(Name);
                                    }else {
                                        TxtPiName.setText("Unknown Controller");
                                    }


                                }
                            }catch (Exception e){
                                TxtPiName.setText("Something wrong happened");
                            }

                        }

                    });

                    //SystemClock.sleep(1000);
                }
            }


        }).start();




}

}

