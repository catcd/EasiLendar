package com.texas.easilendar;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

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
import com.texas.easilendar.ui.helpers.SettingsActivity;

/**
 * Created by SONY on 07-Jul-16.
 */
public abstract class AppDrawerActivity extends AppCompatActivity {
    public static final int DRAWER_ITEM_CALENDARS = 0;
    public static final int DRAWER_ITEM_SHARED_WITH_ME = 1;
    public static final int DRAWER_ITEM_SEARCH = 2;
    public static final int DRAWER_ITEM_NOTIFICATIONS = 3;
    public static final int DRAWER_ITEM_MEETING_PLANER = 4;
    public static final int DRAWER_ITEM_SCHEDULE = 5;
    public static final int DRAWER_ITEM_IMPORT = 6;
    public static final int DRAWER_ITEM_SETTINGS = 7;
    public static final int DRAWER_ITEM_HELP = 8;
    public static final int DRAWER_ITEM_LOGOUT = 9;

    public static final int PROFILE_DRAWER_ITEM_ACCOUNT = 0;
    public static final int PROFILE_DRAWER_ITEM_REGISTER = 1;
    public static final int PROFILE_DRAWER_ITEM_LINK_MY_ACCOUNT = 2;
    public static final int PROFILE_DRAWER_ITEM_CHANGE_PRIVACY = 3;
    public static final int PROFILE_DRAWER_ITEM_EDIT_PROFILE = 4;

    protected Drawer mDrawer;
    protected String mName = "";
    protected String mEmail = "";
    protected Drawable mAvatar = null;

    protected void getAccountInformationForDrawer() {
        // get current name, email, avatar
        mName = "Meo Giay";
        mEmail = "easilendar.texas@gmail.com";
        mAvatar = getResources().getDrawable(R.drawable.logo_white);
    }

    protected void setupNavigationDrawer(Activity mActivity, Toolbar mToolbar, int mSelected) {
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
                .withBadgeStyle(new BadgeStyle().withTextColor(Color.WHITE).withColorRes(R.color.colorError));
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
                        // TODO go to register for anonymous
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
                        // TODO go to link my account
                        return false;
                    }
                });
        ProfileSettingDrawerItem itemChangePrivacy = new ProfileSettingDrawerItem()
                .withIdentifier(PROFILE_DRAWER_ITEM_CHANGE_PRIVACY)
                .withName(getResources().getString(R.string.profile_drawer_item_change_privacy))
                .withIcon(GoogleMaterial.Icon.gmd_share)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        // TODO go to change privacy
                        return false;
                    }
                });
        ProfileSettingDrawerItem itemEditProfile = new ProfileSettingDrawerItem()
                .withIdentifier(PROFILE_DRAWER_ITEM_EDIT_PROFILE)
                .withName(getResources().getString(R.string.profile_drawer_item_edit_profile))
                .withIcon(GoogleMaterial.Icon.gmd_edit)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        // TODO go to edit profile (update information, change password)
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
                        itemLinkMyAccount,
                        itemChangePrivacy,
                        itemEditProfile
                )
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                        if (currentProfile) {
                            // TODO go to profile
                        }
                        return false;
                    }
                })
                .build();

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
                                // TODO go to calendars show
                                return false;
                            case DRAWER_ITEM_SHARED_WITH_ME:
                                // TODO go to shared with me
                                return false;
                            case DRAWER_ITEM_SEARCH:
                                // TODO go to search
                                return false;
                            case DRAWER_ITEM_NOTIFICATIONS:
                                // TODO go to notification
                                return false;
                            case DRAWER_ITEM_MEETING_PLANER:
                                // TODO go to meeting planer
                                return false;
                            case DRAWER_ITEM_SCHEDULE:
                                // TODO go to schedule
                                return false;
                            case DRAWER_ITEM_IMPORT:
                                // TODO go to import calendars
                                return false;
                            case DRAWER_ITEM_SETTINGS:
                                startActivity(new Intent(AppDrawerActivity.this, SettingsActivity.class));
                                return false;
                            case DRAWER_ITEM_HELP:
                                // TODO go to help and feedback
                                return false;
                            case DRAWER_ITEM_LOGOUT:
                                // TODO logout
                                return false;
                        }
                        return true;
                    }
                })
                .build();
    }
}
