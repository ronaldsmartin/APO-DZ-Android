package org.upennapo.app;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

public class LoginActivity extends Activity {
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_activity);
		
		// SharedPreferences settings = getPreferences(0);
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
	
	/**
	 * Opened when clicking button in the LoginView
	 * @param view - the button in login_activity.xml
	 */
	public void login(View view) {
		Intent openAppIntent = new Intent(this, MainActivity.class);
		startActivity(openAppIntent);
	}
}
