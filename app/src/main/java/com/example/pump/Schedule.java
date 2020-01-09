package com.example.pump;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.vicmikhailau.maskededittext.MaskedEditText;

import java.util.concurrent.ExecutionException;

public class Schedule extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener, CompoundButton.OnCheckedChangeListener{

    Button btnSchedule, btnBack, btnCancel, btnSave;
    LinearLayout linearLayout1,linearLayout2, linearLayoutBtnPump, linearLayoutBtnValve, linearLayoutCreateSchedule, linearLayoutPumpController, linearLayoutSchedule;
    ScrollView scrollView1;
    TextView textView5,textView6,textView7,textView8,textView9,textView10,textView11, scheduleNameText, startTimeText, textDisplayValve;
    Switch switchS,switchM,switchT,switchW,switchTh,switchF,switchSa;
    EditText edtTextName, editTextStartTime;
    Boolean editSchedule = false;
    int editScheduleID = -1;
    public static Handler UIHandler = new Handler();

    public static void runOnUI(Runnable runnable) { //Runs code to invoke the main thread
        UIHandler.post(runnable);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        linearLayout1 = findViewById(R.id.LinearLayout1);
        linearLayout2 = findViewById(R.id.LinearLayout2);
        linearLayoutBtnPump = findViewById(R.id.LinearLayoutBtnPump);
        linearLayoutBtnValve = findViewById(R.id.ScrollViewZone);
        linearLayoutCreateSchedule = findViewById(R.id.LinearLayoutCreateSchedule);
        linearLayoutPumpController = findViewById(R.id.LinearLayoutPumpController);
        linearLayoutSchedule = findViewById(R.id.LinearLayoutSchedule);
        scrollView1 = findViewById(R.id.ScrollView1);
        //textView5 = findViewById(R.id.TextView5);
        //textView6 = findViewById(R.id.TextView6);
        //textView7 = findViewById(R.id.TextView7);
        //textView8 = findViewById(R.id.TextView8);
        //textView9 = findViewById(R.id.TextView9);
        //textView10 = findViewById(R.id.TextView10);
        //textView11 = findViewById(R.id.TextView11);
        scheduleNameText = findViewById(R.id.textView10);
        startTimeText = findViewById(R.id.textView6);
        textDisplayValve = findViewById(R.id.TxtDisplayValve);
        edtTextName = findViewById(R.id.EdtTextName);
        editTextStartTime = findViewById(R.id.EditTextManualDuration);

        switchS = findViewById(R.id.SwitchS);
        switchM = findViewById(R.id.SwitchM);
        switchT = findViewById(R.id.SwitchT);
        switchW = findViewById(R.id.SwitchW);
        switchTh = findViewById(R.id.SwitchTh);
        switchF = findViewById(R.id.SwitchF);
        switchSa = findViewById(R.id.SwitchSa);
        btnSchedule = findViewById(R.id.BtnAlarmSchedule);
        btnBack = findViewById(R.id.BtnBack);
        btnCancel = findViewById(R.id.BtnCancel);
        btnSave = findViewById(R.id.BtnSave);


        showCurrentSchedule();

        btnSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddSchedule();
                populatePumpsValves();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //If time goes to the next day, trigger this
                boolean overTime = false;
                String overTimeValue = "";
                int count = 0;
                EditText editTextValve;


                String time1 = editTextStartTime.getText().toString();


                for (int i = 0; i < linearLayoutBtnValve.getChildCount(); i++) {
                    View view = linearLayoutBtnValve.getChildAt(i);
                    editTextValve = view.findViewById(R.id.EditTextManualDuration);
                    if (editTextValve.getText().toString().equals("")) {
                        //don't include
                    } else {
                        count++; //used to see how many valves have been selected

                        try {

                            String[] arrayTime1 = time1.split(":");
                            String[] arrayTime2 = editTextValve.getText().toString().split(":");

                            int minute = Integer.parseInt(arrayTime2[1]) + Integer.parseInt(arrayTime1[1]);
                            int hour = Integer.parseInt(arrayTime2[0]) + Integer.parseInt(arrayTime1[0]);

                            if (minute >= 60) {
                                hour++;
                                minute = minute - 60;

                            }

                            time1 = hour + ":" + minute;


                        } catch (Exception e) {

                        }

                    }
                }

                String[] arrayTime1 = time1.split(":");
                int minute = Integer.parseInt(arrayTime1[1]);
                int hour = Integer.parseInt(arrayTime1[0]);

                if (hour >= 24) {
                    //overTime = true;
                    if (minute < 10) {
                        if((hour-24)<10){
                            overTimeValue = "0"+(hour - 24) + ":0" + minute;
                        }else{
                            overTimeValue = (hour - 24) + ":0" + minute;
                        }

                    } else {
                        if((hour-24)<10){
                            overTimeValue = "0"+(hour - 24) + ":0" + minute;
                        }else{
                            overTimeValue = (hour - 24) + ":" + minute;
                        }

                    }
                }

                if (overTime == false) {


                    if (edtTextName.getText().toString().equals("") || editTextStartTime.length() != 5 || count == 0) {

                        if (edtTextName.getText().toString().equals("")) {
                            scheduleNameText.setTextColor(Color.parseColor("#FF0000"));
                        } else {
                            scheduleNameText.setTextColor(Color.parseColor("#FFFFFF"));
                        }

                        if (editTextStartTime.length() != 5) {
                            startTimeText.setTextColor(Color.parseColor("#FF0000"));
                        } else {
                            startTimeText.setTextColor(Color.parseColor("#FFFFFF"));
                        }

                        if (count == 0) {
                            textDisplayValve.setTextColor(Color.parseColor("#FF0000"));
                        } else {
                            textDisplayValve.setTextColor(Color.parseColor("#FFFFFF"));
                        }
                    } else {

                        String sendSchedule = "";
                        if (editSchedule == true) {
                            sendSchedule = sendSchedule + editScheduleID + "#";
                        }
                        sendSchedule = sendSchedule + edtTextName.getText().toString() + "#";

                        sendSchedule = sendSchedule + editTextStartTime.getText().toString() + "#";
                        TextView txtPump;
                        int pumpID = -1;

                        //Check which pump is selected by Being BOLD
                        for (int i = 0; i < linearLayoutBtnPump.getChildCount(); i++) {
                            View view = linearLayoutBtnPump.getChildAt(i);

                            txtPump = view.findViewById(R.id.TxtPump);
                            int test = txtPump.getId();
                            if (txtPump.getTypeface() != null) {
                                if (txtPump.getTypeface().getStyle() == Typeface.BOLD) {
                                    pumpID = view.getId();
                                }
                            }
                        }

                        sendSchedule = sendSchedule + pumpID + "#";

                        if (switchS.isChecked()) {
                            sendSchedule = sendSchedule + "SUNDAY,";
                        }
                        if (switchM.isChecked()) {
                            sendSchedule = sendSchedule + "MONDAY,";
                        }
                        if (switchT.isChecked()) {
                            sendSchedule = sendSchedule + "TUESDAY,";
                        }
                        if (switchW.isChecked()) {
                            sendSchedule = sendSchedule + "WEDNESDAY,";
                        }
                        if (switchTh.isChecked()) {
                            sendSchedule = sendSchedule + "THURSDAY,";
                        }
                        if (switchF.isChecked()) {
                            sendSchedule = sendSchedule + "FRIDAY,";
                        }
                        if (switchSa.isChecked()) {
                            sendSchedule = sendSchedule + "SATURDAY,";
                        }
                        sendSchedule = sendSchedule + "#";

                        //EditText editTextValve;
                        for (int i = 0; i < linearLayoutBtnValve.getChildCount(); i++) {
                            View view = linearLayoutBtnValve.getChildAt(i);
                            editTextValve = view.findViewById(R.id.EditTextManualDuration);
                            if (editTextValve.getText().toString().equals("") || editTextValve.getText().toString().equals("0:00")) {
                                //don't include
                            }else{
                                String[] TimeCheck = editTextValve.getText().toString().split(":");
                                try {
                                    if (TimeCheck.length > 1) {

                                        if (!TimeCheck[0].equals("") && TimeCheck[1].equals("")) {
                                            sendSchedule = sendSchedule + view.getId() + "," + TimeCheck[0] + ":00" + "#";//Valve ID and duration
                                        } else if (TimeCheck[0].equals("") && !TimeCheck[1].equals("")) {
                                            sendSchedule = sendSchedule + view.getId() + "," + "0:" + TimeCheck[1] + "#";//Valve ID and duration
                                        } else if (!TimeCheck[0].equals("") && !TimeCheck[1].equals("")) {
                                            sendSchedule = sendSchedule + view.getId() + "," + editTextValve.getText().toString() + "#";//Valve ID and duration
                                        }
                                    }else{
                                        if (!TimeCheck[0].equals("") && !TimeCheck[0].equals("0")) {
                                            sendSchedule = sendSchedule + view.getId() + "," + TimeCheck[0] + ":00" + "#";//Valve ID and duration
                                        }
                                    }
                                }catch (Exception e){

                                }

                            }

                        }

                        if (editSchedule == false) {
                            sendSchedule = sendSchedule + "$SCHEDULE";
                            SocketController socketController = new SocketController(Schedule.this, sendSchedule);
                            socketController.execute();
                            finish();
                            Intent schedule = new Intent(Schedule.this,Schedule.class);
                            Schedule.this.startActivity(schedule);
                        } else {
                            sendSchedule = sendSchedule + "$EDITSCHEDULE";
                            SocketController socketController = new SocketController(Schedule.this, sendSchedule);
                            socketController.execute();
                            finish();
                            Intent schedule = new Intent(Schedule.this,Schedule.class);
                            Schedule.this.startActivity(schedule);
                        }

                    }
                } else {
                    final Dialog dialog = new Dialog(Schedule.this);
                    dialog.setContentView(R.layout.activity_confirm);//popup view is the layout you created
                    TextView errorMessage, TextTime;
                    Button btnCancel, btnConfirmDelete;

                    errorMessage = dialog.findViewById(R.id.textView14);
                    TextTime = dialog.findViewById(R.id.TxtEquipmentName);
                    btnCancel = dialog.findViewById(R.id.BtnCancelDelete);
                    btnConfirmDelete = dialog.findViewById(R.id.BtnConfirmDelete);

                    errorMessage.setText("The End time cant be greater than 24hs");
                    TextTime.setText("End Time: " + overTimeValue);
                    btnCancel.setText("Close");
                    btnConfirmDelete.setVisibility(View.GONE);
                    dialog.show();


                    btnCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                }

            }
        });
    }

    @Override
    public void onClick(View v) {
        TextView txtPump;
        for (int i = 0; i < linearLayoutBtnPump.getChildCount(); i++) {
            View view = linearLayoutBtnPump.getChildAt(i);
            //int test = view.getId();
            txtPump = view.findViewById(R.id.TxtPump);
            txtPump.setTypeface(null, Typeface.NORMAL);
        }
        txtPump = v.findViewById(v.getId());
        txtPump.setTypeface(null, Typeface.BOLD);


    }

    @Override
    public boolean onLongClick(final View v) {

        final Dialog dialogLoad = new Dialog(Schedule.this);
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

        new Thread(new Runnable() { //Running on a new thread
            public void run() {

                String SocketData = "";
                SocketData = v.getId() +"$getScheduleInfo";
                SocketController socketController = new SocketController(Schedule.this,SocketData);
                try{
                    final String processData = socketController.execute().get();
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






       return true;
    }

 private void displayScheduleInfo(final String data){

     TextView txtDays, txtStartTime, txtDuration, txtPumpInfo, txtValveInfo;
     Button edit, delete;
     LinearLayout linearLayoutScrollInfo;
     final Dialog dialog = new Dialog(Schedule.this);
     dialog.setContentView(R.layout.schedule_info);//popup view is the layout you created
     String DaysOfWeek = "";
     linearLayoutScrollInfo = dialog.findViewById(R.id.LinearLayoutScrollInfo);
     txtDays = dialog.findViewById(R.id.TxtDays);
     txtStartTime = dialog.findViewById(R.id.TxtStartTime);
     txtPumpInfo = dialog.findViewById(R.id.TxtPumpInfo);
     edit = dialog.findViewById(R.id.BtnEdit);
     delete = dialog.findViewById(R.id.BtnDelete);
     final String[] DeCode = data.split("#");

     String[] date = DeCode[0].split(","); //stores the date information
     for (int i = 0; i < date.length; i++) {
         DaysOfWeek = DaysOfWeek + ": " + date[i] + "   ";
     }
     txtDays.setText(DaysOfWeek);
     txtStartTime.setText("Start Time :" +DeCode[1]);
     txtPumpInfo.setText(DeCode[2]);

     LayoutInflater layoutInflaterValve = LayoutInflater.from(this);

     MaskedEditText editTextDuration;
     TextView txtZoneName;


     for (int i = 6; i < (DeCode.length); i++) {

         String[] zoneTime = DeCode[i].split(",");

         View view = layoutInflaterValve.inflate(R.layout.all_valves_toggle, linearLayoutScrollInfo, false);
         view.setId(Integer.parseInt(zoneTime[0]));
         editTextDuration = view.findViewById(R.id.EditTextManualDuration);
         txtZoneName = view.findViewById(R.id.TxtZoneName);
         editTextDuration.setEnabled(false);
         editTextDuration.setText(zoneTime[2]);
         txtZoneName.setText(zoneTime[1]);
         linearLayoutScrollInfo.addView(view);
     }
        final String ScheduleID = DeCode[3];
     edit.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             dialog.dismiss();
             populatePumpsValves(data);

         }
     });
     delete.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {

             TextView txtEquipmentName;
             Button btnCancelDelete, btnConfirmDelete;
             dialog.setContentView(R.layout.activity_confirm);
             txtEquipmentName = dialog.findViewById(R.id.TxtEquipmentName);
             btnCancelDelete = dialog.findViewById(R.id.BtnCancelDelete);
             btnConfirmDelete = dialog.findViewById(R.id.BtnConfirmDelete);
             
             txtEquipmentName.setText("Are you sure you want to delete this schedule "); //______________________________________________________________________________________________
             btnCancelDelete.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     dialog.dismiss();
                 }
             });

             btnConfirmDelete.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     String SocketData = ScheduleID + "$DeleteSchedule";
                     SocketController socketController = new SocketController(Schedule.this,SocketData);

                     try{
                         socketController.execute().get();

                     }catch (ExecutionException e){

                     }catch (InterruptedException i){

                     }
                     getSchedule();
                     dialog.dismiss();
                 }
             });
             //getSchedule();
             //dialog.dismiss();
         }
     });
     dialog.show();
     //dialog.dismiss();
    }











    private void showCurrentSchedule(){
        linearLayout1.setVisibility(View.VISIBLE);
        linearLayout2.setVisibility(View.VISIBLE);
        linearLayoutCreateSchedule.setVisibility(View.GONE);
        linearLayoutPumpController.setVisibility(View.GONE);
        scrollView1.setVisibility(View.VISIBLE);

        getSchedule();
    }



    private void showAddSchedule(){
        linearLayoutCreateSchedule.setVisibility(View.VISIBLE);
        linearLayoutPumpController.setVisibility(View.VISIBLE);
        linearLayout1.setVisibility(View.GONE);
        linearLayout2.setVisibility(View.GONE);
        scrollView1.setVisibility(View.GONE);

        switchS.setChecked(true);
        switchM.setChecked(true);
        switchT.setChecked(true);
        switchW.setChecked(true);
        switchTh.setChecked(true);
        switchF.setChecked(true);
        switchSa.setChecked(true);
    }

    private void getSchedule() {
        new Thread(new Runnable() { //Running on a new thread
            public void run() {
                LayoutInflater layoutInflaterSchedule = LayoutInflater.from(Schedule.this);
                String processData = "";
                String SocketData = "";



                    Button dialogLoadCancel;
                    final View loadingScreen = layoutInflaterSchedule.inflate(R.layout.loading_screen, linearLayoutSchedule, false);
                    dialogLoadCancel = loadingScreen.findViewById(R.id.BtnCancel);
                    dialogLoadCancel.setVisibility(View.GONE);

                    runOnUI(new Runnable() { //used to speak to main thread
                        @Override
                        public void run() {

                            linearLayoutSchedule.addView(loadingScreen);
                        }
                    });


                SocketData = "getSchedule";
                String[] differentSchedule;
                SocketController socketController = new SocketController(Schedule.this,SocketData);

                try{
                    processData = socketController.execute().get();

                }catch (ExecutionException e){

                }catch (InterruptedException i){

                }
                runOnUI(new Runnable() { //used to speak to main thread
                    @Override
                    public void run() {
                        linearLayoutSchedule.removeAllViews();
                    }
                });


                if(processData.equals("Data Empty") || processData.equals("Server Not Running")) {
                    //No Data
                }else {
                    differentSchedule = processData.split("#");

                    Switch switchSchedule;
                    TextView name, Sunday, Monday, Tuesday, Wednessday, Thursday, Friday, Saturday, PumpName;
                    for (int i = 0; i < differentSchedule.length; i++) {

                        final String[] buttonInfo = differentSchedule[i].split(",");

                        View view = layoutInflaterSchedule.inflate(R.layout.schedules, linearLayoutSchedule, false); //_____________________________________________________________schedules
                        switchSchedule = view.findViewById(R.id.sensor_switch_Enable);
                        name = view.findViewById(R.id.TxtSensorType);
                        Sunday = view.findViewById(R.id.TxtSunday);
                        Monday = view.findViewById(R.id.TxtMonday);
                        Tuesday = view.findViewById(R.id.TxtTuesday);
                        Wednessday = view.findViewById(R.id.TxtWednesday);
                        Thursday = view.findViewById(R.id.TxtThursday);
                        Friday = view.findViewById(R.id.TxtFriday);
                        Saturday = view.findViewById(R.id.TxtSaturday);
                        PumpName = view.findViewById(R.id.TxtPumpNameInfo);
                        View horizontalLine = view.findViewById(R.id.view9);
                        view.setId(Integer.parseInt(buttonInfo[0]));
                        switchSchedule.setId(Integer.parseInt(buttonInfo[0]));
                        name.setText((buttonInfo[1]) + " :" + buttonInfo[2]);
                        view.setOnLongClickListener(Schedule.this); //Used to view the details

                        try {
                            PumpName.setText((buttonInfo[4]));

                            Sunday.setVisibility(View.INVISIBLE);
                            Monday.setVisibility(View.INVISIBLE);
                            Tuesday.setVisibility(View.INVISIBLE);
                            Wednessday.setVisibility(View.INVISIBLE);
                            Thursday.setVisibility(View.INVISIBLE);
                            Friday.setVisibility(View.INVISIBLE);
                            Saturday.setVisibility(View.INVISIBLE);

                            for (int j = 5; j < buttonInfo.length; j++) {


                                if(buttonInfo[j].equals("SUNDAY")){
                                    Sunday.setVisibility(View.VISIBLE);
                                }else if(buttonInfo[j].equals("MONDAY")){
                                    Monday.setVisibility(View.VISIBLE);
                                }else if(buttonInfo[j].equals("TUESDAY")){
                                    Tuesday.setVisibility(View.VISIBLE);
                                }else if(buttonInfo[j].equals("WEDNESDAY")){
                                    Wednessday.setVisibility(View.VISIBLE);
                                }else if(buttonInfo[j].equals("THURSDAY")){
                                    Thursday.setVisibility(View.VISIBLE);
                                }else if(buttonInfo[j].equals("FRIDAY")){
                                    Friday.setVisibility(View.VISIBLE);
                                }else if(buttonInfo[j].equals("SATURDAY")){
                                    Saturday.setVisibility(View.VISIBLE);
                                }

                            }

                        }catch (Exception e){
                            Sunday.setVisibility(View.GONE);
                            Monday.setVisibility(View.GONE);
                            Tuesday.setVisibility(View.GONE);
                            Wednessday.setVisibility(View.GONE);
                            Thursday.setVisibility(View.GONE);
                            Friday.setVisibility(View.GONE);
                            Saturday.setVisibility(View.GONE);
                            PumpName.setVisibility(View.GONE);

                            horizontalLine.setVisibility(View.GONE);
                        }

                        final View add = view;
                        final Switch finalSwitchSchedule = switchSchedule; //need to make final in order to run on main UI thread
                        runOnUI(new Runnable() { //used to speak to main thread
                            @Override
                            public void run() {
                                if(buttonInfo[3].equals("1")){

                                    finalSwitchSchedule.setChecked(true);

                                }
                                else{
                                    finalSwitchSchedule.setChecked(false);
                                }
                                finalSwitchSchedule.setOnCheckedChangeListener(Schedule.this);
                                linearLayoutSchedule.addView(add);
                            }
                        });

                    }
                }
            }
        }).start();

    } //Shows the user the current schedules

    private void populatePumpsValves() { //Adds buttons corresponding to the pumps/ Valves added to the server
        editSchedule = false;
        btnSave.setText("SAVE");

        linearLayoutBtnPump.removeAllViews();
        linearLayoutBtnValve.removeAllViews();

        //Gets the different pumps

        new Thread(new Runnable() { //Running on a new thread
            public void run() {
                String SocketData = "";
                String[] differentButtons;
                SocketData = "getPumps";
                SocketController socketController = new SocketController(Schedule.this,SocketData);

                String processData = "";

                try{
                    processData = socketController.execute().get();

                }catch (ExecutionException e){

                }catch (InterruptedException i){

                }

                LayoutInflater layoutInflaterPump = LayoutInflater.from(Schedule.this);//Pump
                LayoutInflater layoutInflaterValve = LayoutInflater.from(Schedule.this);//Valve

                if(processData.equals("Data Empty")|| processData.equals("Server Not Running")) {
                    //No Data
                }else{
                    differentButtons = processData.split("#");
                    TextView TxtPump;
                    for (int i = 0; i < differentButtons.length; i++) {

                        final String[] buttonInfo = differentButtons[i].split(",");

                        final View view = layoutInflaterPump.inflate(R.layout.all_pumps_toggle, linearLayoutBtnPump, false); //_____________________________________________________________Pump
                        view.setId(Integer.parseInt(buttonInfo[0]));
                        TxtPump = view.findViewById(R.id.TxtPump);
                        if(i == 0){
                            TxtPump.setTypeface(null, Typeface.BOLD);
                        }

                        TxtPump.setText((buttonInfo[1]));
                        TxtPump.setOnClickListener(Schedule.this);

                        runOnUI(new Runnable() { //used to speak to main thread
                            @Override
                            public void run() {
                                linearLayoutBtnPump.addView(view);
                            }
                        });

                                                                               //_____________________________________________________________Pump
                    }
                }


                SocketData = "getValves";                                                                      //_____________________________________________________________Valves
                socketController = new SocketController(Schedule.this,SocketData);
                try{
                    processData = socketController.execute().get();

                }catch (ExecutionException e){

                }catch (InterruptedException i){

                }
                if(processData.equals("Data Empty") || processData.equals("Server Not Running")) {
                    //No Data
                }else{
                    differentButtons = processData.split("#");

                    MaskedEditText editTextDuration;
                    TextView txtZoneName;

                    for (int i = 0; i < differentButtons.length; i++) {

                        String[] buttonInfo = differentButtons[i].split(",");

                        final View view = layoutInflaterValve.inflate(R.layout.all_valves_toggle, linearLayoutBtnValve, false);
                        view.setId(Integer.parseInt(buttonInfo[0]));
                        editTextDuration = view.findViewById(R.id.EditTextManualDuration);
                        txtZoneName = view.findViewById(R.id.TxtZoneName);

                        txtZoneName.setId(Integer.parseInt(buttonInfo[0]));
                        txtZoneName.setText((buttonInfo[1]));

                        runOnUI(new Runnable() { //used to speak to main thread
                            @Override
                            public void run() {
                                linearLayoutBtnValve.addView(view);
                            }
                        });
                                                                                    //_____________________________________________________________Valves
                    }
                }
            }
        }).start();






    }

    private void fillInDates(String[] DeCode){
        switchS.setChecked(false);
        switchM.setChecked(false);
        switchT.setChecked(false);
        switchW.setChecked(false);
        switchTh.setChecked(false);
        switchF.setChecked(false);
        switchSa.setChecked(false);

        String[] date = DeCode[0].split(","); //stores the date information
        for (int i = 0; i < date.length; i++) {

            if(date[i].equals("SUNDAY")){
                switchS.setChecked(true);
            }else if(date[i].equals("MONDAY")){
                switchM.setChecked(true);
            }else if(date[i].equals("TUESDAY")){
                switchT.setChecked(true);
            }else if(date[i].equals("WEDNESDAY")){
                switchW.setChecked(true);
            }else if(date[i].equals("THURSDAY")){
                switchTh.setChecked(true);
            }else if(date[i].equals("FRIDAY")){
                switchF.setChecked(true);
            }else if(date[i].equals("SATURDAY")){
                switchSa.setChecked(true);
            }

        }
    }

    private void populatePumpsValves(String data) { //Adds buttons corresponding to the pumps/ Valves added to the server
        editSchedule = true;
        btnSave.setText("Update");
        showAddSchedule();


        final String[] DeCode = data.split("#");
        fillInDates(DeCode);

        editTextStartTime.setText(DeCode[1]);//_________NOPE
        edtTextName.setText(DeCode[5]);
        editScheduleID = Integer.parseInt(DeCode[3]);
        linearLayoutBtnPump.removeAllViews();
        linearLayoutBtnValve.removeAllViews();

        //Gets the different pumps



        new Thread(new Runnable() { //Running on a new thread
            public void run() {
                String[] differentButtons;
                String SocketData = "";
                SocketData = "getPumps";
                SocketController socketController = new SocketController(Schedule.this,SocketData);

                String processData = "";



                try{
                    processData = socketController.execute().get();

                }catch (ExecutionException e){

                }catch (InterruptedException i){

                }

                LayoutInflater layoutInflaterPump = LayoutInflater.from(Schedule.this);//Pump
                LayoutInflater layoutInflaterValve = LayoutInflater.from(Schedule.this);//Valve


                if(processData.equals("Data Empty") || processData.equals("Server Not Running")) {
                    //No Data
                }else{
                    differentButtons = processData.split("#");
                    TextView TxtPump;
                    for (int i = 0; i < differentButtons.length; i++) {

                        String[] buttonInfo = differentButtons[i].split(",");

                        final View view = layoutInflaterPump.inflate(R.layout.all_pumps_toggle, linearLayoutBtnPump, false); //_____________________________________________________________Pump
                        view.setId(Integer.parseInt(buttonInfo[0]));
                        TxtPump = view.findViewById(R.id.TxtPump);
                        if(DeCode[4].equals(buttonInfo[0])){
                            TxtPump.setTypeface(null, Typeface.BOLD);
                        }

                        TxtPump.setText((buttonInfo[1]));
                        TxtPump.setOnClickListener(Schedule.this);

                        runOnUI(new Runnable() { //used to speak to main thread
                            @Override
                            public void run() {
                                linearLayoutBtnPump.addView(view);   //_____________________________________________________________Pump
                            }
                        });

                    }
                }
                SocketData = "getValves";                                                                      //_____________________________________________________________Valves
                socketController = new SocketController(Schedule.this,SocketData);
                try{
                    processData = socketController.execute().get();

                }catch (ExecutionException e){

                }catch (InterruptedException i){

                }
                if(processData.equals("Data Empty") || processData.equals("Server Not Running")) {
                    //No Data
                }else{
                    differentButtons = processData.split("#");

                    MaskedEditText editTextDuration;
                    TextView txtZoneName;

                    for (int i = 0; i < differentButtons.length; i++) {

                        String[] buttonInfo = differentButtons[i].split(",");

                        final View view = layoutInflaterValve.inflate(R.layout.all_valves_toggle, linearLayoutBtnValve, false);
                        view.setId(Integer.parseInt(buttonInfo[0]));
                        editTextDuration = view.findViewById(R.id.EditTextManualDuration);
                        txtZoneName = view.findViewById(R.id.TxtZoneName);

                        txtZoneName.setId(Integer.parseInt(buttonInfo[0]));
                        txtZoneName.setText((buttonInfo[1]));
                        for (int j = 6; j < (DeCode.length); j++) {
                            String[] zoneTime = DeCode[j].split(",");

                            if(zoneTime[0].equals(buttonInfo[0])){
                                editTextDuration.setText(zoneTime[2]);
                            }
                        }

                        runOnUI(new Runnable() { //used to speak to main thread
                            @Override
                            public void run() {
                                linearLayoutBtnValve.addView(view);         //_____________________________________________________________Valves
                            }
                        });


                    }
                }

            }
        }).start();
    }

    /*
    private void DatePicker(){
        switchS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked == true){
                    textView5.setTypeface(null, Typeface.BOLD);
                }else{
                    textView5.setTypeface(null, Typeface.NORMAL);
                }
            }
        });

        switchM.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked == true){
                    textView6.setTypeface(null, Typeface.BOLD);
                }else{
                    textView6.setTypeface(null, Typeface.NORMAL);
                }
            }
        });

        switchT.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked == true){
                    textView7.setTypeface(null, Typeface.BOLD);
                }else{
                    textView7.setTypeface(null, Typeface.NORMAL);
                }
            }
        });

        switchW.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked == true){
                    textView8.setTypeface(null, Typeface.BOLD);
                }else{
                    textView8.setTypeface(null, Typeface.NORMAL);
                }
            }
        });

        switchTh.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked == true){
                    textView9.setTypeface(null, Typeface.BOLD);
                }else{
                    textView9.setTypeface(null, Typeface.NORMAL);
                }
            }
        });

        switchF.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked == true){
                    textView10.setTypeface(null, Typeface.BOLD);
                }else{
                    textView10.setTypeface(null, Typeface.NORMAL);
                }
            }
        });

        switchSa.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked == true){
                    textView11.setTypeface(null, Typeface.BOLD);
                }else{
                    textView11.setTypeface(null, Typeface.NORMAL);
                }
            }
        });
    }
    */

    @Override
    public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {

        new Thread(new Runnable() { //Running on a new thread
            public void run() {
                String SocketData = "";
                if(isChecked == true){
                    SocketData = buttonView.getId()  + ",1" + "$ChangeSchedule";

                }else{
                    SocketData = buttonView.getId()  + ",0" + "$ChangeSchedule";
                }

                SocketController socketController = new SocketController(Schedule.this,SocketData);

                try{
                    socketController.execute().get();
                }catch (ExecutionException e){

                }catch (InterruptedException i){
                }
            }
        }).start();

    }  //Used for the Virtual View Switches
}

