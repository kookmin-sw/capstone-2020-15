package com.example.smalarm;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class SleepDBHelper extends SQLiteOpenHelper {


    // DBHelper 생성자로 관리할 DB 이름과 버전 정보를 받음
    public SleepDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    // DB를 새로 생성할 때 호출되는 함수
    @Override
    public void onCreate(SQLiteDatabase db) {

        // sleeptime(int), starttime(int), endtime(int) ,time(string(JSON)) , motioncounter(string(JSON))

        db.execSQL("CREATE TABLE MOTION (date_id INTEGER PRIMARY KEY, sleeptime INT, startTime INT, endTime INT, time TEXT , motioncounter TEXT);");
    }

    // DB 업그레이드를 위해 버전이 변경될 때 호출되는 함수
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insert(int date_id, int sleeptime, int starttime, int endtime, String time, String motioncounter ) {

        // 읽고 쓰기가 가능하게 DB 열기
        SQLiteDatabase db = getWritableDatabase();


        // DB에 입력한 값으로 행 추가
        db.execSQL("INSERT INTO MOTION VALUES(" + date_id + "," + sleeptime + "," + starttime + " ," + endtime + ", '" + time + "', '" + motioncounter + "');");
        db.close();
    }

    public int[] getStartEndTime(int date_id){
        SQLiteDatabase db = getReadableDatabase();
        int result[] = new int[2];
        Cursor cursor = db.rawQuery("SELECT * FROM MOTION WHERE date_id = " +date_id+ "; ", null);

        while (cursor.moveToNext()) {
            result[0] = cursor.getInt(2);
            result[1] = cursor.getInt(3);
        }
        return result;
    }

    public int[] getAllSleepTime() {
        // 읽기가 가능하게 DB 열기
        SQLiteDatabase db = getReadableDatabase();
        String result = "";
        int count = 0;
        // DB에 있는 데이터를 쉽게 처리하기 위해 Cursor를 사용하여 테이블에 있는 모든 데이터 출력
        Cursor cursor = db.rawQuery("SELECT * FROM MOTION", null);
        while (cursor.moveToNext()) {
            result += cursor.getInt(1) + " "  ;
            count++; // 데이터크기(string)
        }

        String[] result_string = result.split(" ");
        int result_int[] = new int[count];

        // string[] -> int[]
        for(int i=0; i<count; i++){
            result_int[i] = Integer.parseInt(result_string[i]);
        }
        return result_int;
    }

    public int[] getWeekSleepTime(String week) {
        // 읽기가 가능하게 DB 열기
        SQLiteDatabase db = getReadableDatabase();
        String result = "";
        int count = 0;
        // DB에 있는 데이터를 쉽게 처리하기 위해 Cursor를 사용하여 테이블에 있는 모든 데이터 출력
        Cursor cursor = db.rawQuery("SELECT * FROM MOTION", null);
        while (cursor.moveToNext()) {

            // 해당 row의 주차(week) 정보
            String w = dateToWeek(cursor.getInt(0));

            // 만약 해당 row의 주차(week)정보와 들어온 week정보가 같다면 ex) 52주차의 정보만 뺴냄
            if(week.equals(w)){

                result += cursor.getInt(1) + " "  ;
                count++; // 데이터크기(string)

            }

        }

        String[] result_string = result.split(" ");
        int result_int[] = new int[count];

        // string[] -> int[]
        for(int i=0; i<count; i++){
            result_int[i] = Integer.parseInt(result_string[i]);
        }
        return result_int;
    }

    public int[] getMonthSleepTime(String month) {
        // 읽기가 가능하게 DB 열기
        SQLiteDatabase db = getReadableDatabase();
        String result = "";
        int count = 0;
        // DB에 있는 데이터를 쉽게 처리하기 위해 Cursor를 사용하여 테이블에 있는 모든 데이터 읽음
        Cursor cursor = db.rawQuery("SELECT * FROM MOTION", null);
        while (cursor.moveToNext()) {

            // 해당 row의 월(month) 정보
            String m = dateToMonth(cursor.getInt(0));

            // 만약 해당 row의 월(month)정보와 들어온 month정보가 같다면 ex) 6월달 정보만 뺴냄
            if(month.equals(m)){

                result += cursor.getInt(1) + " "  ;
                count++; // 데이터크기(string)

            }

        }

        String[] result_string = result.split(" ");
        int result_int[] = new int[count];

        // string[] -> int[]
        for(int i=0; i<count; i++){
            result_int[i] = Integer.parseInt(result_string[i]);
        }
        return result_int;
    }

    public HashMap<String,Integer> getDayofWeekSleepTime() {
        // 읽기가 가능하게 DB 열기
        SQLiteDatabase db = getReadableDatabase();
        HashMap<String, Integer> sleepTimePerDay = new HashMap<>();

        // DB에 있는 데이터를 쉽게 처리하기 위해 Cursor를 사용하여 테이블에 있는 모든 데이터 읽음
        Cursor cursor = db.rawQuery("SELECT * FROM MOTION", null);
        while (cursor.moveToNext()) {

            // 해당 row의 요일(dayofweek)정보
            String d = dateToDay(cursor.getInt(0));

            // put key(요일정보) : value(sleeptime)

            // 만약 해당 요일의 밸류가 없다면 새로 put
            if(sleepTimePerDay.get(d) == null){
                sleepTimePerDay.put(d,cursor.getInt(1));
            }
            // 있다면 해당하는 value와 더해서 새로 put
            else{
                int temp = sleepTimePerDay.get(d) + cursor.getInt(1);
                sleepTimePerDay.put(d,temp);
            }

        }

        return sleepTimePerDay;
    }

    public HashMap<String,Integer> getDayofWeekCount() {
        // 읽기가 가능하게 DB 열기
        SQLiteDatabase db = getReadableDatabase();
        HashMap<String, Integer> countPerDay = new HashMap<>();

        // DB에 있는 데이터를 쉽게 처리하기 위해 Cursor를 사용하여 테이블에 있는 모든 데이터 읽음
        Cursor cursor = db.rawQuery("SELECT * FROM MOTION", null);
        while (cursor.moveToNext()) {

            // 해당 row의 요일(dayofweek)정보
            String d = dateToDay(cursor.getInt(0));

            // put key(요일정보) : value(sleeptime)

            // 만약 해당 요일의 밸류가 없다면 새로 put
            if(countPerDay.get(d) == null){
                countPerDay.put(d,1);
            }
            // 있다면 해당하는 value와 더해서 새로 put
            else{
                int temp = countPerDay.get(d) + 1;
                countPerDay.put(d,temp);
            }

        }

        return countPerDay;
    }

    public int getSleepTime(int date_id){

        // 읽기가 가능하게 DB 열기
        SQLiteDatabase db = getReadableDatabase();
        int result = 0;
        Cursor cursor = db.rawQuery("SELECT * FROM MOTION WHERE date_id = " +date_id+ "; ", null);

        while (cursor.moveToNext()) {
            result = cursor.getInt(1);
        }

        return result;
    }

    public void update(int date_id, int sleeptime) {
        SQLiteDatabase db = getWritableDatabase();
        // 입력한 항목과 일치하는 행의 수정
        db.execSQL("UPDATE MOTION SET sleeptime =" + sleeptime + " WHERE date_id='" + date_id + "';");
        db.close();
    }

    public void delete(int date_id) {
        SQLiteDatabase db = getWritableDatabase();
        // 입력한 항목과 일치하는 행 삭제
        db.execSQL("DELETE FROM MOTION WHERE date_id =" + date_id + ";");
        db.close();
    }

    public String getTime(int date_id) {
        // 읽기가 가능하게 DB 열기
        SQLiteDatabase db = getReadableDatabase();
        String result = "";

        // DB에 있는 데이터를 쉽게 처리하기 위해 Cursor를 사용하여 테이블에 있는 모든 데이터 출력
        Cursor cursor = db.rawQuery("SELECT * FROM MOTION WHERE date_id = "+ date_id +";", null);
        while (cursor.moveToNext()) {
            result = cursor.getString(4);
        }
        return result;
    }

    public String getMotion(int date_id) {
        // 읽기가 가능하게 DB 열기
        SQLiteDatabase db = getReadableDatabase();
        String result = "";

        // DB에 있는 데이터를 쉽게 처리하기 위해 Cursor를 사용하여 테이블에 있는 모든 데이터 출력
        Cursor cursor = db.rawQuery("SELECT * FROM MOTION WHERE date_id = "+ date_id +";", null);
        while (cursor.moveToNext()) {
            result = cursor.getString(5);
        }
        return result;
    }


    private String dateToMonth(int date) {

        String s;
        long temp = (long) date;
        temp *= (24 * 60 * 60 * 1000);

        Date hour = new Date (temp);

        SimpleDateFormat format1;
        format1 = new SimpleDateFormat("M");
        s = format1.format(hour);

        return s;

    }

    private String dateToWeek(int date) {

        String s;
        long temp = (long) date;
        temp *= (24 * 60 * 60 * 1000);

        Date hour = new Date (temp);

        SimpleDateFormat format1;
        format1 = new SimpleDateFormat("w");
        s = format1.format(hour);

        return s;

    }

    private String dateToDay(int date) {

        String s;
        long temp = (long) date;
        temp *= (24 * 60 * 60 * 1000);

        Date hour = new Date (temp);

        SimpleDateFormat format1;
        format1 = new SimpleDateFormat("E", new Locale("ko", "KR"));
        s = format1.format(hour);

        return s;

    }



}


