package com.example.sampleevent;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeMap;


public class TimeList extends MainActivity {

    // 구간대별 움직임 담고 있는 map (단, 움직인 경우만이라서 -> time, motionCounter로 보정작업 필요)
    HashMap<Integer, Integer> countMap = new HashMap<>();

    // 그래프 그리기위한 어레이리스트 (time : x축 , motionCounter : y축)
    ArrayList<Integer> time = new ArrayList<>();
    ArrayList<Integer> motionCounter = new ArrayList<>();
    private int startTime;
    private int endTime;

    // 모션카운터가 0인 구간처리를 위한 어레이리스트
    ArrayList<Integer> preTime = new ArrayList<>();

    // 출력을 위한 어레이리스트
    ArrayList<String> print = new ArrayList<>();


    private TextView sleepTime;
    private TextView sleepQulity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_time);
        Intent intent = getIntent();

        // hashmap (구간 : 움직임횟수) 받음
        countMap = (HashMap<Integer, Integer>) intent.getSerializableExtra("hashmap");

        // 값 설정 메소드 hashmap --> pretime(int) --> time(string), motionCounter(int)
        // preTime은 중간중간 값이 비어있을 수도 있음 (구간별 움직임이 0인 경우)
        // time은 공백을 메운 완전한 배열
        setValue();

        //storeSQL(time,motionCounter);

        // 출력
        printInfo();

        // 그래프엑티비티 이동을 위한 리스너
        Button graphButton = (Button) findViewById(R.id.graphBtn);
        graphButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), GraphActivity.class);
                intent.putExtra("time", time);
                intent.putExtra("motionCounter", motionCounter);
                startActivity(intent);
            }
        });

   }

    // SQL 저장
    private void storeSQL(ArrayList<Integer> time, ArrayList<Integer> motionCounter) {

        //time, motionCounter -> sql 에 쓰기 위해서 변환과정(json -> string)
        JSONObject timejson = new JSONObject();
        try {
            timejson.put("timeArrays", new JSONArray(time));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject motionjson = new JSONObject();
        try {
            motionjson.put("timeArrays", new JSONArray(motionCounter));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String timeList = timejson.toString();
        String motionList = timejson.toString();

        //읽을때
        //JSONObject json = new JSONObject(stringreadfromsqlite);
        //ArrayList items = json.optJSONArray("uniqueArrays");
    }

    // 수면질 계산 메소드
    private double setSQ(){

        int stepSleep[] = new int[3];
        double SQ;

        for(int i=0; i<stepSleep.length; i++){
            stepSleep[i] = 0;
        }

        for(int i : motionCounter){

            if( (i>=0) && (i<7) ){ // 깊은수면 stepSleep[0]
                stepSleep[0] += 15;
            }
            else if( (i>=7) && (i<15) ){ // 얕은수면 stepSleep[1]
                stepSleep[1] += 15;
            }
            else if( (i>=15) && (i<30) ){ // 램수면 stepSleep[2]
                stepSleep[2] += 15;
            }

        }
        SQ = (((stepSleep[2]*0.5) + (stepSleep[1]*0.75) + (stepSleep[0]))*100)/(stepSleep[0] + stepSleep[1] + stepSleep[2]);
        return SQ;

   }

    // 중간에 비는 값 보정 메소드
    private void setValue(){

       // 해쉬맵 키값별 오름차순으로 순회하는 이터레이터 설정
       TreeMap<Integer, Integer> tm = new TreeMap<Integer, Integer>(countMap);
       Iterator<Integer> keyiterator = tm.keySet().iterator();

       // 이터레이터로 순회하면서 preTime 어레이리스트에 저장
       while(keyiterator.hasNext()){
           int i = keyiterator.next();
           preTime.add(i);
       }

       // time , motionCounter 값 채워줌 중간공백 없이
       int start = preTime.get(0);
       int target = preTime.get(0);
       int last = preTime.get(preTime.size() - 1);
       int n = 1;

       // 15분 주기로 더해가면서 빈값 채우는 루프
       while(target <= last){

           time.add(target);

           if(countMap.get(target) == null){ // 만약 해당 주기에 count값이 없으면
               motionCounter.add(0);
           }
           else{
               motionCounter.add(countMap.get(target));
           }

           target = start + (15*n);
           n++;
       }

   }

   // print
    private void printInfo(){

        // 수면시간
        startTime = preTime.get(0);
        endTime = preTime.get(preTime.size()-1);
        sleepTime = findViewById(R.id.sleepTime);
        sleepTime.setText("총 수면시간 : " + Integer.toString(endTime - startTime) + "분");

        // 수면질
        String sq = Double.toString(setSQ());
        sleepQulity = findViewById(R.id.sleepQulity);
        sleepQulity.setText("수면 퀄리티 : " + sq + "%");

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


}
