package com.example.smalarm.ui.alarm;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.os.BatteryManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smalarm.R;
import com.example.smalarm.ui.alarm.calendar.CalendarActivity;
import com.example.smalarm.ui.alarm.sleep.SleepSensingActivity;
import com.example.smalarm.ui.alarm.sleep.SleepSensingService;
import com.example.smalarm.ui.alarm.util.AlarmData;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Collection;

public class AlarmFragment extends Fragment implements View.OnClickListener {

//    private AlarmViewModel alarmViewModel;
    private Context mContext;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;

    private FloatingActionButton fab_main, fab_sub1, fab_sub2, fab_sub3;
    private Animation fab_open, fab_close;
    private boolean isFabOpen = false;

    private int REQUEST_TEST = 1;

    public static AlarmFragment newInstance() {
        return new AlarmFragment();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

//        alarmViewModel = new ViewModelProvider(this).get(AlarmViewModel.class);
        View root = inflater.inflate(R.layout.fragment_alarm, container, false);

        getActivity().setTitle("알람 목록");
        mContext = getActivity().getApplicationContext();

        // floating action button - 알람 생성, 캘린더 연동, 수면시작 버튼
        fab_open = AnimationUtils.loadAnimation(mContext, R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(mContext, R.anim.fab_close);

        fab_main = root.findViewById(R.id.fab_main);
        fab_sub1 = root.findViewById(R.id.fab_sub1);
        fab_sub2 = root.findViewById(R.id.fab_sub2);
        fab_sub3 = root.findViewById(R.id.fab_sub3);
        fab_main.setOnClickListener(this);
        fab_sub1.setOnClickListener(this);
        fab_sub2.setOnClickListener(this);
        fab_sub3.setOnClickListener(this);

        recyclerView = root.findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);
//        recyclerView.addItemDecoration(new DividerItemDecoration(AlarmListActivity.this, DividerItemDecoration.VERTICAL));

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(mLayoutManager);

        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                // Row is swiped from recycler view
                // TODO: remove it from adapter
            }
            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                // view the background view
            }
        };

        // attaching the touch helper to recycler view
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);


        return root;
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
                intent = new Intent(mContext, AlarmAddActivity.class);
                startActivity(intent);
                break;
            case R.id.fab_sub2:
                toggleFab();
                Toast.makeText(mContext, "Open Calendar", Toast.LENGTH_SHORT).show();
                intent = new Intent(mContext, CalendarActivity.class); // TODO:
                startActivity(intent);
                break;
            case R.id.fab_sub3:
                toggleFab();
                Toast.makeText(mContext, "Start Sleep", Toast.LENGTH_SHORT).show();

                Intent startService = new Intent(mContext, SleepSensingService.class);
                startService.putExtra("inputExtra", "수면 중 뒤척임 감지중입니다.");
                ContextCompat.startForegroundService(mContext, startService);

                Intent sleepIntent = new Intent(mContext.getApplicationContext(), SleepSensingActivity.class);
                startActivityForResult(sleepIntent, REQUEST_TEST);

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

    @Override
    public void onResume() {
        super.onResume();

        ArrayList<AlarmData> items = new ArrayList<>();
        SharedPreferences sp = getActivity().getSharedPreferences("alarms", Context.MODE_PRIVATE);
        Gson gson = new GsonBuilder().create();
        Collection<?> values = sp.getAll().values();

        for (Object value : values) {
            String json = (String) value;
            items.add(gson.fromJson(json, AlarmData.class));
        }
        mAdapter = new AlarmItemAdapter(getActivity(), items);
        recyclerView.setAdapter(mAdapter);
//        mAdapter.notifyDataSetChanged();

    }
}
