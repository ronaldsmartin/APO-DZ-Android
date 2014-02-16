package org.upennapo.app;

import org.upennapo.app.DirectoryFragment.AsyncBrotherLoader;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;

public class LoginActivity extends Activity {
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_activity);
		
		SharedPreferences settings = getPreferences(0);
//		if(settings.contains("username")){
//			// go to status tab
//		}
//		else{
//			AsyncBrotherLoader loader = new AsyncBrotherLoader();
//			final String urlString = getString(R.string.directory_json_url);
//			loader.execute(urlString);
//			
//			view = inflater.inflate(R.layout.fragment_directory, container, false);
//
//			
//			return view;
//		}
		
	}
}
