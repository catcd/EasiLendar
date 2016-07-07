package com.texas.easilendar.ui.outsides;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.texas.easilendar.ConnectivityReceiver;
import com.texas.easilendar.R;
import com.texas.easilendar.ui.calendars.EventsCalendarActivity;
import com.texas.easilendar.ui.calendars.MonthCalendarActivity;
import com.texas.easilendar.ui.calendars.WeekCalendarActivity;

public class SplashActivity extends AppCompatActivity {
    boolean isLoggedIn = false;
    boolean isOnline = false;
    // Notification
    // Calendar
    // Setting
    // Previous calendar
    String previousCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        /**
         * Showing splash screen while making network calls to download necessary
         * data before launching the app Will use AsyncTask to make http call
         */
        new PrefetchData().execute();
    }

    /**
     * Async Task to make http call
     */
    private class PrefetchData extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Before making http calls
            isOnline = ConnectivityReceiver.isConnected();
            isLoggedIn = checkLoggedIn();

            // TODO get previousCalendar
            previousCalendar = "";
        }

        private boolean checkLoggedIn() {
            // TODO check logged in offline and online
            return false;
        }

        @Override
        protected Void doInBackground(Void... params) {
            /*
             * Will make http call here This call will download required data
             * before launching the app
             * example:
             * 1. Downloading and storing in SQLite
             * 2. Downloading images
             * 3. Fetching and parsing the xml / json
             * 4. Sending device information to server
             * 5. etc.,
             */
            if (isOnline && isLoggedIn) {
                loadNewOnlineData();
            }
            return null;
        }

        private void loadNewOnlineData() {
            // TODO load new online data
            // new notification
            // new event request
            // etc.
            // save to SQLite DB
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // After completing http call
            // will close this activity and launch corresponding activity
            if (isLoggedIn) {
                Intent i;
                // Go to main activity (previous calendar)
                switch (previousCalendar) {
                    case "events":
                        i = new Intent(SplashActivity.this, EventsCalendarActivity.class);
                        break;
                    case "day":
                        i = new Intent(SplashActivity.this, WeekCalendarActivity.class);
                        i.putExtra("weekViewType", WeekCalendarActivity.TYPE_DAY_VIEW);
                        break;
                    case "threeDays":
                        i = new Intent(SplashActivity.this, WeekCalendarActivity.class);
                        i.putExtra("weekViewType", WeekCalendarActivity.TYPE_THREE_DAY_VIEW);
                        break;
                    case "week":
                        i = new Intent(SplashActivity.this, WeekCalendarActivity.class);
                        i.putExtra("weekViewType", WeekCalendarActivity.TYPE_WEEK_VIEW);
                        break;
                    default:
                        i = new Intent(SplashActivity.this, MonthCalendarActivity.class);
                        break;
                }

                // Notify user if user is offline
                if (!isOnline) {
                    Toast.makeText(SplashActivity.this, R.string.splash_offline, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(SplashActivity.this, R.string.splash_welcome, Toast.LENGTH_LONG).show();
                }

                // TODO put extra for main calendar
                startActivity(i);
            } else if(isOnline) {
                Intent i = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(i);
            } else {
                Toast.makeText(SplashActivity.this, R.string.splash_network_error, Toast.LENGTH_LONG).show();
            }

            // Close this activity
            finish();
        }
    }
}
