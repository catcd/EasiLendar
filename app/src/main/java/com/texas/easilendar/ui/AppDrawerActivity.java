package com.texas.easilendar.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.holder.BadgeStyle;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileSettingDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.texas.easilendar.R;
import com.texas.easilendar.helper.ConnectivityReceiver;
import com.texas.easilendar.helper.ImageSaver;
import com.texas.easilendar.ui.calendars.ImportCalendarActivity;
import com.texas.easilendar.ui.features.MeetingPlanerActivity;
import com.texas.easilendar.ui.features.NotificationsActivity;
import com.texas.easilendar.ui.features.ScheduleActivity;
import com.texas.easilendar.ui.features.SearchActivity;
import com.texas.easilendar.ui.helpers.SelectCalendarsActivity;
import com.texas.easilendar.ui.helpers.HelpActivity;
import com.texas.easilendar.ui.helpers.SettingsActivity;
import com.texas.easilendar.ui.outsides.LoginActivity;
import com.texas.easilendar.ui.profiles.LinkMyAccountActivity;
import com.texas.easilendar.ui.profiles.MyProfileActivity;
import com.texas.easilendar.ui.profiles.RegisterAnonymousActivity;
import com.texas.easilendar.ui.profiles.SharedWithMeActivity;

import static com.texas.easilendar.constant.AppDrawerConstant.DRAWER_ITEM_CALENDARS;
import static com.texas.easilendar.constant.AppDrawerConstant.DRAWER_ITEM_HELP;
import static com.texas.easilendar.constant.AppDrawerConstant.DRAWER_ITEM_IMPORT;
import static com.texas.easilendar.constant.AppDrawerConstant.DRAWER_ITEM_LOGOUT;
import static com.texas.easilendar.constant.AppDrawerConstant.DRAWER_ITEM_MEETING_PLANER;
import static com.texas.easilendar.constant.AppDrawerConstant.DRAWER_ITEM_NOTIFICATIONS;
import static com.texas.easilendar.constant.AppDrawerConstant.DRAWER_ITEM_SCHEDULE;
import static com.texas.easilendar.constant.AppDrawerConstant.DRAWER_ITEM_SEARCH;
import static com.texas.easilendar.constant.AppDrawerConstant.DRAWER_ITEM_SETTINGS;
import static com.texas.easilendar.constant.AppDrawerConstant.DRAWER_ITEM_SHARED_WITH_ME;
import static com.texas.easilendar.constant.AppDrawerConstant.PROFILE_DRAWER_ITEM_ACCOUNT;
import static com.texas.easilendar.constant.AppDrawerConstant.PROFILE_DRAWER_ITEM_LINK_MY_ACCOUNT;
import static com.texas.easilendar.constant.AppDrawerConstant.PROFILE_DRAWER_ITEM_REGISTER;
import static com.texas.easilendar.constant.LoginConstant.LOGIN_EXTRA_PREVIOUS_EMAIL;
import static com.texas.easilendar.constant.LoginConstant.LOGIN_USER_TYPE_ANONYMOUS;
import static com.texas.easilendar.constant.LoginConstant.LOGIN_USER_TYPE_FACEBOOK;
import static com.texas.easilendar.constant.LoginConstant.LOGIN_USER_TYPE_GOOGLE;
import static com.texas.easilendar.constant.LoginConstant.LOGIN_USER_TYPE_PASSWORD;
import static com.texas.easilendar.constant.SharedPreferencesConstant.PREFS_LOGIN_USER;
import static com.texas.easilendar.constant.SharedPreferencesConstant.PREFS_LOGIN_USER_AVATAR_FILE_NAME;
import static com.texas.easilendar.constant.SharedPreferencesConstant.PREFS_LOGIN_USER_EMAIL;
import static com.texas.easilendar.constant.SharedPreferencesConstant.PREFS_LOGIN_USER_FULL_NAME;
import static com.texas.easilendar.constant.SharedPreferencesConstant.PREFS_LOGIN_USER_ID;
import static com.texas.easilendar.constant.SharedPreferencesConstant.PREFS_LOGIN_USER_TYPE;
import static com.texas.easilendar.constant.SharedPreferencesConstant.PREFS_SETTINGS;

/**
 * Created by SONY on 07-Jul-16.
 */
public abstract class AppDrawerActivity extends AppCompatActivity
        implements GoogleApiClient.OnConnectionFailedListener {
    protected Drawer mDrawer;
    protected String mLoginType = "";
    protected String mUid = "";
    protected String mName = "";
    protected String mEmail = "";
    protected Drawable mAvatar = null;

    protected void setupNavigationDrawer(Activity mActivity, Toolbar mToolbar, int mSelected) {
        getAccountInformationForDrawer();

        // Items for main drawer
        PrimaryDrawerItem itemCalendars = new PrimaryDrawerItem()
                .withIdentifier(DRAWER_ITEM_CALENDARS)
                .withName(R.string.drawer_item_calendars)
                .withIcon(GoogleMaterial.Icon.gmd_event_available);
        PrimaryDrawerItem itemSharedWithMe = new PrimaryDrawerItem()
                .withIdentifier(DRAWER_ITEM_SHARED_WITH_ME)
                .withName(R.string.drawer_item_shared_with_me)
                .withIcon(GoogleMaterial.Icon.gmd_people);
        PrimaryDrawerItem itemSearch = new PrimaryDrawerItem()
                .withIdentifier(DRAWER_ITEM_SEARCH)
                .withName(R.string.drawer_item_search)
                .withIcon(GoogleMaterial.Icon.gmd_search);
        PrimaryDrawerItem itemNotifications = new PrimaryDrawerItem()
                .withIdentifier(DRAWER_ITEM_NOTIFICATIONS)
                .withName(R.string.drawer_item_notifications)
                .withIcon(GoogleMaterial.Icon.gmd_notifications)
                .withBadge(R.string.drawer_item_notifications_badge)
                .withBadgeStyle(new BadgeStyle()
                        .withTextColor(Color.WHITE)
                        .withColorRes(R.color.colorError)
                );
        PrimaryDrawerItem itemMeetingPlanner = new PrimaryDrawerItem()
                .withIdentifier(DRAWER_ITEM_MEETING_PLANER)
                .withName(R.string.drawer_item_meeting_planner)
                .withIcon(GoogleMaterial.Icon.gmd_schedule);
        PrimaryDrawerItem itemSchedule = new PrimaryDrawerItem()
                .withIdentifier(DRAWER_ITEM_SCHEDULE)
                .withName(R.string.drawer_item_schedule)
                .withIcon(GoogleMaterial.Icon.gmd_view_quilt);
        PrimaryDrawerItem itemImport = new PrimaryDrawerItem()
                .withIdentifier(DRAWER_ITEM_IMPORT)
                .withName(R.string.drawer_item_import)
                .withIcon(GoogleMaterial.Icon.gmd_file_download);
        PrimaryDrawerItem itemSettings = new PrimaryDrawerItem()
                .withIdentifier(DRAWER_ITEM_SETTINGS)
                .withName(R.string.drawer_item_settings)
                .withIcon(GoogleMaterial.Icon.gmd_settings);
        PrimaryDrawerItem itemHelp = new PrimaryDrawerItem()
                .withIdentifier(DRAWER_ITEM_HELP)
                .withName(R.string.drawer_item_help)
                .withIcon(GoogleMaterial.Icon.gmd_live_help);
        PrimaryDrawerItem itemLogout = new PrimaryDrawerItem()
                .withIdentifier(DRAWER_ITEM_LOGOUT)
                .withName(R.string.drawer_item_logout)
                .withIcon(GoogleMaterial.Icon.gmd_transfer_within_a_station);

        // Item on profile section
        ProfileDrawerItem itemAccountProfile = new ProfileDrawerItem()
                .withName(mName)
                .withEmail(mEmail)
                .withIcon(mAvatar)
                .withIdentifier(PROFILE_DRAWER_ITEM_ACCOUNT);
        ProfileSettingDrawerItem itemRegister = new ProfileSettingDrawerItem()
                .withIdentifier(PROFILE_DRAWER_ITEM_REGISTER)
                .withName(getResources().getString(R.string.profile_drawer_item_register))
                .withIcon(GoogleMaterial.Icon.gmd_fiber_new)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        startActivity(new Intent(AppDrawerActivity.this, RegisterAnonymousActivity.class));
                        return false;
                    }
                });
        ProfileSettingDrawerItem itemLinkMyAccount = new ProfileSettingDrawerItem()
                .withIdentifier(PROFILE_DRAWER_ITEM_LINK_MY_ACCOUNT)
                .withName(getResources().getString(R.string.profile_drawer_item_link_my_account))
                .withIcon(GoogleMaterial.Icon.gmd_link)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        startActivity(new Intent(AppDrawerActivity.this, LinkMyAccountActivity.class));
                        return false;
                    }
                });

        // Create the AccountHeader
        AccountHeader header = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.google_now)
                .addProfiles(
                        itemAccountProfile,
                        itemRegister,
                        itemLinkMyAccount
                )
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                        if (currentProfile) {
                            startActivity(new Intent(AppDrawerActivity.this, MyProfileActivity.class));
                        }
                        return false;
                    }
                })
                .build();

        if (!mLoginType.equals(LOGIN_USER_TYPE_ANONYMOUS)) {
            header.removeProfileByIdentifier(PROFILE_DRAWER_ITEM_REGISTER);
        }

        //create the drawer and remember the `Drawer` result object
        mDrawer = new DrawerBuilder()
                .withActivity(mActivity)
                .withToolbar(mToolbar)
                .withAccountHeader(header)
                .addDrawerItems(
                        itemCalendars,
                        itemSharedWithMe,
                        itemSearch,
                        new DividerDrawerItem(),
                        itemNotifications,
                        itemMeetingPlanner,
                        itemSchedule,
                        itemImport,
                        new DividerDrawerItem(),
                        itemSettings,
                        itemHelp,
                        itemLogout
                )
                .withSelectedItem(mSelected)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        switch ((int) drawerItem.getIdentifier()) {
                            case DRAWER_ITEM_CALENDARS:
                                startActivity(new Intent(AppDrawerActivity.this, SelectCalendarsActivity.class));
                                return false;
                            case DRAWER_ITEM_SHARED_WITH_ME:
                                startActivity(new Intent(AppDrawerActivity.this, SharedWithMeActivity.class));
                                return false;
                            case DRAWER_ITEM_SEARCH:
                                startActivity(new Intent(AppDrawerActivity.this, SearchActivity.class));
                                return false;
                            case DRAWER_ITEM_NOTIFICATIONS:
                                startActivity(new Intent(AppDrawerActivity.this, NotificationsActivity.class));
                                return false;
                            case DRAWER_ITEM_MEETING_PLANER:
                                startActivity(new Intent(AppDrawerActivity.this, MeetingPlanerActivity.class));
                                return false;
                            case DRAWER_ITEM_SCHEDULE:
                                startActivity(new Intent(AppDrawerActivity.this, ScheduleActivity.class));
                                return false;
                            case DRAWER_ITEM_IMPORT:
                                startActivity(new Intent(AppDrawerActivity.this, ImportCalendarActivity.class));
                                return false;
                            case DRAWER_ITEM_SETTINGS:
                                startActivity(new Intent(AppDrawerActivity.this, SettingsActivity.class));
                                return false;
                            case DRAWER_ITEM_HELP:
                                startActivity(new Intent(AppDrawerActivity.this, HelpActivity.class));
                                return false;
                            case DRAWER_ITEM_LOGOUT:
                                actionLogout();
                                return false;
                            default:
                                return false;
                        }
                    }
                })
                .build();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this,
                getResources().getString(R.string.login_error_play_service_connection),
                Toast.LENGTH_SHORT).show();
    }

    private void actionLogout() {
        // logout confirm
        if (ConnectivityReceiver.isConnected()) {
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(AppDrawerActivity.this)
                    .setTitle(R.string.logout_title)
                    .setMessage(R.string.logout_confirm)
                    .setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ProgressDialog dialog = new ProgressDialog(AppDrawerActivity.this);
                            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                            dialog.setMessage(getResources().getString(R.string.logout_progress));
                            dialog.setIndeterminate(true);
                            dialog.setCanceledOnTouchOutside(false);
                            dialog.show();

                            logout(dialog);
                        }
                    })
                    .setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // Do nothing
                        }
                    });
            if (mLoginType.equals(LOGIN_USER_TYPE_ANONYMOUS)) {
                alertBuilder.setMessage(R.string.logout_anonymous_confirm).create().show();
            } else {
                alertBuilder.create().show();
            }
        } else {
            Toast.makeText(AppDrawerActivity.this,
                    getResources().getString(R.string.logout_error_no_connection),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void logout(ProgressDialog dialog) {
        // TODO logout delete event from SQLite
        // TODO logout delete noti from SQLite

        // Delete shared preferences
        getSharedPreferences(PREFS_LOGIN_USER, MODE_PRIVATE).edit().clear().apply();
        getSharedPreferences(PREFS_SETTINGS, MODE_PRIVATE).edit().clear().apply();

        // Delete avatar
        new ImageSaver(AppDrawerActivity.this)
                .setFileName(PREFS_LOGIN_USER_AVATAR_FILE_NAME)
                .delete();

        switch (mLoginType) {
            case LOGIN_USER_TYPE_ANONYMOUS:
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    deleteAnonymousUser(user);
                }
                break;
            case LOGIN_USER_TYPE_GOOGLE:
                break;
            case LOGIN_USER_TYPE_FACEBOOK:
                break;
            case LOGIN_USER_TYPE_PASSWORD:
                break;
            default:
                break;
        }

        // sign out from Firebase
        FirebaseAuth.getInstance().signOut();

        Toast.makeText(AppDrawerActivity.this,
                getResources().getString(R.string.logout_success),
                Toast.LENGTH_SHORT).show();

        // Navigate to login
        Intent i = new Intent(AppDrawerActivity.this, LoginActivity.class);
        i.putExtra(LOGIN_EXTRA_PREVIOUS_EMAIL, mEmail);
        startActivity(i);

        dialog.dismiss();
        finish();
    }

    private void deleteAnonymousUser(final FirebaseUser user) {
        user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (!task.isComplete()) {
                    deleteAnonymousUser(user);
                }
            }
        });
    }

    private void getAccountInformationForDrawer() {
        // get current name, email, avatar
        // Restore preferences
        SharedPreferences loginUser = getSharedPreferences(PREFS_LOGIN_USER, MODE_PRIVATE);
        mLoginType = loginUser.getString(PREFS_LOGIN_USER_TYPE, LOGIN_USER_TYPE_ANONYMOUS);
        mUid = loginUser.getString(PREFS_LOGIN_USER_ID, "");
        mName = loginUser.getString(PREFS_LOGIN_USER_FULL_NAME, "Meo Giay");
        mEmail = loginUser.getString(PREFS_LOGIN_USER_EMAIL, "easilendar.texas@gmail.com");

        // get avatar
        Bitmap avatar = new ImageSaver(AppDrawerActivity.this)
                .setFileName(PREFS_LOGIN_USER_AVATAR_FILE_NAME)
                .load();
        if (avatar != null) {
            mAvatar = new BitmapDrawable(getResources(), avatar);
        } else {
            mAvatar = getResources().getDrawable(R.drawable.default_avatar);
        }
    }
}
