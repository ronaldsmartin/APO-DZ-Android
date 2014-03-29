package org.upennapo.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class LoginActivity extends Activity {
	
	public static final String LOGGED_IN_KEY = "USER_LOGGED_IN";
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_activity);	
	}
	
	public void logout(View view) {
		if (userIsLoggedIn()) {
			SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
			editor.remove(LOGGED_IN_KEY);
			editor.apply();
		}
	}
	
	/**
	 * Opened when clicking button in the LoginView
	 * @param view - the button in login_activity.xml
	 */
	public void login(View view) {
		if (userIsLoggedIn()) {
			proceedToApp();
		} else {
			displayPasswordPrompt("Enter Password");
		}
	}
	
	private boolean userIsLoggedIn() {
		SharedPreferences prefs = getPreferences(MODE_PRIVATE);
		return prefs.getBoolean(LOGGED_IN_KEY, false);
	}
	
	private void proceedToApp() {
		Intent openAppIntent = new Intent(this, MainActivity.class);
		startActivity(openAppIntent);
	}
	
	private void displayPasswordPrompt(String title) {
		// This code snippet is thanks to
		// http://www.androidsnippets.com/prompt-user-input-with-an-alertdialog
		
		AlertDialog.Builder prompt = new AlertDialog.Builder(this);
		prompt.setTitle(title);
		prompt.setMessage("Please enter the chapter app password to continue.");

		// Set an EditText view to get user input 
		final EditText input = new EditText(this);
		prompt.setView(input);

		prompt.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				// Retrieve the user input.
				String value = input.getText().toString();
				
				// User entered password correctly.
				if (value.equals(getString(R.string.app_password))) {
					// Store that we have logged in.
					SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
					editor.putBoolean(LOGGED_IN_KEY, true);
					editor.apply();
				  
					// Continue to the main view.
					proceedToApp();
				} else {
					// User entered incorrect password. Try again with new prompt.
					displayPasswordPrompt("Incorrect Password.");
				}
			}
		});

		prompt.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				// Canceled.
			}
		});

		prompt.show();
	}
}
