package com.example.serviceevent;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.XAxisValueFormatter;
import com.github.mikephil.charting.formatter.YAxisValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;


import java.util.ArrayList;


public class InfoActivity extends AppCompatActivity {


    private int stepSleep[] = new int[3];

    // 출력을 위한 어레이리스트
    ArrayList<String> print = new ArrayList<>();

    private TextView sleepTime;
    private TextView sleepQulity;
    private TextView sleepTimeRatio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        // 정보 표시를 위한 변수들 받음 (preTime, time, motionCounter)
        Intent intent = getIntent();
        ArrayList<Integer> time = (ArrayList<Integer>) intent.getSerializableExtra("time");
        ArrayList<Integer> motionCounter = (ArrayList<Integer>) intent.getSerializableExtra("motionCounter");
        ArrayList<Integer> preTime = (ArrayList<Integer>) intent.getSerializableExtra("preTime");

        printInfo(preTime, time, motionCounter); // 수면시간, 수면질, 구간별 움직임
        draw(time, motionCounter);// 그래프

    }

    private void printInfo(ArrayList<Integer> preTime, ArrayList<Integer> time, ArrayList<Integer> motionCounter){


        // 수면시간
        int startTime = preTime.get(0);
        int endTime = preTime.get(preTime.size()-1);
        int totalTime = endTime - startTime;
        sleepTime = findViewById(R.id.sleepTime);
        sleepTime.setText("총 수면시간 : " + Integer.toString(totalTime) + "분");

        // 뒤척임 정도에 기반한 수면질
        String sq = Double.toString(setSQ(motionCounter));
        sleepQulity = findViewById(R.id.sleepQulity);
        sleepQulity.setText("수면 퀄리티 : " + sq + "%");

        // 뒤척임기반으로 한 수면종류 구분
        int percent[] = new int[3];
        for(int i=0; i<stepSleep.length; i++){
            if(stepSleep[i] == 0){
                percent[i] = 0;
            }
            else{
                if(totalTime != 0) {
                    percent[i] = stepSleep[i] / totalTime * 100;
                }
                else{
                    percent[i] = 0;
                }
            }
        }

        // 뒤척임 정도에 따른 수면시간 퍼센테이지
        sleepTimeRatio = findViewById(R.id.sleepTimeRatio);
        sleepTimeRatio.setText("뒤척임 없음 : " + percent[0] + "% , "
        + "조금 뒤척임 : " + percent[1] + "% , "
        + "많이 뒤척임 : " + percent[2] + "%");

        // 구간별 움직임 횟수
        String p;
        for(int i=0; i<time.size(); i++){

            if(i==0){
                p = "~ " + timeToString(time.get(i)) + " : " + motionCounter.get(i);
            }
            else {
                p = timeToString(time.get(i-1)) + " ~ " + timeToString(time.get(i)) + " : " + motionCounter.get(i);
            }
            print.add(p);
        }

        ListView printView = (ListView)findViewById(R.id.countPerTime);
        final ArrayAdapter<String> timeAdabtor = new ArrayAdapter<String>(
                this, android.R.layout.simple_list_item_1, print);
        printView.setAdapter(timeAdabtor);


    }

    // 몇분몇초 변환
    private String timeToString(int time){
        String hour = Integer.toString(time / 60);
        String min = Integer.toString(time % 60);

        return hour + "시 " + min + "분";
    }

    // 수면질 계산 메소드
    private double setSQ(ArrayList<Integer> motionCounter){

        double SQ;

        for(int i=0; i<stepSleep.length; i++){
            stepSleep[i] = 0;
        }

        for(int i : motionCounter){

            if( i==0 ){ // 뒤척임 없음 stepSleep[0]
                stepSleep[0] += 15;
            }
            else if( (i>0) && (i<=15) ){ // 뒤척임 적음 stepSleep[1]
                stepSleep[1] += 15;
            }
            else if( (i>15) && (i<40) ){ // 뒤척임 많음  stepSleep[2] , max 값 40은 기상상태
                stepSleep[2] += 15;
            }

        }
        SQ = (((stepSleep[2]*0.5) + (stepSleep[1]*0.75) + (stepSleep[0]))*100)/(stepSleep[0] + stepSleep[1] + stepSleep[2]);
        return SQ;

    }

    private void draw(ArrayList<Integer> time, ArrayList<Integer> motionCounter){

        LineChart lineChart = (LineChart) findViewById(R.id.lineChart);
        ArrayList<Entry> countGraph = new ArrayList<>();

        // 데이터 추가
        for(int i=0; i<time.size(); i++){

            float count = Float.parseFloat(String.valueOf(motionCounter.get(i)));
            countGraph.add(new Entry(count,i));

        }

        //x축 값 설정 (modTime)
        String[] xaxes = new String[time.size()];

        for(int i=0; i<time.size(); i++){
            xaxes[i] = timeToString(time.get(i));
        }

        LineDataSet lineDataSet1 = new LineDataSet(countGraph, "motionCounter");

        lineDataSet1.setAxisDependency(YAxis.AxisDependency.LEFT);
        lineDataSet1.setDrawCubic(true);
        lineDataSet1.setDrawHorizontalHighlightIndicator(true);
        lineDataSet1.setColor(Color.BLUE);
        lineDataSet1.setDrawCircles(false);

        // y축 오른쪽은 표시 안함
        YAxis rightYAxis = lineChart.getAxisRight();
        rightYAxis.setEnabled(false);

        // y축 왼쪽
        YAxis leftYAxis = lineChart.getAxisLeft();
        leftYAxis.setValueFormatter(new MyYAxisValueFormatter());
        leftYAxis.setEnabled(true);
        leftYAxis.setAxisMaxValue(45);
        leftYAxis.setLabelCount(3,true);
        leftYAxis.setDrawAxisLine(true);
        leftYAxis.setDrawGridLines(true);

        // x축
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM); // x축 위치 지정
        xAxis.setValueFormatter(new MyCustomXAxisValueFormatter());
        xAxis.setDrawAxisLine(true); // x축 라인을 그림 (라벨이 없을때 잘 됨)
        xAxis.setDrawGridLines(true); // 내부 선 그을지 결정
        //xAxis.setLabelCount(2); // 라벨의 개수를 결정 => 나누어 떨어지는 개수로 지정
        //xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
        //xAxis.setValueFormatter(formatter);
        //xAxis.setTextSize(10f); // 크기 지정
        //xAxis.setTextColor(Color.RED); // 색 지정
        //xAxis.setDrawLabels(true); // 라벨(x축 좌표)를 그릴지 결정

        LineData data = new LineData(xaxes,lineDataSet1);
        lineChart.setData(data);
        lineChart.setVisibleXRangeMaximum(65f);
        lineChart.getLegend().setEnabled(false);
        lineChart.setDescription("뒤척임정도");

        data.setDrawValues(false);

    }

    public class MyYAxisValueFormatter implements YAxisValueFormatter {

        @Override
        public String getFormattedValue(float value, YAxis yAxis) {
            // write your logic here
            // access the YAxis object to get more information
            System.out.println(yAxis.mEntries[2]);
            if(value == yAxis.mEntries[0]){
                return "없음";
            }
            if(value == yAxis.mEntries[1]){
                return "적음";
            }
            if(value == yAxis.mEntries[2]){
                return "많음";
            }
            return "";
        }

    }


    public class MyCustomXAxisValueFormatter implements XAxisValueFormatter {

        Intent intent = getIntent();
        ArrayList<Integer> time = (ArrayList<Integer>) intent.getSerializableExtra("time");

        @Override
        public String getXValue(String original, int index, ViewPortHandler viewPortHandler) {

            if(index == 0){ // 처음값
                //return Integer.toString(time.get(0)/60) + "시" + Integer.toString(time.get(0)%60) + "분";
                return timeToString(time.get(index));
            }
            else if(index == time.size()-1){ // 맨마지막값
                //return Integer.toString(time.get(time.size()-1)/60) + "시" + Integer.toString(time.get(time.size()-1)%60) + "분";
                return timeToString(time.get(index));
            }
            else if((time.get(index)%60) == 0){ // 정각
                return timeToString(time.get(index));
            }
            return "";
        }
    }





}
