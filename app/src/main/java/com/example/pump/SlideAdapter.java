package com.example.pump;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.vicmikhailau.maskededittext.MaskedEditText;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;


public class SlideAdapter extends PagerAdapter implements  View.OnClickListener, View.OnLongClickListener{
    private MyCustomObjectListener listener;
    Context context;
    LayoutInflater inflater;
    private int position;
    View view;
    Button btnAddPumpValve, btnViewSchedule, btnEarth, btnStartManual, btnStopManual, btnConnectController, btnAlarm, BtnGraph;
    LinearLayout linearLayoutScrollActiveZone, linearLayoutQueueZone,linearLayoutSensorStatus, linearLayoutScrollManualPump, linearLayoutScrollManualZone, linearLayoutManualPage;
    MaskedEditText editTextDuration;
    TextView textView14,textView15, textView17;
    Switch switchAsyncRun;

    ProgressBar progressBarManual;
    ImageView ManualSuccsessfullImage;

    List<String> pumpIDs = new ArrayList<String>();
    List<String> zoneIDs = new ArrayList<String>();
    List<Integer> selectedEquipment = new ArrayList<Integer>();
    List<Integer> RunningselectedEquipment = new ArrayList<Integer>();



    public static Handler UIHandler = new Handler();



    public SlideAdapter (Context context){
        this.context = context;

    }
    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return (view == o);
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((LinearLayout)object);
    }

    public String[][] getActivePumps(){


        String SocketData = "getActiveSchedule";
        final SocketController socketController = new SocketController(context,SocketData);
        String processData = "";

        try{
            processData = socketController.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR).get();


        }catch (ExecutionException e){

        }catch (InterruptedException i){

        }


        if(processData.equals("No Data") || processData.equals("Server Not Running")){ //return an empty array if no active pump is available
            String[][] NoActive = new String[1][1];
            NoActive[0][0] = "No Data";
            return NoActive;
        }

        String[] splitDatawithManual = processData.split(Pattern.quote("$"));
        String[] splitData = new String[0];
        String ManualData = "";
        //If both are running
        if(splitDatawithManual.length>1){
            splitData = splitDatawithManual[1].split("#");
            ManualData = splitDatawithManual[0];
        }else{

            if(splitDatawithManual[0].contains(",")){
                splitData = splitDatawithManual[0].split("#");
            }else{
                ManualData = splitDatawithManual[0];
            }
        }
        String [][] ActiveEquipment;

        if(!ManualData.equals("")){
            ActiveEquipment = new String[splitData.length+1][6];//we are returnig this
        }else{
            ActiveEquipment = new String[splitData.length][6];//we are returnig this
        }



        int j = 0;
        if(!ManualData.equals("")){
            ActiveEquipment[j][0] = ManualData;
            j=j+1;
        }

            for (int i = 0; i < splitData.length; i++) {


                try {
                    String[] DataRow = splitData[i].split(",");
                    ActiveEquipment[j][0] = DataRow[0];
                    ActiveEquipment[j][1] = DataRow[1];
                    ActiveEquipment[j][2] = DataRow[2];
                    ActiveEquipment[j][3] = DataRow[3];
                    ActiveEquipment[j][4] = DataRow[4];
                    ActiveEquipment[j][5] = DataRow[5];
                    j = j + 1;

                } catch (Exception e) {

                }
            }


        return ActiveEquipment;

    }

    public String[][] getActiveSensor(){

        String SocketData = "getActiveSensorStatus";
        final SocketController socketController = new SocketController(context, SocketData);
        String processData = "";
        try{
            processData = socketController.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR).get();
            runOnUI(new Runnable() { //used to speak to main thread
                @Override
                public void run() {
                    listener.onObjectReady(socketController.getPingTime()); //used to get the delay time
                }
            });
        }catch (ExecutionException e){

        }catch (InterruptedException i){

        }
        if(processData.equals("No Sensor") || processData.equals("Server Not Running")){ //return an empty array if no active pump is available
            String[][] NoActive = new String[1][1];
            NoActive[0][0] = "No Sensor";
            return NoActive;
        }
        String[] splitData = processData.split("#");

        String [][] ActiveSensor = new String[splitData.length][4];//we will be populating this with the sensor data

        for (int i = 0; i < splitData.length; i++) {
            String[] DataRow = splitData[i].split(",");
            try{
                ActiveSensor[i][0] = DataRow[0];
                ActiveSensor[i][1] = DataRow[1];
                ActiveSensor[i][2] = DataRow[2];
                ActiveSensor[i][3] = DataRow[3];
            }catch (Exception e){

            }

        }
        return ActiveSensor;
    }

    public String[][] getQueuePumps(){


        String SocketData = "getQueueSchedule";
        final SocketController socketController = new SocketController(context, SocketData);
        String processData = "";
        try{
            processData = socketController.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR).get();

        }catch (ExecutionException e){

        }catch (InterruptedException i){

        }

        if(processData.equals("No Data") || processData.equals("Server Not Running")){ //return an empty array if no active pump is available
            String[][] NoActive = new String[1][1];
            NoActive[0][0] = "No Data";
            return NoActive;
        }

        String[] splitData = processData.split("#");

        String [][] ActiveEquipment = new String[splitData.length][6];//we are returnig this

        for (int i = 0; i < splitData.length; i++) {
            String[] DataRow = splitData[i].split(",");
            ActiveEquipment[i][0] = DataRow[0];
            ActiveEquipment[i][1] = DataRow[1];
            ActiveEquipment[i][2] = DataRow[2];
            ActiveEquipment[i][3] = DataRow[3];
            ActiveEquipment[i][4] = DataRow[4];
            ActiveEquipment[i][5] = DataRow[5];
        }
        return ActiveEquipment;
    }


    public static void runOnUI(Runnable runnable) { //Runs code to invoke the main thread
        UIHandler.post(runnable);
    }

    //variables to be used in the other User Interfaces
    @NonNull
    @Override
    public Object instantiateItem(@NonNull final ViewGroup container, int position) {

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.position = position;
        //different positions give different pages to show
        if (position == 0) {

            view = inflater.inflate(R.layout.activity_live_view, container, false);
            linearLayoutScrollActiveZone = view.findViewById(R.id.LinearLayoutScrollActiveZone);
            linearLayoutQueueZone = view.findViewById(R.id.LinearLayoutQueueZone);
            linearLayoutSensorStatus = view.findViewById(R.id.LinearLayoutSensorStatus);
            new Thread(new Runnable() { //Running on a new thread
                public void run() { //used to ge the pumps that are in today's schedule
                    String[][] Old = new String[0][0];
                    while (true) {

                        Old = getCurrentSchedule(Old);

                        SystemClock.sleep(8000);
                    }
                }
            }).start();

            new Thread(new Runnable() { //Running on a new thread
                public void run() { //used to ge the pumps that are in today's schedule
                    String[][] OldSensorDetails = new String[0][0];
                    while (true) {

                        OldSensorDetails = getSensorLiveView(OldSensorDetails);

                        SystemClock.sleep(8000);
                    }
                }
            }).start();


            new Thread(new Runnable() { //Running on a new thread
                public void run() { //used to ge the pumps that are in today's schedule
                    String[][] Old = new String[0][0];
                    while (true) {

                        Old = getNextScheudleDue(Old);

                        SystemClock.sleep(8000);
                    }
                }
            }).start();


        }

        else if (position == 1) {



            view = inflater.inflate(R.layout.activity_manual, container, false);
            linearLayoutScrollManualPump = view.findViewById(R.id.LinearLayoutScrollManualPump);
            linearLayoutScrollManualZone = view.findViewById(R.id.LinearLayoutScrollManualZone);
            linearLayoutScrollManualPump.removeAllViews();
            linearLayoutScrollManualZone.removeAllViews();
            linearLayoutManualPage = view.findViewById(R.id.LinearLayoutManualPage);

            progressBarManual = view.findViewById(R.id.progressBarManual);

            ManualSuccsessfullImage = view.findViewById(R.id.ManualSuccsessfullImage);

            textView14= view.findViewById(R.id.TextView14);
            textView15 = view.findViewById(R.id.TextView15);
            textView17 = view.findViewById(R.id.TextView17);
            editTextDuration = view.findViewById(R.id.EditTextManualDuration);
            switchAsyncRun = view.findViewById(R.id.switchAsyncRun);


            textView14.setTextColor(Color.parseColor("#FFFFFF"));
            textView15.setTextColor(Color.parseColor("#FFFFFF"));
            textView17.setTextColor(Color.parseColor("#FFFFFF"));

            progressBarManual.setVisibility(View.INVISIBLE);
            ManualSuccsessfullImage.setVisibility(View.GONE);



            new Thread(new Runnable() { //Running on a new thread
                public void run() {


                    //boolean manualSchedule;
                    boolean OLDmanualSchedule = false;
                    boolean run = false; //Check if code has ran to populate buttons
                    int OLDPumpCount = 0;
                    int OLDZoneCount = 0;
                    while (true) {
                        //if (getPageInt() == 0 || run ==false) {
                        if(run ==false)
                        {

                            String EquipmentCount = getManualPagePumpsZones(OLDPumpCount,OLDZoneCount);
                            String[] EquipmentCountArray = EquipmentCount.split(",");
                            OLDPumpCount = Integer.parseInt(EquipmentCountArray[0]);
                            OLDZoneCount = Integer.parseInt(EquipmentCountArray[1]);



                            OLDmanualSchedule = UpdateManualButtonStatus(OLDmanualSchedule);
                        }
                        SystemClock.sleep(1000);


                    }
                }
                //}
            }).start();

            final MaskedEditText editTextDuration;
            btnStartManual = view.findViewById(R.id.BtnStartManual);
            btnStopManual = view.findViewById(R.id.BtnStopManual);
            editTextDuration = view.findViewById(R.id.EditTextManualDuration);
            editTextDuration.setTextColor(Color.parseColor("#FFFFFF"));

            btnStopManual.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    StopManual();

                }
            });

            btnStartManual.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    StartManual();
                }
            });

        }

        else if (position == 2) {
            view = inflater.inflate(R.layout.settings, container, false);
            btnAddPumpValve = view.findViewById(R.id.BtnAddPumpValve);
            btnAddPumpValve = view.findViewById(R.id.BtnAddPumpValve);
            btnViewSchedule = view.findViewById(R.id.BtnViewSchedule);
            btnConnectController = view.findViewById(R.id.BtnConnectController);
            btnAlarm = view.findViewById(R.id.btnAlarm);
            BtnGraph = view.findViewById(R.id.BtnGraph);


            btnAddPumpValve.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //------------------------------------------------------------------
                    Intent addPump = new Intent(view.getContext(),AddPump_Valve.class);
                    view.getContext().startActivity(addPump);
                    //------------------------------------------------------------------

                }
            });

            btnViewSchedule.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //------------------------------------------------------------------
                    Intent schedule = new Intent(view.getContext(),Schedule.class);
                    view.getContext().startActivity(schedule);
                    //------------------------------------------------------------------
                }
            });

            btnConnectController.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //------------------------------------------------------------------
                    Intent add_controller = new Intent(view.getContext(), select_controller.class);
                    view.getContext().startActivity(add_controller);
                    ((Activity)context).finish();
                    //------------------------------------------------------------------
                }
            });

            btnAlarm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //------------------------------------------------------------------
                    Intent security = new Intent(view.getContext(),Security_Layout.class);
                    view.getContext().startActivity(security);
                    //------------------------------------------------------------------
                }
            });

            BtnGraph.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //------------------------------------------------------------------
                    Intent ChartsPage = new Intent(view.getContext(),Charts.class);
                    view.getContext().startActivity(ChartsPage);
                    //------------------------------------------------------------------
                }
            });
        }

        container.addView(view);
        return view;
    }

    @Override
    public void onClick(View v) {
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
                        int btnValve2ID = Integer.parseInt(zoneIDs.get(arraySize));
                        btnValve2ID = btnValve2ID;
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

    @Override
    public boolean onLongClick(View v) {
        return false;
    }

    public void setCustomObjectListener(MyCustomObjectListener listener) {
        this.listener = listener;
    }

    public interface MyCustomObjectListener { //used to send the delay time
        // need to pass relevant arguments related to the event triggered
        void onObjectReady(String title);

    }

    public int getPageInt(){
        return this.position;
    }

    private String[][] getCurrentSchedule(String[][] Old){

        final LayoutInflater layoutInflaterActiveSchedule = LayoutInflater.from(this.context);//Used to inflate the schedules to the user

        //String[][] OldQueue = new String[0][0];
        if(Old.length <1){
            Button dialogLoadCancel;

            final View v = layoutInflaterActiveSchedule.inflate(R.layout.loading_screen, linearLayoutScrollActiveZone, false);
            dialogLoadCancel = v.findViewById(R.id.BtnCancel);
            dialogLoadCancel.setVisibility(View.GONE);

            runOnUI(new Runnable() { //used to speak to main thread
                @Override
                public void run() {
                    linearLayoutScrollActiveZone.removeAllViews();
                    linearLayoutScrollActiveZone.addView(v);
                }
            });
        }

        String[][] ActiveSchedule = getActivePumps();


        Boolean equals = false;
        for (int i = 0; i < ActiveSchedule.length; i++) {

            try{
                if (Old.length == ActiveSchedule.length) { //if its not the same length, then We need to update the Layout View
                    if (Old[i][0].equals(ActiveSchedule[i][0])) {
                        equals = true;
                        //i = ActiveSchedule.length+1;
                    } else {
                        equals = false;
                        i = ActiveSchedule.length + 1; //jumps out the for loop
                        //j = Old.length +1;
                    }
                }
            }catch (Exception e){

            }

        }



        if(equals == false){

            runOnUI(new Runnable() { //used to speak to main thread
                @Override
                public void run() {
                    linearLayoutScrollActiveZone.removeAllViews();
                }
            });

            TextView txtScheduleName, txtActiveZone, txtActivePump, txtActiveStartTime, txtActiveEndTime, txtNoActivity;
            if(ActiveSchedule[0][0].equals("No Data")){
                final View v = layoutInflaterActiveSchedule.inflate(R.layout.activity_no_live_equipment, linearLayoutScrollActiveZone, false);
                txtNoActivity = v.findViewById(R.id.TxtNoActivity);
                txtNoActivity.setText("No Active Equipment Running");

                if (equals == false) {
                    runOnUI(new Runnable() { //used to speak to main thread
                        @Override
                        public void run() {
                            linearLayoutScrollActiveZone.addView(v);
                        }
                    });
                }
            }else{
                try{

                    if(ActiveSchedule[0][1] ==null){


                        final View v = layoutInflaterActiveSchedule.inflate(R.layout.activity_no_live_equipment, linearLayoutScrollActiveZone, false);
                        txtNoActivity = v.findViewById(R.id.TxtNoActivity);
                        if(ActiveSchedule.length>1) {
                            txtNoActivity.setText("MANUAL SCHEDULE RUNNING WITH SCHEDULE");
                        }else {
                            txtNoActivity.setText("MANUAL SCHEDULE RUNNING WITHOUT SCHEDULE");
                        }

                        runOnUI(new Runnable() { //used to speak to main thread
                            @Override
                            public void run() {
                                linearLayoutScrollActiveZone.addView(v);
                            }
                        });

                        String durationTime = ActiveSchedule[0][0];
                        DateFormat dateFormat = new SimpleDateFormat("HH:mm");
                        Date date = new Date();

                        String time1 = dateFormat.format(date);
                        String time2 = durationTime;

                        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
                        String Time = "";

                        try {

                            String[] arrayTime1 = time1.split(":");
                            String[] arrayTime2 = time2.split(":");
                            int minute = Integer.parseInt(arrayTime2[1]) - Integer.parseInt(arrayTime1[1]);
                            int hour = Integer.parseInt(arrayTime2[0]) - Integer.parseInt(arrayTime1[0]);
                            if (minute < 0 && hour > 0) {
                                hour--;
                                minute = minute + 60;

                            }
                            if (minute < 10) {
                                Time = hour + ":0" + minute;
                            } else {
                                Time = hour + ":" + minute;
                            }

                        } catch (Exception e) {

                        }


                        final View v1 = layoutInflaterActiveSchedule.inflate(R.layout.activity_no_live_equipment, linearLayoutScrollActiveZone, false);
                        txtNoActivity = v1.findViewById(R.id.TxtNoActivity);
                        txtNoActivity.setText("ENDS ON: " + Time);
                        runOnUI(new Runnable() { //used to speak to main thread
                            @Override
                            public void run() {
                                linearLayoutScrollActiveZone.addView(v1);
                            }
                        });
                    }




                }catch (Exception e){

                }
                if (equals == false) {
                    for (int i = 0; i < ActiveSchedule.length; i++) {
                        if(ActiveSchedule[i][1] !=null){
                            final View v = layoutInflaterActiveSchedule.inflate(R.layout.activity_live_view_equipment_status, linearLayoutScrollActiveZone, false);
                            txtScheduleName = v.findViewById(R.id.TxtScheduleName);
                            txtActiveZone = v.findViewById(R.id.TxtActiveZone);
                            txtActivePump = v.findViewById(R.id.TxtActivePump);
                            txtActiveStartTime = v.findViewById(R.id.TxtActiveStartTime);
                            txtActiveEndTime = v.findViewById(R.id.TxtActiveEndTime);
                            txtScheduleName.setText("Schedule: " + ActiveSchedule[i][1]);
                            txtActivePump.setText("Pump: " + ActiveSchedule[i][2]);
                            txtActiveZone.setText("Zone: " + ActiveSchedule[i][3]);
                            txtActiveStartTime.setText("Start: " + ActiveSchedule[i][4]);
                            txtActiveEndTime.setText("End: " + ActiveSchedule[i][5]);

                            runOnUI(new Runnable() { //used to speak to main thread
                                @Override
                                public void run() {
                                    linearLayoutScrollActiveZone.addView(v);
                                }
                            });
                        }

                    }
                }

            }
        }









        return ActiveSchedule;
    }




    private String[][] getSensorLiveView(String[][] OldSensorDetails) {


        final LayoutInflater layoutSensorStatus = LayoutInflater.from(context);//Used to inflate the schedules to the user
        //String[][] OldSensorDetails = new String[0][0];

        if (OldSensorDetails.length < 1) {
            Button dialogLoadCancel;

            final View v = layoutSensorStatus.inflate(R.layout.loading_screen, linearLayoutSensorStatus, false);
            dialogLoadCancel = v.findViewById(R.id.BtnCancel);
            dialogLoadCancel.setVisibility(View.GONE);

            runOnUI(new Runnable() { //used to speak to main thread
                @Override
                public void run() {
                    linearLayoutSensorStatus.removeAllViews();
                    linearLayoutSensorStatus.addView(v);
                }
            });
        }


        String[][] ActiveSensorDetails = getActiveSensor();

        runOnUI(new Runnable() { //used to speak to main thread
            @Override
            public void run() {
                linearLayoutSensorStatus.removeAllViews();
            }
        });


        Boolean sameSensorEquals = false;

        if (ActiveSensorDetails[0][0].equals("No Sensor")) {
            TextView txtNoActivity;
            final View v = layoutSensorStatus.inflate(R.layout.activity_no_live_equipment, linearLayoutSensorStatus, false);
            txtNoActivity = v.findViewById(R.id.TxtNoActivity);
            txtNoActivity.setText("No Sensors");

            if (sameSensorEquals == false) {
                runOnUI(new Runnable() { //used to speak to main thread
                    @Override
                    public void run() {
                        linearLayoutSensorStatus.addView(v);
                    }
                });
            }

        }else {


            for (int i = 0; i < ActiveSensorDetails.length; i++) {

                try {
                    if (ActiveSensorDetails[i][1].equals("Pressure Sensor")) {

                        TextView txtSensorName, txtPressureStatus;
                        ImageView imageSesnorStatus;
                        final View v = layoutSensorStatus.inflate(R.layout.fragment_pressure_display, linearLayoutSensorStatus, false);
                        txtPressureStatus = v.findViewById(R.id.TxtPressureStatus);
                        txtSensorName = v.findViewById(R.id.TxtSensorName);
                        imageSesnorStatus = v.findViewById(R.id.ImageSesnorStatus);

                        txtSensorName.setText(ActiveSensorDetails[i][1]);

                        if (ActiveSensorDetails[i][3].equals("0") || ActiveSensorDetails[i][3].equals("False")) {
                            imageSesnorStatus.setBackgroundResource(R.drawable.pressure_low);
                            txtPressureStatus.setText("Pressure: LOW");
                        } else {
                            imageSesnorStatus.setBackgroundResource(R.drawable.pressure_high);
                            txtPressureStatus.setText("Pressure: HIGH");
                        }
                        runOnUI(new Runnable() { //used to speak to main thread
                            @Override
                            public void run() {
                                linearLayoutSensorStatus.addView(v);
                            }
                        });

                    } else if (ActiveSensorDetails[i][1].equals("Echo Sensor")) {

                        TextView txtSensorName, txtPressureStatus;
                        ImageView imageSesnorStatus;
                        final View v = layoutSensorStatus.inflate(R.layout.fragment_pressure_display, linearLayoutSensorStatus, false);
                        txtPressureStatus = v.findViewById(R.id.TxtPressureStatus);
                        txtSensorName = v.findViewById(R.id.TxtSensorName);
                        imageSesnorStatus = v.findViewById(R.id.ImageSesnorStatus);

                        txtSensorName.setText(ActiveSensorDetails[i][2]);

                        imageSesnorStatus.setBackgroundResource(R.drawable.echo_sensor_img);
                        txtPressureStatus.setText(ActiveSensorDetails[i][3] + " CM");

                        runOnUI(new Runnable() { //used to speak to main thread
                            @Override
                            public void run() {
                                linearLayoutSensorStatus.addView(v);
                            }
                        });

                    } else if (ActiveSensorDetails[i][1].equals("Mini Infrared PIR")) {


                        TextView txtSensorName, txtPressureStatus;
                        ImageView imageSesnorStatus;
                        final View v = layoutSensorStatus.inflate(R.layout.fragment_pressure_display, linearLayoutSensorStatus, false);
                        txtPressureStatus = v.findViewById(R.id.TxtPressureStatus);
                        txtSensorName = v.findViewById(R.id.TxtSensorName);
                        imageSesnorStatus = v.findViewById(R.id.ImageSesnorStatus);

                        txtSensorName.setText(ActiveSensorDetails[i][2]);

                        if (ActiveSensorDetails[i][3].equals("0") || ActiveSensorDetails[i][3].equals("False")) {
                            imageSesnorStatus.setBackgroundResource(R.drawable.pir_low_img);
                            txtPressureStatus.setText("Nothing detected");
                        } else {
                            imageSesnorStatus.setBackgroundResource(R.drawable.pir_high_img);
                            txtPressureStatus.setText("PIR has been set off");
                        }
                        runOnUI(new Runnable() { //used to speak to main thread
                            @Override
                            public void run() {
                                linearLayoutSensorStatus.addView(v);
                            }
                        });

                    } else if (ActiveSensorDetails[i][1].equals("Vibration Sensor")) {

                        TextView txtSensorName, txtPressureStatus;
                        ImageView imageSesnorStatus;
                        final View v = layoutSensorStatus.inflate(R.layout.fragment_pressure_display, linearLayoutSensorStatus, false);
                        txtPressureStatus = v.findViewById(R.id.TxtPressureStatus);
                        txtSensorName = v.findViewById(R.id.TxtSensorName);
                        imageSesnorStatus = v.findViewById(R.id.ImageSesnorStatus);

                        txtSensorName.setText(ActiveSensorDetails[i][2]);

                        if (ActiveSensorDetails[i][3].equals("1") || ActiveSensorDetails[i][3].equals("True")) {
                            imageSesnorStatus.setBackgroundResource(R.drawable.vibration_low_img);
                            txtPressureStatus.setText("No Vibrations");
                        } else {
                            imageSesnorStatus.setBackgroundResource(R.drawable.vibration_high_img);
                            txtPressureStatus.setText("Vibrations Detected!");
                        }
                        runOnUI(new Runnable() { //used to speak to main thread
                            @Override
                            public void run() {
                                linearLayoutSensorStatus.addView(v);
                            }
                        });

                    } else if (ActiveSensorDetails[i][1].equals("Sound Detection Sensor")) {


                        TextView txtSensorName, txtPressureStatus;
                        ImageView imageSesnorStatus;
                        final View v = layoutSensorStatus.inflate(R.layout.fragment_pressure_display, linearLayoutSensorStatus, false);
                        txtPressureStatus = v.findViewById(R.id.TxtPressureStatus);
                        txtSensorName = v.findViewById(R.id.TxtSensorName);
                        imageSesnorStatus = v.findViewById(R.id.ImageSesnorStatus);

                        txtSensorName.setText(ActiveSensorDetails[i][1]);

                        if (ActiveSensorDetails[i][3].equals("0") || ActiveSensorDetails[i][3].equals("False")) {
                            imageSesnorStatus.setBackgroundResource(R.drawable.mic_low_img);
                            txtPressureStatus.setText("No Sound");
                        } else {
                            imageSesnorStatus.setBackgroundResource(R.drawable.mic_high_img);
                            txtPressureStatus.setText("Sound Detected!");
                        }
                        runOnUI(new Runnable() { //used to speak to main thread
                            @Override
                            public void run() {
                                linearLayoutSensorStatus.addView(v);
                            }
                        });

                    }
                }catch (Exception E){

                }

            }
        }

        return ActiveSensorDetails;


    }



    private String[][] getNextScheudleDue(String[][] OldQueue){

        final LayoutInflater layoutQueueSchedule = LayoutInflater.from(this.context);//Used to inflate the schedules to the user
        String[][] QueueSchedule = getQueuePumps();
        Boolean QueueEquals = false;
        for (int i = 0; i < QueueSchedule.length; i++) {

            if (OldQueue.length == QueueSchedule.length) { //if its not the same length, then We need to update the Layout View
                if (OldQueue[i][0].equals(QueueSchedule[i][0])) {
                    QueueEquals = true;
                } else {
                    QueueEquals = false;
                    i = QueueSchedule.length + 1; //jumps out the for loop

                }
            }


        }


        if (QueueEquals == false) {
            OldQueue = QueueSchedule;
            runOnUI(new Runnable() { //used to speak to main thread
                @Override
                public void run() {
                    linearLayoutQueueZone.removeAllViews();
                }
            });
        }


        TextView txtScheduleName, txtActiveZone, txtActivePump, txtActiveStartTime, txtActiveEndTime, txtNoActivity;

        if (QueueSchedule[0][0].equals("No Data")) {
            final View v = layoutQueueSchedule.inflate(R.layout.activity_no_live_equipment, linearLayoutQueueZone, false);
            txtNoActivity = v.findViewById(R.id.TxtNoActivity);
            txtNoActivity.setText("No Active Equipment Queued");

            if (QueueEquals == false) {
                runOnUI(new Runnable() { //used to speak to main thread
                    @Override
                    public void run() {
                        linearLayoutQueueZone.addView(v);
                    }
                });
            }

        } else {
            if (QueueEquals == false) {
                for (int i = 0; i < QueueSchedule.length; i++) {
                    final View v = layoutQueueSchedule.inflate(R.layout.activity_live_view_equipment_status, linearLayoutQueueZone, false);
                    txtScheduleName = v.findViewById(R.id.TxtScheduleName);
                    txtActiveZone = v.findViewById(R.id.TxtActiveZone);
                    txtActivePump = v.findViewById(R.id.TxtActivePump);
                    txtActiveStartTime = v.findViewById(R.id.TxtActiveStartTime);
                    txtActiveEndTime = v.findViewById(R.id.TxtActiveEndTime);
                    txtScheduleName.setText("Schedule: " + QueueSchedule[i][1]);
                    txtActivePump.setText("Pump: " + QueueSchedule[i][2]);
                    txtActiveZone.setText("Zone: " + QueueSchedule[i][3]);
                    txtActiveStartTime.setText("Start: " + QueueSchedule[i][4]);
                    txtActiveEndTime.setText("End: " + QueueSchedule[i][5]);

                    runOnUI(new Runnable() { //used to speak to main thread
                        @Override
                        public void run() {
                            linearLayoutQueueZone.addView(v);
                        }
                    });
                }
            }
        }
        return QueueSchedule;
    }

    private String getManualPagePumpsZones(int OLDPumpCount, int OLDZoneCount){
        LayoutInflater layoutInflaterScrollManualPump = LayoutInflater.from(context);//Used to inflate the Zones and pumps
        LayoutInflater layoutInflaterScrollManualZone = LayoutInflater.from(context);
        Button btnPump, btnPump2, btnPump3, btnValve, btnValve2, btnValve3, btnStop;
        btnStop = view.findViewById(R.id.BtnStopManual);
        String[] differentButtons;

        //Display Loading screen
        if(OLDPumpCount == 0){
            Button dialogLoadCancel;

            final View loadingScreen = layoutInflaterScrollManualPump.inflate(R.layout.loading_screen, linearLayoutScrollManualPump, false);
            dialogLoadCancel = loadingScreen.findViewById(R.id.BtnCancel);
            dialogLoadCancel.setVisibility(View.GONE);

            runOnUI(new Runnable() { //used to speak to main thread
                @Override
                public void run() {
                    linearLayoutScrollManualPump.removeAllViews();
                    linearLayoutScrollManualPump.addView(loadingScreen);
                }
            });
        }




        final SocketController socketControllerPumps = new SocketController(context, "getPumps");

        String processData = "";

        try {
            processData = socketControllerPumps.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR).get();
            //final SocketController finalSocketController = socketController;
            runOnUI(new Runnable() { //used to speak to main thread
                @Override
                public void run() {
                    listener.onObjectReady(socketControllerPumps.getPingTime());
                }
            });
        } catch (ExecutionException e) {

        } catch (InterruptedException i) {

        }



        if (!processData.equals("No Data") && !processData.equals("Server Not Running")) {
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
                    btnPump.setOnClickListener(SlideAdapter.this);
                    v.setId(i*(-1));
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
                                finalBtnPump2.setOnClickListener(SlideAdapter.this);
                            }
                        });


                        pumpIDs.add(buttonInfo2[0]);

                        if ((i + 1) < differentButtons.length) {
                            i++;
                            String[] buttonInfo3 = differentButtons[i].split(","); //stores the information for the second Zone
                            btnPump3.setId(Integer.parseInt(buttonInfo3[0]));


                            btnPump3.setText(buttonInfo3[1]);
                            btnPump3.setVisibility(View.VISIBLE);
                            btnPump3.setOnClickListener(SlideAdapter.this);
                            pumpIDs.add(buttonInfo3[0]);
                        }

                    }

                    final Button finalBtnPump = btnPump;
                    final Button finalBtnPump2 = btnPump2;
                    final Button finalBtnPump3 = btnPump3;
                    //final boolean finalManualSchedule = manualSchedule;
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


        if(OLDZoneCount == 0){
            Button dialogLoadCancel;

            final View loadingScreen = layoutInflaterScrollManualPump.inflate(R.layout.loading_screen, linearLayoutScrollManualZone, false);
            dialogLoadCancel = loadingScreen.findViewById(R.id.BtnCancel);
            dialogLoadCancel.setVisibility(View.GONE);

            runOnUI(new Runnable() { //used to speak to main thread
                @Override
                public void run() {
                    linearLayoutScrollManualZone.removeAllViews();
                    linearLayoutScrollManualZone.addView(loadingScreen);
                }
            });
        }


        final SocketController socketControllerZone = new SocketController(context, "getValves");
        try {
            processData = socketControllerZone.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR).get();
            //final SocketController finalSocketController1 = socketController;
            runOnUI(new Runnable() { //used to speak to main thread
                @Override
                public void run() {
                    listener.onObjectReady(socketControllerZone.getPingTime());
                }
            });
        } catch (ExecutionException e) {

        } catch (InterruptedException i) {

        }
        if (processData.equals("No Data") || processData.equals("Server Not Running")) {
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
                //int j = 0;
                for (int i = 0; i < differentButtons.length; i++) {

                    String[] buttonInfo = differentButtons[i].split(",");

                    View v = layoutInflaterScrollManualZone.inflate(R.layout.all_valves, linearLayoutScrollManualZone, false);

                    btnValve = v.findViewById(R.id.BtnValve);
                    btnValve2 = v.findViewById(R.id.BtnValve2);
                    btnValve3 = v.findViewById(R.id.BtnValve3);

                    btnValve.setBackgroundResource(android.R.drawable.btn_default);
                    btnValve2.setBackgroundResource(android.R.drawable.btn_default);
                    btnValve3.setBackgroundResource(android.R.drawable.btn_default);
                    btnValve2.setVisibility(View.GONE);
                    btnValve3.setVisibility(View.GONE);
                    v.setId(i*(-1));
                    //j++;
                    btnValve.setId(Integer.parseInt(buttonInfo[0]));

                    // if(manualSchedule == true) {
                    //     for (int l = 0; l < RunningselectedEquipment.size(); l++) {
                    //        if (RunningselectedEquipment.get(l) == Integer.parseInt(buttonInfo[0])) {
                    //            btnValve.setBackgroundColor(Color.CYAN);
                    //         }
                    //     }
                    //}

                    btnValve.setText(buttonInfo[1]);
                    btnValve.setOnClickListener(SlideAdapter.this);
                    zoneIDs.add(buttonInfo[0]);
                    if ((i + 1) < differentButtons.length) {
                        i++;
                        String[] buttonInfo2 = differentButtons[i].split(","); //stores the information for the second Zone
                        btnValve2.setId(Integer.parseInt(buttonInfo2[0]));

                        //if(manualSchedule == true) {
                        //    for (int l = 0; l < RunningselectedEquipment.size(); l++) {
                        //       if (RunningselectedEquipment.get(l) == Integer.parseInt(buttonInfo2[0])) {
                        //            btnValve2.setBackgroundColor(Color.CYAN);
                        //        }
                        //    }
                        //}


                        btnValve2.setText(buttonInfo2[1]);
                        btnValve2.setVisibility(View.VISIBLE);
                        btnValve2.setOnClickListener(SlideAdapter.this);
                        zoneIDs.add(buttonInfo2[0]);
                        if ((i + 1) < differentButtons.length) {
                            i++;
                            String[] buttonInfo3 = differentButtons[i].split(","); //stores the information for the second Zone
                            btnValve3.setId(Integer.parseInt(buttonInfo3[0]));


                            btnValve3.setText(buttonInfo3[1]);
                            btnValve3.setVisibility(View.VISIBLE);
                            btnValve3.setOnClickListener(SlideAdapter.this);
                            zoneIDs.add(buttonInfo3[0]);
                        }
                    }

                    //final boolean finalManualSchedule1 = manualSchedule;
                    final View finalView = v;
                    runOnUI(new Runnable() { //used to speak to main thread
                        @Override
                        public void run() {
                            linearLayoutScrollManualZone.addView(finalView);
                        }
                    });
                    //j++;                                                        //_____________________________________________________________Valves
                }

            }
        }
        // run = true;

    return OLDPumpCount + "," + OLDZoneCount;
    }

    private void StopManual(){

        progressBarManual.setVisibility(View.VISIBLE);
        new Thread(new Runnable() { //Running on a new thread
            public void run() { //used to ge the pumps that are in today's schedule
                try{
                    //SocketController socketController = new SocketController(context,"ping");
                    SocketController socketController = new SocketController(context,"StopManualSchedule");
                    final String responce = socketController.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR).get();

                    if(!responce.equals("Server Not Running")) {

                        runOnUI(new Runnable() { //used to speak to main thread
                            @Override
                            public void run() {

                                progressBarManual.setVisibility(View.GONE);
                                ManualSuccsessfullImage.setVisibility(View.VISIBLE);
                                ManualSuccsessfullImage.setImageResource(R.drawable.ic_thumb_up_black_24dp);

                            }
                        });

                        SystemClock.sleep(5000);
                        runOnUI(new Runnable() { //used to speak to main thread
                            @Override
                            public void run() {

                                progressBarManual.setVisibility(View.INVISIBLE);
                                ManualSuccsessfullImage.setVisibility(View.GONE);
                            }
                        });

                    }else{
                        runOnUI(new Runnable() { //used to speak to main thread
                            @Override
                            public void run() {

                                progressBarManual.setVisibility(View.GONE);
                                ManualSuccsessfullImage.setVisibility(View.VISIBLE);
                                ManualSuccsessfullImage.setImageResource(R.drawable.ic_thumb_down_black_24dp);

                            }
                        });
                    }

                }catch (ExecutionException e){

                    runOnUI(new Runnable() { //used to speak to main thread
                        @Override
                        public void run() {

                            progressBarManual.setVisibility(View.GONE);
                            ManualSuccsessfullImage.setVisibility(View.VISIBLE);
                            ManualSuccsessfullImage.setImageResource(R.drawable.ic_thumb_down_black_24dp);

                        }
                    });

                }catch (InterruptedException i){
                    runOnUI(new Runnable() { //used to speak to main thread
                        @Override
                        public void run() {

                            progressBarManual.setVisibility(View.GONE);
                            ManualSuccsessfullImage.setVisibility(View.VISIBLE);
                            ManualSuccsessfullImage.setImageResource(R.drawable.ic_thumb_down_black_24dp);

                        }
                    });
                }
            }
        }).start();


        btnStartManual.setEnabled(true);
        editTextDuration.setEnabled(true);
        editTextDuration.setText("");
        Button btnPump, btnPump2, btnPump3, btnValve, btnValve2, btnValve3;
        int j = 0;
        for (int i = 0; i < linearLayoutScrollManualPump.getChildCount(); i++) {
            View view = linearLayoutScrollManualPump.getChildAt(i);
            btnPump = view.findViewById(Integer.parseInt(pumpIDs.get(j)));
            btnPump.setBackgroundResource(android.R.drawable.btn_default);
            btnPump.setEnabled(true);
            if ((j + 1) < pumpIDs.size()) {
                j++;
                btnPump2 = view.findViewById(Integer.parseInt(pumpIDs.get(j)));
                btnPump2.setBackgroundResource(android.R.drawable.btn_default);
                btnPump2.setEnabled(true);
                if ((j + 1) < pumpIDs.size()) {
                    j++;
                    btnPump3 = view.findViewById(Integer.parseInt(pumpIDs.get(j)));
                    btnPump3.setBackgroundResource(android.R.drawable.btn_default);
                    btnPump3.setEnabled(true);
                }
            }
            j++;
        }

        j = 0;
        for (int i = 0; i < linearLayoutScrollManualZone.getChildCount(); i++) {
            View view = linearLayoutScrollManualZone.getChildAt(i);

            btnValve = view.findViewById(Integer.parseInt(zoneIDs.get(j)));
            btnValve.setBackgroundResource(android.R.drawable.btn_default);
            btnValve.setEnabled(true);
            if ((j + 1) < zoneIDs.size()) {
                j++;
                btnValve2 = view.findViewById(Integer.parseInt(zoneIDs.get(j)));
                btnValve2.setBackgroundResource(android.R.drawable.btn_default);
                btnValve2.setEnabled(true);
                if ((j + 1) < zoneIDs.size()) {
                    j++;
                    btnValve3 = view.findViewById(Integer.parseInt(zoneIDs.get(j)));
                    btnValve3.setBackgroundResource(android.R.drawable.btn_default);
                    btnValve3.setEnabled(true);
                }
            }
            j++;
        }
        selectedEquipment.clear();
    }

    private void StartManual(){
        if (selectedEquipment.size() <= 0 || editTextDuration.length() != 4) {
            if (selectedEquipment.size() <= 0) {
                textView14.setTextColor(Color.parseColor("#FF0000"));
                textView15.setTextColor(Color.parseColor("#FF0000"));
            } else {
                textView14.setTextColor(Color.parseColor("#FFFFFF"));
                textView15.setTextColor(Color.parseColor("#FFFFFF"));
            }
            if (editTextDuration.length() != 4) {
                textView17.setTextColor(Color.parseColor("#FF0000"));
            } else {
                textView17.setTextColor(Color.parseColor("#FFFFFF"));
            }
        } else {
            Button btnPump, btnPump2, btnPump3, btnValve, btnValve2, btnValve3;
            btnStartManual.setEnabled(false);
            editTextDuration.setEnabled(false);
            int j = 0;
            for (int i = 0; i < linearLayoutScrollManualPump.getChildCount(); i++) {
                View view = linearLayoutScrollManualPump.getChildAt(i);
                btnPump = view.findViewById(Integer.parseInt(pumpIDs.get(j)));
                btnPump.setEnabled(false);
                if ((j+1) < pumpIDs.size()) {
                    j++;
                    btnPump2 = view.findViewById(Integer.parseInt(pumpIDs.get(j)));
                    btnPump2.setEnabled(false);
                    if ((j+1) < pumpIDs.size()) {
                        j++;
                        btnPump3 = view.findViewById(Integer.parseInt(pumpIDs.get(j)));
                        btnPump3.setEnabled(false);
                    }
                }
                j++;
            }
            //int test = zoneIDs.size();
            //test = test;


            j = 0;
            for (int i = 0; i < linearLayoutScrollManualZone.getChildCount(); i++) {
                View view = linearLayoutScrollManualZone.getChildAt(i);

                btnValve = view.findViewById(Integer.parseInt(zoneIDs.get(j)));
                //btnValve.setBackgroundResource(android.R.drawable.btn_default);
                btnValve.setEnabled(false);
                if ((j + 1) < zoneIDs.size()) {
                    j++;
                    //i++;
                    btnValve2 = view.findViewById(Integer.parseInt(zoneIDs.get(j)));
                    //btnValve2.setBackgroundResource(android.R.drawable.btn_default);
                    btnValve2.setEnabled(false);
                    if ((j + 1) < zoneIDs.size()) {
                        j++;
                        //i++;
                        btnValve3 = view.findViewById(Integer.parseInt(zoneIDs.get(j)));
                        //btnValve3.setBackgroundResource(android.R.drawable.btn_default);
                        btnValve3.setEnabled(false);
                    }
                    j++;
                }
            }



            String send = "";
            send = send + editTextDuration.getText();


            if(switchAsyncRun.isChecked()){
                send = send + ",1";
            }else{
                send = send + ",0";
            }

            for (int i = 0; i < selectedEquipment.size(); i++) {
                send = send + "," + selectedEquipment.get(i);
            }
            send = send + "$MANUALSCHEDULE";




            progressBarManual.setVisibility(View.VISIBLE);
            final String finalSend = send;
            new Thread(new Runnable() { //Running on a new thread
                public void run() { //used to ge the pumps that are in today's schedule
                    try{
                        //SocketController socketController = new SocketController(context,"ping");
                        SocketController socketController = new SocketController(context, finalSend);

                        final String responce = socketController.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR).get();

                        if(!responce.equals("Server Not Running")) {

                            runOnUI(new Runnable() { //used to speak to main thread
                                @Override
                                public void run() {

                                    progressBarManual.setVisibility(View.GONE);
                                    ManualSuccsessfullImage.setVisibility(View.VISIBLE);
                                    ManualSuccsessfullImage.setImageResource(R.drawable.ic_thumb_up_black_24dp);

                                }
                            });

                            SystemClock.sleep(5000);
                            runOnUI(new Runnable() { //used to speak to main thread
                                @Override
                                public void run() {

                                    progressBarManual.setVisibility(View.INVISIBLE);
                                    ManualSuccsessfullImage.setVisibility(View.GONE);
                                }
                            });

                        }else{
                            runOnUI(new Runnable() { //used to speak to main thread
                                @Override
                                public void run() {

                                    progressBarManual.setVisibility(View.GONE);
                                    ManualSuccsessfullImage.setVisibility(View.VISIBLE);
                                    ManualSuccsessfullImage.setImageResource(R.drawable.ic_thumb_down_black_24dp);

                                }
                            });
                        }

                    }catch (ExecutionException e){

                        runOnUI(new Runnable() { //used to speak to main thread
                            @Override
                            public void run() {

                                progressBarManual.setVisibility(View.GONE);
                                ManualSuccsessfullImage.setVisibility(View.VISIBLE);
                                ManualSuccsessfullImage.setImageResource(R.drawable.ic_thumb_down_black_24dp);

                            }
                        });

                    }catch (InterruptedException i){
                        runOnUI(new Runnable() { //used to speak to main thread
                            @Override
                            public void run() {

                                progressBarManual.setVisibility(View.GONE);
                                ManualSuccsessfullImage.setVisibility(View.VISIBLE);
                                ManualSuccsessfullImage.setImageResource(R.drawable.ic_thumb_down_black_24dp);

                            }
                        });
                    }
                }
            }).start();

        }
    }

    public boolean UpdateManualButtonStatus( boolean OLDmanualSchedule) { //Used to change button status smoothly

        boolean manualSchedule;
        boolean run = false; //Check if code has ran to populate buttons


        String durationTime = "";
        final SocketController socketControllerManual = new SocketController(context,"getManualSchedule");

        String processData = "";

        try {
            processData = socketControllerManual.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR).get();

        } catch (ExecutionException e) {

        } catch (InterruptedException i) {

        }
        RunningselectedEquipment.clear();
        if (processData.equals("No Data")|| processData.equals("Server Not Running")) {
            manualSchedule = false;

            runOnUI(new Runnable() { //used to speak to main thread
                @Override
                public void run() {
                    switchAsyncRun.setEnabled(true);
                }
            });


        } else {
            manualSchedule = true;
            try{

                String[] processDataWithManualStatus = processData.split(Pattern.quote("$"));


                runOnUI(new Runnable() { //used to speak to main thread
                    @Override
                    public void run() {
                        switchAsyncRun.setEnabled(false);
                    }
                });

                if(processDataWithManualStatus[0].equals("0")){
                    runOnUI(new Runnable() { //used to speak to main thread
                        @Override
                        public void run() {
                            switchAsyncRun.setEnabled(false);
                        }
                    });

                }else{
                    runOnUI(new Runnable() { //used to speak to main thread
                        @Override
                        public void run() {
                            switchAsyncRun.setEnabled(true);
                        }
                    });

                }

                String[] equipmentOn = processDataWithManualStatus[1].split("#");
                for (int i = 0; i < equipmentOn.length; i++) {
                    String[] data = equipmentOn[i].split(",");
                    RunningselectedEquipment.add(Integer.parseInt(data[0]));
                }
                String[] dataTime = equipmentOn[0].split(",");
                durationTime = dataTime[1];
            }catch (Exception e){

            }


        }






        if ((manualSchedule == true)  || OLDmanualSchedule != manualSchedule) {

            DateFormat dateFormat = new SimpleDateFormat("HH:mm");
            Date date = new Date();

            String time1 = dateFormat.format(date);
            String time2 = durationTime;

            SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
            String Time = "";
            try {

                String[] arrayTime1 = time1.split(":");
                String[] arrayTime2 = time2.split(":");
                int minute = Integer.parseInt(arrayTime2[1]) - Integer.parseInt(arrayTime1[1]);
                int hour = Integer.parseInt(arrayTime2[0]) - Integer.parseInt(arrayTime1[0]);
                if(minute <0 && hour>0){
                    hour--;
                    minute = minute + 60;

                }
                if (minute < 10) {
                    Time = hour + ":0" + minute;
                } else
                    Time = hour + ":" + minute;
            } catch (Exception e) {
                e.printStackTrace();
            }

            final boolean finalManualSchedule = manualSchedule;
            final String finalDurationTime = durationTime;
            final String finalFullTime = Time;
            runOnUI(new Runnable() { //used to speak to main thread
                @Override
                public void run() {

                    try {


                        if (finalManualSchedule == true) {
                            editTextDuration.setTextColor(Color.parseColor("#FFFFFF"));
                            btnStartManual.setEnabled(false);
                            editTextDuration.setEnabled(false);
                            textView17.setText("Time Left: ");
                            editTextDuration.setText(finalFullTime);

                        } else {
                            editTextDuration.setTextColor(Color.parseColor("#FFFFFF"));
                            btnStartManual.setEnabled(true);
                            editTextDuration.setEnabled(true);
                            editTextDuration.setText("");
                            textView17.setText("Duration");

                        }

                    } catch (Exception e) {

                    }


                }
            });
        }

        Button btnPump, btnPump2, btnPump3, btnValve, btnValve2, btnValve3;

        if (OLDmanualSchedule != manualSchedule) {
            run = true;
            int j = 0;
            for (int i = 0; i < linearLayoutScrollManualPump.getChildCount(); i++) {
                View v = linearLayoutScrollManualPump.getChildAt(i);
                btnPump = v.findViewById(Integer.parseInt(pumpIDs.get(j)));
                final Button finalBtnPump = btnPump;

                if(manualSchedule == true) {
                    runOnUI(new Runnable() { //used to speak to main thread
                        @Override
                        public void run() {
                            finalBtnPump.setEnabled(false);
                        }
                    });


                    for (int l = 0; l < RunningselectedEquipment.size(); l++) {
                        if (RunningselectedEquipment.get(l) == Integer.parseInt(pumpIDs.get(j))) {
                            runOnUI(new Runnable() { //used to speak to main thread
                                @Override
                                public void run() {
                                    finalBtnPump.setBackgroundColor(Color.CYAN);
                                }
                            });
                        }
                    }
                }else{
                    runOnUI(new Runnable() { //used to speak to main thread
                        @Override
                        public void run() {
                            finalBtnPump.setEnabled(true);
                            finalBtnPump.setBackgroundResource(android.R.drawable.btn_default);
                        }
                    });
                    //btnPump.setBackgroundResource(android.R.drawable.btn_default);
                }


                if ((j+1) < pumpIDs.size()) {
                    j++;
                    btnPump2 = v.findViewById(Integer.parseInt(pumpIDs.get(j)));
                    final Button finalBtnPump2 = btnPump2;
                    if(manualSchedule == true) {

                        runOnUI(new Runnable() { //used to speak to main thread
                            @Override
                            public void run() {
                                finalBtnPump2.setEnabled(false);
                                finalBtnPump2.setBackgroundResource(android.R.drawable.btn_default);
                            }
                        });
                        //btnPump2.setEnabled(false);

                        for (int l = 0; l < RunningselectedEquipment.size(); l++) {
                            if (RunningselectedEquipment.get(l) == Integer.parseInt(pumpIDs.get(j))) {

                                runOnUI(new Runnable() { //used to speak to main thread
                                    @Override
                                    public void run() {
                                        //finalBtnPump.setEnabled(false);
                                        finalBtnPump2.setBackgroundColor(Color.CYAN);
                                    }
                                });



                            }
                        }
                    }else{
                        //final Button finalBtnPump2 = btnPump2;
                        runOnUI(new Runnable() { //used to speak to main thread
                            @Override
                            public void run() {
                                finalBtnPump2.setEnabled(true);
                                finalBtnPump2.setBackgroundResource(android.R.drawable.btn_default);
                            }
                        });

                    }

                    //btnPump2.setEnabled(false);
                    if ((j+1) < pumpIDs.size()) {
                        j++;
                        btnPump3 = v.findViewById(Integer.parseInt(pumpIDs.get(j)));
                        final Button finalBtnPump3 = btnPump3;
                        if(manualSchedule == true) {

                            runOnUI(new Runnable() { //used to speak to main thread
                                @Override
                                public void run() {
                                    finalBtnPump3.setBackgroundResource(android.R.drawable.btn_default);
                                    finalBtnPump3.setEnabled(false);
                                }
                            });

                            for (int l = 0; l < RunningselectedEquipment.size(); l++) {
                                if (RunningselectedEquipment.get(l) == Integer.parseInt(pumpIDs.get(j))) {
                                    runOnUI(new Runnable() { //used to speak to main thread
                                        @Override
                                        public void run() {
                                            finalBtnPump3.setBackgroundColor(Color.CYAN);
                                        }
                                    });
                                }
                            }
                        }else{

                            runOnUI(new Runnable() { //used to speak to main thread
                                @Override
                                public void run() {
                                    finalBtnPump3.setEnabled(true);
                                    finalBtnPump3.setBackgroundResource(android.R.drawable.btn_default);
                                }
                            });
                            btnPump3.setBackgroundResource(android.R.drawable.btn_default);
                        }


                    }
                }
                j++;
            }



            j = 0;
            for (int i = 0; i < linearLayoutScrollManualZone.getChildCount(); i++) {
                View v = linearLayoutScrollManualZone.getChildAt(i);

                btnValve = v.findViewById(Integer.parseInt(zoneIDs.get(j)));
                final Button finalBtnValve = btnValve;
                if(manualSchedule == true) {


                    runOnUI(new Runnable() { //used to speak to main thread
                        @Override
                        public void run() {
                            finalBtnValve.setEnabled(false);
                            finalBtnValve.setBackgroundResource(android.R.drawable.btn_default);
                        }
                    });

                    //btnValve.setEnabled(false);
                    for (int l = 0; l < RunningselectedEquipment.size(); l++) {
                        if (RunningselectedEquipment.get(l) == Integer.parseInt(zoneIDs.get(j))) {
                            runOnUI(new Runnable() { //used to speak to main thread
                                @Override
                                public void run() {
                                    //finalBtnValve.setEnabled(true);
                                    finalBtnValve.setBackgroundColor(Color.CYAN);
                                }
                            });
                        }
                    }
                }else{

                    runOnUI(new Runnable() { //used to speak to main thread
                        @Override
                        public void run() {
                            finalBtnValve.setEnabled(true);
                            finalBtnValve.setBackgroundResource(android.R.drawable.btn_default);
                        }
                    });

                }

                //btnValve.setBackgroundResource(android.R.drawable.btn_default);
                //btnValve.setEnabled(false);
                if ((j + 1) < zoneIDs.size()) {
                    j++;
                    //i++;
                    btnValve2 = v.findViewById(Integer.parseInt(zoneIDs.get(j)));
                    final Button finalBtnValve2 = btnValve2;
                    if(manualSchedule == true) {


                        runOnUI(new Runnable() { //used to speak to main thread
                            @Override
                            public void run() {
                                finalBtnValve2.setEnabled(false);
                                finalBtnValve2.setBackgroundResource(android.R.drawable.btn_default);
                            }
                        });


                        for (int l = 0; l < RunningselectedEquipment.size(); l++) {
                            if (RunningselectedEquipment.get(l) == Integer.parseInt(zoneIDs.get(j))) {
                                runOnUI(new Runnable() { //used to speak to main thread
                                    @Override
                                    public void run() {
                                        //finalBtnValve2.setEnabled(false);
                                        finalBtnValve2.setBackgroundColor(Color.CYAN);
                                    }
                                });
                                //btnValve2
                            }
                        }
                    }else{
                        runOnUI(new Runnable() { //used to speak to main thread
                            @Override
                            public void run() {
                                finalBtnValve2.setEnabled(true);
                                finalBtnValve2.setBackgroundResource(android.R.drawable.btn_default);
                            }
                        });

                    }
                    //
                    //btnValve2.setEnabled(false);
                    if ((j + 1) < zoneIDs.size()) {
                        j++;
                        //i++;
                        btnValve3 = v.findViewById(Integer.parseInt(zoneIDs.get(j)));
                        final Button finalBtnValve3 = btnValve3;
                        if(manualSchedule == true) {


                            runOnUI(new Runnable() { //used to speak to main thread
                                @Override
                                public void run() {
                                    finalBtnValve3.setEnabled(false);
                                    finalBtnValve3.setBackgroundResource(android.R.drawable.btn_default);
                                }
                            });

                            for (int l = 0; l < RunningselectedEquipment.size(); l++) {
                                if (RunningselectedEquipment.get(l) == Integer.parseInt(zoneIDs.get(j))) {

                                    runOnUI(new Runnable() { //used to speak to main thread
                                        @Override
                                        public void run() {
                                            //finalBtnValve3.setEnabled(false);
                                            finalBtnValve3.setBackgroundColor(Color.CYAN);
                                        }
                                    });

                                    //btnValve3.setBackgroundColor(Color.CYAN);
                                }
                            }
                        }else{

                            runOnUI(new Runnable() { //used to speak to main thread
                                @Override
                                public void run() {
                                    finalBtnValve3.setEnabled(true);
                                    finalBtnValve3.setBackgroundResource(android.R.drawable.btn_default);
                                }
                            });


                        }

                        //btnValve3.setBackgroundResource(android.R.drawable.btn_default);
                        //btnValve3.setEnabled(false);
                    }
                    j++;
                }
            }



            //}
            //run = true;
            OLDmanualSchedule = manualSchedule;
            RunningselectedEquipment.clear();
            SystemClock.sleep(8000);

        }
        //}
        //}).start();
        return OLDmanualSchedule;
    }
}

