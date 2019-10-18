package com.example.pump;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.vicmikhailau.maskededittext.MaskedEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class SlaveController extends AppCompatActivity implements View.OnLongClickListener{
    Button btnBTScan;
    LinearLayout linearLayoutBTDiscover, linearLayoutScan;
    SocketController socketController;
    ProgressBar progressBarBT;
    List<String> BTName = new ArrayList<String>();
    List<String> BTAdress = new ArrayList<String>();
    public static Handler UIHandler = new Handler();
    public static void runOnUI(Runnable runnable) { //Runs code to invoke the main thread
        UIHandler.post(runnable);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slave_page);
        btnBTScan = findViewById(R.id.BtnBTScan);
        linearLayoutBTDiscover = findViewById(R.id.LinearLayoutBTDiscover);
        progressBarBT = findViewById(R.id.ProgressBarBT);
        progressBarBT.setVisibility(View.VISIBLE);
        progressBarBT.setMax(200);
        btnBTScan.setEnabled(false);
        linearLayoutScan = findViewById(R.id.LinearLayoutScan);
        scanLayout();
        getBTDiscober();


        btnBTScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBarBT.setVisibility(View.VISIBLE);
                btnBTScan.setEnabled(false);
                getBTDiscober();
            }
        });
    }

    private void scanLayout(){
        linearLayoutScan.setVisibility(View.VISIBLE);
    }

    private void getBTDiscober(){

        new Thread(new Runnable() { //Running on a new thread
            public void run() {


        String processData = "Data Empty";
        socketController = new SocketController(SlaveController.this,"getBTdiscover", 50000);
        try{
            processData = socketController.execute().get();

        }catch (ExecutionException e){

        }catch (InterruptedException i){

        }
        final LayoutInflater layoutInflaterBTDevices = LayoutInflater.from(SlaveController.this);
                runOnUI(new Runnable() { //used to speak to main thread
                    @Override
                    public void run() {
                        progressBarBT.setVisibility(View.INVISIBLE);
                        btnBTScan.setEnabled(true);
                        linearLayoutBTDiscover.removeAllViews();
                    }
                });
                BTName.clear();
                BTAdress.clear();
        if(processData.equals("") || processData.equals("Server Not Running")) {
                //No Data

                TextView BTNames;
                final View view = layoutInflaterBTDevices.inflate(R.layout.all_pumps_toggle, linearLayoutBTDiscover, false);
                BTNames = view.findViewById(R.id.TxtPump);
                BTNames.setText("No Nearby Devices");
            }else{

                String[] BTDevicesList = processData.split("#");



                TextView BTNames;

                for (int i = 0; i < BTDevicesList.length; i++) {
                    final View view = layoutInflaterBTDevices.inflate(R.layout.all_pumps_toggle, linearLayoutBTDiscover, false);
                    String[] BTInfo = BTDevicesList[i].split(",");
                    view.setId(i);
                    //editTextDuration = view.findViewById(R.id.EditTextManualDuration);
                    BTNames = view.findViewById(R.id.TxtPump);
                    BTNames.setOnLongClickListener(SlaveController.this);
                    BTNames.setId(i);
                    BTNames.setTextSize(30);
                    BTName.add(BTInfo[1]);
                    BTAdress.add(BTInfo[0]);
                    BTNames.setText(BTInfo[1]);

                    runOnUI(new Runnable() { //used to speak to main thread
                        @Override
                        public void run() {
                            linearLayoutBTDiscover.addView(view);
                        }
                    });


                }
            }
            }
        }).start();

    }

    @Override
    public boolean onLongClick(View v) {

        final Dialog dialog = new Dialog(SlaveController.this);
        final TextView name, BTInfo, mac, txtHeading, editTextSlaveName;
        Button btnAdd, btnCancel;
        dialog.setContentView(R.layout.activity_expand_equipment);//popup view is the layout you created
        name = dialog.findViewById(R.id.TxtEquipmentName);
        mac = dialog.findViewById(R.id.TxtLocation);
        BTInfo = dialog.findViewById(R.id.TxtDescription);
        txtHeading = dialog.findViewById(R.id.TxtGPIO);
        btnAdd = dialog.findViewById(R.id.BtnEdit);
        btnCancel = dialog.findViewById(R.id.BtnDelete);
        editTextSlaveName = dialog.findViewById(R.id.EditTextSlaveName);

        editTextSlaveName.setVisibility(View.VISIBLE);
        mac.setText("MAC ADDRESS:");
        BTInfo.setText(BTAdress.get(v.getId()));
        name.setText(BTName.get(v.getId()));
        btnAdd.setText("Add Salve");

        btnCancel.setText("CANCEL");

        txtHeading.setText("Enter Controller name");

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if(editTextSlaveName.getText().toString().equals("")){
                txtHeading.setTextColor(Color.parseColor("#FF0000"));
                txtHeading.setText("Put the name here FFS");
            }else{
                String data = editTextSlaveName.getText().toString() +"," + BTInfo.getText() + "$StartComminicationWithBlueTooth";
                socketController = new SocketController(SlaveController.this,data);
                socketController.execute();
                dialog.dismiss();
                finish();

            }
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();

        return true;
    }
}
