package com.example.sweetleep_test.ui.dashboard;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import com.example.sweetleep_test.MainActivity;
import com.example.sweetleep_test.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;

public class GraphActivity extends MainActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_dashboard);

        // 그래프 그리기 위한 x축(time), y축(motionCounter) 받음
        Intent intent = getIntent();
        ArrayList<String> time = (ArrayList<String>) intent.getSerializableExtra("time");
        ArrayList<Integer> motionCounter = (ArrayList<Integer>) intent.getSerializableExtra("motionCounter");

        draw(time, motionCounter);

    }

    private void draw(ArrayList modTime, ArrayList modMotionCounter) {

        LineChart lineChart = (LineChart) findViewById(R.id.lineChart);
        ArrayList<Entry> countGraph = new ArrayList<>();

        // 데이터 추가
        for (int i = 0; i < modTime.size(); i++) {

            float count = Float.parseFloat(String.valueOf(modMotionCounter.get(i)));
            countGraph.add(new Entry(count, i));

        }

        // x축 값 설정 (modTime)
        String[] xaxes = new String[modTime.size()];
        for (int i = 0; i < modTime.size(); i++) {
            xaxes[i] = modTime.get(i).toString();
        }

        LineDataSet lineDataSet1 = new LineDataSet(countGraph, "motionCounter");
//        lineDataSet1.setDrawCubic(true);
        lineDataSet1.setDrawHorizontalHighlightIndicator(true);
        lineDataSet1.setColor(Color.BLUE);


        // x축 스타일링 시작
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM); // x축 위치 지정
        //xAxis.setTextSize(10f); // 크기 지정
        //xAxis.setTextColor(Color.RED); // 색 지정
        //xAxis.setDrawLabels(true); // 라벨(x축 좌표)를 그릴지 결정
        //xAxis.setDrawAxisLine(false); // x축 라인을 그림 (라벨이 없을때 잘 됨)
        xAxis.setDrawGridLines(false); // 내부 선 그을지 결정
        //xAxis.setLabelCount(2); // 라벨의 개수를 결정 => 나누어 떨어지는 개수로 지정
        //xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
        //xAxis.setValueFormatter(formatter);

//        LineData data = new LineData(xaxes, lineDataSet1);
        LineData data = new LineData(lineDataSet1);
        lineChart.setData(data);
        lineChart.setVisibleXRangeMaximum(65f);

    }


}





