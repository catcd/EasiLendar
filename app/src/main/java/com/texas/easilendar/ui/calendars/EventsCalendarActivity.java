//package com.texas.easilendar.ui.calendars;
//
//import android.content.Intent;
//import android.os.Handler;
//import android.os.Bundle;
//import android.support.v7.widget.Toolbar;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.widget.Toast;
//
//import com.texas.easilendar.ui.AppDrawerActivity;
//import com.texas.easilendar.R;
//
//import butterknife.BindView;
//import butterknife.ButterKnife;
//
//public class EventsCalendarActivity extends AppDrawerActivity {
//    private final int TIME_INTERVAL = 2000;
//    private boolean doubleBackToExitPressedOnce = false;
//
//    @BindView(R.id.eventsCalendarToolbar) Toolbar eventsCalendarToolbar;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        setContentView(R.layout.activity_events_calendar);
//        ButterKnife.bind(this);
//
//        super.onCreate(savedInstanceState);
//
//        // setup toolbar
//        setSupportActionBar(eventsCalendarToolbar);
//    }
//
//    @Override
//    public void onBackPressed() {
//        if (mDrawer.isDrawerOpen()) {
//            mDrawer.closeDrawer();
//        } else {
//            if (doubleBackToExitPressedOnce) {
//                super.onBackPressed();
//                return;
//            }
//
//            this.doubleBackToExitPressedOnce = true;
//            Toast.makeText(this,
//                    getResources().getString(R.string.notify_press_back_again_to_exit),
//                    Toast.LENGTH_SHORT)
//                    .show();
//
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    doubleBackToExitPressedOnce=false;
//                }
//            }, TIME_INTERVAL);
//        }
//    }
//
//    // Menu icons are inflated just as they were with actionbar
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_calendar_select, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.menu_action_today:
//                // TODO today action
//                return true;
//
//            case R.id.menu_action_month_calendar:
//                startActivity(new Intent(EventsCalendarActivity.this, MonthCalendarActivity.class));
//                finish();
//                return true;
//
//            case R.id.menu_action_week_calendar:
//                Intent mWeekIntent = new Intent(EventsCalendarActivity.this, WeekCalendarActivity.class);
//                mWeekIntent.putExtra("weekViewType", WeekCalendarActivity.TYPE_WEEK_VIEW);
//                startActivity(mWeekIntent);
//                finish();
//                return true;
//
//            case R.id.menu_action_three_days_calendar:
//                Intent mThreeDaysIntent = new Intent(EventsCalendarActivity.this, WeekCalendarActivity.class);
//                mThreeDaysIntent.putExtra("weekViewType", WeekCalendarActivity.TYPE_THREE_DAY_VIEW);
//                startActivity(mThreeDaysIntent);
//                finish();
//                return true;
//
//            case R.id.menu_action_day_calendar:
//                Intent mDayIntent = new Intent(EventsCalendarActivity.this, WeekCalendarActivity.class);
//                mDayIntent.putExtra("weekViewType", WeekCalendarActivity.TYPE_DAY_VIEW);
//                startActivity(mDayIntent);
//                finish();
//                return true;
//
//            default:
//                // If we got here, the user's action was not recognized.
//                // Invoke the superclass to handle it.
//                return super.onOptionsItemSelected(item);
//
//        }
//    }
//}
