package com.example.sampleevent;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

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

public class GraphActivity extends MainActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        // 그래프 그리기 위한 x축(time), y축(motionCounter) 받음
        Intent intent = getIntent();
        ArrayList<Integer> time = (ArrayList<Integer>) intent.getSerializableExtra("time");
        ArrayList<Integer> motionCounter = (ArrayList<Integer>) intent.getSerializableExtra("motionCounter");

        draw(time, motionCounter);

    }


    private void draw(ArrayList<Integer> modTime, ArrayList<Integer> modMotionCounter){

        LineChart lineChart = (LineChart) findViewById(R.id.lineChart);
        ArrayList<Entry> countGraph = new ArrayList<>();

        // 데이터 추가
        for(int i=0; i<modTime.size(); i++){

            float count = Float.parseFloat(String.valueOf(modMotionCounter.get(i)));
            countGraph.add(new Entry(count,i));

        }

         //x축 값 설정 (modTime)
        String[] xaxes = new String[modTime.size()];

        for(int i=0; i<modTime.size(); i++){
              xaxes[i] = modTime.get(i).toString();
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
        leftYAxis.setDrawGridLines(false);

        // x축
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM); // x축 위치 지정
        xAxis.setValueFormatter(new MyCustomXAxisValueFormatter());
        xAxis.setDrawAxisLine(true); // x축 라인을 그림 (라벨이 없을때 잘 됨)
        xAxis.setDrawGridLines(false); // 내부 선 그을지 결정
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

        data.setDrawValues(false);

    }

    public class MyYAxisValueFormatter implements YAxisValueFormatter {

        @Override
        public String getFormattedValue(float value, YAxis yAxis) {
            // write your logic here
            // access the YAxis object to get more information
            System.out.println(yAxis.mEntries[2]);
            if(value == yAxis.mEntries[0]){
                return "깊은수면";
            }
            if(value == yAxis.mEntries[1]){
                return "얕은수면";
            }
            if(value == yAxis.mEntries[2]){
                return "기상";
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
                return Integer.toString(time.get(0)/60) + "시" + Integer.toString(time.get(0)%60) + "분";
            }
            else if(index == time.size()-1){ // 맨마지막값
                return Integer.toString(time.get(time.size()-1)/60) + "시" + Integer.toString(time.get(time.size()-1)%60) + "분";
            }
            else if((time.get(index)%60) == 0){ // 정각
                return Integer.toString(time.get(index));
            }
            return "";
        }
    }


}






