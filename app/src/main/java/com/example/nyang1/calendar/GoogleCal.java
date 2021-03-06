package com.example.nyang1.calendar;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.nyang1.R;
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
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Calendar;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;




public class GoogleCal extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {


    /**
     * Google Calendar API??? ???????????? ?????? ???????????? ?????? ????????? API ????????? ??????
     */

    private com.google.api.services.calendar.Calendar mService = null;

    /**
     * Google Calendar API ?????? ?????? ???????????? ??? AsyncTask??? ??????????????? ?????? ??????
     */
    private  int mID = 0;


    GoogleAccountCredential mCredential;
    private EditText mtitle;
    private EditText mSchedule;
    private TextView mResultText;
    private Button mGetEventButton;
    private Button mAddEventButton;
    private Button mAddCalendarButton;
    private TimePicker mtime;
    ProgressDialog mProgress;


    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;


    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] SCOPES = {CalendarScopes.CALENDAR};

    String year, month, day,mDay;
    String selectedDate, max;

    Date date;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.addcal);

        Intent intent = getIntent();
        year = intent.getStringExtra("???");
        month = intent.getStringExtra("???");
        day = intent.getStringExtra("???");
        mDay = intent.getStringExtra("m???");

        mAddCalendarButton = (Button) findViewById(R.id.button_main_add_calendar);
        mAddEventButton = (Button) findViewById(R.id.button_main_add_event);
        mGetEventButton = (Button) findViewById(R.id.button_main_get_event);
        mtitle = (EditText) findViewById(R.id.cal_title); //?????? ??????
        mSchedule = (EditText) findViewById(R.id.schedule); //??????
        mResultText = (TextView) findViewById(R.id.textview_main_result); //??????
        mtime = (TimePicker) findViewById(R.id.time);

        /**
         * ?????? ???????????? ?????? ?????????
         */
        mAddCalendarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAddCalendarButton.setEnabled(false);
                mtitle.setText("");
                mSchedule.setText("");
                mID = 1;           //????????? ??????
                getResultsFromApi();
                mAddCalendarButton.setEnabled(true);
            }
        });


        mAddEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAddEventButton.setEnabled(false);
//               mtitle.setText("");
                mID = 2;        //????????? ??????
                getResultsFromApi();
                mAddEventButton.setEnabled(true);
            }
        });


        mGetEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGetEventButton.setEnabled(false);
//                mtitle.setText("");
                mID = 3;        //????????? ????????????
                getResultsFromApi();
                mGetEventButton.setEnabled(true);
            }
        });


        // Google Calendar API??? ?????? ????????? ???????????? TextView??? ??????
        mResultText.setVerticalScrollBarEnabled(true);
        mResultText.setMovementMethod(new ScrollingMovementMethod());

        mtitle.setVerticalScrollBarEnabled(true);
        mtitle.setMovementMethod(new ScrollingMovementMethod());
//        mStatusText.setText("????????? ?????? ???????????? ???????????????.");


        // Google Calendar API ???????????? ???????????? ProgressDialog
        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Google Calendar API ?????? ????????????.");


        // Google Calendar API ???????????? ?????? ????????? ?????? ?????????( ?????? ?????? credentials, ????????? ?????? )
        // OAuth 2.0??? ???????????? ?????? ?????? ?????? ??? ???????????? ?????? ??????
        mCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(),
                Arrays.asList(SCOPES)
        ).setBackOff(new ExponentialBackOff()); // I/O ?????? ????????? ???????????? ????????? ?????? ??????

    }




    /**
     * ?????? ?????? ????????? ?????? ???????????? Google Calendar API??? ????????? ??? ??????.
     *
     * ?????? ??????
     *     - Google Play Services ??????
     *     - ????????? ?????? ?????? ??????
     *     - ??????????????? ?????????????????? ????????? ?????? ??????
     *
     * ???????????? ???????????? ????????? ?????? ????????? ??????????????? ??????.
     */
    private String getResultsFromApi() {

        if (!isGooglePlayServicesAvailable()) { // Google Play Services??? ????????? ??? ?????? ??????

            acquireGooglePlayServices();
        } else if (mCredential.getSelectedAccountName() == null) { // ????????? Google ????????? ???????????? ?????? ?????? ??????

            chooseAccount();
        } else if (!isDeviceOnline()) {    // ???????????? ????????? ??? ?????? ??????

//            mStatusText.setText("No network connection available.");
        } else {

            // Google Calendar API ??????
            new MakeRequestTask(GoogleCal.this, mCredential).execute();
        }
        return null;
    }



    /**
     * ??????????????? ??????????????? ?????? ????????? Google Play Services??? ???????????? ????????? ??????
     */
    private boolean isGooglePlayServicesAvailable() {

        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();

        final int connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(this);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }



    /*
     * Google Play Services ??????????????? ????????????????????? ???????????? ?????? ???????????? ????????????????????? ??????????????????
     * ??????????????? ?????????.
     */
    private void acquireGooglePlayServices() {

        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        final int connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(this);

        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {

            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }



    /*
     * ??????????????? ??????????????? Google Play Services??? ?????? ????????? ????????? ????????? ????????? ?????? ???????????? ????????????
     */
    void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode
    ) {

        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();

        Dialog dialog = apiAvailability.getErrorDialog(
                GoogleCal.this,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES
        );
        dialog.show();
    }



    /*
     * Google Calendar API??? ?????? ??????( credentials ) ??? ????????? ?????? ????????? ????????????.
     *
     * ?????? ???????????? ?????? ????????? ????????? ?????? ????????? ????????????????????? ???????????? ??????????????? ??????.
     * GET_ACCOUNTS ???????????? ????????????.
     */
    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    private void chooseAccount() {

        // GET_ACCOUNTS ????????? ????????? ?????????
        if (EasyPermissions.hasPermissions(this, Manifest.permission.GET_ACCOUNTS)) {


            // SharedPreferences?????? ????????? Google ?????? ????????? ????????????.
            String accountName = getPreferences(Context.MODE_PRIVATE)
                    .getString(PREF_ACCOUNT_NAME, null);
            if (accountName != null) {

                // ????????? ?????? ?????? ???????????? ????????????.
                mCredential.setSelectedAccountName(accountName);
                getResultsFromApi();
            } else {


                // ???????????? ?????? ????????? ????????? ??? ?????? ?????????????????? ????????????.
                startActivityForResult(
                        mCredential.newChooseAccountIntent(),
                        REQUEST_ACCOUNT_PICKER);
            }



            // GET_ACCOUNTS ????????? ????????? ?????? ?????????
        } else {


            // ??????????????? GET_ACCOUNTS ????????? ???????????? ?????????????????? ????????????.(????????? ?????? ?????????)
            EasyPermissions.requestPermissions(
                    (Activity)this,
                    "This app needs to access your Google account (via Contacts).",
                    REQUEST_PERMISSION_GET_ACCOUNTS,
                    Manifest.permission.GET_ACCOUNTS);
        }
    }



    /*
     * ?????? ????????? ????????? ???????????? ???????????????, ?????? ?????? ?????? ???????????????, ?????? ????????????????????? ??????????????? ????????????.
     */

    @Override
    protected void onActivityResult(
            int requestCode,  // onActivityResult??? ??????????????? ??? ?????? ????????? ????????? ??????
            int resultCode,   // ????????? ?????? ?????? ??????
            Intent data
    ) {
        super.onActivityResult(requestCode, resultCode, data);


        switch (requestCode) {

            case REQUEST_GOOGLE_PLAY_SERVICES:

                if (resultCode != RESULT_OK) {

//                    mStatusText.setText( " ?????? ?????????????????? ?????? ????????? ???????????? ???????????????."
//                            + "?????? ????????? ???????????? ?????? ??? ?????? ???????????????." );
                } else {

                    getResultsFromApi();
                }
                break;


            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null && data.getExtras() != null) {
                    String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.apply();
                        mCredential.setSelectedAccountName(accountName);
                        getResultsFromApi();
                    }
                }
                break;


            case REQUEST_AUTHORIZATION:

                if (resultCode == RESULT_OK) {
                    getResultsFromApi();
                }
                break;
        }
    }


    /*
     * Android 6.0 (API 23) ???????????? ????????? ?????? ????????? ????????? ????????????
     */
    @Override
    public void onRequestPermissionsResult(
            int requestCode,  //requestPermissions(android.app.Activity, String, int, String[])?????? ????????? ?????? ??????
            @NonNull String[] permissions, // ????????? ?????????
            @NonNull int[] grantResults    // ????????? ?????? ??????. PERMISSION_GRANTED ?????? PERMISSION_DENIED
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }


    /*
     * EasyPermissions ?????????????????? ???????????? ????????? ????????? ???????????? ????????? ?????? ????????????.
     */
    @Override
    public void onPermissionsGranted(int requestCode, List<String> requestPermissionList) {

        // ???????????? ?????? ??????
    }


    /*
     * EasyPermissions ?????????????????? ???????????? ????????? ????????? ???????????? ????????? ?????? ????????????.
     */
    @Override
    public void onPermissionsDenied(int requestCode, List<String> requestPermissionList) {

        // ???????????? ?????? ??????
    }


    /*
     * ??????????????? ??????????????? ????????? ???????????? ????????? ????????????. ???????????? ????????? True ??????, ????????? False ??????
     */
    private boolean isDeviceOnline() {

        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        return (networkInfo != null && networkInfo.isConnected());
    }


    /*
     * ????????? ????????? ???????????? ????????? ID??? ??????
     */
    private String getCalendarID(String calendarTitle){

        String id = null;

        // Iterate through entries in calendar list
        String pageToken = null;
        do {
            CalendarList calendarList = null;
            try {
                calendarList = mService.calendarList().list().setPageToken(pageToken).execute();
            } catch (UserRecoverableAuthIOException e) {
                startActivityForResult(e.getIntent(), REQUEST_AUTHORIZATION);
            }catch (IOException e) {
                e.printStackTrace();
            }
            List<CalendarListEntry> items = calendarList.getItems();


            for (CalendarListEntry calendarListEntry : items) {

                if ( calendarListEntry.getSummary().toString().equals(calendarTitle)) {

                    id = calendarListEntry.getId().toString();
                }
            }
            pageToken = calendarList.getNextPageToken();
        } while (pageToken != null);

        return id;
    }


    /*
     * ?????????????????? Google Calendar API ??????
     */
    private class MakeRequestTask extends AsyncTask<Void, Void, String> {

        private Exception mLastError = null;
        private GoogleCal mActivity;
        List<String> eventStrings = new ArrayList<String>();


        public MakeRequestTask(GoogleCal activity, GoogleAccountCredential credential) {

            mActivity = activity;

            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

            mService = new com.google.api.services.calendar.Calendar
                    .Builder(transport, jsonFactory, credential)
                    .setApplicationName("Google Calendar API Android Quickstart")
                    .build();
        }


        @Override
        protected void onPreExecute() {
            // mStatusText.setText("");
            String summ= mtitle.getText().toString();
            mProgress.show();
//            mSchedule.setText(summ);
            mResultText.setText(summ);
        }


        /*
         * ????????????????????? Google Calendar API ?????? ??????
         */
        @Override
        protected String doInBackground(Void... params) {
            try {

                if ( mID == 1) {
                    return createCalendar();

                }else if (mID == 2) {

                    return addEvent();
                }
                else if (mID == 3) {

                    return getEvent();
                }


            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                return null;
            }

            return null;
        }


        /*
         * CalendarTitle ????????? ??????????????? 10?????? ???????????? ????????? ??????
         */
        private String getEvent() throws IOException, ParseException {

            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            selectedDate = year+"-"+month+"-"+day;
            max = year+"-"+month+"-"+mDay;
            date = dateFormat.parse(selectedDate);
            DateTime selected = new DateTime(date);

            Date timemax = dateFormat.parse(max);
            DateTime max = new DateTime(timemax);

            String calendarID = getCalendarID("CalendarTitle");
            if ( calendarID == null ){
                Toast.makeText(this.mActivity,"???????????? ?????? ???????????????.", Toast.LENGTH_SHORT);
                return "";
            }


            Events events = mService.events().list(calendarID)//"primary")
                    .setMaxResults(10)
                    .setTimeMin(selected)
                    .setTimeMax(max)
                    .setOrderBy("startTime")
                    .setSingleEvents(true)
                    .execute();
            List<Event> items = events.getItems();


            for (Event event : items) {

                DateTime start = event.getStart().getDateTime();
                if (start == null) {

                    // ?????? ???????????? ?????? ????????? ?????? ????????? ??????. ?????? ?????? ?????? ????????? ??????
                    start = event.getStart().getDate();
                }


                eventStrings.add(String.format("%s \n %s \n (%s)", event.getSummary(), event.getDescription(), start));
            }


            return "";
        }

        /*
         * ???????????? ?????? Google ????????? ??? ???????????? ????????????.
         */
        private String createCalendar() throws IOException {

            String ids = getCalendarID("CalendarTitle");
            String summ = mtitle.getText().toString();
            if ( ids != null ){
                Toast.makeText(this.mActivity,"?????? ???????????? ?????????????????????.", Toast.LENGTH_SHORT);
                return "";
            }

            // ????????? ????????? ??????
            com.google.api.services.calendar.model.Calendar calendar = new Calendar();

            // ???????????? ?????? ??????
            calendar.setSummary(summ);


            // ???????????? ????????? ??????
            calendar.setTimeZone("Asia/Seoul");

            // ?????? ???????????? ?????? ?????? ???????????? ??????
            Calendar createdCalendar = mService.calendars().insert(calendar).execute();

            // ????????? ???????????? ID??? ?????????.
            String calendarId = createdCalendar.getId();


            // ?????? ???????????? ????????? ???????????? ?????? ?????? ???????????? ??????
            CalendarListEntry calendarListEntry = mService.calendarList().get(calendarId).execute();

            // ???????????? ???????????? ??????????????? ??????  RGB
            calendarListEntry.setBackgroundColor("#0000ff");

            // ????????? ????????? ?????? ???????????? ??????
            CalendarListEntry updatedCalendarListEntry =
                    mService.calendarList()
                            .update(calendarListEntry.getId(), calendarListEntry)
                            .setColorRgbFormat(true)
                            .execute();

            // ?????? ????????? ???????????? ID??? ??????
            Toast.makeText(this.mActivity,"???????????? ?????????????????????.", Toast.LENGTH_SHORT);
            return "";
        }


        @Override
        protected void onPostExecute(String output) {

            mProgress.hide();
            mtitle.setText(output);

            if ( mID == 3 )   mResultText.setText(TextUtils.join("\n\n", eventStrings));
        }


        @Override
        protected void onCancelled() {
            mProgress.hide();
            if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError)
                                    .getConnectionStatusCode());
                } else if (mLastError instanceof UserRecoverableAuthIOException) {
                    startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            GoogleCal.REQUEST_AUTHORIZATION);
                } else {
                    mSchedule.setText("");
//                    mSchedule.setText("MakeRequestTask The following error occurred:\n" + mLastError.getMessage());
                }
            } else {
                mSchedule.setText("?????? ?????????.");
            }
        }


        private String addEvent() throws ParseException {
            String calendarID = getCalendarID("CalendarTitle");
            String summ = mtitle.getText().toString();
            if ( calendarID == null ){
                Toast.makeText(this.mActivity,"???????????? ?????? ???????????????.", Toast.LENGTH_SHORT);
                return "";

            }

            Event event = new Event()
                    .setSummary(summ)
                    .setDescription(mSchedule.getText().toString());


            java.util.Calendar calander;

            calander = java.util.Calendar.getInstance();

            SimpleDateFormat simpledateformat;
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
            String datestr = dateFormat.format(calander.getTime());
            System.out.println(datestr);        // Date ????????? ???????????? String?????? ????????????.
            selectedDate = year+"-"+month+"-"+day+"T"+mtime.getHour()+":"+mtime.getMinute()+":00+0900";

            date = dateFormat.parse(selectedDate);
            System.out.println(date);


            DateTime startDateTime = new DateTime(date);
            EventDateTime start = new EventDateTime()
                    .setDateTime(startDateTime)
                    .setTimeZone("Asia/Seoul");
            event.setStart(start);


            DateTime endDateTime = new  DateTime(date);
            EventDateTime end = new EventDateTime()
                    .setDateTime(endDateTime)
                    .setTimeZone("Asia/Seoul");
            event.setEnd(end);

            //String[] recurrence = new String[]{"RRULE:FREQ=DAILY;COUNT=2"};
            //event.setRecurrence(Arrays.asList(recurrence));


            try {
                event = mService.events().insert(calendarID, event).execute();
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("Exception", "Exception : " + e.toString());
            }
            System.out.printf("Event created: %s\n", event.getHtmlLink());
            Log.e("Event", "created : " + event.getHtmlLink());
            mtitle.setText("");
            return "";
        }
    }


}