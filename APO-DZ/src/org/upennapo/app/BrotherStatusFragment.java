package org.upennapo.app;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class BrotherStatusFragment extends Fragment {
	
	private User user;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		
//		AsyncUserDataLoader loader = new AsyncUserDataLoader();
//		loader.execute(urlString);
		
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
