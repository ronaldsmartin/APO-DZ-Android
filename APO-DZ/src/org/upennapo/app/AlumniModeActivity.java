package org.upennapo.app;


import android.app.ActionBar;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ArrayAdapter;

public class AlumniModeActivity extends FragmentActivity implements ActionBar.OnNavigationListener {

    /**
     * The serialization (saved instance state) Bundle key representing the
     * current dropdown position.
     */
    private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_alumni_mode);

        // Set up the action bar to show a dropdown list.
        final ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

        // Set up the dropdown list navigation in the action bar.
        actionBar.setListNavigationCallbacks(
                // Specify a SpinnerAdapter to populate the dropdown list.
                new ArrayAdapter<String>(
                        actionBar.getThemedContext(),
                        android.R.layout.simple_list_item_1,
                        android.R.id.text1,
                        getResources().getStringArray(R.array.titles_alumni_mode)),
                this);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // Restore the previously serialized current dropdown position.
        if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
            getActionBar().setSelectedNavigationItem(
                    savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // Serialize the current dropdown position.
        outState.putInt(STATE_SELECTED_NAVIGATION_ITEM,
                getActionBar().getSelectedNavigationIndex());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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

            case R.id.menu_switch_mode:
                Intent switchMode = new Intent(this, MainActivity.class);
                startActivity(switchMode);
                finish();
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
    public boolean onNavigationItemSelected(int position, long id) {

        // When the given dropdown item is selected, show its contents in the
        // container view.

        FragmentManager fm = getSupportFragmentManager();

        Fragment item;
        switch (position) {
            case 0:
                item = fm.findFragmentByTag(AlumDirectoryFragment.TAG);
                if (item == null) {
                    item = AlumDirectoryFragment.newInstance(this);
                    fm.beginTransaction().add(item, AlumDirectoryFragment.TAG);
                }
                break;
            case 1:
                item = WebFragment.newCalendarInstance(this);
                break;
            case 2:
                item = new HelpfulLinksFragment();
                break;
            case 3:
                item = WebFragment.new2048Instance(this);
                break;
            default:
                item = new MainActivity.DummySectionFragment();
        }
        fm.beginTransaction()
                .replace(R.id.container, item)
                .commit();
        return true;
    }
}
