package org.upennapo.app;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class DirectoryFragment extends Fragment{
	
	private List<Brother> directoryList;
	
	public DirectoryFragment() {
    }
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.fragment_directory, container, false);
		
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

