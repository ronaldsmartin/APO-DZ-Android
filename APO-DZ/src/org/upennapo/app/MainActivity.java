package org.upennapo.app;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

public class MainActivity extends FragmentActivity implements ActionBar.TabListener {

    public static final int NUM_TABS = 5;
    private static final int NUM_TAPS_ACTIVATE = 10;
    private static final String EASTER_EGG_UNLOCKED = "2048_UNLOCKED";
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
     * will keep every loaded fragment in memory. If this becomes too memory
     * intensive, it may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;
    /**
     * Used to count the number of times the Helpful Links tab has been selected.
     * When mNumTaps == NUM_TAPS_ACTIVATE, trigger the easter egg activity.
     */
    private int mNumTaps = 0;

    /**
     * Get the corresponding unselected tab icon ID for a tab.
     *
     * @param position for which to get the tab icon
     * @return the unselected tab icon's resource ID for the tab at position
     */
    protected static int getPageIcon(int position) {
        int iconID = 0;
        switch (position) {
            case 0:
                // Brother Status
                iconID = R.drawable.ic_action_tick;
                break;
            case 1:
                // Calendar
                iconID = R.drawable.ic_action_calendar_day;
                break;
            case 2:
                // Brother Directory
                iconID = R.drawable.ic_action_users;
                break;
            case 3:
                // Pledge Directory
                iconID = R.drawable.ic_action_grow;
                break;
            case 4:
                // Helpful Links
                iconID = R.drawable.ic_action_bookmark;
                break;
        }
        return iconID;
    }

    /**
     * Get the corresponding selected tab icon ID for a tab.
     *
     * @param position for which to get the tab icon
     * @return the selected tab icon's resource ID for the tab at position
     */
    protected static int getSelectedPageIcon(int position) {
        int iconID = 0;
        switch (position) {
            case 0:
                // Brother Status
                iconID = R.drawable.ic_action_tick_selected;
                break;
            case 1:
                // Calendar
                iconID = R.drawable.ic_action_calendar_day_selected;
                break;
            case 2:
                // Brother Directory
                iconID = R.drawable.ic_action_users_selected;
                break;
            case 3:
                // Pledge Directory
                iconID = R.drawable.ic_action_grow_selected;
                break;
            case 4:
                // Helpful Links
                iconID = R.drawable.ic_action_bookmark_selected;
                break;
        }
        return iconID;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_PROGRESS);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_main);

        // Set up the action bar.
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the app.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(NUM_TABS - 1);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setIcon(getPageIcon(i))
                            .setTabListener(this)
            );
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        // Enable the 2048 option if unlocked.
        SharedPreferences prefs = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);
        if (prefs.getBoolean(EASTER_EGG_UNLOCKED, false)) {
            menu.findItem(R.id.menu_play_2048).setVisible(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {

            case R.id.menu_play_2048:
                Intent play2048 = new Intent(this, EasterEggActivity.class);
                startActivity(play2048);
                return true;

            case R.id.menu_send_feedback:
                final String feedbackFormUrl = getString(R.string.menu_report_bug_url);
                Intent sendFeedback = new Intent(Intent.ACTION_VIEW, Uri.parse(feedbackFormUrl));
                startActivity(sendFeedback);
                return true;

            case R.id.menu_about_app:
                final String githubPageUrl = getString(R.string.menu_about_app_url);
                Intent openGithubPage = new Intent(Intent.ACTION_VIEW, Uri.parse(githubPageUrl));
                startActivity(openGithubPage);
                return true;

            case R.id.menu_rate_app:
                // Get the package name, removing the suffix if we're in debug mode.
                String packageName = getPackageName();
                if (packageName.endsWith(".dev"))
                    packageName = packageName.substring(0, packageName.length() - 4);

                // Open the app page in the Google Play app or website.
                Uri uri = Uri.parse("market://details?id=" + packageName);
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    startActivity(goToMarket);
                } catch (ActivityNotFoundException e) {
                    uri = Uri.parse("http://play.google.com/store/apps/details?id=" + packageName);
                    goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(goToMarket);
                }
                return true;

            case R.id.menu_switch_user:
                Intent openLoginScreen = new Intent(this, LoginActivity.class);
                openLoginScreen.putExtra(LoginActivity.LOGOUT_INTENT, true);
                startActivity(openLoginScreen);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        int position = tab.getPosition();

        // Whenever the Helpful Links tab is selected, we edge closer to activating the Easter Egg!
        if (position == 4) {
            updateEasterEggStatus();
        }

        setTitle(mSectionsPagerAdapter.getPageTitle(position));
        tab.setIcon(getSelectedPageIcon(position));

        mViewPager.setCurrentItem(position);
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        int position = tab.getPosition();
        tab.setIcon(getPageIcon(position));
        mViewPager.setCurrentItem(position);
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    private void updateEasterEggStatus() {
        SharedPreferences prefs = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);
        if (++mNumTaps == NUM_TAPS_ACTIVATE && !prefs.contains(EASTER_EGG_UNLOCKED)) {

            // Remember that we've unlocked the easter egg.
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(EASTER_EGG_UNLOCKED, true);
            editor.apply();

            invalidateOptionsMenu();

            // Show user unlock message.
            Toast t = Toast.makeText(this, R.string.apo_2048_unlock_msg, Toast.LENGTH_LONG);
            t.show();
        }
    }

    /**
     * A dummy fragment representing a section of the app, but that simply
     * displays dummy text.
     */
    public static class DummySectionFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */

        public DummySectionFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            return inflater.inflate(R.layout.work_in_progress_view, container, false);
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        /**
         * Tags for fragment instance retrieval.
         */
        private static final String TAG_BROTHER_STATUS_FRAG = "BROTHER_STATUS";
        private static final String TAG_CALENDAR_FRAG = "CALENDAR";
        private static final String TAG_BROTHER_DIRECTORY_FRAG = "BROTHER_DIRECTORY";
        private static final String TAG_PLEDGE_DIRECTORY_FRAG = "PLEDGE_DIRECTORY";


        private Fragment brotherStatusFragment = new BrotherStatusFragment();
        private Fragment calendarFragment = new WebFragment();
        private Fragment broDirectoryFragment = new DirectoryFragment();
        private Fragment pledgeDirectoryFragment = new DirectoryFragment();
        private Fragment linksFragment = new HelpfulLinksFragment();

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
            init();
        }

        private boolean retrieveFragments(FragmentManager fm) {
            // TODO: figure out how to use this
            brotherStatusFragment = fm.findFragmentByTag(TAG_BROTHER_STATUS_FRAG);
            calendarFragment = fm.findFragmentByTag(TAG_CALENDAR_FRAG);
            broDirectoryFragment = fm.findFragmentByTag(TAG_BROTHER_DIRECTORY_FRAG);
            pledgeDirectoryFragment = fm.findFragmentByTag(TAG_PLEDGE_DIRECTORY_FRAG);
            return brotherStatusFragment != null && calendarFragment != null
                    && broDirectoryFragment != null && pledgeDirectoryFragment != null;
        }

        private void init() {
            // Set up Brother Status
            Bundle broStatusArgs = new Bundle();
            broStatusArgs.putString(BrotherStatusFragment.URL_KEY, getString(R.string.spreadsheet_url));
            this.brotherStatusFragment.setArguments(broStatusArgs);

            // Set up Calendar
            Bundle calendarUrlArgs = new Bundle();
            calendarUrlArgs.putString(WebFragment.URL_KEY, getString(R.string.calendar_url));
            this.calendarFragment.setArguments(calendarUrlArgs);

            // Set up Directories
            Bundle broDirectoryArgs = new Bundle();
            broDirectoryArgs.putString(DirectoryFragment.SHEET_KEY, getString(R.string.brother_directory_sheet_key));
            this.broDirectoryFragment.setArguments(broDirectoryArgs);

            Bundle pledgeDirectoryArgs = new Bundle();
            pledgeDirectoryArgs.putString(DirectoryFragment.SHEET_KEY, getString(R.string.pledge_directory_sheet_key));
            this.pledgeDirectoryFragment.setArguments(pledgeDirectoryArgs);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    // Brother Status
                    return this.brotherStatusFragment;
                case 1:
                    // Calendar WebView
                    return this.calendarFragment;
                case 2:
                    // Brother Directory
                    return this.broDirectoryFragment;
                case 3:
                    // Pledge Directory
                    return this.pledgeDirectoryFragment;
                case 4:
                    // Helpful Links
                    return this.linksFragment;
                default:
                    // getItem is called to instantiate the fragment for the given page.
                    // Return a DummySectionFragment (defined as a static inner class
                    // below) with the page number as its lone argument.
                    return new DummySectionFragment();
            }
        }

        @Override
        public int getCount() {
            // Show total number of pages.
            return NUM_TABS;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            String sectionName = "";
            switch (position) {
                case 0:
                    // Brother Status
                    sectionName = getString(R.string.title_section1);
                    break;
                case 1:
                    // Calendar
                    sectionName = getString(R.string.title_section2);
                    break;
                case 2:
                    // Brother Directory
                    sectionName = getString(R.string.title_section3);
                    break;
                case 3:
                    // Pledge Directory
                    sectionName = getString(R.string.title_section4);
                    break;
                case 4:
                    // Helpful Links
                    sectionName = getString(R.string.title_section5);
                    break;
            }
            return sectionName;
        }
    }

}
