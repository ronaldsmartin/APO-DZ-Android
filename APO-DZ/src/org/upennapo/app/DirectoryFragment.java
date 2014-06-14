package org.upennapo.app;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

public class DirectoryFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    public static final String URL_KEY = "URL";
    public static final String SHEET_KEY = "SHEET_KEY";

    private String urlString;
    private String sheetKey;

    private View view;
    private SwipeRefreshLayout mSwipeRefreshLayout;

	private ArrayList<Brother> directoryList;


    public DirectoryFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retrieve the arguments passed by the MainActivity
        this.urlString = getArguments().getString(URL_KEY);
        this.sheetKey = getArguments().getString(SHEET_KEY);
    }

	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        Activity context = getActivity();
        context.getActionBar();
        // Activate the progress view in the action bar.
        // Progress bar code is due to http://guides.thecodepath.com/android/Handling-ProgressBars
		showProgressBar();

        // Inflate the View
        view = inflater.inflate(R.layout.fragment_directory, container, false);

        // Make an asynchronous request for the JSON using the URL
        AsyncBrotherLoader loader = new AsyncBrotherLoader();
        loader.execute(urlString, sheetKey);

        // Now find the PullToRefreshLayout to setup
        this.mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_layout);
        this.mSwipeRefreshLayout.setColorScheme(
                R.color.apo_blue, R.color.apo_yellow, R.color.apo_blue, R.color.apo_yellow);
        this.mSwipeRefreshLayout.setOnRefreshListener(this);

		return view;
	}

    /**
     * Show the ActionBar progress bar.
     */
    private void showProgressBar() {
        getActivity().setProgressBarVisibility(true);
    }

    /**
     * Set the progress value of the ActionBar progress bar.
     *
     * @param value of progress in range [0, 10000]
     */
    private void updateProgressValue(int value) {
		getActivity().setProgress(value);
    }


    /**
     * Hide the ActionBar progress bar.
     */
    public void hideProgressBar() {
    	getActivity().setProgressBarVisibility(false);
    }
    
    protected void updateDirectoryList(Brother[] result) {
    	directoryList = new ArrayList<Brother>(Arrays.asList(result));
        updateProgressValue(4000);
        Collections.sort(directoryList, new BrotherComparator());        
        updateProgressValue(7000);
        
        ArrayList<String> alphabetizedNames = new ArrayList<String>();
       	for (Brother brother : directoryList) {
			String firstName =
                    brother.Preferred_Name.length() == 0 ? brother.First_Name : brother.Preferred_Name;
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
    public void onRefresh() {
        // Make an asynchronous request for the JSON using the URL
        AsyncBrotherLoader loader = new AsyncBrotherLoader() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                mSwipeRefreshLayout.setRefreshing(true);
            }

            @Override
            protected Brother[] doInBackground(String... params) {
                return ReadJSON.getDirectoryData(params[0], params[1], getActivity(), true);
            }

            @Override
            protected void onPostExecute(Brother[] result) {
                super.onPostExecute(result);
                mSwipeRefreshLayout.setRefreshing(false);
            }
        };
        loader.execute(urlString, sheetKey);
    }

    private class AsyncBrotherLoader extends AsyncTask<String, Void, Brother[]> {

	    @Override
	    protected void onPreExecute() {        
	        super.onPreExecute();
	        updateProgressValue(1000);
	    }

	    @Override
	    protected Brother[] doInBackground(String... params) {
            return ReadJSON.getDirectoryData(params[0], params[1], getActivity(), false);
        }

        @Override
        protected void onPostExecute(Brother[] result) {
	    	if (result == null) {
	    		// If there is an error getting the result, display an alert.

                Toast failureAlert = Toast.makeText(getActivity(),
                        "Unable to load at this time.", Toast.LENGTH_LONG);
                failureAlert.show();
            } else {
	    		updateDirectoryList(result);
	    	}
	        
			updateProgressValue(10000);
            hideProgressBar();
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

