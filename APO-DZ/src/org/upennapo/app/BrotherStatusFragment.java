package org.upennapo.app;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class BrotherStatusFragment extends Fragment {
	
	private User user;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		
		AsyncUserDataLoader loader = new AsyncUserDataLoader();
		final String urlString = getString(R.string.directory_json_url);
		loader.execute(urlString);
		
		// TODO: fix
		View view = inflater.inflate(R.layout.fragment_directory, container, false);

		
		return view;
	}
	
	private void updateView() {
		
	}
	
	private class AsyncUserDataLoader extends AsyncTask<String, Void, User> {

	    @Override
	    protected void onPreExecute() {        
	        super.onPreExecute();    
	    }

	    @Override
	    protected User doInBackground(String... params) {
	    	return ReadJSON.getBrotherData(params[0], params[1], params[2]);
	    }
	    
	    @Override
	    protected void onPostExecute(User result) {    
	        user = result;
	        updateView();
	    }	    
	}
}
