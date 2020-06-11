package com.example.smalarm.ui.alarm;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.smalarm.R;
import com.example.smalarm.ui.alarm.calendar.CalendarActivity;
import com.example.smalarm.ui.alarm.calendar.CalendarStart;
import com.example.smalarm.ui.alarm.calendar.GpsTracker;
import com.example.smalarm.ui.alarm.util.AlarmData;
import com.example.smalarm.ui.alarm.util.AlarmReceiver;
import com.example.smalarm.ui.alarm.util.DeviceBootReceiver;
import com.example.smalarm.ui.alarm.util.NetworkTask;
import com.google.api.client.util.DateTime;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.threeten.bp.LocalDateTime;
import org.threeten.bp.LocalTime;
import org.threeten.bp.format.DateTimeFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import pub.devrel.easypermissions.EasyPermissions;

public class AlarmAddActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    String[] REQUIRED_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

    private SharedPreferences sharedPreferences;
    private Gson gson = new GsonBuilder().create();
    private String json;

    private TimePicker picker;
    private EditText labelContent;
    private TextView repeatContent;
    private TextView soundContent;
    private TextView smartContent;

    private List<Integer> selectedItems;
    private List<Integer> dayOfWeek = Collections.emptyList();
    private int alarmIdx;
    private int editIdx;
    private boolean smart = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_alarm_add);
        setTitle("알람 설정");

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        picker = findViewById(R.id.timePicker);

        // 앞서 설정한 값으로 보여주기 없으면 디폴트 값은 현재시간
        sharedPreferences = getSharedPreferences("alarms", Context.MODE_PRIVATE);
        editIdx = getIntent().getIntExtra("idx", 0);
        if (editIdx != 0) getReservedTime();
        String time = getIntent().getStringExtra("startTime");
        if (time != null) getReservedTime(time);


        labelContent = findViewById(R.id.labelContent);
        repeatContent = findViewById(R.id.repeatContent);
        soundContent = findViewById(R.id.soundContent);
        smartContent = findViewById(R.id.smartContent);

        labelContent.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(labelContent.getWindowToken(), 0);
                    }
//                    labelContent.clearFocus();
                    if (labelContent.getText().toString().equals(""))
                        labelContent.setText("알람");
                }
                return true;
            }
        });

        ConstraintLayout repeatCl = findViewById(R.id.cl_repeat);
        ConstraintLayout soundCl = findViewById(R.id.cl_sound);
        ConstraintLayout smartCl = findViewById(R.id.cl_smart);
        repeatCl.setOnClickListener(this);
        soundCl.setOnClickListener(this);
        smartCl.setOnClickListener(this);

        Button setBtn = findViewById(R.id.setButton);
        Button delBtn = findViewById(R.id.delButton);
        setBtn.setOnClickListener(this);
        delBtn.setOnClickListener(this);

    }

    private Calendar setCalendar(int hour, int min) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, min);
        calendar.set(Calendar.SECOND, 0);

        return calendar;
    }

    private void getReservedTime() {

        AlarmData alarm = gson.fromJson(json, AlarmData.class);
        String timeDigit = alarm.getTimeDigit();
        int hour = Integer.parseInt(timeDigit.substring(0, 1));
        int min = Integer.parseInt(timeDigit.substring(3, 4));

        Calendar nextNotifyTime = setCalendar(hour, min);

        Date nextDate = nextNotifyTime.getTime();
        String date_text = new SimpleDateFormat("yyyy년 MM월 dd일 EE요일 a hh시 mm분 ", Locale.getDefault()).format(nextDate);
        Toast.makeText(getApplicationContext(), "[처음 실행시] 다음 알람은 " + date_text + "으로 알람이 설정되었습니다!", Toast.LENGTH_SHORT).show();

        // 이전 설정값으로 TimePicker 초기화
        Date currentTime = nextNotifyTime.getTime();
        SimpleDateFormat HourFormat = new SimpleDateFormat("kk", Locale.getDefault());
        SimpleDateFormat MinuteFormat = new SimpleDateFormat("mm", Locale.getDefault());

        int pre_hour = Integer.parseInt(HourFormat.format(currentTime));
        int pre_minute = Integer.parseInt(MinuteFormat.format(currentTime));

        picker.setHour(pre_hour);
        picker.setMinute(pre_minute);
    }

    private void getReservedTime(String time) {
        picker.setHour(Integer.parseInt(time.substring(11, 13)));
        picker.setMinute(Integer.parseInt(time.substring(14, 16)));
    }

    private void setPreferencesAlarm(Calendar calendar) {

        //  Preference에 설정한 알람 저장
        String title = labelContent.getText().toString();
        String timeDigit = new SimpleDateFormat("hh:mm", Locale.getDefault()).format(calendar.getTime());
        String timeUnit = (calendar.get(Calendar.HOUR_OF_DAY) > 12 ? "PM" : "AM");

        if (editIdx == 0) {
            SharedPreferences.Editor counter = getSharedPreferences("count", Context.MODE_PRIVATE).edit();
            alarmIdx = getSharedPreferences("count", Context.MODE_PRIVATE).getInt("count", 0);
            counter.putInt("count", alarmIdx + 1);
            counter.apply();
        } else
            alarmIdx = editIdx;

        AlarmData alarm = new AlarmData(alarmIdx, title, timeDigit, timeUnit, dayOfWeek);
        json = gson.toJson(alarm, AlarmData.class);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(String.valueOf(alarmIdx), json);
        editor.apply();
    }

    private void setAlarm(Calendar calendar) {

//        setPreferencesAlarm(calendar);
        Intent alarmIntent = new Intent(this, AlarmReceiver.class);
        alarmIntent.putExtra("idx", alarmIdx);

        String location = getIntent().getStringExtra("location");

        if (smart) {
            if (location != null) {
                alarmIntent.putExtra("smart", smart);
                alarmIntent.putExtra("location", location);

                GpsTracker gpsTracker = new GpsTracker(this);
                double latitude = gpsTracker.getLatitude();
                double longitude = gpsTracker.getLongitude();

                alarmIntent.putExtra("clat", latitude);
                alarmIntent.putExtra("clng", longitude);

            } else {
                Toast.makeText(getApplicationContext(), "위치정보가 없어 스마트 알람을 설정할 수 없습니다.", Toast.LENGTH_SHORT).show();
                smart = false;
            }
        }

        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        DateTime now = new DateTime(calendar.getTimeInMillis());
//        String setTime = LocalDateTime.parse(calendar.toString(), format).toString();
        String setTime = now.toString();
        sendRequest(setTime);

        alarmIntent.putExtra("startTime", setTime);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, alarmIdx, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);

        if (alarmManager != null) {
            if (dayOfWeek.size() > 0) {
                int min = 7;
                for (Integer day : dayOfWeek) {
                    int interval = day - calendar.get(Calendar.DAY_OF_WEEK) + 1; // Calendar.DAY_OF_WEEK: 일=1~토=7
                    interval += interval < 1 ? 7 : 0;
                    if (min > interval)
                        min = interval;

                    Calendar set = (Calendar) calendar.clone();
                    set.add(Calendar.DATE, interval);
                    alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, set.getTimeInMillis(),
                            AlarmManager.INTERVAL_DAY * 7, pendingIntent);
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, set.getTimeInMillis(), pendingIntent);
                }
                calendar.add(Calendar.DATE, min);
            } else {
                // 이미 지난 시간을 지정했다면 다음날 같은 시간으로 설정
                if (calendar.before(Calendar.getInstance()))
                    calendar.add(Calendar.DATE, 1);
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            }

            Date currentDateTime = calendar.getTime();
            String date_text = new SimpleDateFormat("yyyy년 MM월 dd일 EE요일 a hh시 mm분 ", Locale.getDefault()).format(currentDateTime);
            Toast.makeText(getApplicationContext(), date_text + "으로 알람이 설정되었습니다!", Toast.LENGTH_SHORT).show();

            PackageManager pm = getPackageManager();
            ComponentName receiver = new ComponentName(this, DeviceBootReceiver.class);

            // 부팅 후 실행되는 리시버 사용가능하게 설정
            pm.setComponentEnabledSetting(receiver,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP);
        }
    }

    public void sendRequest(String time) {
        ContentValues values = new ContentValues();
        String url = "http://13.125.23.94:5000/reverse";
        values.put("time", time);

        Log.i("values", String.valueOf(values));
        Log.i("url : ", url);
        NetworkTask networkTask = new NetworkTask(url, values, this, 2);
        networkTask.execute();
    }

    private void cancelAlarm(int idx) {

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(String.valueOf(idx));
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, idx, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (alarmManager != null)
            alarmManager.cancel(pendingIntent);

        if (pendingIntent != null)
            pendingIntent.cancel();

        PackageManager pm = getPackageManager();
        ComponentName receiver = new ComponentName(this, DeviceBootReceiver.class);

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.cl_repeat:
                showRepeatDialog();
                break;
            case R.id.cl_sound:
                showSoundDialog();
                break;
            case R.id.cl_smart:
                showSmartDialog();
                break;
            case R.id.setButton:
                int hour = picker.getHour();
                int minute = picker.getMinute();
                // 현재 지정된 시간으로 알람 시간 설정
                Calendar calendar = setCalendar(hour, minute);
                setAlarm(calendar);
                setPreferencesAlarm(calendar);
                finish();
                break;
            case R.id.delButton:
                cancelAlarm(alarmIdx);
                finish();
                break;
        }
    }


    private void showRepeatDialog() {
        final String[] daysOfWeek = new String[]{"일요일", "월요일", "화요일", "수요일", "목요일", "금요일", "토요일"};
        selectedItems = new ArrayList<>();  // Where we track the selected items

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("반복 설정")
                .setMultiChoiceItems(daysOfWeek, null, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        if (isChecked) {
                            selectedItems.add(which);
                        } else if (selectedItems.contains(which)) {
                            selectedItems.remove(Integer.valueOf(which));
                        }
                    }
                });

        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dayOfWeek = selectedItems;
                Collections.sort(dayOfWeek);
                List<String> days = new ArrayList<>();
                for (int i : dayOfWeek)
                    switch (i) {
                        case 0:
                            days.add("일");
                            break;
                        case 1:
                            days.add("월");
                            break;
                        case 2:
                            days.add("화");
                            break;
                        case 3:
                            days.add("수");
                            break;
                        case 4:
                            days.add("목");
                            break;
                        case 5:
                            days.add("금");
                            break;
                        case 6:
                            days.add("토");
                            break;
                        default:
                            break;
                    }
                String day_text = TextUtils.join(", ", days);
                repeatContent.setText(day_text);
            }
        });
//        builder.setNeutralButton("모두 선택", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                int count = 0;
//                count = adapter.getCount();
//                for (int i = 0; i < count; i++) {
//                    listview.setItemChecked(i, true);
//                }
//            }
//        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showSoundDialog() {
        String[] music = new String[]{"alarm"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("알람 사운드 설정")
                .setSingleChoiceItems(music, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Todo
                    }
                });

        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Todo
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showSmartDialog() {

        final String[] on_off = new String[]{"켜기", "끄기"};
        final int[] selected = {smart ? 0 : 1};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("스마트알람 기능")
                .setSingleChoiceItems(on_off, selected[0], new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selected[0] = which;
                    }
                });

        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                smartContent.setText(on_off[selected[0]]);
                if (selected[0] == 0)
                    smart = true;
                else
                    smart = false;

                if (!checkLocationServicesStatus()) {
                    showDialogForLocationServiceSetting();
                } else {
                    checkRunTimePermission();
                }
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    void checkRunTimePermission() {

        //런타임 퍼미션 처리
        // 1. 위치 퍼미션을 가지고 있는지 체크합니다.
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(AlarmAddActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(AlarmAddActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION);


        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {

            // 2. 이미 퍼미션을 가지고 있다면
            // ( 안드로이드 6.0 이하 버전은 런타임 퍼미션이 필요없기 때문에 이미 허용된 걸로 인식합니다.)


            // 3.  위치 값을 가져올 수 있음


        } else {  //2. 퍼미션 요청을 허용한 적이 없다면 퍼미션 요청이 필요합니다. 2가지 경우(3-1, 4-1)가 있습니다.

            // 3-1. 사용자가 퍼미션 거부를 한 적이 있는 경우에는
            if (ActivityCompat.shouldShowRequestPermissionRationale(AlarmAddActivity.this, REQUIRED_PERMISSIONS[0])) {

                // 3-2. 요청을 진행하기 전에 사용자가에게 퍼미션이 필요한 이유를 설명해줄 필요가 있습니다.
                Toast.makeText(AlarmAddActivity.this, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.", Toast.LENGTH_LONG).show();
                // 3-3. 사용자게에 퍼미션 요청을 합니다. 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(AlarmAddActivity.this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);


            } else {
                // 4-1. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 합니다.
                // 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(AlarmAddActivity.this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            }
        }
    }

    //여기부터는 GPS 활성화를 위한 메소드들
    private void showDialogForLocationServiceSetting() {

        AlertDialog.Builder builder = new AlertDialog.Builder(AlarmAddActivity.this);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n"
                + "위치 설정을 수정하실래요?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }

    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    @Override
    protected void onActivityResult(
            int requestCode,  // onActivityResult가 호출되었을 때 요청 코드로 요청을 구분
            int resultCode,   // 요청에 대한 결과 코드
            Intent data
    ) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case GPS_ENABLE_REQUEST_CODE:

                //사용자가 GPS 활성 시켰는지 검사
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {

                        Log.d("@@@", "onActivityResult : GPS 활성화 되있음");
                        checkRunTimePermission();
                        return;
                    }
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,  // requestPermissions(android.app.Activity, String, int, String[])에서 전달된 요청 코드
            @NonNull String[] permissions, // 요청한 퍼미션
            @NonNull int[] grantResults    // 퍼미션 처리 결과. PERMISSION_GRANTED 또는 PERMISSION_DENIED
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);

        if (requestCode == PERMISSIONS_REQUEST_CODE && grantResults.length == REQUIRED_PERMISSIONS.length) {

            // 요청 코드가 PERMISSIONS_REQUEST_CODE 이고, 요청한 퍼미션 개수만큼 수신되었다면

            boolean check_result = true;


            // 모든 퍼미션을 허용했는지 체크합니다.

            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }

            if (check_result) {
                //위치 값을 가져올 수 있음
                ;
            } else {
                // 거부한 퍼미션이 있다면 앱을 사용할 수 없는 이유를 설명해주고 앱을 종료합니다.2 가지 경우가 있습니다.

                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[1])) {

                    Toast.makeText(AlarmAddActivity.this, "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요.", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Toast.makeText(AlarmAddActivity.this, "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다. ", Toast.LENGTH_LONG).show();
                }
            }

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: { //toolbar의 back키 눌렀을 때 동작
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

}
