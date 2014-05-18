package org.upennapo.app;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

public class DirectoryFragment extends Fragment implements OnRefreshListener {

    public static final String HEADER_KEY = "HEADER";
    public static final String URL_KEY = "URL";
    public static final String SHEET_KEY = "SHEET_KEY";

    private String urlString;
    private String sheetKey;
    private String headerTitle;

    private View view;
    private PullToRefreshLayout mPullToRefreshLayout;

	private ArrayList<Brother> directoryList;


    public DirectoryFragment() {
    }

	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		// Retrieve the arguments passed by the MainActivity
        this.urlString = getArguments().getString(URL_KEY);
        this.sheetKey = getArguments().getString(SHEET_KEY);
        this.headerTitle = getArguments().getString(HEADER_KEY);

        Activity context = getActivity();
        context.getActionBar();
        // Activate the progress view in the action bar.
        // Progress bar code is due to http://guides.thecodepath.com/android/Handling-ProgressBars
		showProgressBar();

        refreshDirectory();

        // Inflate the View
        view = inflater.inflate(R.layout.fragment_directory, container, false);
        TextView header = (TextView) view.findViewById(R.id.directory_header);
        header.setText(headerTitle);

        // Now find the PullToRefreshLayout to setup
        this.mPullToRefreshLayout = (PullToRefreshLayout) view.findViewById(R.id.ptr_layout);
        // Now setup the PullToRefreshLayout
        ActionBarPullToRefresh.from(getActivity())
                // Mark All Children as pullable
                .allChildrenArePullable()
                        // Set a OnRefreshListener
                .listener(this)
                        // Finally commit the setup to our PullToRefreshLayout
                .setup(mPullToRefreshLayout);

		return view;
	}
	
	
	private void showProgressBar() {
        getActivity().setProgressBarVisibility(true);
    }
	
	private void updateProgressValue(int value) {
        // Manage the progress (i.e within an AsyncTask)
        // Valid ranges are from 0 to 10000 (both inclusive). 
        // If 10000 is given, the progress bar will be completely filled and will fade out.
		getActivity().setProgress(value);
    }
    
    // Should be called when an async task has finished
    public void hideProgressBar() {
    	getActivity().setProgressBarVisibility(false);
    }

    private void refreshDirectory() {
        // Make an asynchronous request for the JSON using the URL
        AsyncBrotherLoader loader = new AsyncBrotherLoader() {
            @Override
            protected void onPostExecute(Brother[] result) {
                super.onPostExecute(result);
                DirectoryFragment.this.mPullToRefreshLayout.setRefreshComplete();
            }
        };
        loader.execute(urlString, sheetKey);
    }
    
    protected void updateDirectoryList(Brother[] result) {
    	directoryList = new ArrayList<Brother>(Arrays.asList(result));
        updateProgressValue(4000);
        Collections.sort(directoryList, new BrotherComparator());        
        updateProgressValue(7000);
        
        ArrayList<String> alphabetizedNames = new ArrayList<String>();
       	for (Brother brother : directoryList) {
			String firstName =
				brother.Preferred_Name.equals("") ? brother.First_Name : brother.Preferred_Name;
			alphabetizedNames.add(firstName + " " + brother.Last_Name);
		}
       	updateProgressValue(8500);

		AlphabeticalAdapter adapter =
			new AlphabeticalAdapter(getActivity(), R.layout.centered_textview, R.id.centered_text, alphabetizedNames);
		ListView list = (ListView) view.findViewById(R.id.name_list);
		list.setAdapter(adapter);
		updateProgressValue(9000);
		
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView <?> parent, View view, int position, long id) {
				// Parcel the brother at this index to the details view.
				Intent detailPage = new Intent(getActivity(), DirectoryDetails.class);
				detailPage.putExtra(getString(R.string.dir_brother_data), directoryList.get(position));
				getActivity().startActivity(detailPage);
			}
		});
    }

    @Override
    public void onRefreshStarted(View view) {
        refreshDirectory();
    }

    private class AsyncBrotherLoader extends AsyncTask<String, Void, Brother[]> {

	    @Override
	    protected void onPreExecute() {        
	        super.onPreExecute();
	        updateProgressValue(1000);
	    }

	    @Override
	    protected Brother[] doInBackground(String... params) {
            return ReadJSON.getDirectoryData(params[0], params[1], getActivity());
        }

        @Override
        protected void onPostExecute(Brother[] result) {
	    	if (result == null) {
	    		// If there is an error getting the result, display an alert.
	    		
	    		Toast failureAlert = Toast.makeText(getActivity(), "Unable to load at this time.", Toast.LENGTH_LONG);
	    		failureAlert.show();
	    	} else {
	    		updateDirectoryList(result);
	    	}
	        
			updateProgressValue(10000);
	    }
	}
	
	public class BrotherComparator implements Comparator<Brother> {
		@Override
		public int compare(Brother b1, Brother b2) {
			// Retrieve brother 1's first name.
			final String preferredName1 = b1.Preferred_Name;
			final String firstName1 = preferredName1.length() == 0 ? b1.First_Name : preferredName1;
			
			// Retrieve brother 2's first name.
			final String preferredName2 = b2.Preferred_Name;
			final String firstName2 = preferredName2.length() == 0 ? b2.First_Name : preferredName2;
			
			// Compare and return lexographic ordering.
			return firstName1.compareToIgnoreCase(firstName2);
		}
	}
}

