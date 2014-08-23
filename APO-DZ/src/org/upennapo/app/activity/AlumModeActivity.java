package org.upennapo.app.activity;

import android.app.ActionBar;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;

import org.upennapo.app.R;
import org.upennapo.app.fragment.AlumDirectoryFragment;
import org.upennapo.app.fragment.LinkListFragment;
import org.upennapo.app.fragment.NavigationDrawerFragment;
import org.upennapo.app.fragment.WebFragment;

public class AlumModeActivity extends FragmentActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alum_mode);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        refreshActionBarTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {

        // update the main content by replacing fragments
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
                item = LinkListFragment.newAlumLinksInstance(this);
                break;
            case 3:
                item = WebFragment.new2048Instance(this);
                break;

            case R.id.btn_section1:
                item = fm.findFragmentByTag(AlumDirectoryFragment.TAG);
                if (item == null) {
                    item = AlumDirectoryFragment.newInstance(this);
                    fm.beginTransaction().add(item, AlumDirectoryFragment.TAG);
                }
                break;
            case R.id.btn_section2:
                item = WebFragment.newCalendarInstance(this);
                break;
            case R.id.btn_section3:
                item = LinkListFragment.newAlumLinksInstance(this);
                break;
            case R.id.btn_section4:
                item = WebFragment.new2048Instance(this);
                break;

            default:
                item = new MainActivity.DummySectionFragment();
        }
        fm.beginTransaction()
                .replace(R.id.container, item)
                .commit();

        // Remember the last title.
        refreshActionBarTitle();
    }

    @Override
    public CharSequence getItemSubtitle(int position) {
        switch (position) {
            case R.id.btn_section1:
                return getString(R.string.alumni);
            default:
                return null;
        }
    }

    /**
     * Use the currently selected navigation item as the ActionBar title.
     */
    private void refreshActionBarTitle() {
        mTitle = mNavigationDrawerFragment == null ?
                getTitle() : mNavigationDrawerFragment.getCurrentItemTitle();
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setTitle(mTitle);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.alum_mode, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.menu_about_app:
                final String githubPageUrl = getString(R.string.menu_about_app_url);
                Intent openGithubPage = new Intent(Intent.ACTION_VIEW, Uri.parse(githubPageUrl));
                startActivity(openGithubPage);
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
}
