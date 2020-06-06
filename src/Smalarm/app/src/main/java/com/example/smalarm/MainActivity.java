package com.example.smalarm;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.smalarm.ui.alarm.AlarmFragment;
import com.example.smalarm.ui.alarm.AlarmOffActivity;
import com.example.smalarm.ui.graph.GraphFragment;
import com.example.smalarm.ui.settings.SettingsFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.jakewharton.threetenabp.AndroidThreeTen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class MainActivity extends AppCompatActivity {

    private String TAG = "MainActivity";

    Fragment alarmFragment;
    Fragment graphFragment;
    Fragment settingsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AndroidThreeTen.init(this);

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
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,alarmFragment).commit();
                    return true;
                case R.id.navigation_graph:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, graphFragment).commit();
                    return true;
                case R.id.navigation_settings:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,settingsFragment).commit();
                    return true;
            }
            return false;
        }
    };

//                ((MainActivity) getActivity()).replaceFragment(CalendarFragment .newInstance());
//
//    public void replaceFragment(Fragment fragment) {
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        FragmentTransaction transaction = fragmentManager.beginTransaction();
//        transaction.replace(R.id.fragment_container, fragment);
//        transaction.addToBackStack(null);
//        transaction.commit();
//        // Fragment로 사용할 MainActivity내의 layout공간을 선택
//    }

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
}
