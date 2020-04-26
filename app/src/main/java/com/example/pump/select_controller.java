package com.example.pump;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.vicmikhailau.maskededittext.MaskedEditText;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class select_controller extends AppCompatActivity implements View.OnLongClickListener{

    private Button addControoler, editController, backButton, btnAddSlave, BtnDeleteController, btnCheckForUpdates;
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
    private LinearLayout linearLayoutServerInfo, linearLayoutSlaves,linearLayoutSlavesPage, LinearLayoutAddNewController;
    private Spinner spinnerConnection;
    SocketController socketController;


    ArrayList<String> names = new ArrayList<String>();
    ArrayList<String> nameID = new ArrayList<String>();

    //List<String> SlaveName = new ArrayList<String>();
    //List<String> SlaveID = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final SQLManager sqlManager = new SQLManager(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select__controller);






        //frameLayoutArrow = findViewById(R.id.LinearLayoutArrow);
        LinearLayoutAddNewController = findViewById(R.id.LinearLayoutAddNewController);
        linearLayoutServerInfo = findViewById(R.id.LinearLayoutServerInfo);
        linearLayoutSlaves = findViewById(R.id.LinearLayoutSlaves);
        linearLayoutSlavesPage = findViewById(R.id.LinearLayoutSlavesPage);
        imageButtonArrow = findViewById(R.id.ImageButtonArrow);
        editController = findViewById(R.id.BtnEditServer);
        btnCheckForUpdates = findViewById(R.id.BtnUpdateController);
        backButton = findViewById(R.id.BtnBack);
        btnAddSlave = findViewById(R.id.BtnAddSlave);
        BtnDeleteController = findViewById(R.id.BtnDeleteController);
        addControoler = findViewById(R.id.BtnAddNewController);
        PathLocation = findViewById(R.id.TxtPumpPath);
        PathPort = findViewById(R.id.TxtPumpPort);
        txtPath = findViewById(R.id.textView15);
        txtPort = findViewById(R.id.textView17);
        radioButtonExternal = findViewById(R.id.RadioButtonExternal);
        radioButtonInternal = findViewById(R.id.RadioButtonInternal);
        spinnerConnection = findViewById(R.id.spinnerConnection);
        getpath();
        getSlaves();
        populatePiConnectionSpinner();
        final Dialog dialogLoad = new Dialog(select_controller.this);

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




        editController.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                txtPort.setTextColor(Color.parseColor("#FFFFFF"));
                txtPath.setTextColor(Color.parseColor("#FFFFFF"));

                if (PathLocation.getText().equals("")) {
                    txtPath.setTextColor(Color.parseColor("#FF0000"));
                }

                if (PathPort.getText().equals("")) {
                    txtPort.setTextColor(Color.parseColor("#FF0000"));
                }

                if(radioButtonInternal.isChecked() == true){

                    if((!PathLocation.getText().toString().equals(internalPath) || !PathPort.getText().toString().equals(internalPort)) && (!PathLocation.getText().toString().equals("") || !PathPort.getText().toString().equals(""))){



                        if(!PathLocation.getText().toString().equals(internalPath) || !PathPort.getText().toString().equals(internalPort)){

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

                            internalPath = PathLocation.getText().toString();
                            internalPort = PathPort.getText().toString();

                            new Thread(new Runnable() { //Running on a new thread
                                public void run() {

                                    final SocketController socketControllerManualInternal = new SocketController(select_controller.this, "ping", internalPath, Integer.parseInt(internalPort), true); //test if we get a reply
                                    String processData = "";

                                    try {
                                        processData = socketControllerManualInternal.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR).get();

                                    } catch (ExecutionException e) {

                                    } catch (InterruptedException i) {

                                    }

                                    final String finalProcessData = processData;
                                    runOnUI(new Runnable() { //used to speak to main thread
                                        @Override
                                        public void run() {
                                            if (finalProcessData.equals("success")) {

                                                sqlManager.updateInternalPath(internalPath, internalPort);

                                                displayConnectionStatusInternal(true, dialogLoad);

                                            } else {

                                                displayConnectionStatusInternal(false, dialogLoad);

                                            }
                                        }
                                    });

                                }
                            }).start();


                        }


                    }
                }else{
                    if((!PathLocation.getText().toString().equals(externalPath) || !PathPort.getText().toString().equals(externalPort)) && (!PathLocation.getText().toString().equals("") && !PathPort.getText().toString().equals(""))){



                        if(!PathLocation.getText().toString().equals(externalPath) || !PathPort.getText().toString().equals(externalPort)){

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

                            externalPath = PathLocation.getText().toString();
                            externalPort = PathPort.getText().toString();
                            new Thread(new Runnable() { //Running on a new thread
                                public void run() {

                                    final SocketController socketControllerManualExternal = new SocketController(select_controller.this,"ping", externalPath,Integer.parseInt(externalPort), false); //test if we get a reply
                                    String processData = "";

                                    try {
                                        processData = socketControllerManualExternal.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR).get();

                                        final String finalProcessData1 = processData;
                                        runOnUI(new Runnable() { //used to speak to main thread
                                            @Override
                                            public void run() {
                                                if (finalProcessData1.equals("success")) {

                                                    sqlManager.updateExternalPath(externalPath, externalPort);
                                                    displayConnectionStatusExternal(true, dialogLoad);


                                                }else {
                                                    displayConnectionStatusExternal(false, dialogLoad);
                                                }

                                            }
                                        });






                                    } catch (ExecutionException e) {

                                    } catch (InterruptedException i) {

                                    }

                                }
                            }).start();
                        }


                    }
                }











                }

        });

        btnAddSlave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final SocketController socketControllerManual = new SocketController(select_controller.this,"ping", PathLocation.getText().toString(),Integer.parseInt(PathPort.getText().toString()), true); //test if we get a reply

                String processData = "";

                try {
                    processData = socketControllerManual.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR).get();

                } catch (ExecutionException e) {

                } catch (InterruptedException i) {

                }

                if(processData.equals("success")){

                    //------------------------------------------------------------------
                    Intent salveController = new Intent(select_controller.this,SlaveController.class);

                    finish(); //Closes this activity
                    startActivity(salveController);
                    //------------------------------------------------------------------

                }else{
                    Toast.makeText(select_controller.this,"Could Not Connect To MASTER",Toast.LENGTH_SHORT).show();
                }
            }
        });


        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //------------------------------------------------------------------
                Intent Home = new Intent(select_controller.this,Home.class);

                startActivity(Home);
                //------------------------------------------------------------------

                finish();
            }
        });




        imageButtonArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(viewServer == true){
                    viewServer = false;
                    LinearLayoutAddNewController.setVisibility(View.VISIBLE);
                    linearLayoutServerInfo.setVisibility(View.GONE);
                    linearLayoutSlavesPage.setVisibility(View.VISIBLE);
                    imageButtonArrow.setBackgroundResource(R.drawable.ic_action_down_arrow);
                }else{
                    viewServer = true;
                    LinearLayoutAddNewController.setVisibility(View.GONE);
                    SQLManager sqlManagerName = new SQLManager(select_controller.this);
                    Cursor CursorName = sqlManagerName.getControllerName();

                    if (CursorName.getCount() > 0) {
                        editController.setEnabled(true);
                    }else {
                        editController.setEnabled(false);
                    }



                    linearLayoutServerInfo.setVisibility(View.VISIBLE);
                    linearLayoutSlavesPage.setVisibility(View.GONE);
                    imageButtonArrow.setBackgroundResource(R.drawable.ic_action_up_arrow);
                }
            }
        });


        addControoler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //------------------------------------------------------------------
                Intent PiController = new Intent(select_controller.this,AddPiController.class);
                finish(); //Closes this activity
                startActivity(PiController);
                //------------------------------------------------------------------

            }
        });


        spinnerConnection.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position != -1){
                    try{
                        SQLManager sqlManager1 = new SQLManager(select_controller.this);

                        sqlManager1.setSelectedController(nameID.get(position));

                        getpath();
                        getSlaves();
                    }catch (Exception e){

                    }

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        BtnDeleteController.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(select_controller.this);
                TextView txtEquipmentName;
                Button btnCancelDelete, btnConfirmDelete;
                dialog.setContentView(R.layout.activity_confirm);
                txtEquipmentName = dialog.findViewById(R.id.TxtEquipmentName);
                btnCancelDelete = dialog.findViewById(R.id.BtnCancelDelete);
                btnConfirmDelete = dialog.findViewById(R.id.BtnConfirmDelete);

                txtEquipmentName.setText("Are you sure you want to delete this Controller, you will not receive notifications");
                btnCancelDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                btnConfirmDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SQLManager sqlManager = new SQLManager(select_controller.this);
                        sqlManager.deleteSelectedController();
                        populatePiConnectionSpinner();
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }

        });

        btnCheckForUpdates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(select_controller.this);
                TextView txtEquipmentName, header;
                Button btnCancel, btnConfirm;
                dialog.setContentView(R.layout.activity_confirm);
                txtEquipmentName = dialog.findViewById(R.id.TxtEquipmentName);
                header = dialog.findViewById(R.id.textView14);
                btnCancel = dialog.findViewById(R.id.BtnCancelDelete);
                btnConfirm = dialog.findViewById(R.id.BtnConfirmDelete);
                header.setText("UPDATE");
                txtEquipmentName.setText("Are you sure you want to Update this System?");

                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                btnConfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Button dialogLoadCancel;
                        dialog.setContentView(R.layout.loading_screen);//popup view is the layout you created

                        dialogLoadCancel = dialog.findViewById(R.id.BtnCancel);
                        dialogLoadCancel.setVisibility(View.GONE);
                        dialog.setCanceledOnTouchOutside(false);
                        dialog.setCancelable(false);

                        new Thread(new Runnable() { //Running on a new thread
                            public void run() {

                                String SocketData = "";
                                SocketData = "checkAndUpdate";
                                SocketController socketController = new SocketController(select_controller.this,SocketData);
                                try{
                                    final String processData = socketController.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR).get();
                                    runOnUI(new Runnable() { //used to speak to main thread
                                        @Override
                                        public void run() {
                                            dialog.setContentView(R.layout.schedule_info);//popup view is the layout you created
                                            LayoutInflater layoutInflaterUpdateInfo = LayoutInflater.from(select_controller.this);
                                            LinearLayout LinearLayoutScrollInfo;
                                            TextView txtUpdateInfo, Header, TxtStartTime, textView7, TxtPumpInfo, textView8;
                                            Button BtnHide, BtnDelete;

                                            BtnHide = dialog.findViewById(R.id.BtnEdit);
                                            BtnDelete = dialog.findViewById(R.id.BtnDelete);
                                            Header = dialog.findViewById(R.id.textView13);
                                            TxtStartTime = dialog.findViewById(R.id.TxtStartTime);
                                            textView7 = dialog.findViewById(R.id.textView7);
                                            TxtPumpInfo = dialog.findViewById(R.id.TxtPumpInfo);
                                            textView8 = dialog.findViewById(R.id.textView8);
                                            LinearLayoutScrollInfo = dialog.findViewById(R.id.LinearLayoutScrollInfo);

                                            BtnHide.setText("Hide");
                                            Header.setText("UPDATE STATUS");
                                            TxtStartTime.setText("Do not Turn off the System....");
                                            textView8.setText("System will reboot when complete");


                                            View view = layoutInflaterUpdateInfo.inflate(R.layout.all_valves_toggle, LinearLayoutScrollInfo, false);
                                            MaskedEditText editTextDuration;
                                            editTextDuration = view.findViewById(R.id.EditTextManualDuration);
                                            txtUpdateInfo = view.findViewById(R.id.TxtZoneName);

                                            if(processData.equals("checkAndUpdate")){
                                                txtUpdateInfo.setText("Controller is not supported");
                                            }else if(processData.equals("Already up to date!")){
                                                txtUpdateInfo.setText(processData);
                                                TxtStartTime.setText("You have the latest public release");
                                                textView8.setVisibility(View.GONE);
                                            }
                                            else{
                                                txtUpdateInfo.setText(processData);
                                            }

                                            editTextDuration.setVisibility(View.GONE);
                                            LinearLayoutScrollInfo.addView(view);

                                            textView7.setVisibility(View.GONE);
                                            TxtPumpInfo.setVisibility(View.GONE);
                                            BtnDelete.setVisibility(View.GONE);
                                            BtnHide.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    dialog.dismiss();
                                                }
                                            });
                                            //dialogLoad.dismiss();
                                            //displayScheduleInfo(processData);
                                        }
                                    });
                                }catch (ExecutionException e){

                                }catch (InterruptedException i){

                                }


                            }

                        }).start();

                    }
                });

                dialog.show();
            }
        });




       /* new Thread(new Runnable() { //Running on a new thread
            public void run() {

                String SocketData = "";
                SocketData = v.getId() +"$getScheduleInfo";
                SocketController socketController = new SocketController(Schedule.this,SocketData);
                try{
                    final String processData = socketController.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR).get();
                    runOnUI(new Runnable() { //used to speak to main thread
                        @Override
                        public void run() {
                            dialogLoad.dismiss();
                            displayScheduleInfo(processData);
                        }
                    });
                }catch (ExecutionException e){

                }catch (InterruptedException i){

                }


            }

        }).start();
        */
            }



    private void populatePiConnectionSpinner(){
        SQLManager sqlManager = new SQLManager(select_controller.this);
        names.clear();
        nameID.clear();
        Cursor CursorName = sqlManager.getControllerIDandNames();
        if (CursorName.getCount() > 0) {
            CursorName.moveToFirst();

            while(!CursorName.isAfterLast()) {
                try{
                    names.add(CursorName.getString(0));
                    nameID.add(CursorName.getString(1));

                }catch (Exception e){

                }
                CursorName.moveToNext();
            }
        }

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this,   android.R.layout.simple_spinner_item, names);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
        spinnerConnection.setAdapter(spinnerArrayAdapter);

        int id = sqlManager.getSelectedIndex();
        if(id != -1){
            String stringID = String.valueOf(id);
            int position = nameID.indexOf(stringID);
            spinnerConnection.setSelection(position);
            BtnDeleteController.setEnabled(true);
        }else{
            BtnDeleteController.setEnabled(false);
        }

        if(nameID.size()<1){
            imageButtonArrow.setVisibility(View.GONE);
        }else{
            imageButtonArrow.setVisibility(View.VISIBLE);
        }

    }

    private void getSlaves(){
        new Thread(new Runnable() { //Running on a new thread
            public void run() {
                final LayoutInflater layoutInflaterSlaveDevice = LayoutInflater.from(select_controller.this);
                Button dialogLoadCancel;
                final View loadingScreen = layoutInflaterSlaveDevice.inflate(R.layout.loading_screen, linearLayoutSlaves, false);
                dialogLoadCancel = loadingScreen.findViewById(R.id.BtnCancel);
                dialogLoadCancel.setVisibility(View.GONE);

                runOnUI(new Runnable() { //used to speak to main thread
                    @Override
                    public void run() {
                        linearLayoutSlaves.removeAllViews();
                        linearLayoutSlaves.addView(loadingScreen);
                    }
                });



                String processData = "No Data";
                final SocketController socketController = new SocketController(select_controller.this,"getConnectedSlaves");
                try{
                    processData = socketController.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR).get();

                }catch (ExecutionException e){

                }catch (InterruptedException i){

                }



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
                        SlaveNames.setOnLongClickListener(select_controller.this);
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

        internalPath = "";
        internalPort = "" ;
        externalPath= "";
        externalPort = "";
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
            editController.setText("Update");
        }else{

            editController.setText("Add Server");
            imageButtonArrow.setVisibility(View.GONE);
            backButton.setVisibility(View.GONE);
            linearLayoutSlavesPage.setVisibility(View.GONE);
            viewServer = true;

        }
    }


    public void displayConnectionStatusExternalAndInternal(Boolean finalInternal, Boolean finalExternal, final Dialog dialogLoad){

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
            Save.setText("Save Anyway");
        }

        Close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogLoad.dismiss();
            }
        });

    }


    public void displayConnectionStatusInternal(Boolean finalInternal,  final Dialog dialogLoad){

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
        Close.setText("Close");
        if(finalInternal == true){
            internalText.setText("Internal Connection: Successful");
        }else {
            internalText.setText("Internal Connection: Failed");

        }
        //externalText.setText("External Connection: not given");

        Save.setVisibility(View.GONE);
        TxtPumpInfo.setVisibility(View.GONE);
        linearLayoutScrollInfo.setVisibility(View.GONE);
        txtDays.setVisibility(View.GONE);
        txtStartTime.setVisibility(View.GONE);
        externalText.setVisibility(View.GONE);
        Close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogLoad.dismiss();
            }
        });

    }

    public void displayConnectionStatusExternal(Boolean finalExternal,  final Dialog dialogLoad){

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

        Close.setText("Close");

        if(finalExternal == true){
            externalText.setText("External Connection: Successful");
        }else {
            externalText.setText("External Connection: Failed");
        }

        //internalText.setText("Internal Connection: not given");

        Save.setVisibility(View.GONE);
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

    }



}
