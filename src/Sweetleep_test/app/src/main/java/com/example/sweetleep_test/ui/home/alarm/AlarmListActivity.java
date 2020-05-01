package com.example.sweetleep_test.ui.home.alarm;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sweetleep_test.MainActivity;
import com.example.sweetleep_test.R;
import com.example.sweetleep_test.SleepSensingActivity;
import com.example.sweetleep_test.SleepSensingService;
import com.example.sweetleep_test.ui.home.calendar.CalendarStart;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class AlarmListActivity extends AppCompatActivity implements View.OnClickListener {
    private Context mContext;

    public TextView mTimeDigitText;
    public TextView mTimeUnitText;
    private SwitchCompat mAlarmButtonPower;
    private ImageView mAlarmBellImage;

    private TextView mSundayButton;
    private TextView mMondayButton;
    private TextView mTuesdayButton;
    private TextView mWednesdayButton;
    private TextView mThursdayButton;
    private TextView mFridayButton;
    private TextView mSaturdayButton;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private FloatingActionButton fab_main, fab_sub1, fab_sub2, fab_sub3;
    private Animation fab_open, fab_close;
    private boolean isFabOpen = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("알람 목록");
        setContentView(R.layout.activity_alarm_list);
        mContext = getApplicationContext();

        /**
         *  floating action button - 알람 생성, 캘린더 연동, 수면시작 버튼
         */
        fab_open = AnimationUtils.loadAnimation(mContext, R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(mContext, R.anim.fab_close);

        fab_main = (FloatingActionButton) findViewById(R.id.fab_main);
        fab_sub1 = (FloatingActionButton) findViewById(R.id.fab_sub1);
        fab_sub2 = (FloatingActionButton) findViewById(R.id.fab_sub2);
        fab_sub3 = (FloatingActionButton) findViewById(R.id.fab_sub3);

        fab_main.setOnClickListener(this);
        fab_sub1.setOnClickListener(this);
        fab_sub2.setOnClickListener(this);
        fab_sub3.setOnClickListener(this);

        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);
//        recyclerView.addItemDecoration(new DividerItemDecoration(AlarmListActivity.this, DividerItemDecoration.VERTICAL));

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // TODO: DB에서 데이터 가져오도록
        ArrayList<AlarmData> items = new ArrayList<AlarmData>();
        AlarmData item1 = new AlarmData("기상", "8:30", new int[]{1, 2, 3, 4, 5});
        AlarmData item2 = new AlarmData("수업 1", "10:30", new int[]{1, 3});
        AlarmData item3 = new AlarmData("수업 2", "12:30", new int[]{2, 4, 5});

        items.add(item1);
        items.add(item2);
        items.add(item3);
        mAdapter = new AlarmItemAdapter(AlarmListActivity.this, items);
        recyclerView.setAdapter(mAdapter);


//        String[] items = {"red", "blue", "green"};


//        ListView listView = (ListView) findViewById(R.id.ListViewId);
//        listView.setAdapter(new UserItemAdapter(this, android.R.layout.simple_list_item_1, items));
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view,
//                                    int position, long id) {
//                Toast.makeText(getApplicationContext(),
//                        "Click ListItem Number " + position, Toast.LENGTH_LONG)
//                        .show();
//            }
//        });
//
//        mTimeDigitText = (TextView) findViewById(R.id.timeDigit);
//        mTimeUnitText = (TextView) findViewById(R.id.timeUnit);
//        mAlarmBellImage = (ImageView) findViewById(R.id.alarmBell);
//        mAlarmButtonPower = (SwitchCompat) findViewById(R.id.alarmPowerSwitch);
//
//        mSundayButton = (TextView) findViewById(R.id.sunday);
//        mMondayButton = (TextView) findViewById(R.id.monday);
//        mTuesdayButton = (TextView) findViewById(R.id.tuesday);
//        mWednesdayButton = (TextView) findViewById(R.id.wednesday);
//        mThursdayButton = (TextView) findViewById(R.id.thursday);
//        mFridayButton = (TextView) findViewById(R.id.friday);
//        mSaturdayButton = (TextView) findViewById(R.id.saturday);

    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.fab_main:
                toggleFab();
                break;
            case R.id.fab_sub1:
                toggleFab();
                Toast.makeText(this, "Add Alarm", Toast.LENGTH_SHORT).show();
                intent = new Intent(this, MainActivity.class); // TODO: main과 알람설정화면 분리
                startActivity(intent);
                finish();
                break;
            case R.id.fab_sub2:
                toggleFab();
                Toast.makeText(this, "Open Calendar", Toast.LENGTH_SHORT).show();
                intent = new Intent(this, CalendarStart.class); // TODO:
                startActivity(intent);
                finish();
                break;
            case R.id.fab_sub3:
                toggleFab();
                Toast.makeText(this, "Start Sleep", Toast.LENGTH_SHORT).show();

                Intent startService = new Intent(this, SleepSensingService.class);
                startService.putExtra("inputExtra", "수면 중 뒤척임 감지중입니다.");
                ContextCompat.startForegroundService(this, startService);

                Intent sleepActivity = new Intent(getApplicationContext(), SleepSensingActivity.class);
                startActivity(sleepActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));

                break;
        }
    }

    private void toggleFab() {
        if (isFabOpen) {
            fab_main.setImageResource(R.drawable.ic_add_white);
            fab_sub1.startAnimation(fab_close);
            fab_sub2.startAnimation(fab_close);
            fab_sub3.startAnimation(fab_close);
            fab_sub1.setClickable(false);
            fab_sub2.setClickable(false);
            fab_sub3.setClickable(false);
            isFabOpen = false;
        } else {
            fab_main.setImageResource(R.drawable.ic_clear_white);
            fab_sub1.startAnimation(fab_open);
            fab_sub2.startAnimation(fab_open);
            fab_sub3.startAnimation(fab_open);
            fab_sub1.setClickable(true);
            fab_sub2.setClickable(true);
            fab_sub3.setClickable(true);
            isFabOpen = true;
        }
    }
}

