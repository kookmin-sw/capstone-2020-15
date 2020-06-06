//package com.example.smalarm.ui.alarm.calendar;
//
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.graphics.Color;
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.AlertDialog;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.appcompat.widget.Toolbar;
//import androidx.core.content.ContextCompat;
//import androidx.fragment.app.Fragment;
//import androidx.recyclerview.widget.DividerItemDecoration;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.smalarm.R;
//import com.google.android.material.floatingactionbutton.FloatingActionButton;
//import com.jakewharton.threetenabp.AndroidThreeTen;
//import com.kizitonwose.calendarview.CalendarView;
//import com.kizitonwose.calendarview.model.CalendarDay;
//import com.kizitonwose.calendarview.model.CalendarMonth;
//import com.kizitonwose.calendarview.model.DayOwner;
//import com.kizitonwose.calendarview.ui.DayBinder;
//import com.kizitonwose.calendarview.ui.MonthHeaderFooterBinder;
//import com.kizitonwose.calendarview.ui.ViewContainer;
//
//import org.jetbrains.annotations.NotNull;
//import org.threeten.bp.DayOfWeek;
//import org.threeten.bp.LocalDate;
//import org.threeten.bp.YearMonth;
//import org.threeten.bp.format.DateTimeFormatter;
//import org.threeten.bp.temporal.WeekFields;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Collections;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Locale;
//import java.util.Map;
//import java.util.Set;
//import java.util.UUID;
//
//import kotlin.Unit;
//import kotlin.jvm.functions.Function1;
//
//
////private InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
//
////private val Context.inputMethodManager
////        get() = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//
//class Event {
//    String id;
//    String text;
//    LocalDate date;
//}
//
//class CalendarEventsAdapter extends RecyclerView.Adapter<CalendarEventsAdapter.CalendarEventsViewHolder> {
//    List<Event> events = new ArrayList<Event>();
////        List<Event> events = mutableListOf<Event>();
//
//    private LayoutInflater inflater;
//
//    public CalendarEventsAdapter(Context context) {
//        inflater = LayoutInflater.from(context);
//    }
//
//    @NonNull
//    @Override
//    public CalendarEventsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
////        return CalendarEventsViewHolder(parent.inflate(R.layout.item_calendar_event, parent, false));
//
//        View view = inflater.inflate(R.layout.item_calendar_event, parent, false);
//        return new CalendarEventsViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull CalendarEventsViewHolder holder, int position) {
//        holder.bind(events.get(position));
//    }
//
//    @Override
//    public int getItemCount() {
//        return events.size();
//    }
//
//
//    static class CalendarEventsViewHolder extends RecyclerView.ViewHolder {
//
//        public CalendarEventsViewHolder(@NonNull View containerView) {
//            super(containerView);
//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
////                        events[adapterPosition]
//                }
//            });
//        }
//
//        void bind(Event event) {
////                itemEventText.text = event.text;   id
//        }
//    }
//}
//
//
////public class CalendarActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener {
//public class CalendarFragment extends Fragment {
//
//    private CalendarEventsAdapter eventsAdapter;
////
////    {
////        new AlertDialog.Builder(requireContext())
////                .setMessage("삭제하시겠습니까?")
////                .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
////                    @Override
////                    public void onClick(DialogInterface dialog, int which) {
//////                        deleteEvent(it);  // it == Event
////                    }
////                })
////                .setNegativeButton(R.string.close, null)
////                .show();
////    }
//
//    private CalendarView calendarView;
//    private CalendarStart calConn;
//
//    private LocalDate selectedDate = null;
//    private LocalDate today = LocalDate.now();
//
//    private DateTimeFormatter titleSameYearFormatter = DateTimeFormatter.ofPattern("MMMM");
//    private DateTimeFormatter titleFormatter = DateTimeFormatter.ofPattern("MMM yyyy");
//    private DateTimeFormatter selectionFormatter = DateTimeFormatter.ofPattern("d MMM yyyy");
//    private Map<LocalDate, List<Event>> events = new HashMap<>();
//
//    ArrayList<DayOfWeek> daysOfWeek;
//    YearMonth currentMonth;
//
//    CalendarView calendar;
//    TextView selectedDateText;
//    RecyclerView rv_calendar;
//    FloatingActionButton addBtn;
//
//
////    Set<Long> disabledDays = new HashSet<>();
//
////    @Override
////    protected void onCreate(@Nullable Bundle savedInstanceState) {
////        super.onCreate(savedInstanceState);
////        setTitle("일정 선택");
////        setContentView(R.layout.activity_calendar);
////        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
////        calConn = new CalendarStart();
////
////        AndroidThreeTen.init(this);
////
//////        addEvent();
////        initViews();
////    }
//
////    @Override
////    protected void onCreate(@Nullable Bundle savedInstanceState) {
////        super.onCreate(savedInstanceState);
////
////    }
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//
//        View root = inflater.inflate(R.layout.activity_calendar, container, false);
//
//        return root;
//    }
//
//    ArrayList<DayOfWeek> daysOfWeekFromLocale()  {
//        DayOfWeek firstDayOfWeek = WeekFields.of(Locale.getDefault()).getFirstDayOfWeek();
//        ArrayList<DayOfWeek> daysOfWeek = new ArrayList<>();
//        daysOfWeek.addAll(Arrays.asList(DayOfWeek.values()));
//
//        // Order `daysOfWeek` array so that firstDayOfWeek is at index 0.
//        if (firstDayOfWeek != DayOfWeek.MONDAY) {
//            List<DayOfWeek> rhs =daysOfWeek.subList(firstDayOfWeek.ordinal(),daysOfWeek.size()-1);
//            List<DayOfWeek> lhs = daysOfWeek.subList(0,firstDayOfWeek.ordinal());
//            rhs.addAll(lhs);
//            daysOfWeek.addAll(rhs);
//        }
//        return daysOfWeek;
//    }
//
//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//
//        calendar = view.findViewById(R.id.calendar);
//        selectedDateText = view.findViewById(R.id.selectedDateText);
//        rv_calendar = view.findViewById(R.id.rv_calendar);
//        addBtn = view.findViewById(R.id.addButton);
//
//        rv_calendar.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false));
//        rv_calendar.setAdapter(eventsAdapter);
//        rv_calendar.addItemDecoration(new DividerItemDecoration(requireContext(), RecyclerView.VERTICAL));
//
//
//        daysOfWeek = daysOfWeekFromLocale();
//        currentMonth = YearMonth.now();
//        calendar.setup(currentMonth.minusMonths(10), currentMonth.plusMonths(10), daysOfWeek.get(0));
//        calendar.scrollToMonth(currentMonth);
//
//        if (savedInstanceState == null) {
//            calendar.post(new Runnable() {
//                @Override
//                public void run() {
//                    selectDate(today);
//                }
//            });
//        }
//
//        class DayViewContainer extends ViewContainer {
//
//            CalendarDay day;
//            TextView textView;
//            View dotView;
//
//            public DayViewContainer(@NotNull View view) {
//                super(view);
//                textView = view.findViewById(R.id.dayText);
//                dotView = view.findViewById(R.id.dotView);
//
//                view.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        if (day.getOwner() == DayOwner.THIS_MONTH) {
//                            selectDate(day.getDate());
//                        }
//                    }
//                });
//            }
//
//        }
//
//        calendar.setDayBinder(new DayBinder<DayViewContainer>() {
//            @NotNull
//            @Override
//            public DayViewContainer create(@NotNull View view) {
//                return new DayViewContainer(view);
//            }
//
//            @Override
//            public void bind(@NotNull DayViewContainer container, @NotNull CalendarDay day) {
//                container.day = day;
//                TextView textView = container.textView;
//                View dotView = container.dotView;
//
//                textView.setText(day.getDate().getDayOfMonth());
//
//                if (day.getOwner() == DayOwner.THIS_MONTH) {
//                    makeVisible(textView);
//                    LocalDate date = day.getDate();
//                    if (today.equals(date)) {
//                        textView.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
//                        textView.setBackgroundResource(R.drawable.today_bg_oval);
//                        makeInVisible(dotView);
//                    } else if (selectedDate.equals(date)) {
//                        textView.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));
//                        textView.setBackgroundResource(R.drawable.selected_bg_oval);
//                        makeInVisible(dotView);
//                    } else {
//                        textView.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
//                        textView.setBackground(null);
//                        if (!events.get(day.getDate()).isEmpty())
//                            makeVisible(dotView);
//                    }
//                } else {
//                    makeInVisible(textView);
//                    makeInVisible(dotView);
//                }
//            }
//
//            void makeVisible(View v) {
//                v.setVisibility(View.VISIBLE);
//            }
//
//            void makeInVisible(View v) {
//                v.setVisibility(View.INVISIBLE);
//            }
//        });
//
//
//        calendar.setMonthScrollListener(new Function1<CalendarMonth, Unit>() {
//            @Override
//            public Unit invoke(CalendarMonth month) {
//
//                if (month.getYear() == today.getYear()) {
//                    getActivity().setTitle(titleSameYearFormatter.format(month.getYearMonth()));
//                } else {
//                    getActivity().setTitle(titleFormatter.format(month.getYearMonth()));
//                }
//                selectDate(month.getYearMonth().atDay(1));
//
//                return Unit.INSTANCE;
//            }
//        });
//
//
//        class MonthViewContainer extends ViewContainer {
//            LinearLayout legendLayout;
//
//            public MonthViewContainer(@NotNull View view) {
//                super(view);
//                legendLayout = view.findViewById(R.id.legendLayout);
//            }
//        }
//
//        calendar.setMonthHeaderBinder(new MonthHeaderFooterBinder<MonthViewContainer>() {
//            @NotNull
//            @Override
//            public MonthViewContainer create(@NotNull View view) {
//                return new MonthViewContainer(view);
//            }
//
//            @Override
//            public void bind(@NotNull MonthViewContainer container, @NotNull CalendarMonth month) {
//                if (container.legendLayout.getTag() == null) {
//                    container.legendLayout.setTag(month.getYearMonth());
//
//                    int count = container.legendLayout.getChildCount();
//                    TextView tv = null;
//                    for (int i = 0; i < count; i++) {
//                        tv = (TextView) container.legendLayout.getChildAt(i);
//                        tv.setText(daysOfWeek.get(i).name().charAt(0));
//                        tv.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
//                    }
//                }
//
//            }
//
//
////        addBtn.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////                inputDialog.show();
////            }
////        });
//
//        });
//    }
//
//
//
////    class DayViewContainer extends ViewContainer {
////        TextView dayText;
////
////        public DayViewContainer(@NotNull View view) {
////            super(view);
////            dayText = view.findViewById(R.id.calendarDayText);
////        }
////    }
//
//
////    private void initViews() {
//////        calendarView.setCalendarOrientation(OrientationHelper.HORIZONTAL);
//////        disablePastCalendarDays();
////
////        DayBinder<DayViewContainer> dayBinder = new DayBinder<DayViewContainer>() {
////            @NotNull
////            @Override
////            public DayViewContainer create(@NotNull View view) {
////                return null;
////            }
////
////            @Override
////            public void bind(@NotNull DayViewContainer container, @NotNull CalendarDay day) {
////                container.dayText.setText(day.getDate().getDayOfMonth());
////                if (day.getOwner() == DayOwner.THIS_MONTH) {
////                    container.dayText.setTextColor(Color.WHITE);
////                } else {
////                    container.dayText.setTextColor(Color.GRAY);
////                }
////            }
////
////        };
//////        ((RadioGroup) findViewById(R.id.rg_selection_type)).setOnCheckedChangeListener(this);
////    }
//
////
////    @Override
////    public boolean onCreateOptionsMenu(Menu menu) {
////        MenuInflater inflater = getMenuInflater();
////        inflater.inflate(R.menu.calendar_menu, menu);
////        return true;
////    }
////
////    @Override
////    public boolean onOptionsItemSelected(MenuItem item) {
////        switch (item.getItemId()) {
////            case R.id.clear_selections:
////                clearSelectionsMenuClick();
////                return true;
////            case R.id.show_selections:
////                List<Calendar> days = calendarView.getSelectedDates();
////
////                calConn.getResultsFromApi();
//////                String result = "";
////////                for (int i = 0; i < days.size(); i++) {
////////                    Calendar calendar = days.get(i);
////////                    final int day = calendar.get(Calendar.DAY_OF_MONTH);
////////                    final int month = calendar.get(Calendar.MONTH);
////////                    final int year = calendar.get(Calendar.YEAR);
////////                    String week = new SimpleDateFormat("EE").format(calendar.getTime());
////////                    String day_full = year + "년 " + (month + 1) + "월 " + day + "일 " + week + "요일";
////////                    result += (day_full + "\n");
////////                }
////////                Toast.makeText(CalendarActivity.this, result, Toast.LENGTH_LONG).show();
////                return true;
////            default:
////                return super.onOptionsItemSelected(item);
////        }
////    }
////
////    private void clearSelectionsMenuClick() {
////        calendarView.clearSelections();
////    }
////
////    @Override
////    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
////        clearSelectionsMenuClick();
////        switch (checkedId) {
////            case R.id.rb_single:
////                calendarView.setSelectionType(SelectionType.SINGLE);
////                break;
////            case R.id.rb_multiple:
////                calendarView.setSelectionType(SelectionType.MULTIPLE);
////                break;
////            case R.id.rb_range:
////                calendarView.setSelectionType(SelectionType.RANGE);
////                break;
////            case R.id.rb_none:
////                calendarView.setSelectionType(SelectionType.NONE);
////                break;
////        }
////    }
////
////    private void disablePastCalendarDays() { // TODO
////        Calendar c = Calendar.getInstance();
////        List<Calendar> days = calendarView.getSelectedDates();
//////        Set<Long> disabledDaysSet = new HashSet<>();
//////        disabledDaysSet.add(System.currentTimeMillis());
//////        for (int i = c.get(Calendar.DAY_OF_MONTH); i > 0; i--) {
//////            c.add(Calendar.DATE, -1);
//////            System.out.println(c);
//////            disabledDaysSet.add(c.getTimeInMillis());
//////        }
////        calendarView.setDisabledDaysCriteria(new DisabledDaysCriteria(1, c.get(Calendar.DATE), DisabledDaysCriteriaType.DAYS_OF_MONTH));
//////        calendarView.setDisabledDays(disabledDaysSet);
////    }
//
////    void addEvent() {
////        Event event = new Event()
////                .setSummary("Google I/O 2015")
////                .setLocation("800 Howard St., San Francisco, CA 94103")
////                .setDescription("A chance to hear more about Google's developer products.");
////
////        DateTime startDateTime = new DateTime("2015-05-28T09:00:00-07:00");
////        EventDateTime start = new EventDateTime()
////                .setDateTime(startDateTime)
////                .setTimeZone("America/Los_Angeles");
////        event.setStart(start);
////
//////        DateTime endDateTime = new DateTime("2015-05-28T17:00:00-07:00");
//////        EventDateTime end = new EventDateTime()
//////                .setDateTime(endDateTime)
//////                .setTimeZone("America/Los_Angeles");
//////        event.setEnd(end);
////
////        String[] recurrence = new String[]{"RRULE:FREQ=DAILY;COUNT=2"};
////        event.setRecurrence(Arrays.asList(recurrence));
////
////        EventAttendee[] attendees = new EventAttendee[]{
////                new EventAttendee().setEmail("lpage@example.com"),
////                new EventAttendee().setEmail("sbrin@example.com"),
////        };
////        event.setAttendees(Arrays.asList(attendees));
////
////        EventReminder[] reminderOverrides = new EventReminder[]{
////                new EventReminder().setMethod("email").setMinutes(24 * 60),
////                new EventReminder().setMethod("popup").setMinutes(10),
////        };
////        Event.Reminders reminders = new Event.Reminders()
////                .setUseDefault(false)
////                .setOverrides(Arrays.asList(reminderOverrides));
////        event.setReminders(reminders);
////
////        String calendarId = "primary";
//////        event = service.events().insert(calendarId, event).execute();
////        System.out.printf("Event created: %s\n", event.getHtmlLink());
////    }
//
//    private void selectDate(LocalDate date) {
//        if (selectedDate != date) {
//            LocalDate oldDate = selectedDate;
//            selectedDate = date;
//            if (oldDate != null)
//                calendar.notifyDateChanged(oldDate);
//            calendar.notifyDateChanged(date);
//            updateAdapterForDate(date);
//        }
//    }
//
//    private void saveEvent(String text) {
//        if (text.isEmpty()) {
////            Toast.makeText(requireContext(), R.string.example_3_empty_input_text, Toast.LENGTH_LONG).show()
//        } else {
////            selectedDate. ?.let {
////                events[it] = events[it].orEmpty().plus(Event(UUID.randomUUID().toString(), text, it))
////                updateAdapterForDate(it)
////            }
//        }
//    }
//
//    private void deleteEvent(Event event) {
//        LocalDate date = event.date;
//        if (!(events.get(date).isEmpty()))
//            events.get(date).remove(event);
//        updateAdapterForDate(date);
//    }
//
//    private void updateAdapterForDate(LocalDate date) {
//        eventsAdapter.events.clear();
//        eventsAdapter.events.addAll(events.get(date).isEmpty() ? Collections.EMPTY_LIST : events.get(date));
//        eventsAdapter.notifyDataSetChanged();
//        selectedDateText.setText(selectionFormatter.format(date));
//    }
//
//}
