package com.example.smalarm.ui.alarm.calendar;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smalarm.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.CalendarScopes;
import com.kizitonwose.calendarview.CalendarView;
import com.kizitonwose.calendarview.model.CalendarDay;
import com.kizitonwose.calendarview.model.CalendarMonth;
import com.kizitonwose.calendarview.model.DayOwner;
import com.kizitonwose.calendarview.ui.DayBinder;
import com.kizitonwose.calendarview.ui.MonthHeaderFooterBinder;
import com.kizitonwose.calendarview.ui.ViewContainer;

import org.jetbrains.annotations.NotNull;
import org.threeten.bp.DayOfWeek;
import org.threeten.bp.LocalDate;
import org.threeten.bp.YearMonth;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.temporal.WeekFields;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import pub.devrel.easypermissions.EasyPermissions;


//private InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);

class CalendarEventsAdapter extends RecyclerView.Adapter<CalendarEventsAdapter.CalendarEventsViewHolder> {

    List<Event> events = new ArrayList<>();
    private View view;
    private LayoutInflater inflater;

    public CalendarEventsAdapter(Context context) {
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public CalendarEventsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = inflater.inflate(R.layout.item_calendar_event, parent, false);
        return new CalendarEventsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarEventsViewHolder holder, int position) {
        holder.bind(view, events.get(position));
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    static class CalendarEventsViewHolder extends RecyclerView.ViewHolder {

        public CalendarEventsViewHolder(@NonNull View containerView) {
            super(containerView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                        events[adapterPosition]   // TODO
                }
            });
        }

        void bind(View view, Event event) {
            TextView eventItem = view.findViewById(R.id.itemEventText);
            eventItem.setText(event.text);
        }
    }
}

public class CalendarActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    CalendarEventsAdapter eventsAdapter;
//    {
//        new AlertDialog.Builder(requireContext())
//                .setMessage("삭제하시겠습니까?")
//                .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
////                        deleteEvent(it);  // it == Event
//                    }
//                })
//                .setNegativeButton(R.string.close, null)
//                .show();
//    }

    private LocalDate selectedDate;
    private LocalDate today = LocalDate.now();

    private DateTimeFormatter titleFormatter = DateTimeFormatter.ofPattern("yyyy년 MMM");
    private DateTimeFormatter selectionFormatter = DateTimeFormatter.ofPattern("yyyy년 MMM dd일 E");
    private Map<LocalDate, List<Event>> events = new HashMap<>();

    ArrayList<DayOfWeek> daysOfWeek;
    YearMonth currentMonth;

    CalendarView calendar;
    TextView selectedDateText;
    RecyclerView rv_calendar;
    FloatingActionButton addBtn;

    GoogleAccountCredential mCredential;
    CalendarStart permission;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("일정 선택");
        setContentView(R.layout.activity_calendar); // TODO

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(),
                Arrays.asList(CalendarStart.SCOPES)
        ).setBackOff(new ExponentialBackOff()); // I/O 예외 상황을 대비해서 백오프 정책 사용
        permission =new CalendarStart(this, mCredential);

        new AlertDialog.Builder(this)
                .setMessage("캘린더 사용을 위해 구글 계정 인증이 필요합니다.") // TODO: 캘린더 생성이 필요합니다.
                .setPositiveButton("인증", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        permission.mID = today.getMonth().getValue();
                        permission.getResultsFromApi();
                        saveEvent();
                    }
                })
                .setNegativeButton(R.string.close, null)
                .show();

        selectedDateText = findViewById(R.id.selectedDateText);
        currentMonth = YearMonth.now();

        calendar = findViewById(R.id.calendar);
        calendar.setup(currentMonth.minusMonths(10), currentMonth.plusMonths(10), DayOfWeek.SUNDAY);
        calendar.scrollToMonth(currentMonth);
        daysOfWeek = daysOfWeekFromLocale();

        if (savedInstanceState == null) {
            calendar.post(new Runnable() {
                @Override
                public void run() {
                    selectDate(today);
                }
            });
        }

        eventsAdapter = new CalendarEventsAdapter(this);
        rv_calendar = findViewById(R.id.rv_calendar);
        rv_calendar.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false));
        rv_calendar.setAdapter(eventsAdapter);
        rv_calendar.addItemDecoration(new DividerItemDecoration(getApplicationContext(), RecyclerView.VERTICAL));

        addBtn = findViewById(R.id.addButton);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                inputDialog.show();
                permission.mID = 5;        // 이벤트 가져오기
                permission.getResultsFromApi();

            }
        });

        class DayViewContainer extends ViewContainer {

            CalendarDay day;
            TextView textView;
            View dotView;

            public DayViewContainer(@NotNull View view) {
                super(view);
                textView = view.findViewById(R.id.dayText);
                dotView = view.findViewById(R.id.dotView);

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (day.getOwner() == DayOwner.THIS_MONTH) {
                            selectDate(day.getDate());
                        }
                    }
                });
            }
        }

        calendar.setDayBinder(new DayBinder<DayViewContainer>() {
            @NotNull
            @Override
            public DayViewContainer create(@NotNull View view) {
                return new DayViewContainer(view);
            }

            @Override
            public void bind(@NotNull DayViewContainer container, @NotNull CalendarDay day) {
                container.day = day;
                TextView textView = container.textView;
                View dotView = container.dotView;

                textView.setText(String.valueOf(day.getDate().getDayOfMonth()));

                if (day.getOwner() == DayOwner.THIS_MONTH) {
                    makeVisible(textView);
                    LocalDate date = day.getDate();

                    if (date.equals(today)) {
                        textView.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                        textView.setBackgroundResource(R.drawable.today_bg_oval);
                        makeInVisible(dotView);
                    } else if (date.equals(selectedDate)) {
                        textView.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));
                        textView.setBackgroundResource(R.drawable.selected_bg_oval);
                        makeInVisible(dotView);
                    } else {
                        textView.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
                        textView.setBackground(null);
                        if (events.get(day.getDate()) != null)
                            makeVisible(dotView);
                        else
                            makeInVisible(dotView);
                    }
                } else {
                    makeInVisible(textView);
                    makeInVisible(dotView);
                }
                updateAdapterForDate(day.getDate());
            }

            void makeVisible(View v) {
                v.setVisibility(View.VISIBLE);
            }

            void makeInVisible(View v) {
                v.setVisibility(View.INVISIBLE);
            }
        });


        calendar.setMonthScrollListener(new Function1<CalendarMonth, Unit>() {
            @Override
            public Unit invoke(CalendarMonth month) {
                setTitle(titleFormatter.format(month.getYearMonth()));
                selectDate(month.getYearMonth().atDay(1));

                permission.mID = month.getMonth()-1;
                permission.getResultsFromApi();
                saveEvent();

                return Unit.INSTANCE;
            }
        });

        class MonthViewContainer extends ViewContainer {
            LinearLayout legendLayout;

            public MonthViewContainer(@NotNull View view) {
                super(view);
                legendLayout = view.findViewById(R.id.legendLayout);
            }
        }

        calendar.setMonthHeaderBinder(new MonthHeaderFooterBinder<MonthViewContainer>() {
            @NotNull
            @Override
            public MonthViewContainer create(@NotNull View view) {
                return new MonthViewContainer(view);
            }

            @Override
            public void bind(@NotNull MonthViewContainer container, @NotNull CalendarMonth month) {
                if (container.legendLayout.getTag() == null) {
                    container.legendLayout.setTag(month.getYearMonth());

                    int count = container.legendLayout.getChildCount();
                    TextView tv;
                    for (int i = 0; i < count; i++) {
                        tv = (TextView) container.legendLayout.getChildAt(i);
                        tv.setText(daysOfWeek.get(i).toString().substring(0,3));
//                        tv.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
                    }
                }
            }
        });
    }

    private ArrayList<DayOfWeek> daysOfWeekFromLocale() {
        DayOfWeek firstDayOfWeek = WeekFields.of(Locale.getDefault()).getFirstDayOfWeek();
        ArrayList<DayOfWeek> daysOfWeek = new ArrayList<>(Arrays.asList(DayOfWeek.values()));

        // Order `daysOfWeek` array so that firstDayOfWeek is at index 0.
        if (firstDayOfWeek != DayOfWeek.MONDAY) {
            List<DayOfWeek> rhs = daysOfWeek.subList(firstDayOfWeek.ordinal(), daysOfWeek.size());
            List<DayOfWeek> lhs = daysOfWeek.subList(0, firstDayOfWeek.ordinal());
            rhs.addAll(lhs);
            daysOfWeek= new ArrayList<>(rhs);
        }
        return daysOfWeek;
    }

    private void selectDate(LocalDate date) {
        if (selectedDate != date) {
            LocalDate oldDate = selectedDate;
            selectedDate = date;
            if (oldDate != null)
                calendar.notifyDateChanged(oldDate);
            calendar.notifyDateChanged(date);
            updateAdapterForDate(date);
        }
    }

    void saveEvent() {
//        if (text.isEmpty()) {
//            Toast.makeText(requireContext(), R.string.example_3_empty_input_text, Toast.LENGTH_LONG).show()
//        } else {
//            selectedDate. ?.let {
//                events[it] = events[it].orEmpty().plus(Event(UUID.randomUUID().toString(), text, it))
//                updateAdapterForDate(it)
//            }
//        }
        for( LocalDate date : permission.string.keySet() ){
            Event event = new Event(UUID.randomUUID().toString(), permission.string.get(date), date);
            if(events.get(date) == null) {
                ArrayList<Event> list = new ArrayList<>();
                events.put(date, list);
            }
            events.get(date).add(event);
        }


//        for (String text : permission.strings) {
//            Event event = new Event(UUID.randomUUID().toString(), text, selectedDate);
//            if(events.get(selectedDate) == null) {
//                ArrayList<Event> list = new ArrayList<>();
//                events.put(selectedDate, list);
//            }
//            events.get(selectedDate).add(event);
//        }
//        updateAdapterForDate(selectedDate);

//        permission.eventStrings
    }

    private void deleteEvent(Event event) {
        LocalDate date = event.date;
        if (events.get(date) != null)
            events.get(date).remove(event);
        updateAdapterForDate(date);
    }

    private void updateAdapterForDate(LocalDate date) {
        eventsAdapter.events.clear();
        eventsAdapter.events.addAll(events.get(date) == null ? new ArrayList<Event>() : events.get(date));
        eventsAdapter.notifyDataSetChanged();
        selectedDateText.setText(selectionFormatter.format(date));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: { // toolbar의 back키 눌렀을 때 동작
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }


    /*
     * 구글 플레이 서비스 업데이트 다이얼로그, 구글 계정 선택 다이얼로그, 인증 다이얼로그에서 되돌아올때 호출된다.
     */
    @Override
    protected void onActivityResult(
            int requestCode,  // onActivityResult가 호출되었을 때 요청 코드로 요청을 구분
            int resultCode,   // 요청에 대한 결과 코드
            Intent data
    ) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case CalendarStart.REQUEST_GOOGLE_PLAY_SERVICES:

                if (resultCode == Activity.RESULT_OK) {
                    permission.getResultsFromApi();
//                    mStatusText.setText(R.string.request_google_play_services);
                } else {
                    Toast.makeText(this, R.string.request_google_play_services, Toast.LENGTH_LONG).show();
                }
                break;

            case CalendarStart.REQUEST_ACCOUNT_PICKER:
                if (resultCode == Activity.RESULT_OK && data != null && data.getExtras() != null) {
                    String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(CalendarStart.PREF_ACCOUNT_NAME, accountName);
                        editor.apply();
                        mCredential.setSelectedAccountName(accountName);
                        permission.getResultsFromApi();
                    }
                }
                break;

            case CalendarStart.REQUEST_AUTHORIZATION:

                if (resultCode == Activity.RESULT_OK) {
                    permission.getResultsFromApi();
                }
                break;
        }
    }


    /*
     * Android 6.0 (API 23) 이상에서 런타임 권한 요청시 결과를 리턴받음
     */
    @Override
    public void onRequestPermissionsResult(
            int requestCode,  // requestPermissions(android.app.Activity, String, int, String[])에서 전달된 요청 코드
            @NonNull String[] permissions, // 요청한 퍼미션
            @NonNull int[] grantResults    // 퍼미션 처리 결과. PERMISSION_GRANTED 또는 PERMISSION_DENIED
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {

    }
}
