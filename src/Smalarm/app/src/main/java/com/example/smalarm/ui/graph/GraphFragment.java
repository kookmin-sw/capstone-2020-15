package com.example.smalarm.ui.graph;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.renderscript.ScriptIntrinsicYuvToRGB;
import android.util.JsonReader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.smalarm.MainActivity;
import com.example.smalarm.R;
import com.example.smalarm.SleepDBHelper;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
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
import java.util.Locale;
import java.util.TreeMap;

public class GraphFragment extends Fragment {

    private GraphViewModel graphViewModel;

    // MPAndroid Chart
    private LineChart lineChart;
    private BarChart barChart;

    // TextView
    private TextView starttime;
    private TextView sleeptime;
    private TextView endtime;

    private TextView total;
    private TextView week;
    private TextView month;
    private TextView totalAVG;
    private TextView weekAVG;
    private TextView monthAVG;
    private TextView todayStat;
    private TextView barChartDescription;

    // 시간별 움직임정보
    private ArrayList<Integer> time = new ArrayList<>();
    private ArrayList<Integer> motionCounter = new ArrayList<>();

    // 수면시간 데이터
    private String start_time;
    private String end_time;
    private String today_sleeptime;
    private int avg_total;
    private int avg_week;
    private int avg_month;
    private HashMap<String, Integer> avgSleepTimePerDay = new HashMap<>();

    // chart data size
    private int linchart_data_size;

    // 초기 날짜 세팅(오늘) date_id
    Calendar cal = Calendar.getInstance();
    private int date_id = (int) (cal.getTimeInMillis() / (24 * 60 * 60 * 1000));

    // 데이트픽커 콜백메소드
    private DatePickerDialog.OnDateSetListener callbackMethod;

    public static GraphFragment newInstance() {
        return new GraphFragment();
    }


    public View onCreateView(@NonNull final LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        graphViewModel =
                new ViewModelProvider(this).get(GraphViewModel.class);

        View root = inflater.inflate(R.layout.fragment_graph, container, false);

        // Values
        starttime = root.findViewById(R.id.starttime);
        sleeptime = root.findViewById(R.id.sleeptime);
        endtime = root.findViewById(R.id.endtime);

        total = root.findViewById(R.id.total);
        week = root.findViewById(R.id.week);
        month = root.findViewById(R.id.month);

        totalAVG = root.findViewById(R.id.totalAVG);
        weekAVG = root.findViewById(R.id.weekAVG);
        monthAVG = root.findViewById(R.id.monthAVG);
        todayStat = root.findViewById(R.id.todayStat);
        barChartDescription = root.findViewById(R.id.barChartDescription);

        // Chart
        lineChart = (LineChart) root.findViewById(R.id.lineChart);
        barChart = (BarChart) root.findViewById(R.id.barChart);

        // Connect DB
        final SleepDBHelper dbHelper = new SleepDBHelper(getContext(), "SleepTime.db", null, 1);

        // 6월 10일 기록 insert
        try {
            dbHelper.insert(18421,441-118, 118, 441,
                    "[120,135,150,165,180,195,210,225,240,255,270,285,300,315,330,345,360,375,390,405,420,435]",
                    "[40,22,38,9,3,3,4,5,0,2,31,4,0,10,0,12,7,22,15,20,1,3]");
            dbHelper.insert(18422,437-129, 129, 437,
                    "[135,150,165,180,195,210,225,240,255,270,285,300,315,330,345,360,375,390,405,420,435]",
                    "[40,0,22,38,9,0,0,0,5,0,2,31,4,0,10,0,12,7,2,3,22]");
            dbHelper.insert(18423,435-165, 165, 435,
                    "[165,180,195,210,225,240,255,270,285,300,315,330,345,360,375,390,405,420,435]",
                    "[40,0,22,38,9,0,0,0,5,0,2,31,4,0,10,0,12,7,22]");
        }
        catch (Exception e){
            e.printStackTrace();
        }

        // Draw linechart (today)
        drawLineChart(dbHelper, date_id);
        // Set AVG values (today)
        showStatValues(dbHelper, date_id, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        // Draw barchart (all scope)
        drawBarChart();


        // Date (date_id) picker event
        todayStat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                callbackMethod = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // 선택한 날짜에 해당하는 기록 보여줌
                        Calendar date = Calendar.getInstance();
                        date.set(Calendar.YEAR, year);
                        date.set(Calendar.MONTH, monthOfYear);
                        date.set(Calendar.DATE, dayOfMonth);

                        int date_id = (int) (date.getTimeInMillis() / (24 * 60 * 60 * 1000));

                            // Draw Linechart (picked date)
                            drawLineChart(dbHelper, date_id);
                            // Show values (선택된 날짜가 속해 있는 주, 월의 통계)
                            showStatValues(dbHelper, date_id, year, monthOfYear, dayOfMonth);
                            // Draw Barchart (All scope)
                            drawBarChart();

                    }
                };

                Calendar cal = Calendar.getInstance();

                int year = cal.get ( cal.YEAR );
                int month = cal.get ( cal.MONTH );
                int date = cal.get ( cal.DATE ) ;

                DatePickerDialog dialog = new DatePickerDialog(getContext(), callbackMethod, year, month, date);
                dialog.show();
            }
        });

        return root;
    }


    // Draw LineChart
    private void drawLineChart(SleepDBHelper dbHelper, int date_id) {

        try {
            // DB에서 오늘 날짜에 해당하는 정보들 읽어옴
            String DBtime = dbHelper.getTime(date_id);
            String DBmotion = dbHelper.getMotion(date_id);

            // JSON -> ArrayList로
            time = JsonToArrayList(DBtime);
            motionCounter = JsonToArrayList(DBmotion);
            linchart_data_size = time.size();
        } catch (Exception e) { // 만약에 data가 없으면
            linchart_data_size = 0;
            e.printStackTrace();
        }

        // 값이 있을때만 그래프 그림
        if (linchart_data_size != 0) {

            ArrayList<Entry> values = new ArrayList<>();

            // 데이터 추가 (X축 : time, Y축 : modCounter)
            for (int i = 0; i < motionCounter.size(); i++) {
                values.add(new Entry(time.get(i), motionCounter.get(i)));
            }

            // Set dataset
            LineDataSet lineDataSet = new LineDataSet(values, "sleep data");
            lineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
            lineDataSet.setDrawCircles(false);
            lineDataSet.setDrawHorizontalHighlightIndicator(true);
            lineDataSet.setColor(Color.parseColor("#B45F04"));
            lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
            lineDataSet.setDrawValues(false);

            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(lineDataSet);

            LineData data = new LineData(dataSets);
            lineChart.setData(data);
            lineChart.invalidate();
            lineChart.getLegend().setEnabled(false);
            lineChart.setTouchEnabled(false);
            lineChart.setDragEnabled(false);
            lineChart.getDescription().setText("수면중 뒤척임 정보");
            lineChart.getDescription().setTextSize(10);
            data.setDrawValues(false);

            ValueFormatter yAxisformatter = new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    return "";
                }
            };

            ValueFormatter xAxisformatter = new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    int i = (int) value;
                    if (i % 60 == 0) {
                        return String.valueOf(i / 60) + "시";
                    }
                    return "";
                }
            };

            // x축 설정
            XAxis xAxis = lineChart.getXAxis();
            xAxis.setValueFormatter(xAxisformatter);
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM); // x축 위치 지정
            xAxis.setDrawAxisLine(true); // x축 라인을 그림 (라벨이 없을때 잘 됨)
            xAxis.setDrawGridLines(false); // 내부 선 그을지 결정
            //xAxis.setLabelCount(0); // 라벨의 개수를 결정 => 나누어 떨어지는 개수로 지정
            //xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
            //xAxis.setTextSize(10f); // 크기 지정
            //xAxis.setTextColor(Color.RED); // 색 지정
            //xAxis.setDrawLabels(true); // 라벨(x축 좌표)를 그릴지 결정
            xAxis.setLabelCount(linchart_data_size);

            // y축 설정 (left)
            YAxis rightYAxis = lineChart.getAxisRight();
            rightYAxis.setEnabled(true);
            rightYAxis.setValueFormatter(yAxisformatter);
            rightYAxis.setDrawGridLines(false);

            // y축 설정 (right)
            YAxis leftYAxis = lineChart.getAxisLeft();
            leftYAxis.setEnabled(true);
            //leftYAxis.setDrawAxisLine(true);
            //leftYAxis.setLabelCount(4, true);
            leftYAxis.setValueFormatter(yAxisformatter);
            leftYAxis.setDrawGridLines(false);

        } else {
            lineChart.clear();
            lineChart.setNoDataText("수면 기록이 없습니다.");
            Paint p = lineChart.getPaint(Chart.PAINT_INFO);
            p.setColor(Color.parseColor("#190707"));
        }

    }

    // Draw BarChart
    private void drawBarChart() {

        ArrayList<Integer> valList = new ArrayList<>();

        // 초기값 세팅 value는 0으로
        for (int i = 0; i < 7; i++) {
            valList.add(0);
        }

        for (String s : avgSleepTimePerDay.keySet()) {

            if (s.equals("월")) {
                valList.set(0, avgSleepTimePerDay.get(s));
            }
            if (s.equals("화")) {
                valList.set(1, avgSleepTimePerDay.get(s));
            }
            if (s.equals("수")) {
                valList.set(2, avgSleepTimePerDay.get(s));
            }
            if (s.equals("목")) {
                valList.set(3, avgSleepTimePerDay.get(s));
            }
            if (s.equals("금")) {
                valList.set(4, avgSleepTimePerDay.get(s));
            }
            if (s.equals("토")) {
                valList.set(5, avgSleepTimePerDay.get(s));
            }
            if (s.equals("일")) {
                valList.set(6, avgSleepTimePerDay.get(s));
            }

            // 값이 있을때만 차트 그림
            if (valList.size() != 0) {

                ArrayList<BarEntry> entries = new ArrayList<>();
                for (int i = 0; i < valList.size(); i++) {
                    entries.add(new BarEntry(i, valList.get(i)));
                }

                BarDataSet sleepAVGdata = new BarDataSet(entries, "요일별 평균수면시간 (분)"); // 변수로 받아서 넣어줘도 됨
                BarData barData = new BarData();
                barData.addDataSet(sleepAVGdata);
                sleepAVGdata.setColors(Collections.singletonList(Color.parseColor("#B45F04"))); //

                barChart.setData(barData);
                barChart.animateXY(1000, 1000);
                barChart.invalidate();
                barData.setValueTextSize(11);
                barChart.getDescription().setEnabled(false);
                barChart.setTouchEnabled(false);
                barChart.setDragEnabled(false);
                barChart.getLegend().setEnabled(false);

                // y축 은 표시 안함
                YAxis rightYAxis = barChart.getAxisRight();
                rightYAxis.setEnabled(false);
                YAxis leftYAxis = barChart.getAxisLeft();
                leftYAxis.setEnabled(false);

                // x축 value formatter
                final ArrayList<String> xAxisLabel = new ArrayList<>();
                xAxisLabel.add("월");
                xAxisLabel.add("화");
                xAxisLabel.add("수");
                xAxisLabel.add("목");
                xAxisLabel.add("금");
                xAxisLabel.add("토");
                xAxisLabel.add("일");

                XAxis xAxis = barChart.getXAxis();
                xAxis.setTextSize(10);

                ValueFormatter barChartformatter = new ValueFormatter() {
                    @Override
                    public String getFormattedValue(float value) {
                        return xAxisLabel.get((int) value);
                    }
                };
                xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
                xAxis.setValueFormatter(barChartformatter);
                xAxis.setDrawGridLines(false);
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM); // x축 위치 지정
            }
        }

    }


    // Show Stat values
    private void showStatValues(SleepDBHelper dbHelper, int date_id, int year, int monthOfYear, int dayOfMonth) {

        // Set stat values
        setSleepTimeStat(dbHelper, date_id);

        // Show stat values
        starttime.setText(start_time);
        sleeptime.setText(today_sleeptime);
        endtime.setText(end_time);
        total.setText("총");
        week.setText(dateToMonth(date_id) + "월 " + dateToWeekMonth(date_id) + "주차");
        month.setText(dateToMonth(date_id) + "월");
        totalAVG.setText(minToHourMin(avg_total));
        weekAVG.setText(minToHourMin(avg_week));
        monthAVG.setText(minToHourMin(avg_month));
        todayStat.setText(year + "년 " + (monthOfYear + 1) + "월 " + dayOfMonth + "일");
        barChartDescription.setText("요일별 (분)");
    }


    // Set avg values (day, week, month, dayofweek)
    private void setSleepTimeStat(SleepDBHelper dbHelper, int date_id) {

        // 오늘 수면시간, 시작시간, 종료시간
        try {
            start_time = (dbHelper.getStartEndTime(date_id)[0] / 60) + "시 " + (dbHelper.getStartEndTime(date_id)[0] % 60) + "분";
            end_time = (dbHelper.getStartEndTime(date_id)[1] / 60) + "시 " + (dbHelper.getStartEndTime(date_id)[1] % 60) + "분";
            today_sleeptime = String.valueOf(dbHelper.getSleepTime(date_id) / 60) + "시간 "
                    + String.valueOf(dbHelper.getSleepTime(date_id) % 60) + "분";
        }
        catch (Exception e){ // DB에서 읽어온 오늘 수면시간이 없을 때
            start_time = "0분";
            end_time = "0분";
            today_sleeptime = "0분";
            e.printStackTrace();
        }

        // 총 수면시간들의 평균
        try {
            int sleep_total = 0;
            int sleep[] = dbHelper.getAllSleepTime();
            for (int i : sleep) {
                sleep_total += i;
            }
            avg_total = sleep_total / sleep.length;
        } catch (Exception e) { // DB에서 읽어온 총 수면시간(sleep[])이 없을 때
            avg_total = 0;
            e.printStackTrace();
        }

        // 이번주 수면시간 평균
        try {
            String week = dateToWeek(date_id);
            int sleep_total_week = 0;
            int[] sleep_week = dbHelper.getWeekSleepTime(week);
            for (int i : sleep_week) {
                sleep_total_week += i;
            }
            avg_week = sleep_total_week / sleep_week.length;
        } catch (Exception e) { // DB에서 읽어온 주별수면시간(sleep_week[])이 없을 때
            avg_week = 0;
            e.printStackTrace();
        }

        // 이번달 수면시간 평균
        try {
            String month = dateToMonth(date_id);
            int sleep_total_month = 0;
            int[] sleep_month = dbHelper.getMonthSleepTime(month);
            for (int i : sleep_month) {
                sleep_total_month += i;
            }
            avg_month = sleep_total_month / sleep_month.length;
        } catch (Exception e) { // DB에서 읽어온 월별수면시간(sleep_total[])이 없을 때
            avg_month = 0;
            e.printStackTrace();
        }

        // 요일별 수면시간 평균
        try {
            HashMap<String, Integer> totalSleepTimePerDay = dbHelper.getDayofWeekSleepTime();// 요일별 총 수면시간정보
            HashMap<String, Integer> getDayofWeekCount = dbHelper.getDayofWeekCount();// 요일별 count 정보

            for (String s : totalSleepTimePerDay.keySet()) {
                int temp;
                temp = totalSleepTimePerDay.get(s) / getDayofWeekCount.get(s);
                avgSleepTimePerDay.put(s, temp);
            }
        } catch (Exception e) { // DB에서 읽어온 요일별 평균수면시간이 없을 때 0으로 세팅
            String[] days = {"월", "화", "수", "목", "금", "토", "일"};
            for (String s : days) {
                avgSleepTimePerDay.put(s, 0);
            }
        }
    }

    // Convert Json to ArrayList
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

    // Convert m to h:m
    private String minToHourMin(int min) {
        if(min == 0){
            return 0 +"분";
        }
        else {
            int h = min / 60;
            int m = min % 60;
            String s = h + "시간 " + m + "분";
            return s;
        }
    }

    // 현재 날짜의 년도
    private String dateToYear(int date) {

        String s;
        long temp = (long) date;
        temp *= (24 * 60 * 60 * 1000);

        Date hour = new Date(temp);

        SimpleDateFormat format1;
        format1 = new SimpleDateFormat("Y");
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

    // 현재 날짜의 주차(월)
    private String dateToWeekMonth(int date) {

        String s;
        long temp = (long) date;
        temp *= (24 * 60 * 60 * 1000);

        Date hour = new Date(temp);

        SimpleDateFormat format1;
        format1 = new SimpleDateFormat("W");
        s = format1.format(hour);

        return s;

    }


}