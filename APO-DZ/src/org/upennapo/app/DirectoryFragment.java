package org.upennapo.app;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Arrays;
import java.util.List;
import java.util.Comparator;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

public class DirectoryFragment extends Fragment{
	
	public static final String URL_KEY   = "URL";
	public static final String SHEET_KEY = "SHEET_KEY";
	private List<Brother> directoryList;
	private View view;
	
	public DirectoryFragment() {
    }

	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		// Retrieve the argumentS passed by the MainActivity
        final String urlString = getArguments().getString(URL_KEY);
        final String sheetKey  = getArguments().getString(SHEET_KEY);
		
		// Make an asynchronous request for the JSON using the URL
		AsyncBrotherLoader loader = new AsyncBrotherLoader();
		loader.execute(urlString, sheetKey);
		
		// Inflate the View
		view = inflater.inflate(R.layout.fragment_directory, container, false);
		return view;
	}
	
	private class AsyncBrotherLoader extends AsyncTask<String, Void, Brother[]> {

	    @Override
	    protected void onPreExecute() {        
	        super.onPreExecute();    
	    }

	    @Override
	    protected Brother[] doInBackground(String... params) {
	    	return ReadJSON.getDirectoryData(params[0], params[1]);
	    }
	    
	    @Override
	    protected void onPostExecute(Brother[] result) {
	        directoryList = new ArrayList<Brother>(Arrays.asList(result));
	        Collections.sort(directoryList, new BrotherComparator());        
	        
	        List<String> firstLast = new ArrayList<String>();
	       	for (Brother brother: directoryList) {
				String firstName =
					brother.Preferred_Name.equals("") ? brother.First_Name : brother.Preferred_Name;
				firstLast.add(firstName + " " + brother.Last_Name);
			}

			AlphabeticalAdapter adapter =
				new AlphabeticalAdapter(getActivity(), R.layout.centered_textview, R.id.centered_text, firstLast);
			ListView list = (ListView) view.findViewById(R.id.name_list);
			
			list.setAdapter(adapter);
			
			list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				public void onItemClick(AdapterView <?> parent, View view, int position, long id) {
					// Prepare data to send to the details view.
					Brother b = directoryList.get(position);
					HashMap<String,String> broMap = new HashMap<String,String>();
					broMap.put(Brother.LAST_NAME_KEY, b.Last_Name);
					broMap.put(Brother.FIRST_NAME_KEY, b.First_Name);
					broMap.put(Brother.PREFERRED_NAME_KEY, b.Preferred_Name);
					broMap.put(Brother.EMAIL_ADDRESS_KEY, b.Email_Address);
					broMap.put(Brother.PHONE_NUMBER_KEY, b.Phone_Number);
					broMap.put(Brother.PLEDGE_CLASS_KEY, b.Pledge_Class);
					broMap.put(Brother.GRADUATION_YEAR_KEY, String.valueOf(b.Expected_Graduation_Year));
					broMap.put(Brother.SCHOOL_KEY, b.School);
					broMap.put(Brother.MAJOR_KEY, b.Major);
					broMap.put(Brother.BIRTHDAY_KEY, b.Birthday);
					
					// Open the details view.
					Intent detailPage = new Intent(getActivity(), DirectoryDetails.class);
					detailPage.putExtra(getString(R.string.dir_brother_data), broMap);
					getActivity().startActivity(detailPage);
				}
				
			});
	        
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

