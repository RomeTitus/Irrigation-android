package com.example.pump;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

public class Charts extends AppCompatActivity {
    HorizontalBarChart barChart;
    LinearLayout ChartLinearLayout, LinearLayoutSetGraphTime;
    Button BtnChartBack, BtnChartExit, BtnYear, BtnMonth, BtnWeek, BtnYesterday, BtnCustom;
    public static Handler UIHandler = new Handler();
    public static void runOnUI(Runnable runnable) { //Runs code to invoke the main thread
        UIHandler.post(runnable);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charts);
        ChartLinearLayout = findViewById(R.id.ChartLinearLayout);
        LinearLayoutSetGraphTime = findViewById(R.id.LinearLayoutSetGraphTime);
        barChart = findViewById(R.id.mp_BatChart);

        BtnChartExit = findViewById(R.id.BtnChartExit);
        BtnYear = findViewById(R.id.BtnYear);
        BtnMonth = findViewById(R.id.BtnMonth);
        BtnWeek = findViewById(R.id.BtnWeek);
        BtnYesterday = findViewById(R.id.BtnYesterday);
        BtnCustom = findViewById(R.id.BtnCustom);

        BtnChartBack = findViewById(R.id.BtnChartBack);
        ChartLinearLayout.setVisibility(View.GONE);
        LinearLayoutSetGraphTime.setVisibility(View.VISIBLE);


        BtnYear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //GET a year of Data
                getReadingTotal();
                ChartLinearLayout.setVisibility(View.VISIBLE);
                LinearLayoutSetGraphTime.setVisibility(View.GONE);

            }
        });

        BtnMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //GET a Month of Data
                getLast30Days();
                ChartLinearLayout.setVisibility(View.VISIBLE);
                LinearLayoutSetGraphTime.setVisibility(View.GONE);

            }
        });

        BtnWeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //GET Last week of Data
                ChartLinearLayout.setVisibility(View.VISIBLE);
                LinearLayoutSetGraphTime.setVisibility(View.GONE);
                getLastWeekData();
            }
        });

        BtnYesterday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getYesterdayData();
                ChartLinearLayout.setVisibility(View.VISIBLE);
                LinearLayoutSetGraphTime.setVisibility(View.GONE);

                //Get Yesterday's Data
            }
        });

        BtnCustom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });








        BtnChartBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayoutSetGraphTime.setVisibility(View.VISIBLE);
                ChartLinearLayout.setVisibility(View.GONE);
                barChart.clear();
            }
        });

        BtnChartExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    private ArrayList<BarEntry> dataValues1(){
        ArrayList<BarEntry> dataValue = new ArrayList<BarEntry>();

        dataValue.add(new BarEntry(1,1));
        dataValue.add(new BarEntry(2,2));
        dataValue.add(new BarEntry(3,3));
        dataValue.add(new BarEntry(4,4));
        return dataValue;
}

private void getLastWeekData(){
    new Thread(new Runnable() { //Running on a new thread
        public void run() {

            String processData = "No Data";
            final SocketController socketControllerInternal = new SocketController(Charts.this, "getWeekData");
            try {
                processData = socketControllerInternal.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR).get();
                if(!processData.equals("Server Not Running")){
                    setGraphWithRawData(processData);
                }

            } catch (ExecutionException e) {

            } catch (InterruptedException i) {

            }
            runOnUI(new Runnable() { //used to speak to main thread
                @Override
                public void run() {
                    ChartLinearLayout.setVisibility(View.VISIBLE);
                    LinearLayoutSetGraphTime.setVisibility(View.GONE);
                }
            });
        }
    }).start();
}

    private void getYesterdayData(){
        new Thread(new Runnable() { //Running on a new thread
            public void run() {

                String processData = "No Data";
                final SocketController socketControllerInternal = new SocketController(Charts.this, "getYesterdayData");
                try {
                    processData = socketControllerInternal.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR).get();
                    if(!processData.equals("Server Not Running")){
                        setGraphWithRawData(processData);
                    }

                } catch (ExecutionException e) {

                } catch (InterruptedException i) {

                }
                runOnUI(new Runnable() { //used to speak to main thread
                    @Override
                    public void run() {
                        ChartLinearLayout.setVisibility(View.VISIBLE);
                        LinearLayoutSetGraphTime.setVisibility(View.GONE);
                    }
                });
            }
        }).start();
    }

    private void getLast30Days(){
        new Thread(new Runnable() { //Running on a new thread
            public void run() {

                String processData = "No Data";
                final SocketController socketControllerInternal = new SocketController(Charts.this, "getLast30Days");
                try {
                    processData = socketControllerInternal.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR).get();
                    if(!processData.equals("Server Not Running")){
                        setGraphWithRawData(processData);
                    }

                } catch (ExecutionException e) {

                } catch (InterruptedException i) {

                }
                runOnUI(new Runnable() { //used to speak to main thread
                    @Override
                    public void run() {
                        ChartLinearLayout.setVisibility(View.VISIBLE);
                        LinearLayoutSetGraphTime.setVisibility(View.GONE);
                    }
                });
            }
        }).start();
    }


    private void getReadingTotal(){
        new Thread(new Runnable() { //Running on a new thread
            public void run() {

                String processData = "No Data";
                final SocketController socketControllerInternal = new SocketController(Charts.this, "getReadingTotal");
                try {
                    processData = socketControllerInternal.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR).get();
                    if(!processData.equals("Server Not Running")){
                        setGraphWithRawData(processData);
                    }

                } catch (ExecutionException e) {

                } catch (InterruptedException i) {

                }
                runOnUI(new Runnable() { //used to speak to main thread
                    @Override
                    public void run() {
                        ChartLinearLayout.setVisibility(View.VISIBLE);
                        LinearLayoutSetGraphTime.setVisibility(View.GONE);
                    }
                });
            }
        }).start();
    }

private void setGraphWithRawData(String rawData){

    //rawData = "Pool Pump,1,2020-03-07 07:54,1495$Small Jumping Arena ,10,2020-03-07 09:10,158$Tyre Arena,12,2020-03-07 08:00,179$Nut Hut,13,2020-03-08 16:42,58$Lucerne,15,2020-03-10 16:40,76$David Stables,2,2020-03-09 15:34,64$Big Pump,3,2020-03-07 07:51,949$Long Line,5,2020-03-08 16:05,102$Main Arena,6,2020-03-07 07:51,187$Vegetable Garden,7,2020-03-07 10:13,101$My Garden,8,2020-03-13 13:50,47$Zone 10,9,2020-03-09 09:30,1072";
    //rawData = "Small Jumping Arena ,10,2020-03-07 09:10,158$Tyre Arena,12,2020-03-07 08:00,179$Nut Hut,13,2020-03-08 16:42,58$Lucerne,15,2020-03-10 16:40,76$David Stables,2,2020-03-09 15:34,64$Long Line,5,2020-03-08 16:05,102$Main Arena,6,2020-03-07 07:51,187$Vegetable Garden,7,2020-03-07 10:13,101$My Garden,8,2020-03-13 13:50,47";
    ArrayList<BarEntry> dataValue = new ArrayList<BarEntry>();
    final ArrayList<String> xAxisLabelName = new ArrayList<>();
    BarData barData = new BarData();

    String[] WeekDataArray = rawData.split(Pattern.quote("$"));

    for (int i = 0; i < WeekDataArray.length; i++) {
        String[] WeekDataInfo = WeekDataArray[i].split(",");
        float minutes = Float.parseFloat(WeekDataInfo[3]);
        dataValue.add(new BarEntry(i,minutes));
        xAxisLabelName.add(WeekDataInfo[0]);

    }

    BarDataSet barDataSet1 = new BarDataSet(dataValue, "");
    barDataSet1.setColors(ColorTemplate.COLORFUL_COLORS);
    barDataSet1.setValueTextSize(15);
    barDataSet1.setDrawValues(true);

    barData.addDataSet(barDataSet1);
    //barChart.getLayoutParams().height=100*WeekDataArray.length;
    barChart.setData(barData);
    //barChart.animateXY(2000, 2000);
    barChart.setMaxVisibleValueCount(WeekDataArray.length);


    XAxis xAxis = barChart.getXAxis();
    xAxis.setTextSize(12);

    //xAxis.setPosition(XAxis.XAxisPosition.BOTTOM_INSIDE);
    xAxis.setPosition(XAxis.XAxisPosition.TOP_INSIDE);
    ValueFormatter formatter = new ValueFormatter() {


        @Override
        public String getFormattedValue(float value) {
            try {
                int index = (int) value;
                return xAxisLabelName.get(index);
            } catch (Exception e) {
                return "";
            }
            //return xAxisLabelName.get((int) value);
        }
    };
    xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
    xAxis.setValueFormatter(formatter);



    barChart.invalidate();




}

}
