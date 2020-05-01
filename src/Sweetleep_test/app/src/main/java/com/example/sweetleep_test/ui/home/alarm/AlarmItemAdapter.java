package com.example.sweetleep_test.ui.home.alarm;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sweetleep_test.R;

import java.util.Collections;
import java.util.List;

public class AlarmItemAdapter extends RecyclerView.Adapter<AlarmItemAdapter.AlarmViewHolder> {

    //    private String[] mDataset;
    private Context context;
    private LayoutInflater inflater;

    List<AlarmData> data = Collections.emptyList();
    AlarmData current;
    int currentPos = 0;

    public AlarmItemAdapter(Context context, List<AlarmData> data) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = data;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public AlarmItemAdapter.AlarmViewHolder onCreateViewHolder(ViewGroup parent,
                                                               int viewType) {
        View view=inflater.inflate(R.layout.listitem, parent,false);
        AlarmViewHolder holder=new AlarmViewHolder(view);
        return holder;
    }


    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(AlarmViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
//        holder.textView.setText(mDataset[position]);

        // Get current position of item in recyclerview to bind data and assign values from list
        AlarmViewHolder myHolder= (AlarmViewHolder) holder;
        AlarmData current=data.get(position);
        myHolder.timeDigit.setText(current.timeDigit);
        myHolder.timeUnit.setText(current.timeUnit);
        myHolder.title.setText(current.title);

        for (Integer i:current.repeat) {
            myHolder.days[i].setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
        }

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class AlarmViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView timeDigit;
        public TextView timeUnit; // AM/PM
        public TextView title;
        public TextView[] days = new TextView[7];

        public AlarmViewHolder(View itemView) {
            super(itemView);
            timeDigit = (TextView) itemView.findViewById(R.id.timeDigit);
            timeUnit = (TextView) itemView.findViewById(R.id.timeUnit);
            title = (TextView) itemView.findViewById(R.id.title);
            days[0] = (TextView) itemView.findViewById(R.id.sunday);
            days[1] = (TextView) itemView.findViewById(R.id.monday);
            days[2] = (TextView) itemView.findViewById(R.id.tuesday);
            days[3] = (TextView) itemView.findViewById(R.id.wednesday);
            days[4] = (TextView) itemView.findViewById(R.id.thursday);
            days[5] = (TextView) itemView.findViewById(R.id.friday);
            days[6] = (TextView) itemView.findViewById(R.id.saturday);
        }
    }
}


//public class UserItemAdapter extends ArrayAdapter<AlarmInfo> {
//    private ArrayList<AlarmInfo> alarms;
//
//    public UserItemAdapter(Context context, int textViewResourceId, ArrayList<AlarmInfo> alarms) {
//        super(context, textViewResourceId, alarms);
//        this.alarms = alarms;
//    }
//
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        View v = convertView;
//        if (v == null) {
//            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            v = vi.inflate(R.layout.listitem, null);
//        }
//        AlarmInfo alarm = alarms.get(position);
//        if (alarm != null) {
//            TextView title = v.findViewById(R.id.title);
//            TextView time = v.findViewById(R.id.timeDigit);
//            TextView[] days = new TextView[7];
//            days[0] = (TextView) v.findViewById(R.id.sunday);
//            days[1]  = (TextView) v.findViewById(R.id.monday);
//            days[2]  = (TextView) v.findViewById(R.id.tuesday);
//            days[3]  = (TextView) v.findViewById(R.id.wednesday);
//            days[4]  = (TextView) v.findViewById(R.id.thursday);
//            days[5]  = (TextView) v.findViewById(R.id.friday);
//            days[6]  = (TextView) v.findViewById(R.id.saturday);
//
//            if (title != null) {
//                title.setText(alarm.title);
//            }
//            if (time != null) {
//                time.setText(alarm.time);
//            }
//            if (days != null){
//
//                for (int i = 0; i < alarm.selected.length; i++) {
//                    int day = alarm.selected[i];
//                    days[day].setTextColor(v.getResources().getColor(R.color.colorAccent));
//                }
//            }
//
//
//        }
//
//        return v;
//    }
//}