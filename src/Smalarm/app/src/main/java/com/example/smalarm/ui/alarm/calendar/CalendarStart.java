package com.example.smalarm.ui.alarm.calendar;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.icu.util.LocaleData;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.smalarm.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Calendar;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;

import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.format.DateTimeFormatter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class CalendarStart {

    static String TAG = "CalendarStart";
    CalendarActivity mContext;
    private com.google.api.services.calendar.Calendar mService = null;
    int mID = 0;

    GoogleAccountCredential mCredential;
    List<String> strings = new ArrayList<>();
    Map<LocalDate, String> string = new HashMap<>();

    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;

    String calendar_title = null;
    static final String PREF_ACCOUNT_NAME = "accountName";
    static final String[] SCOPES = {CalendarScopes.CALENDAR};

    public CalendarStart(Context context, GoogleAccountCredential credential) {
        mContext = (CalendarActivity) context;
        mCredential = credential;
    }

    /**
     * 다음 사전 조건을 모두 만족해야 Google Calendar API를 사용할 수 있다.
     * <p>
     * 사전 조건
     * - Google Play Services 설치
     * - 유효한 구글 계정 선택
     * - 안드로이드 디바이스에서 인터넷 사용 가능
     * <p>
     * 하나라도 만족하지 않으면 해당 사항을 사용자에게 알림.
     */
    protected String getResultsFromApi() {

        if (!isGooglePlayServicesAvailable()) { // Google Play Services를 사용할 수 없는 경우

            acquireGooglePlayServices();
        } else if (mCredential.getSelectedAccountName() == null) { // 유효한 Google 계정이 선택되어 있지 않은 경우

            chooseAccount();
        } else if (!isDeviceOnline()) {    // 인터넷을 사용할 수 없는 경우
            Toast.makeText(mContext, "No network connection available.", Toast.LENGTH_SHORT).show();
        } else {

            // Google Calendar API 호출
            new MakeRequestTask(mContext, mCredential).execute();
        }
        return null;
    }


    /**
     * 안드로이드 디바이스에 최신 버전의 Google Play Services가 설치되어 있는지 확인
     */
    private boolean isGooglePlayServicesAvailable() {

        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();

        final int connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(mContext);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }


    /*
     * Google Play Services 업데이트로 해결가능하다면 사용자가 최신 버전으로 업데이트하도록 유도하기위해 대화상자를 보여줌.
     */
    private void acquireGooglePlayServices() {

        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        final int connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(mContext);

        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }


    /*
     * 안드로이드 디바이스에 Google Play Services가 설치 안되어 있거나 오래된 버전인 경우 보여주는 대화상자
     */
    private void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode
    ) {

        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();

        Dialog dialog = apiAvailability.getErrorDialog(
                mContext,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES
        );
        dialog.show();
    }


    /*
     * Google Calendar API의 자격 증명( credentials ) 에 사용할 구글 계정을 설정한다.
     *
     * 전에 사용자가 구글 계정을 선택한 적이 없다면 다이얼로그에서 사용자를 선택하도록 한다.
     * GET_ACCOUNTS 퍼미션이 필요하다.
     */
    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    private void chooseAccount() {

        // GET_ACCOUNTS 권한을 가지고 있다면
        if (EasyPermissions.hasPermissions(mContext, Manifest.permission.GET_ACCOUNTS)) {
            // SharedPreferences에서 저장된 Google 계정 이름을 가져온다.
            String accountName = mContext.getPreferences(Context.MODE_PRIVATE)
                    .getString(PREF_ACCOUNT_NAME, null);
            if (accountName != null) {
                // 선택된 구글 계정 이름으로 설정한다.
                mCredential.setSelectedAccountName(accountName);
                getResultsFromApi();
            } else {
                // 사용자가 구글 계정을 선택할 수 있는 다이얼로그를 보여준다.
                mContext.startActivityForResult(
                        mCredential.newChooseAccountIntent(),
                        REQUEST_ACCOUNT_PICKER);
            }
            // GET_ACCOUNTS 권한을 가지고 있지 않다면
        } else {
            // 사용자에게 GET_ACCOUNTS 권한을 요구하는 다이얼로그를 보여준다.(주소록 권한 요청함)
            EasyPermissions.requestPermissions(
                    mContext,
                    "This app needs to access your Google account (via Contacts).",
                    REQUEST_PERMISSION_GET_ACCOUNTS,
                    Manifest.permission.GET_ACCOUNTS);
        }
    }

    /*
     * 안드로이드 디바이스가 인터넷 연결되어 있는지 확인한다. 연결되어 있다면 True, 아니면 False 리턴
     */
    private boolean isDeviceOnline() {

        ConnectivityManager connMgr = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        return (networkInfo != null && networkInfo.isConnected());
    }


    /*
     * 캘린더 이름에 대응하는 캘린더 ID를 리턴
     */
    private String getCalendarID(String calendarTitle) {

        String id = null;

        // Iterate through entries in calendar list
        String pageToken = null;
        do {
            CalendarList calendarList = null;
            try {
                calendarList = mService.calendarList().list().setPageToken(pageToken).execute();
            } catch (UserRecoverableAuthIOException e) {
                mContext.startActivityForResult(e.getIntent(), REQUEST_AUTHORIZATION);
            } catch (IOException e) {
                e.printStackTrace();
            }
            List<CalendarListEntry> items = calendarList.getItems();


            for (CalendarListEntry calendarListEntry : items) {

                if (calendarListEntry.getSummary().toString().equals(calendarTitle)) {

                    id = calendarListEntry.getId().toString();
                }
            }
            pageToken = calendarList.getNextPageToken();
        } while (pageToken != null);

        return id;
    }


    /*
     * 비동기적으로 Google Calendar API 호출
     */
    private class MakeRequestTask extends AsyncTask<Void, Void, String> {

        private Exception mLastError = null;
        private CalendarActivity mActivity;
        List<String> eventStrings = new ArrayList<String>();
        Map<LocalDate, String> eventString = new HashMap<>();

        public MakeRequestTask(CalendarActivity activity, GoogleAccountCredential credential) {

            mActivity = activity;

            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

            mService = new com.google.api.services.calendar.Calendar
                    .Builder(transport, jsonFactory, credential)
                    .setApplicationName("Smalarm")
                    .build();
        }

        @Override
        protected void onPreExecute() {
            if (calendar_title != null) {
//                showCalendar();
            }
        }

        /*
         * 백그라운드에서 Google Calendar API 호출 처리
         */
        @Override
        protected String doInBackground(Void... params) {
            try {

                if (mID == 0) { // TODO : 1~12 제외 다른걸로

                    return createCalendar();
                } else if (1 <= mID && mID <= 12) {

                    return getEvent(mID);
                }

            } catch (Exception e) {
                mLastError = e;
                Log.e(TAG, e.toString());
                cancel(true);
                return null;
            }

            return null;
        }

        public String getEvent(int month) throws IOException {
            DateTime now = new DateTime(System.currentTimeMillis());

            java.util.Calendar calendar = java.util.Calendar.getInstance();

            calendar.set(java.util.Calendar.MONTH, month);
            calendar.set(java.util.Calendar.DATE, 1);
            DateTime first = new DateTime(calendar.getTime());

            int lastDate = calendar.getActualMaximum(java.util.Calendar.DAY_OF_MONTH);
            calendar.set(java.util.Calendar.DATE, lastDate);
            DateTime last = new DateTime(calendar.getTime());
//            DateTime max = new DateTime(month);

            return getEvent(first, last);
        }

        /*
         * CalendarTitle 이름의 캘린더에서 이벤트를 가져와 리턴
         */
        public String getEvent(DateTime min, DateTime max) throws IOException {

            String calendarID = getCalendarID(mContext.getString(R.string.calendar_title));
            if (calendarID == null) {
                return "캘린더를 먼저 생성하세요.";
            }

            Events events = mService.events().list(calendarID)//"primary")
//                    .setMaxResults(100)
                    .setTimeMin(min)
                    .setTimeMax(max)
                    .setOrderBy("startTime")
                    .setSingleEvents(true)
                    .execute();
//            Events events = mService.events()
//                    .list(calendarID)
//                    .setTimeMin(min)
//                    .setTimeMax(max)
//                    .execute();
            List<Event> items = events.getItems();

            for (Event event : items) {

                DateTime start = event.getStart().getDateTime();
                DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

                if (start == null) {

                    // 모든 이벤트가 시작 시간을 갖고 있지는 않다. 그런 경우 시작 날짜만 사용
                    start = event.getStart().getDate();
                    System.out.println(LocalDate.parse(start.toString()));
                    eventString.put(LocalDate.parse(start.toString()), event.getSummary());

                }else{
                    System.out.println(LocalDateTime.parse(start.toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")));
                    eventString.put(LocalDate.parse(start.toString(), format), event.getSummary());

                }

                String location = event.getLocation();
                if (location != null) {

                }

//                List<String> recurrence = event.getRecurrence();
//                if(recurrence!=null){
//                    System.out.println(recurrence);
//                    String[] split = recurrence.get(0).toString().split(";");
//                    System.out.println(split[0].split("=")[1]);
//                    System.out.println(split[1].split("=")[1]);
//                }
//                eventString.put(LocalDate.parse(start.toString()), event.getSummary());
                eventStrings.add(String.format("%s \n (%s)", event.getSummary(), start));
            }
            System.out.println(eventStrings);

            string = eventString;
            strings = eventStrings;
//            mContext.saveEvent();
            return eventStrings.size() + "개의 데이터를 가져왔습니다.";
        }

        /*
         * 선택되어 있는 Google 계정에 새 캘린더를 추가한다.
         */
        private String createCalendar() throws IOException {

            calendar_title = getCalendarID(mContext.getString(R.string.calendar_title));

            if (calendar_title != null) {

                return "이미 캘린더가 생성되어 있습니다. ";
            }

            // 새로운 캘린더 생성
            com.google.api.services.calendar.model.Calendar calendar = new Calendar();

            // TODO: 캘린더의 제목 설정
            calendar.setSummary("Smalarm 캘린더");


            // 캘린더의 시간대 설정
            calendar.setTimeZone("Asia/Seoul");

            // 구글 캘린더에 새로 만든 캘린더를 추가
            Calendar createdCalendar = mService.calendars().insert(calendar).execute();

            // 추가한 캘린더의 ID를 가져옴.
            String calendarId = createdCalendar.getId();


            // 구글 캘린더의 캘린더 목록에서 새로 만든 캘린더를 검색
            CalendarListEntry calendarListEntry = mService.calendarList().get(calendarId).execute();

            // TODO: 캘린더의 배경색을 primary color로 표시  RGB
            calendarListEntry.setBackgroundColor("#FEBC5C");
//            calendarListEntry.setBackgroundColor(String.valueOf(R.color.colorPrimary));

            // 변경한 내용을 구글 캘린더에 반영
            CalendarListEntry updatedCalendarListEntry =
                    mService.calendarList()
                            .update(calendarListEntry.getId(), calendarListEntry)
                            .setColorRgbFormat(true)
                            .execute();

            // 새로 추가한 캘린더의 ID를 리턴
            return "캘린더가 생성되었습니다.";
        }


        @Override
        protected void onPostExecute(String output) {
//            if (calendar_title != null) {
//                showCalendar();
//            }
//            mStatusText.setText(output);
//
//            if (mID == 3) mResultText.setText(TextUtils.join("\n\n", eventStrings));
        }


        @Override
        protected void onCancelled() {
            if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError)
                                    .getConnectionStatusCode());
                } else if (mLastError instanceof UserRecoverableAuthIOException) {
                    mContext.startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            CalendarStart.REQUEST_AUTHORIZATION);
                } else {
//                    mStatusText.setText("MakeRequestTask The following error occurred:\n" + mLastError.getMessage());
                }
            } else {
//                mStatusText.setText("요청 취소됨.");
            }
        }


    }


}