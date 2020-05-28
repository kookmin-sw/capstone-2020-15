package com.example.smalarm.ui.alarm;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smalarm.R;
import com.example.smalarm.ui.alarm.util.AlarmData;

import java.util.ArrayList;
import java.util.List;

public class AlarmItemAdapter extends RecyclerView.Adapter<AlarmItemAdapter.AlarmViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    private List<AlarmData> data = new ArrayList<>();
//    int currentPos = 0;

    public AlarmItemAdapter(Context context, List<AlarmData> data) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.data.addAll(data);
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public AlarmItemAdapter.AlarmViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                               int viewType) {
        View view = inflater.inflate(R.layout.item_alarm_list, parent, false);
        return new AlarmViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull AlarmViewHolder holder, int position) {

        // Get current position of item in recyclerview to bind data and assign values from list
        AlarmData current = data.get(position);

        holder.timeDigit.setText(current.timeDigit);
        holder.timeUnit.setText(current.timeUnit);
        holder.title.setText(current.title);
        holder.on_off.setChecked(true);

        for (Integer i : current.repeat) {
            holder.days[i].setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
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
        public SwitchCompat on_off;

        public AlarmViewHolder(View itemView) {
            super(itemView);
            timeDigit = itemView.findViewById(R.id.timeDigit);
            timeUnit = itemView.findViewById(R.id.timeUnit);
            title = itemView.findViewById(R.id.title);

            days[0] = itemView.findViewById(R.id.sunday);
            days[1] = itemView.findViewById(R.id.monday);
            days[2] = itemView.findViewById(R.id.tuesday);
            days[3] = itemView.findViewById(R.id.wednesday);
            days[4] = itemView.findViewById(R.id.thursday);
            days[5] = itemView.findViewById(R.id.friday);
            days[6] = itemView.findViewById(R.id.saturday);

            on_off = itemView.findViewById(R.id.alarmPowerSwitch);
        }
    }
}