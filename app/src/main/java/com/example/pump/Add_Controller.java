package com.example.pump;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.ExecutionException;

public class Add_Controller extends AppCompatActivity implements View.OnLongClickListener{

    private Button addController, backButton, btnAddSlave;
    private TextView PathLocation, PathPort, txtPath, txtPort;
    private ImageButton imageButtonArrow;
    private boolean viewServer = false;
    private RadioButton radioButtonExternal, radioButtonInternal;
    private String internalPath, internalPort, externalPath, externalPort;
    public static Handler UIHandler = new Handler();
    public static void runOnUI(Runnable runnable) { //Runs code to invoke the main thread
        UIHandler.post(runnable);
    }
    //private LinearLayout frameLayoutArrow;
    private LinearLayout linearLayoutServerInfo, linearLayoutSlaves,linearLayoutSlavesPage;
    SocketController socketController;

    //List<String> SlaveName = new ArrayList<String>();
    //List<String> SlaveID = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final SQLManager sqlManager = new SQLManager(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add__controller);






        //frameLayoutArrow = findViewById(R.id.LinearLayoutArrow);
        linearLayoutServerInfo = findViewById(R.id.LinearLayoutServerInfo);
        linearLayoutSlaves = findViewById(R.id.LinearLayoutSlaves);
        linearLayoutSlavesPage = findViewById(R.id.LinearLayoutSlavesPage);
        imageButtonArrow = findViewById(R.id.ImageButtonArrow);
        addController = findViewById(R.id.BtnAddServer);
        backButton = findViewById(R.id.BtnBack);
        btnAddSlave = findViewById(R.id.BtnAddSlave);
        PathLocation = findViewById(R.id.TxtPumpPath);
        PathPort = findViewById(R.id.TxtPumpPort);
        txtPath = findViewById(R.id.textView15);
        txtPort = findViewById(R.id.textView17);
        radioButtonExternal = findViewById(R.id.RadioButtonExternal);
        radioButtonInternal = findViewById(R.id.RadioButtonInternal);
        getpath();

        radioButtonInternal.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                txtPath.setText("Pump Controller External Path");
                txtPort.setText("Pump Controller External Port");
                if(externalPath != null && externalPort !=null){
                    PathLocation.setText(externalPath);
                    PathPort.setText(externalPort);
                }else{
                    PathLocation.setText("");
                    PathPort.setText("");
                }


            }
        });
        radioButtonExternal.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                txtPath.setText("Pump Controller Internal Path");
                txtPort.setText("Pump Controller Internal Port");
                if(internalPath != null && internalPort !=null) {
                    PathLocation.setText(internalPath);
                    PathPort.setText(internalPort);
                }else{
                    PathLocation.setText("");
                    PathPort.setText("");
                }
            }
        });




        addController.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtPort.setTextColor(Color.parseColor("#FFFFFF"));
                txtPath.setTextColor(Color.parseColor("#FFFFFF"));
                if (PathLocation.getText().toString().equals("") || PathPort.getText().toString().equals("")) {

                    if (PathLocation.getText().equals("")) {
                        txtPath.setTextColor(Color.parseColor("#FF0000"));
                    }

                    if (PathPort.getText().equals("")) {
                        txtPort.setTextColor(Color.parseColor("#FF0000"));
                    }
                }else{
                    if(radioButtonInternal.isChecked() == true){
                        internalPath = PathLocation.getText().toString();
                        internalPort = PathPort.getText().toString();
                    }else{
                        externalPath = PathLocation.getText().toString();
                        externalPort = PathPort.getText().toString();

                    }
                    Toast.makeText(Add_Controller.this, "Trying to add server, Please wait", Toast.LENGTH_SHORT).show();
/*
                    if(externalPath == null || externalPort == null || internalPath == null || internalPort ==null) {
                    }
 */
                    if(externalPath != null && externalPort !=null) {
                        new Thread(new Runnable() { //Running on a new thread
                            public void run() {

                                final SocketController socketControllerManualExternal = new SocketController(Add_Controller.this,"ping", externalPath,Integer.parseInt(externalPort), false); //test if we get a reply
                                String processData = "";

                                try {
                                    processData = socketControllerManualExternal.execute().get();

                                } catch (ExecutionException e) {

                                } catch (InterruptedException i) {

                                }

                                final String finalProcessData = processData;
                                runOnUI(new Runnable() { //used to speak to main thread
                                    @Override
                                    public void run() {
                                        if (finalProcessData.equals("success")) {
                                            Toast.makeText(Add_Controller.this, "Connected to External!", Toast.LENGTH_SHORT).show();
                                            sqlManager.updateExternalPath(externalPath, externalPort);
                                            finish();
                                        } else {
                                            Toast.makeText(Add_Controller.this, "Could Not Connect to External", Toast.LENGTH_SHORT).show();
                                            //Toast.makeText(Add_Controller.this,"please Create your Account First", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });

                            }
                        }).start();
                    }

                    if(internalPath != null && internalPort !=null) {

                        new Thread(new Runnable() { //Running on a new thread
                            public void run() {

                        final SocketController socketControllerManualInternal = new SocketController(Add_Controller.this, "ping", internalPath, Integer.parseInt(internalPort), true); //test if we get a reply
                        String processData = "";

                        try {
                            processData = socketControllerManualInternal.execute().get();

                        } catch (ExecutionException e) {

                        } catch (InterruptedException i) {

                        }

                                final String finalProcessData = processData;
                                runOnUI(new Runnable() { //used to speak to main thread
                                    @Override
                                    public void run() {
                                        if (finalProcessData.equals("success")) {
                                            Toast.makeText(Add_Controller.this, "Connected to Internal!", Toast.LENGTH_SHORT).show();
                                            sqlManager.updateInternalPath(internalPath, internalPort);
                                            finish();
                                        } else {
                                            Toast.makeText(Add_Controller.this, "Could Not Connect to Internal", Toast.LENGTH_SHORT).show();
                                            //Toast.makeText(Add_Controller.this,"please Create your Account First", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });

                            }
                        }).start();


                    }

                }
            }

        });

        btnAddSlave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final SocketController socketControllerManual = new SocketController(Add_Controller.this,"ping", PathLocation.getText().toString(),Integer.parseInt(PathPort.getText().toString()), true); //test if we get a reply

                String processData = "";

                try {
                    processData = socketControllerManual.execute().get();

                } catch (ExecutionException e) {

                } catch (InterruptedException i) {

                }

                if(processData.equals("success")){

                    //------------------------------------------------------------------
                    Intent salveController = new Intent(Add_Controller.this,SlaveController.class);

                    finish(); //Closes this activity
                    startActivity(salveController);
                    //------------------------------------------------------------------

                }else{
                    Toast.makeText(Add_Controller.this,"Could Not Connect To MASTER",Toast.LENGTH_SHORT).show();
                }
            }
        });


        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });




        imageButtonArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(viewServer == true){
                    viewServer = false;
                    linearLayoutServerInfo.setVisibility(View.GONE);
                    linearLayoutSlavesPage.setVisibility(View.VISIBLE);
                    imageButtonArrow.setBackgroundResource(R.drawable.ic_action_down_arrow);
                }else{
                    viewServer = true;
                    linearLayoutServerInfo.setVisibility(View.VISIBLE);
                    linearLayoutSlavesPage.setVisibility(View.GONE);
                    imageButtonArrow.setBackgroundResource(R.drawable.ic_action_up_arrow);
                }
            }
        });
        getSlaves();
    }

    private void getSlaves(){
        new Thread(new Runnable() { //Running on a new thread
            public void run() {

                String processData = "Data Empty";
                final SocketController socketController = new SocketController(Add_Controller.this,"getConnectedSlaves");
                try{
                    processData = socketController.execute().get();

                }catch (ExecutionException e){

                }catch (InterruptedException i){

                }

                final LayoutInflater layoutInflaterSlaveDevice = LayoutInflater.from(Add_Controller.this);

                runOnUI(new Runnable() { //used to speak to main thread
                    @Override
                    public void run() {
                        linearLayoutSlaves.removeAllViews();
                    }
                });

                if(processData.equals("No Data") || processData.equals("Server Not Running")) {
                    //No Data

                    TextView BTNames;
                    final View view = layoutInflaterSlaveDevice.inflate(R.layout.all_pumps_toggle, linearLayoutSlaves, false);
                    BTNames = view.findViewById(R.id.TxtPump);
                    BTNames.setText("No Connected Devices");
                    runOnUI(new Runnable() { //used to speak to main thread
                        @Override
                        public void run() {
                            linearLayoutSlaves.addView(view);
                        }
                    });

                }else{
                    String[] SlaveList = processData.split("#");

                    TextView SlaveNames;

                    for (int i = 0; i < SlaveList.length; i++) {
                        final View view = layoutInflaterSlaveDevice.inflate(R.layout.all_pumps_toggle, linearLayoutSlaves, false);
                        String[] SlaveInfo = SlaveList[i].split(",");
                        view.setId(i);

                        SlaveNames = view.findViewById(R.id.TxtPump);
                        SlaveNames.setOnLongClickListener(Add_Controller.this);
                        SlaveNames.setId(i);
                        SlaveNames.setTextSize(30);
                        //SlaveName.add(SlaveInfo[1]);
                        //SlaveID.add(SlaveInfo[0]);
                        SlaveNames.setText(SlaveInfo[1]);

                        runOnUI(new Runnable() { //used to speak to main thread
                            @Override
                            public void run() {
                                linearLayoutSlaves.addView(view);
                            }
                        });


                    }
                }
            }
        }).start();
    }


    @Override
    public boolean onLongClick(View v) {
        return true;
    }

    private void getpath(){
        SQLManager sqlManager = new SQLManager(this);

        Cursor path = sqlManager.getPath();
        if(path.getCount()>0){
            //btnAddSlave.setText("");
            path.moveToNext();
            if(path.getString(0) != null){
                internalPath = path.getString(0);
                internalPort = path.getString(1);
            }
            if(path.getString(2) != null){
                externalPath = path.getString(2);
                externalPort = path.getString(3);
            }

            PathLocation.setText(path.getString(0));
            PathPort.setText(path.getString(1));
            viewServer = false;
            linearLayoutServerInfo.setVisibility(View.GONE);
            imageButtonArrow.setVisibility(View.VISIBLE);
            linearLayoutSlavesPage.setVisibility(View.VISIBLE);
            imageButtonArrow.setBackgroundResource(R.drawable.ic_action_down_arrow);
            addController.setText("Update");
        }else{

            addController.setText("Add Server");
            imageButtonArrow.setVisibility(View.GONE);
            backButton.setVisibility(View.GONE);
            linearLayoutSlavesPage.setVisibility(View.GONE);
            viewServer = true;

        }
    }

}
