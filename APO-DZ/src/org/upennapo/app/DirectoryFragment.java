package org.upennapo.app;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class DirectoryFragment extends Fragment{
	
	private List<Brother> directoryList;
	private View view;
	
	public DirectoryFragment() {
    }
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		
		AsyncBrotherLoader loader = new AsyncBrotherLoader();
		final String urlString = getString(R.string.directory_json_url);
		loader.execute(urlString);
		
		view = inflater.inflate(R.layout.fragment_directory, container, false);

		
		return view;
	}
	
	public class AsyncBrotherLoader extends AsyncTask<String, Void, Brother[]> {

	    @Override
	    protected void onPreExecute() {        
	        super.onPreExecute();    
	    }

	    @Override
	    protected Brother[] doInBackground(String... params) {
	    	return ReadJSON.getDirectoryData(params[0]);
	    }
	    
	    @Override
	    protected void onPostExecute(Brother[] result) {
	        directoryList = new ArrayList<Brother>(Arrays.asList(result));
	        for (Brother brother : directoryList) {
	        	Log.d("brotherData", brother.toString());
	        }
	        
	        
	        List<String> firstLast = new ArrayList<String>();
	        String firstName;
			for (Brother obj: directoryList){
				if (obj.Preferred_Name != "") firstName = obj.Preferred_Name;
				else firstName = obj.First_Name;
				firstLast.add(firstName + " " + obj.Last_Name);
			}
			Collections.sort(firstLast);
			
			// if this works
			AlphabeticalAdapter adapt = new AlphabeticalAdapter(getActivity(), android.R.layout.simple_list_item_activated_1, firstLast);
			
			// if it doesn't work
//			ArrayAdapter<String> adapt = new ArrayAdapter<String>(getActivity(),
//	                android.R.layout.simple_list_item_activated_1, firstLast);
			
			ImageView im  = (ImageView) view.findViewById(R.id.viet);
			TextView t = (TextView) view.findViewById(R.id.text);
			ListView list = (ListView) view.findViewById(R.id.name_list);
			
			t.setText("");
			list.setAdapter(adapt);
			
			list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				public void onItemClick(AdapterView <?> parent, View view, int position, long id) {
					Brother b = directoryList.get(position);
					HashMap<String,String> broMap = new HashMap<String,String>();
					broMap.put(Brother.LAST_NAME_KEY, b.Last_Name);
					broMap.put(Brother.FIRST_NAME_KEY, b.First_Name);
					broMap.put(Brother.EMAIL_ADDRESS_KEY, b.Email_Address);
					broMap.put(Brother.PHONE_NUMBER_KEY, b.Phone_Number);
					broMap.put(Brother.PLEDGE_CLASS_KEY, b.Pledge_Class);
					broMap.put(Brother.GRADUATION_YEAR_KEY, String.valueOf(b.Expected_Graduation_Year));
					broMap.put(Brother.SCHOOL_KEY, b.School);
					broMap.put(Brother.MAJOR_KEY, b.Major);
					broMap.put(Brother.BIRTHDAY_KEY, b.Birthday);
					
					Intent broPage = new Intent(getActivity(), DirectoryDetails.class);
					broPage.putExtra(getString(R.string.dir_brother_data), broMap);
					getActivity().startActivity(broPage);
				}
				
			});
	        
	    }
	    
	}
	
}

