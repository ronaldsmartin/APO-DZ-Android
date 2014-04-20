package org.upennapo.app;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class BrotherStatusFragment extends Fragment {
	
	// Constants
	public static final String URL_KEY = "SPREADSHEET_URL";
	
	private User user;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		SharedPreferences prefs = getActivity().getSharedPreferences("LoginActivity", Context.MODE_PRIVATE);
		final String firstName = prefs.getString(LoginActivity.USER_FIRSTNAME_KEY, ""),
					 lastName  = prefs.getString(LoginActivity.USER_LASTNAME_KEY, ""),
					 spreadsheetUrl = getArguments().getString(URL_KEY);
		
		AsyncUserDataLoader loader = new AsyncUserDataLoader();
		loader.execute(spreadsheetUrl, firstName, lastName);
		
		View view = inflater.inflate(R.layout.work_in_progress_view, container, false);
		
		return view;
	}
	
	private void updateView() {
		Log.v("BrotherStatusFragment", this.user.toString());
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
