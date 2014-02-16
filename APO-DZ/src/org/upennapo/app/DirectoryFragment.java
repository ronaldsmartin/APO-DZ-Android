package org.upennapo.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Arrays;
import java.util.List;

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
import android.widget.ListView;

public class DirectoryFragment extends Fragment{
	
	private List<Brother> directoryList;
	
	public DirectoryFragment() {
    }
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.fragment_directory, container, false);
		

		List<String> firstLast = new ArrayList<String>();
		for (Brother obj: directoryList){
			firstLast.add(obj.First_Name + " " + obj.Last_Name);
		}
		
		// if this works
		AlphabeticalAdapter adapt = new AlphabeticalAdapter(getActivity(), android.R.layout.simple_list_item_activated_1, firstLast);
		
		// if it doesn't work
//		ArrayAdapter<String> adapt = new ArrayAdapter<String>(getActivity(),
//                android.R.layout.simple_list_item_activated_1, firstLast);
		
		
		ListView list = (ListView) view.findViewById(R.id.name_list);
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
		
		AsyncBrotherLoader loader = new AsyncBrotherLoader();
		final String urlString = getString(R.string.directory_json_url);
		loader.execute(urlString);
		
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
	    }
	    
	}
	
}

