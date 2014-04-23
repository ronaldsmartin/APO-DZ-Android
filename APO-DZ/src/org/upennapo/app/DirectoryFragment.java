package org.upennapo.app;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Arrays;
import java.util.Comparator;

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

public class DirectoryFragment extends Fragment{
	
	public static final String HEADER_KEY = "HEADER";
	public static final String URL_KEY   = "URL";
	public static final String SHEET_KEY = "SHEET_KEY";
	private ArrayList<Brother> directoryList;
	private View view;
	
	public DirectoryFragment() {
    }

	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		// Retrieve the arguments passed by the MainActivity
        final String urlString   = getArguments().getString(URL_KEY);
        final String sheetKey    = getArguments().getString(SHEET_KEY);
        final String headerTitle = getArguments().getString(HEADER_KEY);
		
        Activity context = getActivity();
        context.getActionBar();
        // Activate the progress view in the action bar.
        // Progress bar code is due to http://guides.thecodepath.com/android/Handling-ProgressBars
		showProgressBar();
		
        // Make an asynchronous request for the JSON using the URL
    	AsyncBrotherLoader loader = new AsyncBrotherLoader();
    	loader.execute(urlString, sheetKey);
		
		// Inflate the View
		view = inflater.inflate(R.layout.fragment_directory, container, false);
		TextView header = (TextView) view.findViewById(R.id.directory_header);
		header.setText(headerTitle);
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
	
	private class AsyncBrotherLoader extends AsyncTask<String, Void, Brother[]> {

	    @Override
	    protected void onPreExecute() {        
	        super.onPreExecute();
	        updateProgressValue(1000);
	    }

	    @Override
	    protected Brother[] doInBackground(String... params) {
	    	Brother[] results = ReadJSON.getDirectoryData(params[0], params[1], getActivity());
	    	return results;
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

