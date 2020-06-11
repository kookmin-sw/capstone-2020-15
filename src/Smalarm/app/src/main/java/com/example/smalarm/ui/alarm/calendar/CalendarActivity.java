package com.example.smalarm.ui.alarm.calendar;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smalarm.R;
import com.example.smalarm.ui.alarm.AlarmAddActivity;
import com.example.smalarm.ui.alarm.util.NetworkTask;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.model.Event;

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
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.YearMonth;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.temporal.WeekFields;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
    public void onBindViewHolder(@NonNull CalendarEventsViewHolder holder, final int position) {
        holder.bind(view, events);
//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    static class CalendarEventsViewHolder extends RecyclerView.ViewHolder {


        public CalendarEventsViewHolder(@NonNull View containerView) {
            super(containerView);
//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
////                        events[adapterPosition]   // TODO
//                }
//            });
        }

        void bind(final View view, final List<Event> events) {
            for(final Event event:events) {
                TextView eventItem = view.findViewById(R.id.itemEventText);
                eventItem.setText(event.getSummary());
                eventItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        new AlertDialog.Builder(v.getContext())
                                .setMessage("이 일정으로 알람을 정하시겠습니까?")
                                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        CalendarStart cs = ((CalendarActivity) v.getContext()).permission;
                                        cs.eventId = event.getId();
                                        cs.mID = 2;
                                        cs.getResultsFromApi();
                                    }
                                })
                                .setNegativeButton(R.string.cancel, null)
                                .show();
                    }
                });
            }
        }
    }
}

public class CalendarActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    CalendarEventsAdapter eventsAdapter;
//

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
    ProgressDialog mProgress;
    FloatingActionButton addBtn;

    GoogleAccountCredential mCredential;
    CalendarStart permission;

    @Override
    protected void onResume() {
        super.onResume();

        calendar.post(new Runnable() {
            @Override
            public void run() {
                selectDate(today);
//                    permission.mID = 1;
//                    permission.getResultsFromApi();
//                    saveEvent();
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
    }

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
        permission = new CalendarStart(this, mCredential);

        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Google Calendar API 호출 중입니다.");

        new AlertDialog.Builder(this)
                .setMessage("캘린더 사용을 위해 구글 계정 인증이 필요합니다.") // TODO: 캘린더 생성이 필요합니다.
                .setPositiveButton("인증", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        permission.mID = 1;
                        permission.getResultsFromApi();
//                        saveEvent();
                    }
                })
                .setNegativeButton(R.string.close, null)
                .show();

        selectedDateText = findViewById(R.id.selectedDateText);
        calendar = findViewById(R.id.calendar);

        currentMonth = YearMonth.now();
        daysOfWeek = daysOfWeekFromLocale();

        calendar.setup(currentMonth.minusMonths(10), currentMonth.plusMonths(10), DayOfWeek.SUNDAY);
        calendar.scrollToMonth(currentMonth);

        eventsAdapter = new CalendarEventsAdapter(this);

        rv_calendar = findViewById(R.id.rv_calendar);
        rv_calendar.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false));
        rv_calendar.setAdapter(eventsAdapter);
        rv_calendar.addItemDecoration(new DividerItemDecoration(getApplicationContext(), RecyclerView.VERTICAL));

//        addBtn = findViewById(R.id.addButton);
//        addBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                inputDialog.show();
//                permission.mID = 1;        // 이벤트 가져오기
//                permission.getResultsFromApi();
//                saveEvent();
//            }
//        });

        calendar.setMonthScrollListener(new Function1<CalendarMonth, Unit>() {
            @Override
            public Unit invoke(CalendarMonth month) {
                setTitle(titleFormatter.format(month.getYearMonth()));
                selectDate(month.getYearMonth().atDay(1));
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
                        tv.setText(daysOfWeek.get(i).toString().substring(0, 3));
//                        tv.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
                    }
                }
            }
        });

//        if (savedInstanceState == null) {
//            calendar.post(new Runnable() {
//                @Override
//                public void run() {
//                    selectDate(today);
////                    permission.mID = 1;
////                    permission.getResultsFromApi();
////                    saveEvent();
//                }
//            });
//        }
    }

    private ArrayList<DayOfWeek> daysOfWeekFromLocale() {
        DayOfWeek firstDayOfWeek = WeekFields.of(Locale.getDefault()).getFirstDayOfWeek();
        ArrayList<DayOfWeek> daysOfWeek = new ArrayList<>(Arrays.asList(DayOfWeek.values()));

        // Order `daysOfWeek` array so that firstDayOfWeek is at index 0.
        if (firstDayOfWeek != DayOfWeek.MONDAY) {
            List<DayOfWeek> rhs = daysOfWeek.subList(firstDayOfWeek.ordinal(), daysOfWeek.size());
            List<DayOfWeek> lhs = daysOfWeek.subList(0, firstDayOfWeek.ordinal());
            rhs.addAll(lhs);
            daysOfWeek = new ArrayList<>(rhs);
        }
        return daysOfWeek;
    }

    private void selectDate(LocalDate date) {
        if (selectedDate != date) {
            LocalDate oldDate = selectedDate;
            selectedDate = date;
            if (oldDate != null)
                calendar.notifyDateChanged(oldDate);
        }
        calendar.notifyDateChanged(date);
        updateAdapterForDate(date);
        selectedDateText.setText(selectionFormatter.format(date));
    }

    void saveEvent() {
        for (LocalDate date : permission.eventMap.keySet()) {
            for (Event event : permission.eventMap.get(date)) {
//                String id = event.getId();
//                String summary = event.getSummary();
//                Event event = new Event(id, summary, date);

                if (events.get(date) == null) {
                    ArrayList<Event> list = new ArrayList<>();
                    events.put(date, list);
                }
                events.get(date).add(event);
                updateAdapterForDate(date);
            }
        }
        calendar.notifyCalendarChanged();
        selectDate(today);
    }

    void getEvent(Event event) {

        Intent intent = new Intent(this, AlarmAddActivity.class);

        String location = event.getLocation();
        if (location != null)
            intent.putExtra("location", event.getLocation());

        DateTime start = event.getStart().getDateTime();
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

        if (start == null) {
            // 이벤트가 시작 시간을 갖고 있지 않으면 시작 날짜만 사용
            start = event.getStart().getDate();
            LocalDate date = LocalDate.parse(start.toString());
        } else {
            LocalDateTime date = LocalDateTime.parse(start.toString(), format);
            intent.putExtra("startTime", start.toString());
        }
        startActivity(intent);

//        String.valueOf(LocalDateTime.parse(start.toString(), format))

//        LocalDate date = event.date;
//        if (events.get(date) != null)
//            events.get(date).remove(event);
//        updateAdapterForDate(date);
    }

    private void updateAdapterForDate(LocalDate date) {
        eventsAdapter.events.clear();
        eventsAdapter.events.addAll(events.get(date) == null ? new ArrayList<Event>() : events.get(date));
        eventsAdapter.notifyDataSetChanged();
//        selectedDateText.setText(selectionFormatter.format(date));
    }

    public String getCurrentAddress(double latitude, double longitude) {

        //지오코더... GPS를 주소로 변환
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        List<Address> addresses;

        try {

            addresses = geocoder.getFromLocation(
                    latitude,
                    longitude,
                    7);
        } catch (IOException ioException) {
            //네트워크 문제
            Toast.makeText(this, "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show();
            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(this, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
            return "잘못된 GPS 좌표";

        }


        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(this, "주소 미발견", Toast.LENGTH_LONG).show();
            return "주소 미발견";

        }

        Address address = addresses.get(0);
        return address.getAddressLine(0).toString() + "\n";

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: { // toolbar의 back키 눌렀을 때 동작
                mProgress.dismiss();
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
