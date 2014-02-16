package org.upennapo.app;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class DirectoryDetails extends Activity {
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.directory_details);
		TextView name = (TextView) findViewById(R.id.name);
		String preferredName = savedInstanceState.getString(Brother.PREFERRED_NAME_KEY);
		String firstName = savedInstanceState.getString(Brother.FIRST_NAME_KEY);
		if (preferredName != "") firstName = preferredName;
		name.setText(firstName + " " + savedInstanceState.getString(Brother.LAST_NAME_KEY));
	}
}
