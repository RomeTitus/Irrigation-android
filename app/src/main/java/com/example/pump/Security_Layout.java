package com.example.pump;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextClock;
import android.widget.TextView;

import com.vicmikhailau.maskededittext.MaskedEditText;

import java.util.concurrent.ExecutionException;

public class Security_Layout extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener, CompoundButton.OnCheckedChangeListener {


    public static Handler UIHandler = new Handler();

    public static void runOnUI(Runnable runnable) { //Runs code to invoke the main thread
        UIHandler.post(runnable);
    }

    LinearLayout LinearLayoutShowHomePage, LinearLayoutAddAlarm, LinearLayoutAlarmSchedule, ScrollViewAlarmSensor;
    Button BtnAlarmSchedule, BtnCancel, BtnSave, BtnBack;
    TextView txtSensorList;
    EditText EdtTextName;
    MaskedEditText EndTimeAlarm, StartTimeAlarm;
    Boolean isEdit = false;
    int ScheduleID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security__layout);
        LinearLayoutShowHomePage = findViewById(R.id.LinearLayoutShowHomePage);
        LinearLayoutAddAlarm = findViewById(R.id.LinearLayoutAddAlarm);
        LinearLayoutAlarmSchedule = findViewById(R.id.LinearLayoutAlarmSchedule);
        ScrollViewAlarmSensor = findViewById(R.id.ScrollViewAlarmSensor);

        EdtTextName = findViewById(R.id.EdtTextName);

        txtSensorList = findViewById(R.id.txtSensorList);

        EndTimeAlarm = findViewById(R.id.EndTimeAlarm);
        StartTimeAlarm = findViewById(R.id.StartTimeAlarm);

        BtnAlarmSchedule = findViewById(R.id.BtnAlarmSchedule);
        BtnCancel = findViewById(R.id.BtnCancel);
        BtnSave = findViewById(R.id.BtnSave);
        BtnBack = findViewById(R.id.BtnBack);

        ShowHomePage();


        //--------------------------------------------------------------------------------------------------
        BtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //--------------------------------------------------------------------------------------------------
        //Displays the add schedule page when pressed
        //--------------------------------------------------------------------------------------------------
        BtnAlarmSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isEdit = false;
                ShowAddAlarmPage();
            }
        });
        //--------------------------------------------------------------------------------------------------

        //Displays the home Alarm page when pressed
        //--------------------------------------------------------------------------------------------------
        BtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowHomePage();
            }
        });
        //--------------------------------------------------------------------------------------------------

        //Saved the data to the raspberry pi
        //--------------------------------------------------------------------------------------------------
        BtnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //checks if anything is left out

                if((getSelectedSensors().equals("")) || (EdtTextName.getText().toString().equals(""))||(StartTimeAlarm.getText().toString().length() <= 3) || (EndTimeAlarm.getText().toString().length() <= 3)){
                    if(StartTimeAlarm.getText().toString().length() <= 3){
                        StartTimeAlarm.setTextColor(Color.parseColor("#FF0000"));

                    }else{
                        StartTimeAlarm.setTextColor(Color.parseColor("#FFFFFF"));
                    }
                    if(EndTimeAlarm.getText().toString().length() <= 3){
                        EndTimeAlarm.setTextColor(Color.parseColor("#FF0000"));

                    }else{
                        EndTimeAlarm.setTextColor(Color.parseColor("#FFFFFF"));
                    }
                    if(!EdtTextName.getText().toString().equals("")){
                        EdtTextName.setTextColor(Color.parseColor("#FFFFFF"));
                    }else{
                        EdtTextName.setTextColor(Color.parseColor("#FFFFFF"));
                    }
                    if(getSelectedSensors().equals("")){
                        txtSensorList.setTextColor(Color.parseColor("#FFFFFF"));
                    }else{
                        txtSensorList.setTextColor(Color.parseColor("#FFFFFF"));
                    }
                }else{
                    String AlarmScheduleData;
                    if(isEdit == true){
                        AlarmScheduleData = "" + ScheduleID;
                        AlarmScheduleData = AlarmScheduleData + "," + EdtTextName.getText().toString();
                        AlarmScheduleData = AlarmScheduleData + "," + StartTimeAlarm.getText().toString() + "," + EndTimeAlarm.getText().toString();
                        AlarmScheduleData = AlarmScheduleData + "," + getSelectedSensors();

                        AlarmScheduleData = AlarmScheduleData + "$EditSecuritySchedule";
                        SocketController socketController = new SocketController(Security_Layout.this, AlarmScheduleData);
                        socketController.execute();
                        finish();
                        Intent schedule = new Intent(Security_Layout.this,Security_Layout.class);
                        Security_Layout.this.startActivity(schedule);
                    }else{
                        AlarmScheduleData = EdtTextName.getText().toString();
                        AlarmScheduleData = AlarmScheduleData + "," + StartTimeAlarm.getText().toString() + "," + EndTimeAlarm.getText().toString();
                        AlarmScheduleData = AlarmScheduleData + "," + getSelectedSensors();

                        AlarmScheduleData = AlarmScheduleData + "$CreateSecuritySchedule";
                        SocketController socketController = new SocketController(Security_Layout.this, AlarmScheduleData);
                        socketController.execute();
                        finish();
                        Intent schedule = new Intent(Security_Layout.this,Security_Layout.class);
                        Security_Layout.this.startActivity(schedule);
                    }

                }




            }
        });

        //--------------------------------------------------------------------------------------------------

    }

    private void ShowHomePage(){
        LinearLayoutAddAlarm.setVisibility(View.GONE);
        LinearLayoutShowHomePage.setVisibility(View.VISIBLE);
        getAlarmSchedule();
    }

    private void ShowAddAlarmPage(){
        LinearLayoutAddAlarm.setVisibility(View.VISIBLE);
        LinearLayoutShowHomePage.setVisibility(View.GONE);

        getAlarmSensors();
    }

    private void getAlarmSensors(){
        new Thread(new Runnable() { //Running on a new thread
            public void run() {

                String processData = "";
                String SocketData = "";
                runOnUI(new Runnable() { //used to speak to main thread
                    @Override
                    public void run() {
                        ScrollViewAlarmSensor.removeAllViews();
                    }
                });

                SocketData = "getAlarmSensors";
                String[] differentAlarmSensor;
                SocketController socketController = new SocketController(Security_Layout.this,SocketData);

                try{
                    processData = socketController.execute().get();

                }catch (ExecutionException e){

                }catch (InterruptedException i){

                }

                LayoutInflater layoutInflaterSchedule = LayoutInflater.from(Security_Layout.this);//Pump

                if(processData.equals("Data Empty") || processData.equals("Server Not Running")) {
                    //No Data
                }else {
                    differentAlarmSensor = processData.split("#");

                    Switch switchAlarmSensor;
                    TextView name, type;
                    for (int i = 0; i < differentAlarmSensor.length; i++) {

                        final String[] buttonInfo = differentAlarmSensor[i].split(",");

                        View v = layoutInflaterSchedule.inflate(R.layout.sensor_alarm_display, ScrollViewAlarmSensor, false);
                        switchAlarmSensor = v.findViewById(R.id.sensor_switch_Enable);
                        type = v.findViewById(R.id.TxtSensorType);
                        name = v.findViewById(R.id.TxtSensorName);

                        name.setText(buttonInfo[1]);
                        type.setText(buttonInfo[2]);
                        v.setId(Integer.parseInt(buttonInfo[0]));


                        final View add = v;
                        runOnUI(new Runnable() { //used to speak to main thread
                            @Override
                            public void run() {
                                ScrollViewAlarmSensor.addView(add);
                            }
                        });

                    }
                }
            }
        }).start();
    }

    private void getAlarmSensors(final String[] editData){
        isEdit = true;
        LinearLayoutAddAlarm.setVisibility(View.VISIBLE);
        LinearLayoutShowHomePage.setVisibility(View.GONE);
        EdtTextName.setText(editData[1]);
        StartTimeAlarm.setText(editData[2]);
        EndTimeAlarm.setText(editData[3]);
        BtnSave.setText("Update");

        new Thread(new Runnable() { //Running on a new thread
            public void run() {

                String processData = "";
                String SocketData = "";
                runOnUI(new Runnable() { //used to speak to main thread
                    @Override
                    public void run() {
                        ScrollViewAlarmSensor.removeAllViews();
                    }
                });

                SocketData = "getAlarmSensors";
                String[] differentAlarmSensor;
                SocketController socketController = new SocketController(Security_Layout.this,SocketData);

                try{
                    processData = socketController.execute().get();

                }catch (ExecutionException e){

                }catch (InterruptedException i){

                }

                LayoutInflater layoutInflaterSchedule = LayoutInflater.from(Security_Layout.this);//Pump

                if(processData.equals("Data Empty") || processData.equals("Server Not Running")) {
                    //No Data
                }else {
                    differentAlarmSensor = processData.split("#");

                    Switch switchAlarmSensor;
                    TextView name, type;
                    for (int i = 0; i < differentAlarmSensor.length; i++) {

                        final String[] buttonInfo = differentAlarmSensor[i].split(",");

                        View v = layoutInflaterSchedule.inflate(R.layout.sensor_alarm_display, ScrollViewAlarmSensor, false);
                        switchAlarmSensor = v.findViewById(R.id.sensor_switch_Enable);
                        type = v.findViewById(R.id.TxtSensorType);
                        name = v.findViewById(R.id.TxtSensorName);

                        name.setText(buttonInfo[1]);
                        type.setText(buttonInfo[2]);
                        v.setId(Integer.parseInt(buttonInfo[0]));



                        final View add = v;
                        final Switch finalSwitchAlarmSensor = switchAlarmSensor;
                        runOnUI(new Runnable() { //used to speak to main thread
                            @Override
                            public void run() {
                                for (int j = 4; j < editData.length; j++) {
                                    if(editData[j].equals(buttonInfo[0])){
                                        finalSwitchAlarmSensor.setChecked(true);
                                        j = editData.length;//Stops the loop because it already found a match
                                    }else{
                                        finalSwitchAlarmSensor.setChecked(false);
                                    }
                                    j = j +2;
                                }
                                ScrollViewAlarmSensor.addView(add);
                            }
                        });

                    }
                }
            }
        }).start();
    }

    private void getAlarmSchedule() {
        new Thread(new Runnable() { //Running on a new thread
            public void run() {
                LayoutInflater layoutInflaterAlarmSchedule = LayoutInflater.from(Security_Layout.this);
                String processData = "";
                String SocketData = "";



                Button dialogLoadCancel;
                final View loadingScreen = layoutInflaterAlarmSchedule.inflate(R.layout.loading_screen, LinearLayoutAlarmSchedule, false);
                dialogLoadCancel = loadingScreen.findViewById(R.id.BtnCancel);
                dialogLoadCancel.setVisibility(View.GONE);

                runOnUI(new Runnable() { //used to speak to main thread
                    @Override
                    public void run() {

                        LinearLayoutAlarmSchedule.addView(loadingScreen);
                    }
                });

                SocketData = "getAlarmSchedule";
                String[] differentSchedule;
                SocketController socketController = new SocketController(Security_Layout.this,SocketData);

                try{
                    processData = socketController.execute().get();

                }catch (ExecutionException e){

                }catch (InterruptedException i){

                }
                runOnUI(new Runnable() { //used to speak to main thread
                    @Override
                    public void run() {
                        LinearLayoutAlarmSchedule.removeAllViews();
                    }
                });


                if(processData.equals("Data Empty") || processData.equals("Server Not Running")) {
                    //No Data
                }else {
                    differentSchedule = processData.split("#");


                    Switch switchSchedule;
                    TextView name, times;
                    for (int i = 0; i < differentSchedule.length; i++) {

                        final String[] buttonInfo = differentSchedule[i].split(",");

                        View view = layoutInflaterAlarmSchedule.inflate(R.layout.sensor_alarm_display, LinearLayoutAlarmSchedule, false);
                        switchSchedule = view.findViewById(R.id.sensor_switch_Enable);

                        times = view.findViewById(R.id.TxtSensorType);
                        name = view.findViewById(R.id.TxtSensorName);

                        //name.setId(Integer.parseInt(buttonInfo[0]));
                        switchSchedule.setId(Integer.parseInt(buttonInfo[0]));
                        name.setText((buttonInfo[1]));
                        view.setOnLongClickListener(Security_Layout.this); //Used to view the details
                        view.setId(Integer.parseInt(buttonInfo[0]));
                        times.setText((buttonInfo[2]) + " - " + (buttonInfo[3]));

                        final View add = view;
                        final Switch finalSwitchSchedule = switchSchedule; //need to make final in order to run on main UI thread
                        runOnUI(new Runnable() { //used to speak to main thread
                            @Override
                            public void run() {
                                if(buttonInfo[4].equals("1")){

                                    finalSwitchSchedule.setChecked(true);

                                }
                                else{
                                    finalSwitchSchedule.setChecked(false);
                                }
                                finalSwitchSchedule.setOnCheckedChangeListener(Security_Layout.this);
                                LinearLayoutAlarmSchedule.addView(add);
                            }
                        });
                    }
                }
            }
        }).start();

    } //Shows the user the current schedules


    private String getSelectedSensors() {
    String sensors = "";
    Switch switchAlarmSensor;
        for (int i = 0; i < ScrollViewAlarmSensor.getChildCount(); i++) {
            View v = ScrollViewAlarmSensor.getChildAt(i);
            switchAlarmSensor = v.findViewById(R.id.sensor_switch_Enable);
            if(switchAlarmSensor.isChecked()){
                if(i == 0){
                    sensors = "" + v.getId();
                }else{
                    sensors = sensors + "," + v.getId();
                }
            }
        }
        return sensors;
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public boolean onLongClick(View v) {
        displayAlarmScheduleInfoPopUp(v.getId());
        return true;
    }

    public void displayAlarmScheduleInfoPopUp(final int id){
        final Dialog dialogLoad = new Dialog(Security_Layout.this);
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

        //Load The Alarm Schedule Info

        new Thread(new Runnable() { //Running on a new thread
            public void run() {

                String processData = "";

                String SocketData = id + "$getAlarmScheduleDetail";
                final String[] differentAlarmSensor;
                SocketController socketController = new SocketController(Security_Layout.this,SocketData);

                try{
                    processData = socketController.execute().get();

                }catch (ExecutionException e){

                }catch (InterruptedException i){

                }


                if(processData.equals("Data Empty") || processData.equals("Server Not Running")) {

                    //No Data
                }else {
                    differentAlarmSensor = processData.split(",");

                        runOnUI(new Runnable() { //used to speak to main thread
                            @Override
                            public void run() {
                                ScheduleID = Integer.parseInt(differentAlarmSensor[0]);
                                LayoutInflater layoutInflaterSensors = LayoutInflater.from(Security_Layout.this);
                                LinearLayout linearLayoutScrollInfo;
                                TextView Heading, subHeading, thirdHeading, ThirdHeadingInfo, EquipmentHeading, SensorName, SensorType;
                                Button edit, delete;
                                Switch toggleSwitch;

                                dialogLoad.setContentView(R.layout.schedule_info);

                                Heading = dialogLoad.findViewById(R.id.textView13);
                                subHeading = dialogLoad.findViewById(R.id.TxtStartTime);
                                thirdHeading = dialogLoad.findViewById(R.id.textView7);
                                ThirdHeadingInfo = dialogLoad.findViewById(R.id.TxtPumpInfo);
                                EquipmentHeading = dialogLoad.findViewById(R.id.textView8);
                                linearLayoutScrollInfo = dialogLoad.findViewById(R.id.LinearLayoutScrollInfo);
                                edit = dialogLoad.findViewById(R.id.BtnEdit);
                                delete = dialogLoad.findViewById(R.id.BtnDelete);

                                Heading.setText(differentAlarmSensor[1]);
                                subHeading.setVisibility(View.INVISIBLE);
                                thirdHeading.setText("Active Times");
                                ThirdHeadingInfo.setText("Start: " + differentAlarmSensor[2] + " End: " + differentAlarmSensor[3]);
                                EquipmentHeading.setText("Sensors");



                                for (int i = 4; i < differentAlarmSensor.length; i++) {
                                    //final String[] buttonInfo = differentAlarmSensor[i].split(",");
                                    i++; //Skips the sensor ID

                                    View v = layoutInflaterSensors.inflate(R.layout.sensor_alarm_display, linearLayoutScrollInfo, false);
                                    SensorName = v.findViewById(R.id.TxtSensorName);
                                    SensorType = v.findViewById(R.id.TxtSensorType);
                                    toggleSwitch = v.findViewById(R.id.sensor_switch_Enable);

                                    SensorName.setText(differentAlarmSensor[i]);
                                    i++;
                                    SensorType.setText(differentAlarmSensor[i]);

                                    toggleSwitch.setVisibility(View.GONE);
                                    linearLayoutScrollInfo.addView(v);
                                }

                                edit.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialogLoad.dismiss();
                                        getAlarmSensors(differentAlarmSensor);

                                    }
                                });

                                delete.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        TextView txtEquipmentName;
                                        Button btnCancelDelete, btnConfirmDelete;
                                        dialogLoad.setContentView(R.layout.activity_confirm);
                                        txtEquipmentName = dialogLoad.findViewById(R.id.TxtEquipmentName);
                                        btnCancelDelete = dialogLoad.findViewById(R.id.BtnCancelDelete);
                                        btnConfirmDelete = dialogLoad.findViewById(R.id.BtnConfirmDelete);

                                        txtEquipmentName.setText("Are you sure you want to delete this Alarm schedule "); //______________________________________________________________________________________________
                                        btnCancelDelete.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                dialogLoad.dismiss();
                                            }
                                        });

                                        btnConfirmDelete.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {

                                                String SocketData = ScheduleID + "$DeleteSecuritySchedule";
                                                SocketController socketController = new SocketController(Security_Layout.this,SocketData);

                                                try{
                                                    socketController.execute().get();

                                                }catch (ExecutionException e){

                                                }catch (InterruptedException i){

                                                }
                                                ShowHomePage();
                                                dialogLoad.dismiss();

                                            }
                                        });


                                    }
                                });
                            }
                        });


                }
            }
        }).start();

        //dialog.setContentView(R.layout.schedule_info);//popup view is the layout you created

    }


    @Override
    public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {
        new Thread(new Runnable() { //Running on a new thread
            public void run() {
                String SocketData = "";

                if(isChecked == true){
                    SocketData = buttonView.getId()  + ",1" + "$ChangeAlarmSchedule";

                }else{
                    SocketData = buttonView.getId()  + ",0" + "$ChangeAlarmSchedule";
                }

                SocketController socketController = new SocketController(Security_Layout.this,SocketData);

                try{
                    socketController.execute().get();
                }catch (ExecutionException e){

                }catch (InterruptedException i){

                }
            }
        }).start();
    }
}
