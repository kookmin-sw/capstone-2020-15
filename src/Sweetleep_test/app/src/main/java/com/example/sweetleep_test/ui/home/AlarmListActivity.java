package com.example.sweetleep_test.ui.home;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sweetleep_test.R;

import java.util.ArrayList;
import java.util.List;

public class AlarmListActivity extends AppCompatActivity {

//    final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(MainActivity.this);

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


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("알람 목록");
        setContentView(R.layout.activity_alarm_list);
        /**
         * 알람 추가 버튼 클릭시 BottomSheetDialog
         * TODO: 빠른 알람 dialog, 일반 알람 activity, 캘린더 연동 activity 연결
         */
//        bottomSheetDialog.setContentView(R.layout.activity_alarm_list);
//        bottomSheetDialog.show();

//        String[] items = {"red", "blue", "green"};
        ArrayList<AlarmInfo> items = new ArrayList<AlarmInfo>();
        AlarmInfo item1 = new AlarmInfo("기상", "8:30", new int[]{1, 2, 3, 4, 5});
        AlarmInfo item2 = new AlarmInfo("수업 1", "10:30", new int[]{1, 3});
        AlarmInfo item3 = new AlarmInfo("수업 2", "12:30", new int[]{2,4, 5});
        items.add(item1);
        items.add(item2);
        items.add(item3);

        ListView listView = (ListView) findViewById(R.id.ListViewId);
        listView.setAdapter(new UserItemAdapter(this, android.R.layout.simple_list_item_1, items));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Toast.makeText(getApplicationContext(),
                        "Click ListItem Number " + position, Toast.LENGTH_LONG)
                        .show();
            }
        });


        mTimeDigitText = (TextView) findViewById(R.id.timeDigit);
        mTimeUnitText = (TextView) findViewById(R.id.timeUnit);
        mAlarmBellImage = (ImageView) findViewById(R.id.alarmBell);
        mAlarmButtonPower = (SwitchCompat) findViewById(R.id.alarmPowerSwitch);

        mSundayButton = (TextView) findViewById(R.id.sunday);
        mMondayButton = (TextView) findViewById(R.id.monday);
        mTuesdayButton = (TextView) findViewById(R.id.tuesday);
        mWednesdayButton = (TextView) findViewById(R.id.wednesday);
        mThursdayButton = (TextView) findViewById(R.id.thursday);
        mFridayButton = (TextView) findViewById(R.id.friday);
        mSaturdayButton = (TextView) findViewById(R.id.saturday);



    }
}

