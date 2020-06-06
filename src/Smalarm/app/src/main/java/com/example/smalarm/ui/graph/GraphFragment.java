package com.example.smalarm.ui.graph;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.renderscript.ScriptIntrinsicYuvToRGB;
import android.util.JsonReader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.smalarm.MainActivity;
import com.example.smalarm.R;
import com.example.smalarm.SleepDBHelper;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.XAxisValueFormatter;
import com.github.mikephil.charting.formatter.YAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

public class GraphFragment extends Fragment {

    private GraphViewModel graphViewModel;

    TextView as, bs, cs;

    // 시간별 움직임정보
    private ArrayList<Integer> time = new ArrayList<>();
    private ArrayList<Integer> motionCounter = new ArrayList<>();

    // 평균 수면시간 데이터
    private int avg_total;
    private int avg_week;
    private int avg_month;
    private HashMap<String, Integer> avgSleepTimePerDay = new HashMap<>();


    public static GraphFragment newInstance() {
        return new GraphFragment();
    }



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        graphViewModel =
                new ViewModelProvider(this).get(GraphViewModel.class);

        View root = inflater.inflate(R.layout.fragment_graph, container, false);

        as = root.findViewById(R.id.a);
        bs = root.findViewById(R.id.b);
        cs = root.findViewById(R.id.c);



        // 수면기록 읽어옴
        SleepDBHelper dbHelper = new SleepDBHelper(getContext(), "SleepTime.db", null, 1);

//        try {
//            dbHelper.insert(18411, 450, "금", "b");
//            dbHelper.insert(18412, 450, "토", "b");
//            dbHelper.insert(18413, 450, "일", "b");
//            dbHelper.insert(18414, 450, "월", "b");
//            dbHelper.insert(18415, 350, "화", "b");
//            dbHelper.insert(18416, 470, "수", "b");
//            dbHelper.insert(18417, 420, "목", "b");
//            dbHelper.insert(18418, 410, "금", "b");
//            dbHelper.insert(18419, 470, "토", "b");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }


        // 오늘 날짜
        Calendar cal = Calendar.getInstance();
        int date_id = (int) (cal.getTimeInMillis() / (24 * 60 * 60 * 1000));

//        ////////////////////////////////////// DRAW GRAPH /////////////////////////////////////////////


        try {
            // DB에서 오늘 날짜에 해당하는 정보들 읽어옴
            String DBtime = dbHelper.getTime(date_id);
            String DBmotion = dbHelper.getMotion(date_id);

            // JSON -> ArrayList로
            time = JsonToArrayList(DBtime);
            motionCounter = JsonToArrayList(DBmotion);

            // 값이 있을때만 그래프 그림
            if (time.size() != 0) {

                // Draw graph
                LineChart lineChart = (LineChart) root.findViewById(R.id.lineChart);

                ArrayList<Entry> values = new ArrayList<>();
                String[] xaxes = new String[time.size()];

                // 데이터 추가
                for (int i = 0; i < motionCounter.size(); i++) {
                    values.add(new Entry(motionCounter.get(i), i));
                }

                //x축 값 설정 (modTime)
                for (int i = 0; i < time.size(); i++) {
                    xaxes[i] = timeToStringHHmm(time.get(i));
                }

                LineDataSet lineDataSet = new LineDataSet(values, "sleep data");

                lineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
                lineDataSet.setDrawCubic(true);
                lineDataSet.setDrawHorizontalHighlightIndicator(true);
                lineDataSet.setColor(Color.BLUE);
                lineDataSet.setDrawCircles(false);
                lineDataSet.setDrawFilled(false); // 차트 아래 fill(채우기) 설정
                lineDataSet.setFillColor(Color.YELLOW); // 차트 아래 채우기 색 설정

                // y축 오른쪽은 표시 안함
                YAxis rightYAxis = lineChart.getAxisRight();
                rightYAxis.setEnabled(false);

                // y축 왼쪽
                YAxis leftYAxis = lineChart.getAxisLeft();
                leftYAxis.setValueFormatter(new MyYAxisValueFormatter());
                leftYAxis.setEnabled(true);
                //leftYAxis.setAxisMaxValue(2f);
                leftYAxis.setLabelCount(4, true);
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

                LineData data = new LineData(xaxes, lineDataSet);
                lineChart.setData(data);
                //lineChart.setVisibleXRangeMaximum(65f);
                lineChart.getLegend().setEnabled(false);

                Date today = new Date();
                SimpleDateFormat format1;
                format1 = new SimpleDateFormat("MM월 dd일 E요일 수면기록");

                lineChart.setDescription(format1.format(today));

                data.setDrawValues(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////


        // Set AVG values
        try {
            setSleepTimeStat(dbHelper, date_id);
        } catch (Exception e) {
            avg_total = 0;
            avg_month = 0;
            avg_week = 0;
            e.printStackTrace();
        }


        //////////////////////////////////////// DRAW BARCHART ////////////////////////////////////////////////////////////

        BarChart barChart = (BarChart) root.findViewById(R.id.barChart);

        ArrayList<Integer> valList = new ArrayList<>();
        ArrayList<String> labelList = new ArrayList<>();

        try {

            for (int i = 0; i < avgSleepTimePerDay.size(); i++) {
                valList.add(i);
                labelList.add(String.valueOf(i));
            }

            for (String s : avgSleepTimePerDay.keySet()) {
                int index = 0;

                if (s.equals("월")) {
                    index = 0;
                }
                if (s.equals("화")) {
                    index = 1;
                }
                if (s.equals("수")) {
                    index = 2;
                }
                if (s.equals("목")) {
                    index = 3;
                }
                if (s.equals("금")) {
                    index = 4;
                }
                if (s.equals("토")) {
                    index = 5;
                }
                if (s.equals("일")) {
                    index = 6;
                }

                labelList.set(index, s);
                valList.set(index, avgSleepTimePerDay.get(s));

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 값이 있을때만 차트 그림
        if (valList.size() != 0) {

            ArrayList<BarEntry> entries = new ArrayList<>();
            for (int i = 0; i < valList.size(); i++) {
                entries.add(new BarEntry((Integer) valList.get(i), i));
            }

            BarDataSet depenses = new BarDataSet(entries, "요일별 평균수면시간 (분)"); // 변수로 받아서 넣어줘도 됨
            //depenses.setAxisDependency(YAxis.AxisDependency.RIGHT);
            barChart.setDescription(" ");

            ArrayList<String> labels = new ArrayList<String>();
            for (int i = 0; i < labelList.size(); i++) {
                labels.add((String) labelList.get(i));
            }

            BarData barData = new BarData(labels, depenses); // 라이브러리 v3.x 사용하면 에러 발생함
            depenses.setColors(Collections.singletonList(Color.LTGRAY)); //

            barChart.setData(barData);
            barChart.animateXY(1000, 1000);
            barChart.invalidate();

            barData.setValueTextSize(11);

            // y축 은 표시 안함
            YAxis rightYAxis = barChart.getAxisRight();
            rightYAxis.setEnabled(false);
            YAxis leftYAxis = barChart.getAxisLeft();
            leftYAxis.setEnabled(false);
            XAxis xAxis = barChart.getXAxis();
            xAxis.setTextSize(14);
        }


        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////



        as.setText("AVG (total) : " + avg_total);
        bs.setText("AVG (this week): " + avg_week);
        cs.setText("AVG (this month): " + avg_month);

        return root;
    }

    private void setSleepTimeStat(SleepDBHelper dbHelper, int date_id) {

        // 총 수면시간들의 평균
        int sleep_total = 0;
        int sleep[] = dbHelper.getAllSleepTime();
        for (int i : sleep) {
            sleep_total += i;
        }
        avg_total = sleep_total / sleep.length;

        // 이번주 수면시간 평균
        String week = dateToWeek(date_id);
        int sleep_total_week = 0;
        int[] sleep_week = dbHelper.getWeekSleepTime(week);
        for (int i : sleep_week) {
            sleep_total_week += i;
        }
        avg_week = sleep_total_week / sleep_week.length;

        // 이번달 수면시간 평균
        String month = dateToMonth(date_id);
        int sleep_total_month = 0;
        int[] sleep_month = dbHelper.getMonthSleepTime(month);
        for (int i : sleep_month) {
            sleep_total_month += i;
        }
        avg_month = sleep_total_month / sleep_month.length;


        // 요일별 수면시간 평균
        HashMap<String, Integer> totalSleepTimePerDay = dbHelper.getDayofWeekSleepTime();// 요일별 총 수면시간정보
        HashMap<String, Integer> getDayofWeekCount = dbHelper.getDayofWeekCount();// 요일별 count 정보

        for (String s : totalSleepTimePerDay.keySet()) {
            int temp;
            temp = totalSleepTimePerDay.get(s) / getDayofWeekCount.get(s);
            avgSleepTimePerDay.put(s, temp);
        }


    }

    private ArrayList<Integer> JsonToArrayList(String jsonString) {

        ArrayList<Integer> list = new ArrayList<Integer>();

        // JSON -> ArrayList로
        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (jsonArray != null) {
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    list.add(Integer.parseInt(jsonArray.getString(i)));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return list;
    }

    // 몇시 몇분 변환
    private String timeToStringHHmm(int time) {


        String s;
        long temp = (long) time;
        temp *= (60 * 1000);

        Date hour = new Date(temp);

        SimpleDateFormat format1;
        format1 = new SimpleDateFormat("HH:mm");
        s = format1.format(hour);

        return s;
    }

    // 현재 날짜의 월
    private String dateToMonth(int date) {

        String s;
        long temp = (long) date;
        temp *= (24 * 60 * 60 * 1000);

        Date hour = new Date(temp);

        SimpleDateFormat format1;
        format1 = new SimpleDateFormat("M");
        s = format1.format(hour);

        return s;

    }

    // 현재 날짜의 주차
    private String dateToWeek(int date) {

        String s;
        long temp = (long) date;
        temp *= (24 * 60 * 60 * 1000);

        Date hour = new Date(temp);

        SimpleDateFormat format1;
        format1 = new SimpleDateFormat("w");
        s = format1.format(hour);

        return s;

    }

    // 현재 날짜의 요일
    private String dateToDay(int date) {

        String s;
        long temp = (long) date;
        temp *= (24 * 60 * 60 * 1000);

        Date hour = new Date(temp);

        SimpleDateFormat format1;
        format1 = new SimpleDateFormat("E");
        s = format1.format(hour);

        return s;

    }


    public class MyYAxisValueFormatter implements YAxisValueFormatter {

        @Override
        public String getFormattedValue(float value, YAxis yAxis) {
            // write your logic here
            // access the YAxis object to get more information

            if (value > 30f && value <= 45f) {
                return "기상";
            }
            if (value > 15f && value <= 30f) {
                return "얕은잠";
            }
            if (value > 0f && value <= 15f) {
                return "깊은잠";
            }
            if (value == 0f) {
                return "0";
            }
            return "";
        }

    }


    public class MyCustomXAxisValueFormatter implements XAxisValueFormatter {

        @Override
        public String getXValue(String original, int index, ViewPortHandler viewPortHandler) {

            String s = original;

            String[] split = s.split(":");
            String h = split[0];
            String m = split[1];

            // 정각일떄
            if (m.equals("00")) {
                return h;
            }
            // 맨 끝값일때
            if (index == time.size() - 1) {
                return s;
            }
            // 처음 값일때
            if (index == 0) {
                return s;
            }

            return "";
        }
    }

}