package com.example.smalarm;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.smalarm.ui.alarm.AlarmFragment;
import com.example.smalarm.ui.graph.GraphFragment;
import com.example.smalarm.ui.settings.SettingsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.jakewharton.threetenabp.AndroidThreeTen;


public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_TEST = 65537;
    private String TAG = "MainActivity";

    Fragment alarmFragment;
    Fragment graphFragment;
    Fragment settingsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AndroidThreeTen.init(this);

        // 권한요청
        if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }


        BottomNavigationView navView = findViewById(R.id.nav_view);

        alarmFragment = new AlarmFragment();
        graphFragment = new GraphFragment();
        settingsFragment = new SettingsFragment();

        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

//        if (savedInstanceState == null) {
//            getSupportFragmentManager()
//                    .beginTransaction()
//                    .replace(R.id.container, AlarmFragment.newInstance())
//                    .addToBackStack(null)
//                    .commitNow();
//        }

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.fragment_container, AlarmFragment.newInstance());
        transaction.addToBackStack(null);
        transaction.commit();

//        Intent passedIntent = getIntent();
//        processIntent(passedIntent);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_alarm:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, alarmFragment).commit();
                    return true;
                case R.id.navigation_graph:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, graphFragment).commit();
                    return true;
                case R.id.navigation_settings:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, settingsFragment).commit();
                    return true;
            }
            return false;
        }
    };


    //                ((MainActivity) getActivity()).replaceFragment(CalendarFragment .newInstance());
//
    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
        // Fragment로 사용할 MainActivity내의 layout공간을 선택
    }

    //    @Override
//    protected void onNewIntent(Intent intent) {
//        processIntent(intent);
//        super.onNewIntent(intent);
//    }
//
//    private void processIntent(Intent intent) {
//        if (intent != null) {
//            String command = intent.getStringExtra("command");
//
//            if ("show".equals(command)) {
//                startActivity(new Intent(this, AlarmOffActivity.class));
//            }
//        }
//
//    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_TEST) {
            if (resultCode == RESULT_OK) {

                // 그래프 프레그먼트 띄어줌
                //replaceFragment(GraphFragment.newInstance());
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, graphFragment).commit();

            } else {   // RESULT_CANCEL
                Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_SHORT).show();
            }

        }
    }
}
