package org.upennapo.app;

import java.util.Locale;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Intent;
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

public class MainActivity extends FragmentActivity implements ActionBar.TabListener {

	public static final int NUM_TABS = 5;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_PROGRESS);
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
                            .setTabListener(this));
        }
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
            case R.id.menu_switch_user:
            	Intent openLoginScreen = new Intent(this, LoginActivity.class);
            	openLoginScreen.putExtra(LoginActivity.LOGOUT_INTENT, true);
            	startActivity(openLoginScreen);
            	finish();
                return true;
            case R.id.menu_report_bug:
            	final String githubIssueUrl = getString(R.string.menu_report_bug_url);
                Intent openGithubIssue = new Intent(Intent.ACTION_VIEW, Uri.parse(githubIssueUrl));
                startActivity(openGithubIssue);
                return true;
            case R.id.menu_about_app:
            	final String githubPageUrl = getString(R.string.menu_about_app_url);
                Intent openGithubPage = new Intent(Intent.ACTION_VIEW, Uri.parse(githubPageUrl));
                startActivity(openGithubPage);
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


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

    	private final Fragment brotherStatusFragment   = new BrotherStatusFragment();
    	private final Fragment calendarFragment        = new CalendarFragment();
    	private final Fragment broDirectoryFragment    = new DirectoryFragment();
    	private final Fragment pledgeDirectoryFragment = new DirectoryFragment();
    	private final Fragment linksFragment           = new HelpfulLinksFragment();
    	
        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
            init();
        }
        
        private void init() {
        	// Set up Brother Status
        	Bundle broStatusArgs = new Bundle();
        	broStatusArgs.putString(BrotherStatusFragment.URL_KEY, getString(R.string.spreadsheet_url));
        	this.brotherStatusFragment.setArguments(broStatusArgs);
        	
        	// Set up Calendar
            Bundle calendarUrlArgs = new Bundle();
            calendarUrlArgs.putString(CalendarFragment.URL_KEY, getString(R.string.calendar_url));
    		this.calendarFragment.setArguments(calendarUrlArgs);
    		
    		// Set up Directories
    		Bundle broDirectoryArgs = new Bundle();
    		broDirectoryArgs.putString(DirectoryFragment.URL_KEY, getString(R.string.brother_directory_json_url));
    		broDirectoryArgs.putString(DirectoryFragment.SHEET_KEY, "ActiveBrotherDirectory");
    		broDirectoryArgs.putString(DirectoryFragment.HEADER_KEY, "Brother Directory");
    		this.broDirectoryFragment.setArguments(broDirectoryArgs);
    		
    		Bundle pledgeDirectoryArgs = new Bundle();
    		pledgeDirectoryArgs.putString(DirectoryFragment.URL_KEY, getString(R.string.pledge_directory_json_url));
    		pledgeDirectoryArgs.putString(DirectoryFragment.SHEET_KEY, "PledgeDirectory");
    		pledgeDirectoryArgs.putString(DirectoryFragment.HEADER_KEY, "Pledge Directory");
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
            Locale l = Locale.getDefault();
            String sectionName = "";
            switch (position) {
                case 0:
                	// Brother Status
                	sectionName = getString(R.string.title_section1).toUpperCase(l);
                	break;
                case 1:
                	// Calendar
                	sectionName = getString(R.string.title_section2).toUpperCase(l);
                	break;
                case 2:
                	// Brother Directory
                	sectionName = getString(R.string.title_section3).toUpperCase(l);
                	break;
                case 3:
                	// Pledge Directory
                	sectionName = getString(R.string.title_section4).toUpperCase(l);
                	break;
                case 4:
                	// Helpful Links
                	sectionName = getString(R.string.title_section5).toUpperCase(l);
                	break;
            }
            return sectionName;
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
        public static final String ARG_SECTION_NUMBER = "section_number";

        public DummySectionFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.work_in_progress_view, container, false);
            return rootView;
        }
    }

}
