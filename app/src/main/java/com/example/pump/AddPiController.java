package com.example.pump;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class AddPiController extends AppCompatActivity {
    Button BtnBack, BtnAddController;
    EditText TxtName, TxtInternalConnection, TxtInternalPort, TxtExternalConnection, TxtExternalPort;


    public static Handler UIHandler = new Handler();
    public static void runOnUI(Runnable runnable) { //Runs code to invoke the main thread
        UIHandler.post(runnable);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pi_controller);
        BtnBack = findViewById(R.id.BtnBack);
        BtnAddController = findViewById(R.id.BtnAddController);

        TxtName = findViewById(R.id.TxtName);
        TxtInternalConnection = findViewById(R.id.TxtInternalConnection);
        TxtInternalPort = findViewById(R.id.TxtInternalPort);
        TxtExternalConnection = findViewById(R.id.TxtExternalConnection);
        TxtExternalPort = findViewById(R.id.TxtExternalPort);
        checkIfThereIsMoreControllers();

        BtnAddController.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Dialog dialogLoad = new Dialog(AddPiController.this);
                final String name = TxtName.getText().toString();
                final String InternalConnection = TxtInternalConnection.getText().toString();
                final String InternalPort = TxtInternalPort.getText().toString();
                final String ExternalConnection = TxtExternalConnection.getText().toString();
                final String ExternalPort = TxtExternalPort.getText().toString();
            if(!TxtName.getText().toString().equals("") &&((!TxtInternalConnection.getText().toString().equals("") && !TxtInternalPort.getText().toString().equals("")) ||(!TxtExternalConnection.getText().toString().equals("") && !TxtExternalPort.getText().toString().equals("")))){


                final Button dialogLoadCancel;
                dialogLoad.setContentView(R.layout.loading_screen);//popup view is the layout you created
                dialogLoadCancel = dialogLoad.findViewById(R.id.BtnCancel);
                dialogLoadCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogLoad.dismiss();
                    }
                });
                dialogLoad.show();

                if((!TxtInternalConnection.getText().toString().equals("")) && (!TxtInternalPort.getText().toString().equals("")) &&(!TxtExternalConnection.getText().toString().equals("")) && (!TxtExternalPort.getText().toString().equals(""))){

                    new Thread(new Runnable() { //Running on a new thread
                        public void run() {

                            Boolean internal = false;
                            Boolean external = false;
                            String Mac = "";
                            String processData = "Data Empty";
                            final SocketController socketControllerInternal = new SocketController(AddPiController.this, "getMAC",InternalConnection,  Integer.parseInt(InternalPort), true);
                            //final SocketController socketController = new SocketController(AddPiController.this, "getMAC");
                            try {
                                processData = socketControllerInternal.execute().get();
                                if(!processData.equals("Server Not Running")){
                                    Mac = processData;
                                    internal = true;
                                }

                            } catch (ExecutionException e) {

                            } catch (InterruptedException i) {

                            }

                            final SocketController socketControllerExternal = new SocketController(AddPiController.this, "getMAC",ExternalConnection,  Integer.parseInt(ExternalPort), false);
                            try {
                                processData = socketControllerExternal.execute().get();
                                if(!processData.equals("Server Not Running")){
                                    Mac = processData;
                                    external = true;
                                }

                            } catch (ExecutionException e) {

                            } catch (InterruptedException i) {

                            }

                            final Boolean finalInternal = internal;
                            final Boolean finalExternal = external;
                            final String finalMac = Mac;
                            runOnUI(new Runnable() { //used to speak to main thread
                                @Override
                                public void run() {
                                    displayConnectionStatusExternalAndInternal(finalInternal, finalExternal,InternalConnection, InternalPort, ExternalConnection, ExternalPort, finalMac, name, dialogLoad);
                                }
                            });
                        }
                    }).start();





                }else if(!TxtInternalConnection.getText().toString().equals("") && !TxtInternalPort.getText().toString().equals("")){

                    new Thread(new Runnable() { //Running on a new thread
                        public void run() {

                            Boolean internal = false;

                            String Mac = "";
                            String processData = "Data Empty";
                            final SocketController socketControllerInternal = new SocketController(AddPiController.this, "getMAC",InternalConnection,  Integer.parseInt(InternalPort), true);
                            //final SocketController socketController = new SocketController(AddPiController.this, "getMAC");
                            try {
                                processData = socketControllerInternal.execute().get();
                                if(!processData.equals("Server Not Running")){
                                    Mac = processData;
                                    internal = true;
                                }

                            } catch (ExecutionException e) {

                            } catch (InterruptedException i) {

                            }


                            final Boolean finalInternal = internal;
                            final String finalMac = Mac;
                            runOnUI(new Runnable() { //used to speak to main thread
                                @Override
                                public void run() {
                                    displayConnectionStatusInternal(finalInternal, InternalConnection, InternalPort, finalMac, name, dialogLoad);
                                }
                            });
                        }
                    }).start();


                }else if(!TxtExternalConnection.getText().toString().equals("") && !TxtExternalPort.getText().toString().equals("")){

                    new Thread(new Runnable() { //Running on a new thread
                        public void run() {

                            Boolean external = false;

                            String Mac = "";
                            String processData = "Data Empty";
                            final SocketController socketControllerExternal = new SocketController(AddPiController.this, "getMAC",ExternalConnection,  Integer.parseInt(ExternalPort), false);
                            //final SocketController socketController = new SocketController(AddPiController.this, "getMAC");
                            try {
                                processData = socketControllerExternal.execute().get();
                                if(!processData.equals("Server Not Running")){
                                    Mac = processData;
                                    external = true;
                                }

                            } catch (ExecutionException e) {

                            } catch (InterruptedException i) {

                            }

                            final Boolean finalExternal = external;
                            final String finalMac = Mac;
                            runOnUI(new Runnable() { //used to speak to main thread
                                @Override
                                public void run() {
                                    displayConnectionStatusExternal(finalExternal, ExternalConnection, ExternalPort, finalMac, name, dialogLoad);
                                }
                            });
                        }
                    }).start();


                }




                }
            }
        });

        BtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //------------------------------------------------------------------
                Intent SelectController = new Intent(AddPiController.this,select_controller.class);
                finish(); //Closes this activity
                startActivity(SelectController);
                //------------------------------------------------------------------
            }
        });

    }

    public void displayConnectionStatusExternalAndInternal(Boolean finalInternal, Boolean finalExternal, final String InternalConnection, final String InternalPort, final String ExternalConnection, final String ExternalPort, final String mac, final String Name, final Dialog dialog){
        final Dialog dialogLoad = dialog;
        dialogLoad.setContentView(R.layout.schedule_info);//popup view is the layout you created
        LinearLayout linearLayoutScrollInfo = dialogLoad.findViewById(R.id.LinearLayoutScrollInfo);
        TextView txtDays = dialogLoad.findViewById(R.id.TxtDays);
        TextView txtStartTime = dialogLoad.findViewById(R.id.TxtStartTime);
        TextView txtPumpInfo = dialogLoad.findViewById(R.id.TxtPumpInfo);
        TextView heading = dialogLoad.findViewById(R.id.textView13);
        TextView internalText = dialogLoad.findViewById(R.id.textView7);
        TextView externalText = dialogLoad.findViewById(R.id.textView8);
        TextView TxtPumpInfo = dialogLoad.findViewById(R.id.TxtPumpInfo);
        Button Save = dialogLoad.findViewById(R.id.BtnEdit);
        Button Close = dialogLoad.findViewById(R.id.BtnDelete);

        heading.setText("Connection Status!");
        Save.setText("Save");
        Close.setText("Close");
        if(finalInternal == true){
            internalText.setText("Internal Connection: Successful");
        }else {
            internalText.setText("Internal Connection: Failed");
        }

        if(finalExternal == true){
            externalText.setText("External Connection: Successful");
        }else {
            externalText.setText("External Connection: Failed");
        }
        if(finalExternal == false && finalInternal == false){
            Save.setVisibility(View.GONE);
        }

        TxtPumpInfo.setVisibility(View.GONE);
        linearLayoutScrollInfo.setVisibility(View.GONE);
        txtDays.setVisibility(View.GONE);
        txtStartTime.setVisibility(View.GONE);

        if(finalInternal == false && finalExternal == false){
            Save.setVisibility(View.GONE);
        }

        Close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogLoad.dismiss();
            }
        });

        Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogLoad.dismiss();
                SQLManager sqlManager = new SQLManager(AddPiController.this);
                sqlManager.addNewControllerExternalAndInternal(InternalConnection,InternalPort,ExternalConnection, ExternalPort, mac, Name);
                //------------------------------------------------------------------
                Intent SelectController = new Intent(AddPiController.this,select_controller.class);
                finish(); //Closes this activity
                startActivity(SelectController);
                //------------------------------------------------------------------
            }
        });
    }

    public void displayConnectionStatusInternal(Boolean finalInternal, final String InternalConnection, final String InternalPort,final String mac, final String Name, final Dialog dialog){
        final Dialog dialogLoad = dialog;
        dialogLoad.setContentView(R.layout.schedule_info);//popup view is the layout you created
        LinearLayout linearLayoutScrollInfo = dialogLoad.findViewById(R.id.LinearLayoutScrollInfo);
        TextView txtDays = dialogLoad.findViewById(R.id.TxtDays);
        TextView txtStartTime = dialogLoad.findViewById(R.id.TxtStartTime);
        TextView txtPumpInfo = dialogLoad.findViewById(R.id.TxtPumpInfo);
        TextView heading = dialogLoad.findViewById(R.id.textView13);
        TextView internalText = dialogLoad.findViewById(R.id.textView7);
        TextView externalText = dialogLoad.findViewById(R.id.textView8);
        TextView TxtPumpInfo = dialogLoad.findViewById(R.id.TxtPumpInfo);
        Button Save = dialogLoad.findViewById(R.id.BtnEdit);
        Button Close = dialogLoad.findViewById(R.id.BtnDelete);

        heading.setText("Connection Status!");
        Save.setText("Save");
        Close.setText("Close");
        if(finalInternal == true){
            internalText.setText("Internal Connection: Successful");
        }else {
            internalText.setText("Internal Connection: Failed");
            Save.setVisibility(View.GONE);
        }
        externalText.setText("External Connection: not given");

        TxtPumpInfo.setVisibility(View.GONE);
        linearLayoutScrollInfo.setVisibility(View.GONE);
        txtDays.setVisibility(View.GONE);
        txtStartTime.setVisibility(View.GONE);

        Close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogLoad.dismiss();
            }
        });


        Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogLoad.dismiss();
                SQLManager sqlManager = new SQLManager(AddPiController.this);
                sqlManager.addNewControllerInternal(InternalConnection,InternalPort, mac, Name);


                //------------------------------------------------------------------
                Intent SelectController = new Intent(AddPiController.this,select_controller.class);
                finish(); //Closes this activity
                startActivity(SelectController);
                //------------------------------------------------------------------

            }
        });
    }

    public void displayConnectionStatusExternal(Boolean finalExternal, final String ExternalConnection, final String ExternalPort, final String mac, final String Name, final Dialog dialog){
        final Dialog dialogLoad = dialog;
        dialogLoad.setContentView(R.layout.schedule_info);//popup view is the layout you created
        LinearLayout linearLayoutScrollInfo = dialogLoad.findViewById(R.id.LinearLayoutScrollInfo);
        TextView txtDays = dialogLoad.findViewById(R.id.TxtDays);
        TextView txtStartTime = dialogLoad.findViewById(R.id.TxtStartTime);
        TextView txtPumpInfo = dialogLoad.findViewById(R.id.TxtPumpInfo);
        TextView heading = dialogLoad.findViewById(R.id.textView13);
        TextView internalText = dialogLoad.findViewById(R.id.textView7);
        TextView externalText = dialogLoad.findViewById(R.id.textView8);
        TextView TxtPumpInfo = dialogLoad.findViewById(R.id.TxtPumpInfo);
        Button Save = dialogLoad.findViewById(R.id.BtnEdit);
        Button Close = dialogLoad.findViewById(R.id.BtnDelete);

        heading.setText("Connection Status!");
        Save.setText("Save");
        Close.setText("Close");

        if(finalExternal == true){
            internalText.setText("External Connection: Successful");
        }else {
            internalText.setText("External Connection: Failed");
            Save.setVisibility(View.GONE);
        }

        externalText.setText("Internal Connection: not given");

        TxtPumpInfo.setVisibility(View.GONE);
        linearLayoutScrollInfo.setVisibility(View.GONE);
        txtDays.setVisibility(View.GONE);
        txtStartTime.setVisibility(View.GONE);
        Close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogLoad.dismiss();
            }
        });


        Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogLoad.dismiss();
                SQLManager sqlManager = new SQLManager(AddPiController.this);
                sqlManager.addNewControllerExternal(ExternalConnection,ExternalPort, mac, Name);

                //------------------------------------------------------------------
                Intent SelectController = new Intent(AddPiController.this,select_controller.class);
                finish(); //Closes this activity
                startActivity(SelectController);
                //------------------------------------------------------------------
            }
        });
    }

    public void checkIfThereIsMoreControllers(){

        try{
            ArrayList<String> nameID = new ArrayList<String>();

            SQLManager sqlManager = new SQLManager(AddPiController.this);
            nameID.clear();
            Cursor CursorName = sqlManager.getControllerIDandNames();
            if (CursorName.getCount() > 0) {
                CursorName.moveToFirst();

                while(!CursorName.isAfterLast()) {
                    try{
                        nameID.add(CursorName.getString(1));

                    }catch (Exception e){

                    }
                    CursorName.moveToNext();
                }
            }

            if(nameID.size()<1){
                BtnBack.setVisibility(View.GONE);
            }else{
                BtnBack.setVisibility(View.VISIBLE);
            }
        }catch (Exception e){

        }


    }


}
