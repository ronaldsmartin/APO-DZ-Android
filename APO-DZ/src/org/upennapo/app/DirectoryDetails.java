package org.upennapo.app;

import java.util.HashMap;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class DirectoryDetails extends Activity {
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.directory_details);
		TextView name = (TextView) findViewById(R.id.name);
		HashMap<String,String> map = (HashMap<String, String>) getIntent().getSerializableExtra(getString(R.string.dir_brother_data));
		String preferredName = map.get(Brother.PREFERRED_NAME_KEY);
		String firstName;
		if (preferredName.length() == 0){
			firstName = map.get(Brother.FIRST_NAME_KEY);
		}
		else{
			firstName = preferredName;
		}
		name.setText(firstName + " " + map.get(Brother.LAST_NAME_KEY)); 
		TextView emailAdd = (TextView) findViewById(R.id.email);
		emailAdd.setText(map.get(Brother.EMAIL_ADDRESS_KEY));
		TextView phone = (TextView) findViewById(R.id.phone);
		phone.setText(map.get(Brother.PHONE_NUMBER_KEY));
		TextView year = (TextView) findViewById(R.id.year);
		year.setText(map.get(Brother.GRADUATION_YEAR_KEY));
		TextView pledge = (TextView) findViewById(R.id.pledge_class);
		pledge.setText(map.get(Brother.PLEDGE_CLASS_KEY));
		TextView major = (TextView) findViewById(R.id.major);
		major.setText(map.get(Brother.MAJOR_KEY));
		
		Button email_b = (Button) findViewById(R.id.email_button);
		email_b.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
	}
}
