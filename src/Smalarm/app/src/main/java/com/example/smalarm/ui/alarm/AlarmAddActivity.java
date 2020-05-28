package com.example.smalarm.ui.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.smalarm.R;
import com.example.smalarm.ui.alarm.util.AlarmData;
import com.example.smalarm.ui.alarm.util.AlarmReceiver;
import com.example.smalarm.ui.alarm.util.DeviceBootReceiver;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

enum DayOfWeek {
    일, 월, 화, 수, 목, 금, 토
}

public class AlarmAddActivity extends AppCompatActivity implements View.OnClickListener {

    private SharedPreferences sharedPreferences;
    private Gson gson = new GsonBuilder().create();
    private String json;

    private TimePicker picker;
    private EditText labelContent;
    private TextView repeatContent;

    private List<Integer> selectedItems;
    private List<Integer> dayOfWeek = Collections.emptyList();
    private int alarmIdx;
    private int editIdx;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fragment_alarm_add);
        picker = findViewById(R.id.timePicker);

        // 앞서 설정한 값으로 보여주기 없으면 디폴트 값은 현재시간
        sharedPreferences = getSharedPreferences("alarms", Context.MODE_PRIVATE);
        editIdx = getIntent().getIntExtra("idx", 0);
        if (editIdx != 0) getReservedTime();

        labelContent = findViewById(R.id.labelContent);
        repeatContent = findViewById(R.id.repeatContent);

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
        ConstraintLayout snoozeCl = findViewById(R.id.cl_snooze);
        repeatCl.setOnClickListener(this);
        soundCl.setOnClickListener(this);
        snoozeCl.setOnClickListener(this);

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

    private void setPreferencesAlarm(Calendar calendar) {

        //  Preference에 설정한 알람 저장
        String title = labelContent.getText().toString();
        String timeDigit = new SimpleDateFormat("hh:mm", Locale.getDefault()).format(calendar.getTime());
        String timeUnit = (calendar.get(Calendar.HOUR_OF_DAY) > 12 ? "PM" : "AM");

        AlarmData alarm = new AlarmData(title, timeDigit, timeUnit, dayOfWeek);
        json = gson.toJson(alarm, AlarmData.class);

        if (editIdx == 0) {
            SharedPreferences.Editor counter = getSharedPreferences("count", Context.MODE_PRIVATE).edit();
            alarmIdx = getSharedPreferences("count", Context.MODE_PRIVATE).getInt("count", 0);
            counter.putInt("count", alarmIdx + 1);
            counter.apply();
        } else
            alarmIdx = editIdx;

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(String.valueOf(alarmIdx), json);
        editor.apply();
    }

    private void setAlarm(Calendar calendar) {

//        setPreferencesAlarm(calendar);
        Intent alarmIntent = new Intent(this, AlarmReceiver.class);
        alarmIntent.putExtra("idx", alarmIdx);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, alarmIdx, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);

        if (alarmManager != null) {
            if (dayOfWeek.size() > 0) {
                int min = 7;
                for (Integer day : dayOfWeek) {
                    int interval = day - calendar.get(Calendar.DAY_OF_WEEK) + 1; // Calendar.DAY_OF_WEEK: 일=1~토=7
                    interval += interval < 1? 7: 0;
                    if (min>interval)
                        min = interval;

                    Calendar set = (Calendar) calendar.clone();
                    set.add(Calendar.DATE, interval);
                    alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, set.getTimeInMillis(),
                            AlarmManager.INTERVAL_DAY * 7, pendingIntent);
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, set.getTimeInMillis(), pendingIntent);
                }
                calendar.add(Calendar.DATE, min);
            } else{
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

    private void cancelAlarm() {

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, alarmIdx, alarmIntent, PendingIntent.FLAG_ONE_SHOT);

        if (alarmManager != null)
            alarmManager.cancel(pendingIntent);

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
// TODO               showSoundDialog();
                break;
            case R.id.cl_snooze:
// TODO               showSnoozeDialog();
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
                cancelAlarm();
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

        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
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

}
