package org.upennapo.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TextView.OnEditorActionListener;

public class LoginActivity extends Activity {
	
	public static final String LOGGED_IN_KEY      = "USER_LOGGED_IN";
	public static final String USER_FIRSTNAME_KEY = "USER_FIRST_NAME";
	public static final String USER_LASTNAME_KEY  = "USER_LAST_NAME";
	public static final String LOGOUT_INTENT      = "USER_LOGOUT_INTENT";
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_activity);
		
		if (getIntent().getBooleanExtra(LOGOUT_INTENT, false)) {
			updateNameLabel();
		} else if (userIsLoggedIn() && hasUsername()) {
			proceedToApp();
		}
	}
	
	public void logout(View view) {
		// Thanks to http://stackoverflow.com/questions/2115758/how-to-display-alert-dialog-in-android
		if (userIsLoggedIn() || hasUsername()) {
			// Confirm logout before proceeding.
			new AlertDialog.Builder(this)
		    .setTitle("Confirm Logout")
		    .setMessage("Are you sure you want to logout?")
		    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
		    	@Override
		        public void onClick(DialogInterface dialog, int which) { 
		            // continue with delete
		        	SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
					editor.clear();
					if (editor.commit()) {
						resetNameLabel();
						
						// Tell the user logout is complete.
						Toast logoutNotification = Toast.makeText(LoginActivity.this, "Logout successful!", Toast.LENGTH_SHORT);
						logoutNotification.show();
					}
		        }
		     })
		    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int which) { 
		            // do nothing
		        }
		     })
		    .setIcon(android.R.drawable.ic_dialog_alert)
		    .show();
		}
	}
	
	/**
	 * Opened when clicking login button in the LoginView
	 * @param view - the button in login_activity.xml
	 */
	public void login(View view) {
		if (hasUsername()) {
			if (userIsLoggedIn()) {
				proceedToApp();
			} else {
				showPasswordPrompt("Enter Password");
			}
		} else {
			showNamePrompt();
		}
	}
	
	
	private void updateNameLabel() {
		// Change the prefix.
		TextView welcomeLabel = (TextView) findViewById(R.id.login_status);
		if (hasUsername()) {
			welcomeLabel.setText(R.string.login_status_logged_in);
			
			// Add the name.
			TextView nameLabel = (TextView) findViewById(R.id.name);
			SharedPreferences prefs = getPreferences(MODE_PRIVATE);
			String firstName = prefs.getString(USER_FIRSTNAME_KEY, "");
			String lastName = prefs.getString(USER_LASTNAME_KEY, "");
			nameLabel.setText(firstName + " " + lastName);
		} else {
			resetNameLabel();
		}
	}
	
	private void resetNameLabel() {
		TextView welcomeLabel = (TextView) findViewById(R.id.login_status);
		welcomeLabel.setText(R.string.login_status_logged_out);
		TextView nameLabel = (TextView) findViewById(R.id.name);
		nameLabel.setText("");	
	}
	
	private boolean userIsLoggedIn() {
		SharedPreferences prefs = getPreferences(MODE_PRIVATE);
		return prefs.getBoolean(LOGGED_IN_KEY, false);
	}
	
	private boolean hasUsername() {
		SharedPreferences prefs = getPreferences(MODE_PRIVATE);
		return prefs.contains(USER_FIRSTNAME_KEY) && prefs.contains(USER_LASTNAME_KEY);
	}
	
	private void proceedToApp() {
		Intent openAppIntent = new Intent(this, MainActivity.class);
		startActivity(openAppIntent);
		finish();
	}
	
	protected void showPasswordPrompt(String title) {
		// This code snippet is thanks to
		// http://www.androidsnippets.com/prompt-user-input-with-an-alertdialog
		
		AlertDialog.Builder prompt = new AlertDialog.Builder(this);
		prompt.setTitle(title);
		prompt.setMessage("Please enter the chapter app password to continue.");

		// Create an EditText view to get user input that censors the input password.
		final EditText input = new EditText(this);
		input.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
		input.setTransformationMethod(PasswordTransformationMethod.getInstance());
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
					showPasswordPrompt("Incorrect Password.");
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
	
	
	private void showNamePrompt() {
		FragmentManager fm = getFragmentManager();
		NamePromptDialogFragment namePrompt = new NamePromptDialogFragment();
		namePrompt.show(fm, "NamePromptDialogFragment");
		
		updateNameLabel();
	}
	
	public static class NamePromptDialogFragment extends DialogFragment implements OnEditorActionListener {

		private EditText firstNameField, lastNameField;
		
		/**
		 * 
		 */
		public NamePromptDialogFragment() {
			// Auto-generated constructor stub
		}


		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View view = inflater.inflate(R.layout.name_prompt_dialog_fragment, container);

			// Get references to the EditText fields in the dialog.
			this.firstNameField = (EditText) view.findViewById(R.id.name_prompt_dialog_first_name_entry);
			this.lastNameField  = (EditText) view.findViewById(R.id.name_prompt_dialog_last_name_entry);
			
			// Set a listener so that pressing the enter button submits the text in the entry fields.
			this.lastNameField.setOnEditorActionListener(this);
			
			// Show soft keyboard automatically
			this.firstNameField.requestFocus();
			getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
			return super.onCreateView(inflater, container, savedInstanceState);
		}
		
		@Override
	    public Dialog onCreateDialog(Bundle savedInstanceState) {
	        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

	        View view = getActivity().getLayoutInflater().inflate(R.layout.name_prompt_dialog_fragment, null);
	        final EditText firstNameEditText = (EditText) view.findViewById(R.id.name_prompt_dialog_first_name_entry),
	        			   lastNameEditText  = (EditText) view.findViewById(R.id.name_prompt_dialog_last_name_entry);

	 		// Customize the window.
	        alertDialogBuilder.setView(view);
	        alertDialogBuilder.setTitle(R.string.name_entry_title);
	        alertDialogBuilder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
	            @Override
	            public void onClick(DialogInterface dialog, int which) {
	            	// Return input text to activity
	    			final String firstName = firstNameEditText.getText().toString().trim(),
	    						 lastName  = lastNameEditText.getText().toString().trim();
	    			
	    			// Validate data, proceeding only if both fields are nonempty.
	    			if (firstName.equals("") || lastName.equals("")) {
	    				new AlertDialog.Builder(getActivity())
	    			    .setTitle("Blank Name")
	    			    .setMessage("Please enter your first and last name exactly as it appears on the APO spreadsheet.")
	    			    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
	    			    	@Override
	    			        public void onClick(DialogInterface dialog, int which) {
	    			    		dialog.dismiss();
	    			        }
	    			     })
	    			    .setIcon(android.R.drawable.ic_dialog_alert)
	    			    .show();
	    			} else {
	    				// Store that we have logged in.
		    			SharedPreferences.Editor editor = getActivity().getPreferences(Context.MODE_PRIVATE).edit();
		    			editor.putString(LoginActivity.USER_FIRSTNAME_KEY, firstName);
		    			editor.putString(LoginActivity.USER_LASTNAME_KEY, lastName);
		    			editor.apply();
		    			
		    			// Update the login status label.
		    			LoginActivity rootActivity = (LoginActivity) getActivity();
		    			rootActivity.updateNameLabel();
		    			
		    			// Tell the user logout is complete.
						Toast loginNotification = Toast.makeText(getActivity(), "Greetings, " + firstName + "!", Toast.LENGTH_SHORT);
						loginNotification.show();
		    			
		    			dialog.dismiss();
	    			}
	            }
	        });
	        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	            @Override
	            public void onClick(DialogInterface dialog, int which) {
	                dialog.cancel();
	            }
	        });

	        return alertDialogBuilder.create();
	    }


		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			if (EditorInfo.IME_ACTION_DONE == actionId) {
	            dismiss();
	            return true;
	        }
	        return false;
		}
	}
}
