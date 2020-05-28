package com.example.smalarm.ui.graph;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.example.smalarm.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.XAxisValueFormatter;
import com.github.mikephil.charting.formatter.YAxisValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;

public class GraphFragment extends Fragment {

    private GraphViewModel graphViewModel;

    // 수면 단계 저장 배열
    private int stepSleep[] = new int[3];

    // 수면정보
    private TextView sleepTime;
    private TextView sleepQulity;
    private TextView sleepTimeRatio;

    // 날짜 기록하기 위한 변수
    Calendar cal = Calendar.getInstance();
    private int month = cal.get(Calendar.MONTH) + 1;
    private int day = cal.get(Calendar.DAY_OF_MONTH);
    private String description = Integer.toString(month) + "월 " + Integer.toString(day) + "일 수면기록";


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        graphViewModel =
                new ViewModelProvider(this).get(GraphViewModel.class);

        View root = inflater.inflate(R.layout.fragment_graph, container, false);
//        final TextView textView = root.findViewById(R.id.text_graph);
//        graphViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });

        // 쓰기권한요청
        requestWritePermission();

        // 정보 표시를 위한 변수들 받음 (preTime, time, motionCounter)
        Intent intent = getActivity().getIntent();
        ArrayList<Integer> time = (ArrayList<Integer>) intent.getSerializableExtra("time");
        ArrayList<Integer> motionCounter = (ArrayList<Integer>) intent.getSerializableExtra("motionCounter");
        ArrayList<Integer> preTime = (ArrayList<Integer>) intent.getSerializableExtra("preTime");

        printInfo(preTime, time, motionCounter); // 수면시간, 수면질, 구간별 움직임
        draw(time, motionCounter);// 그래프

        // 수면기록 view 캡쳐
        Button capButton = (Button) getView().findViewById(R.id.capture);
        capButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LinearLayout container = (LinearLayout) getView().findViewById(R.id.info);

                Bitmap bitmap = Bitmap.createBitmap(container.getWidth(), container.getHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                Drawable bgDrawable = container.getBackground();
                if (bgDrawable != null) {
                    bgDrawable.draw(canvas);
                } else {
                    canvas.drawColor(Color.WHITE);
                }
                container.draw(canvas);

                FileOutputStream fos;
                try {
                    fos = new FileOutputStream(Environment.getExternalStorageDirectory().toString() + "/" + description + ".jpeg");
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

            }
        });


        return root;
    }


    //쓰기 권한 요청
    private void requestWritePermission() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            } else {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        1);
            }
        }
    }


    private void printInfo(ArrayList<Integer> preTime, ArrayList<Integer> time, ArrayList<Integer> motionCounter) {

        // 수면시간
        int startTime = preTime.get(0);
        int endTime = preTime.get(preTime.size() - 1);
        int totalTime = endTime - startTime;
        if (totalTime == 0) {
            totalTime = 15;
        }
        sleepTime = getView().findViewById(R.id.sleepTime);
        sleepTime.setText("총 수면시간 : " + Integer.toString(totalTime) + "분");

        // 뒤척임 정도에 기반한 수면질
        String sq = Double.toString(setSQ(motionCounter));
        sleepQulity = getView().findViewById(R.id.sleepQulity);
        sleepQulity.setText("수면 퀄리티 : " + sq + "%");

        int deep_percent;
        int light_percent;

        deep_percent = (stepSleep[0] + stepSleep[1]) / totalTime * 100;
        light_percent = stepSleep[2] / totalTime * 100;


        // 뒤척임 정도에 따른 수면시간 퍼센테이지
        sleepTimeRatio = getView().findViewById(R.id.sleepTimeRatio);
        sleepTimeRatio.setText(
                "깊은수면 : " + timeToString(stepSleep[0] + stepSleep[1]) + "(" + deep_percent + "%)\n"
                        + "얕은수면 : " + timeToString(stepSleep[2]) + "(" + light_percent + "%)"
        );

        // 구간별 움직임 횟수
//        String p;
//        for(int i=0; i<time.size(); i++){
//
//            if(i==0){
//                p = "~ " + timeToString(time.get(i)) + " : " + motionCounter.get(i);
//            }
//            else {
//                p = timeToString(time.get(i-1)) + " ~ " + timeToString(time.get(i)) + " : " + motionCounter.get(i);
//            }
//            print.add(p);
//        }
//
//        ListView printView = (ListView)findViewById(R.id.countPerTime);
//        final ArrayAdapter<String> timeAdabtor = new ArrayAdapter<String>(
//                this, android.R.layout.simple_list_item_1, print);
//        printView.setAdapter(timeAdabtor);


    }

    // 몇분몇초 변환
    private String timeToString(int time) {
        String hour = Integer.toString(time / 60);
        String min = Integer.toString(time % 60);

        if ((time / 60) == 0) {
            return min + "분";
        } else {
            return hour + "시간 " + min + "분";
        }
    }

    // 수면질 계산 메소드
    private double setSQ(ArrayList<Integer> motionCounter) {

        double SQ;

        for (int i = 0; i < stepSleep.length; i++) {
            stepSleep[i] = 0;
        }

        for (int i : motionCounter) {

            if (i == 0) { // 뒤척임 없음 stepSleep[0]
                stepSleep[0] += 15;
            } else if ((i > 0) && (i <= 15)) { // 뒤척임 적음 stepSleep[1]
                stepSleep[1] += 15;
            } else if ((i > 15) && (i < 40)) { // 뒤척임 많음  stepSleep[2] , max 값 40은 기상상태
                stepSleep[2] += 15;
            }

        }
        SQ = (((stepSleep[2] * 0.5) + (stepSleep[1] * 0.75) + (stepSleep[0])) * 100) / (stepSleep[0] + stepSleep[1] + stepSleep[2]);
        return SQ;

    }

    private void draw(ArrayList<Integer> time, ArrayList<Integer> motionCounter) {

        LineChart lineChart = (LineChart) getView().findViewById(R.id.lineChart);
        ArrayList<Entry> countGraph = new ArrayList<>();

        // 데이터 추가
        for (int i = 0; i < time.size(); i++) {

            int temp = motionCounter.get(i);
            float count = 0f;

            if (temp == 40) { // 기상
                count = 3f;
            } else if (15 <= temp && temp < 40) { // 얕은잠
                count = 2f;
            } else if (0 <= temp && temp < 15) { // 깊은잠
                count = 0f;
            }

            countGraph.add(new Entry(count, i));


        }

        //x축 값 설정 (modTime)
        String[] xaxes = new String[time.size()];

        for (int i = 0; i < time.size(); i++) {
            xaxes[i] = timeToString(time.get(i));
        }

        LineDataSet lineDataSet1 = new LineDataSet(countGraph, "a");

        lineDataSet1.setAxisDependency(YAxis.AxisDependency.LEFT);
        lineDataSet1.setDrawCubic(false);
        lineDataSet1.setDrawHorizontalHighlightIndicator(true);
        lineDataSet1.setColor(Color.BLUE);
        lineDataSet1.setDrawCircles(false);
        lineDataSet1.setDrawFilled(true); // 차트 아래 fill(채우기) 설정
        lineDataSet1.setFillColor(Color.YELLOW); // 차트 아래 채우기 색 설정

        // y축 오른쪽은 표시 안함
        YAxis rightYAxis = lineChart.getAxisRight();
        rightYAxis.setEnabled(false);

        // y축 왼쪽
        YAxis leftYAxis = lineChart.getAxisLeft();
        leftYAxis.setValueFormatter(new MyYAxisValueFormatter());
        leftYAxis.setEnabled(true);
        //leftYAxis.setAxisMaxValue(2f);
        //leftYAxis.setLabelCount(3,true);
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

        LineData data = new LineData(xaxes, lineDataSet1);
        lineChart.setData(data);
        lineChart.setVisibleXRangeMaximum(65f);
        lineChart.getLegend().setEnabled(false);

        lineChart.setDescription(description);

        data.setDrawValues(false);

    }

    public class MyYAxisValueFormatter implements YAxisValueFormatter {

        @Override
        public String getFormattedValue(float value, YAxis yAxis) {
            // write your logic here
            // access the YAxis object to get more information

            if (value == 0f) {
                return "깊은잠";
            }
            if (value == 2f) {
                return "얕은잠";
            }
            if (value == 3f) {
                return "기상";
            }
            return "";
        }

    }


    public class MyCustomXAxisValueFormatter implements XAxisValueFormatter {

        Intent intent = getActivity().getIntent();
        ArrayList<Integer> time = (ArrayList<Integer>) intent.getSerializableExtra("time");

        @Override
        public String getXValue(String original, int index, ViewPortHandler viewPortHandler) {

            if ((index == 0) || (index == time.size() - 1)) { // 처음값 or 마지막값

                return Integer.toString(time.get(index) / 60) + "시 " + Integer.toString(time.get(index) % 60) + "분";
            }
            if (time.get(index) % 60 == 0) {

                return Integer.toString(time.get(index) / 60) + "시";
            }
            return "";
        }
    }
}