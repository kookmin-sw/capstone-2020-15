package com.example.sweetleep_test.ui.home.calendar;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.RadioGroup;

import androidx.annotation.IdRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.OrientationHelper;

import com.applikeysolutions.cosmocalendar.utils.SelectionType;
import com.applikeysolutions.cosmocalendar.view.CalendarView;
import com.example.sweetleep_test.R;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.EventReminder;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CalendarActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener {

    private CalendarView calendarView;
    private CalendarStart calConn;

    Set<Long> disabledDays = new HashSet<>();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("일정 선택");
        setContentView(R.layout.activity_calendar);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        calConn = new CalendarStart();
//        addEvent();
        initViews();
    }

    private void initViews() {
        calendarView = (CalendarView) findViewById(R.id.calendar_view);
        calendarView.setCalendarOrientation(OrientationHelper.HORIZONTAL);

//        disablePastCalendarDays();

        ((RadioGroup) findViewById(R.id.rg_selection_type)).setOnCheckedChangeListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.calendar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.clear_selections:
                clearSelectionsMenuClick();
                return true;

            case R.id.show_selections:
                List<Calendar> days = calendarView.getSelectedDates();

                calConn.getResultsFromApi();
//                String result = "";
////                for (int i = 0; i < days.size(); i++) {
////                    Calendar calendar = days.get(i);
////                    final int day = calendar.get(Calendar.DAY_OF_MONTH);
////                    final int month = calendar.get(Calendar.MONTH);
////                    final int year = calendar.get(Calendar.YEAR);
////                    String week = new SimpleDateFormat("EE").format(calendar.getTime());
////                    String day_full = year + "년 " + (month + 1) + "월 " + day + "일 " + week + "요일";
////                    result += (day_full + "\n");
////                }
////                Toast.makeText(CalendarActivity.this, result, Toast.LENGTH_LONG).show();

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }




    private void clearSelectionsMenuClick() {
        calendarView.clearSelections();

    }


    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        clearSelectionsMenuClick();
        switch (checkedId) {

            case R.id.rb_single:
                calendarView.setSelectionType(SelectionType.SINGLE);
                break;

            case R.id.rb_multiple:
                calendarView.setSelectionType(SelectionType.MULTIPLE);
                break;

            case R.id.rb_range:
                calendarView.setSelectionType(SelectionType.RANGE);
                break;

            case R.id.rb_none:
                calendarView.setSelectionType(SelectionType.NONE);
                break;
        }
    }

    private void disablePastCalendarDays(){ // TODO
        Calendar c = Calendar.getInstance();

        Set<Long> disabledDaysSet = new HashSet<>();
        disabledDaysSet.add(System.currentTimeMillis());
        for (int i = c.get(Calendar.DAY_OF_MONTH); i > 0 ; i--) {
            c.add(Calendar.DATE, -1);
            System.out.println(c);
            disabledDaysSet.add(c.getTimeInMillis());
        }

        calendarView.setDisabledDays(disabledDaysSet);
    }

    void addEvent() {
        Event event = new Event()
                .setSummary("Google I/O 2015")
                .setLocation("800 Howard St., San Francisco, CA 94103")
                .setDescription("A chance to hear more about Google's developer products.");

        DateTime startDateTime = new DateTime("2015-05-28T09:00:00-07:00");
        EventDateTime start = new EventDateTime()
                .setDateTime(startDateTime)
                .setTimeZone("America/Los_Angeles");
        event.setStart(start);

//        DateTime endDateTime = new DateTime("2015-05-28T17:00:00-07:00");
//        EventDateTime end = new EventDateTime()
//                .setDateTime(endDateTime)
//                .setTimeZone("America/Los_Angeles");
//        event.setEnd(end);

        String[] recurrence = new String[]{"RRULE:FREQ=DAILY;COUNT=2"};
        event.setRecurrence(Arrays.asList(recurrence));

        EventAttendee[] attendees = new EventAttendee[]{
                new EventAttendee().setEmail("lpage@example.com"),
                new EventAttendee().setEmail("sbrin@example.com"),
        };
        event.setAttendees(Arrays.asList(attendees));

        EventReminder[] reminderOverrides = new EventReminder[]{
                new EventReminder().setMethod("email").setMinutes(24 * 60),
                new EventReminder().setMethod("popup").setMinutes(10),
        };
        Event.Reminders reminders = new Event.Reminders()
                .setUseDefault(false)
                .setOverrides(Arrays.asList(reminderOverrides));
        event.setReminders(reminders);

        String calendarId = "primary";
//        event = service.events().insert(calendarId, event).execute();
        System.out.printf("Event created: %s\n", event.getHtmlLink());
    }
}
