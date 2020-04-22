package com.example.sweetleep_test.ui.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sweetleep_test.R;

import java.util.ArrayList;

public class UserItemAdapter extends ArrayAdapter<AlarmInfo> {
    private ArrayList<AlarmInfo> alarms;

    public UserItemAdapter(Context context, int textViewResourceId, ArrayList<AlarmInfo> alarms) {
        super(context, textViewResourceId, alarms);
        this.alarms = alarms;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.listitem, null);
        }
        AlarmInfo alarm = alarms.get(position);
        if (alarm != null) {
            TextView title = v.findViewById(R.id.title);
            TextView time = v.findViewById(R.id.timeDigit);
            TextView[] days = new TextView[7];
            days[0] = (TextView) v.findViewById(R.id.sunday);
            days[1]  = (TextView) v.findViewById(R.id.monday);
            days[2]  = (TextView) v.findViewById(R.id.tuesday);
            days[3]  = (TextView) v.findViewById(R.id.wednesday);
            days[4]  = (TextView) v.findViewById(R.id.thursday);
            days[5]  = (TextView) v.findViewById(R.id.friday);
            days[6]  = (TextView) v.findViewById(R.id.saturday);

            if (title != null) {
                title.setText(alarm.title);
            }
            if (time != null) {
                time.setText(alarm.time);
            }
            if (days != null){

                for (int i = 0; i < alarm.selected.length; i++) {
                    int day = alarm.selected[i];
                    days[day].setTextColor(v.getResources().getColor(R.color.colorAccent));
                }
            }


        }

        return v;
    }
}