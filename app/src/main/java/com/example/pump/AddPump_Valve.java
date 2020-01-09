package com.example.pump;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class AddPump_Valve extends AppCompatActivity implements View.OnLongClickListener , View.OnClickListener, AdapterView.OnItemSelectedListener{

public static Handler UIHandler = new Handler();

public static void runOnUI(Runnable runnable) { //Runs code to invoke the main thread
        UIHandler.post(runnable);
    }

Button btnAddEquipmentToDatabase, btnCancelAdd, btnAddEquipment, btnBack;
EditText editTextName, editTextGPIO, editTextGPIODirectDrive;
CheckBox checkBoxDirectOnline;
LinearLayout linearLayoutPumps, linearLayoutValves, linearLayoutAddPump, linearLayoutShowEquipment, linearLayoutSensorType, linearLayoutSlaves, linearLayoutScrollManualPump,
        linearLayoutScrollManualZone, linearLayoutPumpZone,linearLayoutSensor;
String SocketData;
RadioButton radioValve, radioPump, radioSensor;
TextView txtEquipmentName, txtLocation, txtGPIO, txtGPIODirectOnline;
ScrollView scrollViewAddSensorValve;
Spinner spinnerSensor;
boolean editEquipment = false;
String EquipmentID;
String getPumpsForInfo, getZonesForInfo;

    List<String> pumpIDs = new ArrayList<String>();
    List<String> zoneIDs = new ArrayList<String>();
    List<Integer> selectedEquipment = new ArrayList<Integer>();

    ArrayAdapter<String> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_equipment);
        btnAddEquipmentToDatabase = findViewById(R.id.BtnAdd);
        btnCancelAdd = findViewById(R.id.BtnCancelAdd);
        btnBack = findViewById(R.id.btnBackFromEquipment);
        btnAddEquipment = findViewById(R.id.BtnAddEquipment);
        editTextName = findViewById(R.id.EditTextName);
        //editTextLocation = (EditText)findViewById(R.id.EditTextLocation);
        //editTextDescription = (EditText) findViewById(R.id.EditTextDescription);
        editTextGPIO = findViewById(R.id.EditTextGPIO);
        scrollViewAddSensorValve = findViewById(R.id.ScrollViewAddSensorValve);
        linearLayoutSensorType = findViewById(R.id.LinearLayoutSensorType);
        linearLayoutSlaves = findViewById(R.id.LinearLayoutSlaves);
        linearLayoutPumpZone = findViewById(R.id.LinearLayoutPumpZone);
        linearLayoutSensor = findViewById(R.id.LinearLayoutSensor);
        radioValve = findViewById(R.id.RadioValve);
        radioPump = findViewById(R.id.RadioPump);
        radioSensor = findViewById(R.id.RadioSensor);
        txtGPIODirectOnline = findViewById(R.id.txtGPIODirectOnline);
        checkBoxDirectOnline = findViewById(R.id.CheckBoxVSD);
        editTextGPIODirectDrive = findViewById(R.id.EditTextGPIOVXD);
        txtGPIODirectOnline.setVisibility(View.GONE);
        checkBoxDirectOnline.setVisibility(View.GONE);
        editTextGPIODirectDrive.setVisibility(View.GONE);
        spinnerSensor = findViewById(R.id.SpinnerSensor);

        radioValve.setOnClickListener(AddPump_Valve.this);
        radioPump.setOnClickListener(AddPump_Valve.this);
        radioSensor.setOnClickListener(AddPump_Valve.this);

        txtEquipmentName = findViewById(R.id.TxtEquipmentName);
        txtLocation = findViewById(R.id.TxtLocation);
        txtGPIO = findViewById(R.id.TxtGPIO);

        linearLayoutPumps = findViewById(R.id.LinearLayoutPumps);
        linearLayoutValves = findViewById(R.id.LinearLayoutValves);
        linearLayoutAddPump = findViewById(R.id.LinearLayoutAddPump);
        linearLayoutShowEquipment = findViewById(R.id.LinearLayoutEquipment);
        showCurrentEquipment();
        populatePumpsValves();

//https://developer.android.com/guide/topics/ui/controls/spinner#java
        adapter = new ArrayAdapter<String>(AddPump_Valve.this,
                android.R.layout.simple_expandable_list_item_1, getResources().getStringArray(R.array.sensor_array));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_item,list);
        spinnerSensor.setAdapter(adapter);
        spinnerSensor.setOnItemSelectedListener(AddPump_Valve.this);


        btnAddEquipment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddEquipment();
                showEquipmentLayout();
                getSlaves();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnAddEquipmentToDatabase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if(editTextName.getText().toString().equals("")|| editTextGPIO.getText().toString().equals("")){
                if(editTextName.getText().toString().equals("")){

                    txtEquipmentName.setTextColor(Color.parseColor("#FF0000"));

                }else{

                    txtEquipmentName.setTextColor(Color.parseColor("#808080"));
                }


                if(editTextGPIO.getText().toString().equals("")){
                    txtGPIO.setTextColor(Color.parseColor("#FF0000"));
                }
                else{

                    txtGPIO.setTextColor(Color.parseColor("#808080"));
                }
            }else{//____________________________________________________________________________retried the information to be added
                if (editEquipment == true) {
                    if (radioPump.isChecked() == true || radioValve.isChecked() == true){       //Update Equipment table
                        SocketData = EquipmentID + ",";
                        SocketData = SocketData + editTextName.getText().toString();
                        SocketData = SocketData + "," +getSelectedController();
                        SocketData = SocketData + "," + editTextGPIO.getText().toString();

                        if (radioValve.isChecked()) {
                            SocketData = SocketData + ",0";
                        }else{
                            SocketData = SocketData + ",1";
                        }
                        if (checkBoxDirectOnline.isChecked() == true) {
                            SocketData = SocketData + "," + editTextGPIODirectDrive.getText().toString();
                        }
                        //SocketData = SocketData + "," + editTextGPIODirectDrive.getText().toString();
                        SocketData = SocketData + "$" + "updateEquipment";
                        SocketController socketController = new SocketController(AddPump_Valve.this,SocketData);
                        socketController.execute();
                        finish();
                        Intent schedule = new Intent(AddPump_Valve.this,AddPump_Valve.class);
                        AddPump_Valve.this.startActivity(schedule);
                    }else{                                                                      //Update Sensor Table
                        SocketData = EquipmentID + ",";
                        SocketData = SocketData + editTextName.getText().toString();
                        SocketData = SocketData + "," +getSelectedController();
                        SocketData = SocketData + "," + editTextGPIO.getText().toString();
                        SocketData = SocketData + "," + spinnerSensor.getSelectedItem().toString();
                        if(spinnerSensor.getSelectedItem().equals("Echo Sensor")){
                            SocketData = SocketData + "," + editTextGPIODirectDrive.getText();
                        }
                        for (int i = 0; i < selectedEquipment.size(); i++) {
                            SocketData = SocketData + "," + selectedEquipment.get(i);
                        }
                        SocketData = SocketData + "$" + "EDIT_SENSOR";
                        SocketController socketController = new SocketController(AddPump_Valve.this, SocketData);
                        socketController.execute();
                        finish();
                        Intent schedule = new Intent(AddPump_Valve.this,AddPump_Valve.class);
                        AddPump_Valve.this.startActivity(schedule);
                    }

                } else { //_______________________________________________________________________________ADD_PUMP_DirectOnline

                    if (radioPump.isChecked() == true) {

                        if (checkBoxDirectOnline.isChecked()) {
                            SocketData = "";
                            SocketData = editTextName.getText().toString();
                            SocketData = SocketData + "," + getSelectedController();

                            SocketData = SocketData + "," + editTextGPIO.getText().toString();
                            SocketData = SocketData + "," + editTextGPIODirectDrive.getText().toString();
                            SocketData = SocketData + "$" + "ADD_PUMP_DirectOnline";
                            SocketController socketController = new SocketController(AddPump_Valve.this, SocketData);
                            socketController.execute();
                            finish();
                            Intent schedule = new Intent(AddPump_Valve.this,AddPump_Valve.class);
                            AddPump_Valve.this.startActivity(schedule);
                        } else {

                        SocketData = "";
                        SocketData = editTextName.getText().toString();
                        SocketData = SocketData + "," + getSelectedController();

                        SocketData = SocketData + "," + editTextGPIO.getText().toString();
                        SocketData = SocketData + "$" + "ADD_PUMP";

                        SocketController socketController = new SocketController(AddPump_Valve.this, SocketData);
                        socketController.execute();
                        finish();
                            Intent schedule = new Intent(AddPump_Valve.this,AddPump_Valve.class);
                            AddPump_Valve.this.startActivity(schedule);
                    }
                    } else if(radioValve.isChecked() == true) {
                        SocketData = "";
                        SocketData = editTextName.getText().toString();
                        SocketData = SocketData + "," +getSelectedController();
                        SocketData = SocketData + "," + editTextGPIO.getText().toString();
                        SocketData = SocketData + "$" + "ADD_VALVE";

                        SocketController socketController = new SocketController(AddPump_Valve.this, SocketData);
                        socketController.execute();
                        finish();
                        Intent schedule = new Intent(AddPump_Valve.this,AddPump_Valve.class);
                        AddPump_Valve.this.startActivity(schedule);
                    }else if(radioSensor.isChecked() == true){
                        SocketData = "";
                        SocketData = editTextName.getText().toString();
                        SocketData = SocketData + "," +getSelectedController();
                        SocketData = SocketData + "," + editTextGPIO.getText().toString();
                        SocketData = SocketData + "," + spinnerSensor.getSelectedItem().toString();
                        if(spinnerSensor.getSelectedItem().equals("Echo Sensor")){
                            SocketData = SocketData + "," + editTextGPIODirectDrive.getText();
                        }
                        for (int i = 0; i < selectedEquipment.size(); i++) {
                            SocketData = SocketData + "," + selectedEquipment.get(i);
                        }
                        SocketData = SocketData + "$" + "ADD_SENSOR";
                        SocketController socketController = new SocketController(AddPump_Valve.this, SocketData);
                        socketController.execute();
                        finish();
                        Intent schedule = new Intent(AddPump_Valve.this,AddPump_Valve.class);
                        AddPump_Valve.this.startActivity(schedule);

                    }
                }
            }
            }
        });


        btnCancelAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        checkBoxDirectOnline.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    txtGPIODirectOnline.setVisibility(View.VISIBLE);
                    editTextGPIODirectDrive.setVisibility(View.VISIBLE);
                }else{
                    editTextGPIODirectDrive.setVisibility(View.GONE);
                    txtGPIODirectOnline.setVisibility(View.GONE);
                }
            }
        });


        spinnerSensor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if(spinnerSensor.getSelectedItem().equals("Echo Sensor")){
                txtGPIODirectOnline.setText("Select the Trigger Pin");
                txtGPIODirectOnline.setVisibility(View.VISIBLE);
                editTextGPIODirectDrive.setVisibility(View.VISIBLE);
            }else{
                editTextGPIODirectDrive.setVisibility(View.GONE);
                txtGPIODirectOnline.setVisibility(View.GONE);
            }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void getSlaves(){
        new Thread(new Runnable() { //Running on a new thread
            public void run() {

                String processData = "Data Empty";
                final SocketController socketController = new SocketController(AddPump_Valve.this,"getConnectedSlaves");
                try{
                    processData = socketController.execute().get();

                }catch (ExecutionException e){

                }catch (InterruptedException i){

                }

                final LayoutInflater layoutInflaterSlaveDevice = LayoutInflater.from(AddPump_Valve.this);

                runOnUI(new Runnable() { //used to speak to main thread
                    @Override
                    public void run() {
                        linearLayoutSlaves.removeAllViews();
                    }
                });

                /*



                if(processData.equals("No Data") || processData.equals("Server Not Running")) {
                    //No Data
                    TextView SlaveNames;
                    final View hostView = layoutInflaterSlaveDevice.inflate(R.layout.all_pumps_toggle, linearLayoutSlaves, false);
                    //hostView.setId(0);
                    SlaveNames = hostView.findViewById(R.id.TxtPump);
                    SlaveNames.setOnClickListener(AddPump_Valve.this);
                    //SlaveNames.setId(0);
                    SlaveNames.setTextSize(30);
                    SlaveNames.setText("MASTER");
                    SlaveNames.setTypeface(null, Typeface.BOLD);
                    hostView.setId(0);

                    runOnUI(new Runnable() { //used to speak to main thread
                        @Override
                        public void run() {
                            scrollViewAddSensorValve.setVisibility(View.GONE);

                        }
                    });
                    //scrollViewAddSensorValve.setVisibility(View.GONE);

                }else
                */
                //{
                    scrollViewAddSensorValve.setVisibility(View.VISIBLE);
                    String[] SlaveList = processData.split("#");

                    TextView SlaveNames;
                    final View hostView = layoutInflaterSlaveDevice.inflate(R.layout.all_pumps_toggle, linearLayoutSlaves, false);
                    //hostView.setId(0);
                    SlaveNames = hostView.findViewById(R.id.TxtPump);
                    SlaveNames.setOnClickListener(AddPump_Valve.this);
                    //SlaveNames.setId(0);
                    SlaveNames.setTextSize(30);
                    SlaveNames.setText("MASTER");
                    SlaveNames.setTypeface(null, Typeface.BOLD);
                    hostView.setId(0);
                runOnUI(new Runnable() { //used to speak to main thread
                    @Override
                    public void run() {
                        linearLayoutSlaves.addView(hostView);

                    }
                });

                    for (int i = 0; i < SlaveList.length; i++) {
                        if (processData.equals("No Data") || processData.equals("Server Not Running")) {

                        }else {


                            final View view = layoutInflaterSlaveDevice.inflate(R.layout.all_pumps_toggle, linearLayoutSlaves, false);
                            String[] SlaveInfo = SlaveList[i].split(",");
                            view.setId(Integer.parseInt(SlaveInfo[0]));

                            SlaveNames = view.findViewById(R.id.TxtPump);
                            SlaveNames.setOnClickListener(AddPump_Valve.this);

                            //SlaveNames.setId(Integer.parseInt(SlaveInfo[0]));

                            SlaveNames.setTextSize(30);

                            SlaveNames.setText(SlaveInfo[1]);

                            runOnUI(new Runnable() { //used to speak to main thread
                                @Override
                                public void run() {
                                    //linearLayoutSlaves.addView(hostView);
                                    linearLayoutSlaves.addView(view);

                                }
                            });

                        }
                    }
                }

        }).start();
    }

    private void getSlaves(final int id){
        new Thread(new Runnable() { //Running on a new thread
            public void run() {

                String processData = "Data Empty";
                final SocketController socketController = new SocketController(AddPump_Valve.this,"getConnectedSlaves");
                try{
                    processData = socketController.execute().get();

                }catch (ExecutionException e){

                }catch (InterruptedException i){

                }

                final LayoutInflater layoutInflaterSlaveDevice = LayoutInflater.from(AddPump_Valve.this);

                runOnUI(new Runnable() { //used to speak to main thread
                    @Override
                    public void run() {
                        linearLayoutSlaves.removeAllViews();
                    }
                });
/*


                if(processData.equals("No Data") || processData.equals("Server Not Running")) {
                    //No Data
                    TextView SlaveNames;
                    final View hostView = layoutInflaterSlaveDevice.inflate(R.layout.all_pumps_toggle, linearLayoutSlaves, false);
                    //hostView.setId(0);
                    SlaveNames = hostView.findViewById(R.id.TxtPump);
                    SlaveNames.setOnClickListener(AddPump_Valve.this);
                    //SlaveNames.setId(0);
                    SlaveNames.setTextSize(30);
                    SlaveNames.setText("MASTER");
                    SlaveNames.setTypeface(null, Typeface.BOLD);
                    hostView.setId(0);

                    scrollViewAddSensorValve.setVisibility(View.GONE);
                }else{
                    */
                runOnUI(new Runnable() { //used to speak to main thread
                    @Override
                    public void run() {
                        scrollViewAddSensorValve.setVisibility(View.VISIBLE);
                    }
                });

                    String[] SlaveList = processData.split("#");

                    TextView SlaveNames;
                    final View hostView = layoutInflaterSlaveDevice.inflate(R.layout.all_pumps_toggle, linearLayoutSlaves, false);
                    //hostView.setId(0);
                    SlaveNames = hostView.findViewById(R.id.TxtPump);
                    SlaveNames.setOnClickListener(AddPump_Valve.this);
                    //SlaveNames.setId(0);
                    SlaveNames.setTextSize(30);
                    SlaveNames.setText("MASTER");
                    if (id == 0) {
                        SlaveNames.setTypeface(null, Typeface.BOLD);
                    }

                    hostView.setId(0);
                runOnUI(new Runnable() { //used to speak to main thread
                    @Override
                    public void run() {
                        linearLayoutSlaves.addView(hostView);

                    }
                });

                    for (int i = 0; i < SlaveList.length; i++) {
                        if (processData.equals("No Data") || processData.equals("Server Not Running")) {

                        }else {


                        final View view = layoutInflaterSlaveDevice.inflate(R.layout.all_pumps_toggle, linearLayoutSlaves, false);
                            String[] SlaveInfo = SlaveList[i].split(",");
                        view.setId(Integer.parseInt(SlaveInfo[0]));

                        SlaveNames = view.findViewById(R.id.TxtPump);
                        SlaveNames.setOnClickListener(AddPump_Valve.this);
                        if (id == Integer.parseInt(SlaveInfo[0])) {
                            SlaveNames.setTypeface(null, Typeface.BOLD);
                        }
                        //SlaveNames.setId(Integer.parseInt(SlaveInfo[0]));

                        SlaveNames.setTextSize(30);

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

    private int getSelectedController(){
        TextView txtPump;
        int pumpID = -1;
        for (int i = 0; i < linearLayoutSlaves.getChildCount(); i++) {
            View view = linearLayoutSlaves.getChildAt(i);

            txtPump = view.findViewById(R.id.TxtPump);

            if (txtPump.getTypeface() != null) {
                if (txtPump.getTypeface().getStyle() == Typeface.BOLD) {
                    pumpID = view.getId();
                }
            }
        }
        return pumpID;
    }

    private void showCurrentEquipment(){
        linearLayoutAddPump.setVisibility(View.GONE);
        linearLayoutShowEquipment.setVisibility(View.VISIBLE);
    }

    private void showAddEquipment(){
        editEquipment = false;
        radioPump.setVisibility(View.VISIBLE);
        radioValve.setVisibility(View.VISIBLE);
        radioSensor.setVisibility(View.VISIBLE);
        radioValve.setChecked(true);
        //editTextLocation.setText("");

        editTextGPIO.setText("");
        editTextName.setText("");
        EquipmentID = "";
        btnAddEquipment.setText("Save");
        linearLayoutAddPump.setVisibility(View.VISIBLE);
        linearLayoutShowEquipment.setVisibility(View.GONE);
        SelectedEquipment();
    }

    private void populatePumpsValves() {
        editEquipment = false;
        //btnSave.setText("SAVE");

        linearLayoutPumps.removeAllViews();
        linearLayoutValves.removeAllViews();

        new Thread(new Runnable() { //Running on a new thread
            public void run() {
                String[] differentEquipment;
                //Gets the different pumps
                LayoutInflater layoutInflaterPump = LayoutInflater.from(AddPump_Valve.this);//Pump
                Button dialogLoadCancel;
                final View loadingScreen = layoutInflaterPump.inflate(R.layout.loading_screen, linearLayoutPumps, false);
                dialogLoadCancel = loadingScreen.findViewById(R.id.BtnCancel);
                dialogLoadCancel.setVisibility(View.GONE);

                runOnUI(new Runnable() { //used to speak to main thread
                    @Override
                    public void run() {

                        linearLayoutPumps.addView(loadingScreen);
                    }
                });

                String SocketData = "getPumps";
                SocketController socketController = new SocketController(AddPump_Valve.this, SocketData);

                String processData = "";

                try{
                    processData = socketController.execute().get();

                }catch (ExecutionException e){

                }catch (InterruptedException i){

                }


                runOnUI(new Runnable() { //used to speak to main thread
                    @Override
                    public void run() {

                        linearLayoutPumps.removeAllViews();
                    }
                });



                if(processData.equals("Data Empty") || processData.equals("Server Not Running")) {
                    //No Data
                }else{
                    final String finalProcessData = processData;
                    runOnUI(new Runnable() { //used to speak to main thread
                        @Override
                        public void run() {
                            getPumpsForInfo = finalProcessData;
                        }
                    });
                    differentEquipment = processData.split("#");
                    TextView TxtPump;
                    for (int i = 0; i < differentEquipment.length; i++) {

                        String[] buttonInfo = differentEquipment[i].split(",");

                        final View view = layoutInflaterPump.inflate(R.layout.all_pumps_toggle, linearLayoutPumps, false); //_____________________________________________________________Pump
                        view.setId(Integer.parseInt(buttonInfo[0]));
                        TxtPump = view.findViewById(R.id.TxtPump);

                        TxtPump.setId(Integer.parseInt(buttonInfo[0]));
                        TxtPump.setText((buttonInfo[1]));
                        TxtPump.setOnLongClickListener(AddPump_Valve.this);
                        runOnUI(new Runnable() { //used to speak to main thread
                            @Override
                            public void run() {
                                linearLayoutPumps.addView(view);                                                         //_____________________________________________________________Pump
                            }
                        });


                    }
                }


            }
        }).start();


        new Thread(new Runnable() { //Running on a new thread
            public void run() {

                LayoutInflater layoutInflaterValve = LayoutInflater.from(AddPump_Valve.this);//Valve
                Button dialogLoadCancel;
                final View loadingScreen = layoutInflaterValve.inflate(R.layout.loading_screen, linearLayoutValves, false);
                dialogLoadCancel = loadingScreen.findViewById(R.id.BtnCancel);
                dialogLoadCancel.setVisibility(View.GONE);

                runOnUI(new Runnable() { //used to speak to main thread
                    @Override
                    public void run() {

                        linearLayoutValves.addView(loadingScreen);
                    }
                });

                String[] differentEquipment;
                SocketData = "getValves";                                                                      //_____________________________________________________________Valves
                SocketController socketController = new SocketController(AddPump_Valve.this,SocketData);
                String processData = "";
                try{
                  processData = socketController.execute().get();

                }catch (ExecutionException e){

                }catch (InterruptedException i){

                }


                runOnUI(new Runnable() { //used to speak to main thread
                    @Override
                    public void run() {

                        linearLayoutValves.removeAllViews();
                    }
                });


                if(processData.equals("Data Empty") || processData.equals("Server Not Running")) {
                    //No Data
                }else{
                    final String finalProcessData = processData;
                    runOnUI(new Runnable() { //used to speak to main thread
                        @Override
                        public void run() {
                            getZonesForInfo = finalProcessData;
                        }
                    });
                    differentEquipment = processData.split("#");

                    //MaskedEditText editTextDuration;
                    TextView txtZoneName;

                    for (int i = 0; i < differentEquipment.length; i++) {

                        String[] buttonInfo = differentEquipment[i].split(",");

                        final View view = layoutInflaterValve.inflate(R.layout.all_pumps_toggle, linearLayoutValves, false);
                        view.setId(Integer.parseInt(buttonInfo[0]));
                        txtZoneName = view.findViewById(R.id.TxtPump);
                        txtZoneName.setId(Integer.parseInt(buttonInfo[0]));//negative to separate the ID'S
                        txtZoneName.setText((buttonInfo[1]));
                        txtZoneName.setOnLongClickListener(AddPump_Valve.this);
                        runOnUI(new Runnable() { //used to speak to main thread
                            @Override
                            public void run() {
                                linearLayoutValves.addView(view); //_____________________________________________________________Valves
                            }
                        });


                    }
                }




            }
        }).start();


        new Thread(new Runnable() { //Running on a new thread
            public void run() {
                LayoutInflater layoutInflaterSensor = LayoutInflater.from(AddPump_Valve.this);//Sensor

                Button dialogLoadCancel;
                final View loadingScreen = layoutInflaterSensor.inflate(R.layout.loading_screen, linearLayoutSensor, false);
                dialogLoadCancel = loadingScreen.findViewById(R.id.BtnCancel);
                dialogLoadCancel.setVisibility(View.GONE);

                runOnUI(new Runnable() { //used to speak to main thread
                    @Override
                    public void run() {

                        linearLayoutSensor.addView(loadingScreen);
                    }
                });

                String[] differentEquipment;
                SocketData = "getSensors";                                                                      //_____________________________________________________________Sensors
                String processData = "";
                SocketController socketController = new SocketController(AddPump_Valve.this,SocketData);
                try{
                    processData = socketController.execute().get();

                }catch (ExecutionException e){

                }catch (InterruptedException i){

                }

                runOnUI(new Runnable() { //used to speak to main thread
                    @Override
                    public void run() {

                        linearLayoutSensor.removeAllViews();
                    }
                });

                if(processData.equals("Data Empty") || processData.equals("Server Not Running")) {
                    //No Data
                }else{
                    differentEquipment = processData.split("#");

                    //MaskedEditText editTextDuration;
                    TextView txtZoneName;

                    for (int i = 0; i < differentEquipment.length; i++) {

                        String[] buttonInfo = differentEquipment[i].split(",");

                        final View view = layoutInflaterSensor.inflate(R.layout.all_pumps_toggle, linearLayoutSensor, false);
                        view.setId(Integer.parseInt(buttonInfo[0]));
                        txtZoneName = view.findViewById(R.id.TxtPump);
                        txtZoneName.setId(Integer.parseInt(buttonInfo[0])*(-1));
                        txtZoneName.setText((buttonInfo[1]));
                        txtZoneName.setOnLongClickListener(AddPump_Valve.this);
                        runOnUI(new Runnable() { //used to speak to main thread
                            @Override
                            public void run() {
                                linearLayoutSensor.addView(view); //_____________________________________________________________Valves
                            }
                        });


                    }
                }
            }
        }).start();




    }//Adds buttons corresponding to the pumps/ Valves added to the server

    private void EquipmentInfo(String decode){
        TextView txtEquipmentName, txtLocation, txtDescription, txtGPIO;
        Button btnEdit,btnDelete;
        final String[] data = decode.split("#");
        final Dialog dialog = new Dialog(AddPump_Valve.this);
        dialog.setContentView(R.layout.activity_expand_equipment);//popup view is the layout you created
        txtEquipmentName = dialog.findViewById(R.id.TxtEquipmentName);

        txtLocation = dialog.findViewById(R.id.TxtLocation);
        txtDescription = dialog.findViewById(R.id.TxtDescription);

        txtLocation.setVisibility(View.VISIBLE);

        txtGPIO = dialog.findViewById(R.id.TxtGPIO);
        btnEdit = dialog.findViewById(R.id.BtnEdit);
        btnDelete = dialog.findViewById(R.id.BtnDelete);
        txtLocation.setText(data[1]); //Name
        if(data[3].equals("1")){
            txtEquipmentName.setText("Pump");
        }else{
            txtEquipmentName.setText("Zone");
        }
        if(data.length>5){
            txtDescription.setText("Direct Drive Input: " + data[2]);
            txtGPIO.setText("Output: " + data[5]);
            txtDescription.setVisibility(View.VISIBLE);

        }else{
            txtDescription.setVisibility(View.GONE);
            txtGPIO.setText("GPIO: " + data[2]);
        }




        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditEquipment(data);
                dialog.dismiss();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView txtEquipmentName;
                Button btnCancelDelete, btnConfirmDelete;
                dialog.setContentView(R.layout.activity_confirm);
                txtEquipmentName = dialog.findViewById(R.id.TxtEquipmentName);
                btnCancelDelete = dialog.findViewById(R.id.BtnCancelDelete);
                btnConfirmDelete = dialog.findViewById(R.id.BtnConfirmDelete);
                txtEquipmentName.setText("Are you sure you want to delete: " + data[1]);
                btnCancelDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                btnConfirmDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteEquipment(data);
                        dialog.dismiss();
                    }
                });

            }
        });
        dialog.show();
    }

    private void SensorInfo(String decode){
        TextView txtSensorBanner, txtEquipmentName, txtDescription, txtGPIO, txtStartTime, txtEquipmentWithSensor;
        Button btnEdit,btnDelete;
        final String[] data = decode.split("#");
        final Dialog dialog = new Dialog(AddPump_Valve.this);
        dialog.setContentView(R.layout.schedule_info);//popup view is the layout you created
        btnEdit = dialog.findViewById(R.id.BtnEdit);
        btnDelete = dialog.findViewById(R.id.BtnDelete);

        txtEquipmentWithSensor = dialog.findViewById(R.id.textView8);
        txtEquipmentWithSensor.setText("Attached to these Equipment:");
        txtStartTime = dialog.findViewById(R.id.TxtStartTime);
        txtStartTime.setVisibility(View.GONE);
        txtSensorBanner = dialog.findViewById(R.id.textView13);
        txtSensorBanner.setText(data[2]);
        txtEquipmentName = dialog.findViewById(R.id.textView7);
        txtEquipmentName.setText(data[1]);
        txtGPIO = dialog.findViewById(R.id.TxtPumpInfo);
        txtGPIO.setText("Pin IN: " + data[3]);
        LinearLayout linearLayoutScrollInfo = dialog.findViewById(R.id.LinearLayoutScrollInfo);
        final LayoutInflater layoutInflaterSensor = LayoutInflater.from(AddPump_Valve.this);
        for (int i = 5; i < data.length; i++) {
            final View view = layoutInflaterSensor.inflate(R.layout.all_pumps_toggle, linearLayoutScrollInfo, false);
            txtDescription = view.findViewById(R.id.TxtPump);
            txtDescription.setTextSize(20);
            String[] differentPumps = getPumpsForInfo.split("#");
            for (int j = 0; j <differentPumps.length; j++) {
                String[] buttonInfo = differentPumps[j].split(",");
                if(buttonInfo[0].equals(data[i])){
                    txtDescription.setText(buttonInfo[1]);
                }
            }
            String[] differentZones = getZonesForInfo.split("#");
            for (int j = 0; j <differentZones.length; j++) {
                String[] buttonInfo = differentZones[j].split(",");
                if(buttonInfo[0].equals(data[i])){
                    txtDescription.setText(buttonInfo[1]);
                }
            }
            linearLayoutScrollInfo.addView(view);

        }


        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditEquipment(data);
                dialog.dismiss();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView txtEquipmentName;
                Button btnCancelDelete, btnConfirmDelete;
                dialog.setContentView(R.layout.activity_confirm);
                txtEquipmentName = dialog.findViewById(R.id.TxtEquipmentName);
                btnCancelDelete = dialog.findViewById(R.id.BtnCancelDelete);
                btnConfirmDelete = dialog.findViewById(R.id.BtnConfirmDelete);
                txtEquipmentName.setText("Are you sure you want to delete: " + data[1]);
                btnCancelDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                btnConfirmDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteEquipment(data);
                        dialog.dismiss();
                    }
                });

            }
        });
        dialog.show();
    }

    private void deleteEquipment(String[] data) {
    String id = data[0];

        if (data[3].equals("1") || data[3].equals("0")) {
            SocketController socketController = new SocketController(AddPump_Valve.this, id + "$deleteEquipment");
            socketController.execute();
            finish();
        }else{
            SocketController socketController = new SocketController(AddPump_Valve.this, id + "$DELETE_SENSOR");
            socketController.execute();
            finish();
        }
    }

    private void showEditEquipment(String[] data){
        radioValve.setVisibility(View.GONE);
        radioPump.setVisibility(View.GONE);
        radioSensor.setVisibility(View.GONE);
        //int length = data.length;
        if(data[3].equals("1")){
            radioPump.setChecked(true);
            showEquipmentLayout();
            getSlaves(Integer.parseInt(data[4]));

            editEquipment = true;
            EquipmentID = data[0];
            //editTextLocation.setText(data[2]);

            editTextGPIO.setText(data[2]);
            editTextName.setText(data[1]);
            checkBoxDirectOnline.setVisibility(View.VISIBLE);
            if(data.length >5){
                editTextGPIODirectDrive.setText(data[5]);
                txtGPIODirectOnline.setVisibility(View.VISIBLE);
                editTextGPIODirectDrive.setVisibility(View.VISIBLE);

                checkBoxDirectOnline.setChecked(true);
            }else{
                txtGPIODirectOnline.setVisibility(View.GONE);
                editTextGPIODirectDrive.setVisibility(View.GONE);
                checkBoxDirectOnline.setChecked(false);
            }
            btnAddEquipmentToDatabase.setText("Update");
            linearLayoutAddPump.setVisibility(View.VISIBLE);
            linearLayoutShowEquipment.setVisibility(View.GONE);
        }else if (data[3].equals("0")){
            radioValve.setChecked(true);
            showEquipmentLayout();
            getSlaves(Integer.parseInt(data[4]));

            editEquipment = true;
            EquipmentID = data[0];
            //editTextLocation.setText(data[2]);

            editTextGPIO.setText(data[2]);
            editTextName.setText(data[1]);
            btnAddEquipmentToDatabase.setText("Update");
            linearLayoutAddPump.setVisibility(View.VISIBLE);
            linearLayoutShowEquipment.setVisibility(View.GONE);

        }else{

            editEquipment = true;
            EquipmentID = data[0];
            editTextGPIO.setText(data[3]);
            editTextName.setText(data[1]);
            btnAddEquipmentToDatabase.setText("Update");
            linearLayoutAddPump.setVisibility(View.VISIBLE);
            linearLayoutShowEquipment.setVisibility(View.GONE);
            spinnerSensor.setSelection(adapter.getPosition(data[2]));
            if(data[2].equals("Echo Sensor")){
                editTextGPIODirectDrive.setText(data[4]);
                getSlaves(Integer.parseInt(data[5]));
                SelectedEquipment(data, 6);
            }else {
                getSlaves(Integer.parseInt(data[4]));
                SelectedEquipment(data, 5);
            }
                showSensorLayout();
        }

        //editTextLocation.setText(data[2]);

    }

    @Override
    public boolean onLongClick(final View v) {
        final Dialog dialogLoad = new Dialog(AddPump_Valve.this);
        new Thread(new Runnable() { //Running on a new thread
            public void run() {

                runOnUI(new Runnable() { //used to speak to main thread
                    @Override
                    public void run() {

                        final Button dialogLoadCancel;
                        ProgressBar progressBar;
                        TextView connectionInfo;
                        dialogLoad.setContentView(R.layout.loading_screen);//popup view is the layout you created
                        progressBar = dialogLoad.findViewById(R.id.progressBar);
                        dialogLoadCancel = dialogLoad.findViewById(R.id.BtnCancel);
                        dialogLoadCancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialogLoad.dismiss();
                            }
                        });

                        dialogLoad.show();
                    }
                });

                String SocketData = "";
                int id = v.getId();
                int nagative = -1;



                if(id>nagative){
                    SocketData = (id +"$getEquipmentInfo");
                    SocketController socketController = new SocketController(AddPump_Valve.this,SocketData);
                    try{
                        final String processData = socketController.execute().get();
                        runOnUI(new Runnable() { //used to speak to main thread
                            @Override
                            public void run() {
                                dialogLoad.dismiss();
                                EquipmentInfo(processData);
                            }
                        });

                        //displayScheduleInfo(processData);
                    }catch (ExecutionException e){

                    }catch (InterruptedException i){

                    }
                }else{
                    id = id * (-1);
                    SocketData = id +"$getSensorInfo";

                    SocketController socketController = new SocketController(AddPump_Valve.this,SocketData);
                    try{
                        final String processData = socketController.execute().get();

                        runOnUI(new Runnable() { //used to speak to main thread
                            @Override
                            public void run() {
                                dialogLoad.dismiss();
                                SensorInfo(processData);
                            }
                        });


                        //displayScheduleInfo(processData);
                    }catch (ExecutionException e){

                    }catch (InterruptedException i){

                    }
                }




            }
        }).start();






        return true;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.RadioPump:
                checkBoxDirectOnline.setVisibility(View.VISIBLE);
                txtGPIODirectOnline.setText("DirectOnline Power Off");
                //txtGPIODirectOnline.setVisibility(View.VISIBLE);
                showEquipmentLayout();
                break;
            case R.id.RadioValve:
                txtGPIODirectOnline.setVisibility(View.GONE);
                checkBoxDirectOnline.setVisibility(View.GONE);
                showEquipmentLayout();
                break;
            case R.id.RadioSensor:
                txtGPIODirectOnline.setVisibility(View.GONE);
                checkBoxDirectOnline.setVisibility(View.GONE);
                showSensorLayout();
                break;
            case R.id.TxtPump:
                TextView txtPump;
                for (int i = 0; i < linearLayoutSlaves.getChildCount(); i++) {
                    View view = linearLayoutSlaves.getChildAt(i);
                    //int test = view.getId();
                    txtPump = view.findViewById(R.id.TxtPump);
                    txtPump.setTypeface(null, Typeface.NORMAL);
                }
                txtPump = v.findViewById(v.getId());
                txtPump.setTypeface(null, Typeface.BOLD);

                break;
            default:
                Button btnPump, btnPump2, btnPump3, btnValve, btnValve2, btnValve3;
                int arraySize;
                for (int i = 0; i < pumpIDs.size(); i++) {
                    if(v.getId() == Integer.parseInt(pumpIDs.get(i))){
                        i = pumpIDs.size();
                        arraySize = -1;
                        boolean match = false;
                        for (int k = 0; k < selectedEquipment.size(); k++){
                            if (v.getId() ==selectedEquipment.get(k)) {
                                btnPump = v.findViewById(v.getId());
                                btnPump.setBackgroundResource(android.R.drawable.btn_default);
                                selectedEquipment.remove(k);
                                match = true;
                            }
                        }
                        if (match==false){
                            selectedEquipment.add(v.getId());
                            btnPump = v.findViewById(v.getId());
                            btnPump.setBackgroundColor(Color.CYAN);
                        }

                        for (int k = 0; k < linearLayoutScrollManualPump.getChildCount(); k++) {
                            arraySize++;
                            View view = linearLayoutScrollManualPump.getChildAt(k);
                            btnPump = view.findViewById(Integer.parseInt(pumpIDs.get(arraySize)));
                            match = false;
                            for (int j = 0; j < selectedEquipment.size(); j++){
                                if(Integer.parseInt(pumpIDs.get(arraySize)) == selectedEquipment.get(j)) {
                                    match = true;

                                }
                            }
                            if(match == false){
                                btnPump.getBackground().clearColorFilter();
                            }
                            if((arraySize+1) < pumpIDs.size()){
                                arraySize++;
                                btnPump2 = view.findViewById(Integer.parseInt(pumpIDs.get(arraySize)));

                                match = false;
                                for (int j = 0; j < selectedEquipment.size(); j++){
                                    if(Integer.parseInt(pumpIDs.get(arraySize)) == selectedEquipment.get(j)) {
                                        match = true;

                                    }
                                }
                                if(match == false){
                                    btnPump2.getBackground().clearColorFilter();
                                }
                                if((arraySize+1) < pumpIDs.size()){
                                    arraySize++;
                                    btnPump3 = view.findViewById(Integer.parseInt(pumpIDs.get(arraySize)));
                                    match = false;
                                    for (int j = 0; j < selectedEquipment.size(); j++){
                                        //int test = Integer.parseInt(pumpIDs.get(arraySize));
                                        //int tes = selectedEquipment.get(j);
                                        if(Integer.parseInt(pumpIDs.get(arraySize)) == selectedEquipment.get(j)) {
                                            match = true;
                                        }
                                    }
                                    if(match == false){
                                        btnPump3.getBackground().clearColorFilter();
                                    }
                                }
                            }

                        }


                    }
                }

                for (int i = 0; i < zoneIDs.size(); i++) {
                    if(v.getId() == Integer.parseInt(zoneIDs.get(i))){
                        i = zoneIDs.size();
                        arraySize = -1;
                        boolean match = false;
                        for (int k = 0; k < selectedEquipment.size(); k++){
                            if (v.getId() ==selectedEquipment.get(k)) {
                                btnValve = v.findViewById(v.getId());
                                btnValve.setBackgroundResource(android.R.drawable.btn_default);
                                selectedEquipment.remove(k);
                                match = true;
                            }
                        }
                        if (match==false){
                            selectedEquipment.add(v.getId());
                            btnValve = v.findViewById(v.getId());
                            btnValve.setBackgroundColor(Color.CYAN);
                        }

                        for (int k = 0; k < linearLayoutScrollManualZone.getChildCount(); k++) {
                            arraySize++;
                            View view = linearLayoutScrollManualZone.getChildAt(k);
                            btnValve = view.findViewById(Integer.parseInt(zoneIDs.get(arraySize)));
                            match = false;
                            for (int j = 0; j < selectedEquipment.size(); j++){
                                if(Integer.parseInt(zoneIDs.get(arraySize)) == selectedEquipment.get(j)) {
                                    match = true;

                                }
                            }
                            if(match == false){
                                btnValve.getBackground().clearColorFilter();
                            }

                            if((arraySize+1) < zoneIDs.size()){
                                arraySize++;
                                btnValve2 = view.findViewById(Integer.parseInt(zoneIDs.get(arraySize)));

                                match = false;
                                for (int j = 0; j < selectedEquipment.size(); j++){
                                    if(Integer.parseInt(zoneIDs.get(arraySize)) == selectedEquipment.get(j)) {
                                        match = true;

                                    }
                                }
                                if(match == false){
                                    btnValve2.getBackground().clearColorFilter();
                                }


                                if((arraySize+1) < zoneIDs.size()){
                                    arraySize++;
                                    btnValve3 = view.findViewById(Integer.parseInt(zoneIDs.get(arraySize)));

                                    match = false;
                                    for (int j = 0; j < selectedEquipment.size(); j++){
                                        //int test = Integer.parseInt(zoneIDs.get(arraySize));
                                        //int tes = selectedEquipment.get(j);
                                        if(Integer.parseInt(zoneIDs.get(arraySize)) == selectedEquipment.get(j)) {
                                            match = true;

                                        }
                                    }
                                    if(match == false){
                                        btnValve3.getBackground().clearColorFilter();
                                    }


                                }
                            }

                        }
                    }
                }





        }
    }

    private void SelectedEquipment(){
        linearLayoutScrollManualPump = findViewById(R.id.LinearLayoutScrollManualPump);
        linearLayoutScrollManualZone = findViewById(R.id.LinearLayoutScrollManualZone);
        linearLayoutScrollManualPump.removeAllViews();
        linearLayoutScrollManualZone.removeAllViews();


        new Thread(new Runnable() { //Running on a new thread
            public void run() {
                int OLDPumpCount = 0;
                int OLDZoneCount = 0;




                        Button btnPump, btnPump2, btnPump3, btnValve, btnValve2, btnValve3;

                String[] differentButtons;
                        SocketController socketController = new SocketController(AddPump_Valve.this, "getPumps");

                        String processData = "";

                        try {
                            processData = socketController.execute().get();
                        } catch (ExecutionException e) {

                        } catch (InterruptedException i) {

                        }
                        LayoutInflater layoutInflaterScrollManualPump = LayoutInflater.from(AddPump_Valve.this);//Used to inflate the Zones and pumps
                        LayoutInflater layoutInflaterScrollManualZone = LayoutInflater.from(AddPump_Valve.this);


                        if (!processData.equals("Data Empty") && !processData.equals("Server Not Running")) {
                            //No Data
                            differentButtons = processData.split("#");

                            if (differentButtons.length != OLDPumpCount) {
                                pumpIDs.clear();
                                runOnUI(new Runnable() { //used to speak to main thread
                                    @Override
                                    public void run() {
                                        linearLayoutScrollManualPump.removeAllViews();
                                    }
                                });
                                OLDPumpCount = differentButtons.length;
                                for (int i = 0; i < differentButtons.length; i++) {

                                    String[] buttonInfo = differentButtons[i].split(",");

                                    final View v = layoutInflaterScrollManualPump.inflate(R.layout.all_pumps, linearLayoutScrollManualPump, false); //_____________________________________________________________Pump
                                    btnPump = v.findViewById(R.id.BtnPump);
                                    btnPump2 = v.findViewById(R.id.BtnPump2);
                                    btnPump3 = v.findViewById(R.id.BtnPump3);

                                    btnPump.setBackgroundResource(android.R.drawable.btn_default);
                                    btnPump2.setBackgroundResource(android.R.drawable.btn_default);
                                    btnPump3.setBackgroundResource(android.R.drawable.btn_default);
                                    btnPump2.setVisibility(View.GONE);
                                    btnPump3.setVisibility(View.GONE);
                                    pumpIDs.add(buttonInfo[0]);
                                    btnPump.setId(Integer.parseInt(buttonInfo[0]));

                                    btnPump.setText(buttonInfo[1]);
                                    btnPump.setOnClickListener(AddPump_Valve.this);
                                    v.setId(i);
                                    if ((i + 1) < differentButtons.length) {
                                        i++;
                                        String[] buttonInfo2 = differentButtons[i].split(","); //stores the information for the second Zone
                                        btnPump2.setId(Integer.parseInt(buttonInfo2[0]));


                                        btnPump2.setText(buttonInfo2[1]);
                                        btnPump2.setVisibility(View.VISIBLE);

                                        final Button finalBtnPump2 = btnPump2;

                                        runOnUI(new Runnable() { //used to speak to main thread
                                            @Override
                                            public void run() {
                                                finalBtnPump2.setOnClickListener(AddPump_Valve.this);
                                            }
                                        });


                                        pumpIDs.add(buttonInfo2[0]);

                                        if ((i + 1) < differentButtons.length) {
                                            i++;
                                            String[] buttonInfo3 = differentButtons[i].split(","); //stores the information for the second Zone
                                            btnPump3.setId(Integer.parseInt(buttonInfo3[0]));


                                            btnPump3.setText(buttonInfo3[1]);
                                            btnPump3.setVisibility(View.VISIBLE);
                                            btnPump3.setOnClickListener(AddPump_Valve.this);
                                            pumpIDs.add(buttonInfo3[0]);
                                        }

                                    }

                                    final Button finalBtnPump = btnPump;
                                    final Button finalBtnPump2 = btnPump2;
                                    final Button finalBtnPump3 = btnPump3;

                                    runOnUI(new Runnable() { //used to speak to main thread
                                        @Override
                                        public void run() {

                                            linearLayoutScrollManualPump.addView(v);
                                        }
                                    });

                                    //_____________________________________________________________Pump
                                }
                            }
                        }

                                    //_____________________________________________________________Valves
                        socketController = new SocketController(AddPump_Valve.this, "getValves");
                        try {
                            processData = socketController.execute().get();
                        } catch (ExecutionException e) {

                        } catch (InterruptedException i) {

                        }
                        if (processData.equals("Data Empty") || processData.equals("Server Not Running")) {
                            //No Data
                        } else {
                            differentButtons = processData.split("#");
                            if (OLDZoneCount != differentButtons.length) {
                                runOnUI(new Runnable() { //used to speak to main thread
                                    @Override
                                    public void run() {
                                        linearLayoutScrollManualZone.removeAllViews();
                                    }
                                });
                                OLDZoneCount = differentButtons.length;
                                zoneIDs.clear();

                                for (int i = 0; i < differentButtons.length; i++) {

                                    String[] buttonInfo = differentButtons[i].split(",");

                                    final View v = layoutInflaterScrollManualZone.inflate(R.layout.all_valves, linearLayoutScrollManualZone, false);

                                    btnValve = v.findViewById(R.id.BtnValve);
                                    btnValve2 = v.findViewById(R.id.BtnValve2);
                                    btnValve3 = v.findViewById(R.id.BtnValve3);

                                    btnValve.setBackgroundResource(android.R.drawable.btn_default);
                                    btnValve2.setBackgroundResource(android.R.drawable.btn_default);
                                    btnValve3.setBackgroundResource(android.R.drawable.btn_default);
                                    btnValve2.setVisibility(View.GONE);
                                    btnValve3.setVisibility(View.GONE);
                                    v.setId(i);

                                    btnValve.setId(Integer.parseInt(buttonInfo[0]));
                                    btnValve.setText(buttonInfo[1]);
                                    btnValve.setOnClickListener(AddPump_Valve.this);
                                    zoneIDs.add(buttonInfo[0]);
                                    if ((i + 1) < differentButtons.length) {
                                        i++;
                                        String[] buttonInfo2 = differentButtons[i].split(","); //stores the information for the second Zone
                                        btnValve2.setId(Integer.parseInt(buttonInfo2[0]));

                                        btnValve2.setText(buttonInfo2[1]);
                                        btnValve2.setVisibility(View.VISIBLE);
                                        btnValve2.setOnClickListener(AddPump_Valve.this);
                                        zoneIDs.add(buttonInfo2[0]);
                                        if ((i + 1) < differentButtons.length) {
                                            i++;
                                            String[] buttonInfo3 = differentButtons[i].split(","); //stores the information for the second Zone
                                            btnValve3.setId(Integer.parseInt(buttonInfo3[0]));


                                            btnValve3.setText(buttonInfo3[1]);
                                            btnValve3.setVisibility(View.VISIBLE);
                                            btnValve3.setOnClickListener(AddPump_Valve.this);
                                            zoneIDs.add(buttonInfo3[0]);
                                        }
                                    }

                                    //final boolean finalManualSchedule1 = manualSchedule;
                                    final Button finalBtnValve = btnValve;
                                    final Button finalBtnValve2 = btnValve2;
                                    final Button finalBtnValve3 = btnValve3;
                                    runOnUI(new Runnable() { //used to speak to main thread
                                        @Override
                                        public void run() {

                                            linearLayoutScrollManualZone.addView(v);
                                        }
                                    });
                                                                                          //_____________________________________________________________Valves
                                }
                            }
                        }
                    }
        }).start();






    }

    private void SelectedEquipment(String[] data, int StartPosition){
        selectedEquipment.clear();
        for (int i = StartPosition; i < data.length; i++) {
            selectedEquipment.add(Integer.parseInt(data[i]));
        }
        linearLayoutScrollManualPump = findViewById(R.id.LinearLayoutScrollManualPump);
        linearLayoutScrollManualZone = findViewById(R.id.LinearLayoutScrollManualZone);
        linearLayoutScrollManualPump.removeAllViews();
        linearLayoutScrollManualZone.removeAllViews();


        new Thread(new Runnable() { //Running on a new thread
            public void run() {
                int OLDPumpCount = 0;
                int OLDZoneCount = 0;




                Button btnPump, btnPump2, btnPump3, btnValve, btnValve2, btnValve3;

                String[] differentButtons;
                SocketController socketController = new SocketController(AddPump_Valve.this, "getPumps");

                String processData = "";

                try {
                    processData = socketController.execute().get();
                } catch (ExecutionException e) {

                } catch (InterruptedException i) {

                }
                LayoutInflater layoutInflaterScrollManualPump = LayoutInflater.from(AddPump_Valve.this);//Used to inflate the Zones and pumps
                LayoutInflater layoutInflaterScrollManualZone = LayoutInflater.from(AddPump_Valve.this);


                if (!processData.equals("Data Empty") && !processData.equals("Server Not Running")) {
                    //No Data
                    differentButtons = processData.split("#");

                    if (differentButtons.length != OLDPumpCount) {
                        pumpIDs.clear();
                        runOnUI(new Runnable() { //used to speak to main thread
                            @Override
                            public void run() {
                                linearLayoutScrollManualPump.removeAllViews();
                            }
                        });
                        OLDPumpCount = differentButtons.length;
                        for (int i = 0; i < differentButtons.length; i++) {

                            String[] buttonInfo = differentButtons[i].split(",");

                            final View v = layoutInflaterScrollManualPump.inflate(R.layout.all_pumps, linearLayoutScrollManualPump, false); //_____________________________________________________________Pump
                            btnPump = v.findViewById(R.id.BtnPump);
                            btnPump2 = v.findViewById(R.id.BtnPump2);
                            btnPump3 = v.findViewById(R.id.BtnPump3);

                            btnPump.setBackgroundResource(android.R.drawable.btn_default);
                            btnPump2.setBackgroundResource(android.R.drawable.btn_default);
                            btnPump3.setBackgroundResource(android.R.drawable.btn_default);
                            btnPump2.setVisibility(View.GONE);
                            btnPump3.setVisibility(View.GONE);
                            pumpIDs.add(buttonInfo[0]);
                            btnPump.setId(Integer.parseInt(buttonInfo[0]));

                            for (int j = 0; j < selectedEquipment.size(); j++) {
                                if(Integer.parseInt(buttonInfo[0]) == selectedEquipment.get(j))
                                btnPump.setBackgroundColor(Color.CYAN);
                            }

                            btnPump.setText(buttonInfo[1]);
                            btnPump.setOnClickListener(AddPump_Valve.this);
                            v.setId(i);
                            if ((i + 1) < differentButtons.length) {
                                i++;
                                String[] buttonInfo2 = differentButtons[i].split(","); //stores the information for the second Zone
                                btnPump2.setId(Integer.parseInt(buttonInfo2[0]));

                                for (int j = 0; j < selectedEquipment.size(); j++) {
                                    if(Integer.parseInt(buttonInfo2[0]) == selectedEquipment.get(j))
                                        btnPump2.setBackgroundColor(Color.CYAN);
                                }

                                btnPump2.setText(buttonInfo2[1]);
                                btnPump2.setVisibility(View.VISIBLE);

                                final Button finalBtnPump2 = btnPump2;

                                runOnUI(new Runnable() { //used to speak to main thread
                                    @Override
                                    public void run() {
                                        finalBtnPump2.setOnClickListener(AddPump_Valve.this);
                                    }
                                });


                                pumpIDs.add(buttonInfo2[0]);

                                if ((i + 1) < differentButtons.length) {
                                    i++;
                                    String[] buttonInfo3 = differentButtons[i].split(","); //stores the information for the second Zone
                                    btnPump3.setId(Integer.parseInt(buttonInfo3[0]));

                                    for (int j = 0; j < selectedEquipment.size(); j++) {
                                        if(Integer.parseInt(buttonInfo3[0]) == selectedEquipment.get(j))
                                            btnPump3.setBackgroundColor(Color.CYAN);
                                    }

                                    btnPump3.setText(buttonInfo3[1]);
                                    btnPump3.setVisibility(View.VISIBLE);
                                    btnPump3.setOnClickListener(AddPump_Valve.this);
                                    pumpIDs.add(buttonInfo3[0]);
                                }

                            }

                            final Button finalBtnPump = btnPump;
                            final Button finalBtnPump2 = btnPump2;
                            final Button finalBtnPump3 = btnPump3;

                            runOnUI(new Runnable() { //used to speak to main thread
                                @Override
                                public void run() {

                                    linearLayoutScrollManualPump.addView(v);
                                }
                            });

                            //_____________________________________________________________Pump
                        }
                    }
                }

                //_____________________________________________________________Valves
                socketController = new SocketController(AddPump_Valve.this, "getValves");
                try {
                    processData = socketController.execute().get();
                } catch (ExecutionException e) {

                } catch (InterruptedException i) {

                }
                if (processData.equals("Data Empty") || processData.equals("Server Not Running")) {
                    //No Data
                } else {
                    differentButtons = processData.split("#");
                    if (OLDZoneCount != differentButtons.length) {
                        runOnUI(new Runnable() { //used to speak to main thread
                            @Override
                            public void run() {
                                linearLayoutScrollManualZone.removeAllViews();
                            }
                        });
                        OLDZoneCount = differentButtons.length;
                        zoneIDs.clear();

                        for (int i = 0; i < differentButtons.length; i++) {

                            String[] buttonInfo = differentButtons[i].split(",");

                            final View v = layoutInflaterScrollManualZone.inflate(R.layout.all_valves, linearLayoutScrollManualZone, false);

                            btnValve = v.findViewById(R.id.BtnValve);
                            btnValve2 = v.findViewById(R.id.BtnValve2);
                            btnValve3 = v.findViewById(R.id.BtnValve3);

                            btnValve.setBackgroundResource(android.R.drawable.btn_default);
                            btnValve2.setBackgroundResource(android.R.drawable.btn_default);
                            btnValve3.setBackgroundResource(android.R.drawable.btn_default);
                            btnValve2.setVisibility(View.GONE);
                            btnValve3.setVisibility(View.GONE);
                            v.setId(i);

                            btnValve.setId(Integer.parseInt(buttonInfo[0]));

                            for (int j = 0; j < selectedEquipment.size(); j++) {
                                if(Integer.parseInt(buttonInfo[0]) == selectedEquipment.get(j))
                                    btnValve.setBackgroundColor(Color.CYAN);
                            }

                            btnValve.setText(buttonInfo[1]);
                            btnValve.setOnClickListener(AddPump_Valve.this);
                            zoneIDs.add(buttonInfo[0]);
                            if ((i + 1) < differentButtons.length) {
                                i++;
                                String[] buttonInfo2 = differentButtons[i].split(","); //stores the information for the second Zone
                                btnValve2.setId(Integer.parseInt(buttonInfo2[0]));

                                for (int j = 0; j < selectedEquipment.size(); j++) {
                                    if(Integer.parseInt(buttonInfo2[0]) == selectedEquipment.get(j))
                                        btnValve2.setBackgroundColor(Color.CYAN);
                                }

                                btnValve2.setText(buttonInfo2[1]);
                                btnValve2.setVisibility(View.VISIBLE);
                                btnValve2.setOnClickListener(AddPump_Valve.this);
                                zoneIDs.add(buttonInfo2[0]);
                                if ((i + 1) < differentButtons.length) {
                                    i++;
                                    String[] buttonInfo3 = differentButtons[i].split(","); //stores the information for the second Zone
                                    btnValve3.setId(Integer.parseInt(buttonInfo3[0]));

                                    for (int j = 0; j < selectedEquipment.size(); j++) {
                                        if(Integer.parseInt(buttonInfo3[0]) == selectedEquipment.get(j))
                                            btnValve3.setBackgroundColor(Color.CYAN);
                                    }

                                    btnValve3.setText(buttonInfo3[1]);
                                    btnValve3.setVisibility(View.VISIBLE);
                                    btnValve3.setOnClickListener(AddPump_Valve.this);
                                    zoneIDs.add(buttonInfo3[0]);
                                }
                            }

                            //final boolean finalManualSchedule1 = manualSchedule;
                            final Button finalBtnValve = btnValve;
                            final Button finalBtnValve2 = btnValve2;
                            final Button finalBtnValve3 = btnValve3;
                            runOnUI(new Runnable() { //used to speak to main thread
                                @Override
                                public void run() {

                                    linearLayoutScrollManualZone.addView(v);
                                }
                            });
                            //_____________________________________________________________Valves
                        }
                    }
                }
            }
        }).start();






    }

    private void showSensorLayout(){
        linearLayoutPumpZone.setVisibility(View.VISIBLE);
        linearLayoutSensorType.setVisibility(View.VISIBLE);
    }

    private void showEquipmentLayout(){
        linearLayoutPumpZone.setVisibility(View.GONE);
        linearLayoutSensorType.setVisibility(View.GONE);
    }



    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
// Another interface callback
    }
}


