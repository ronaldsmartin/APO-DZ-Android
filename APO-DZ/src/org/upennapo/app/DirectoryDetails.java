package org.upennapo.app;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class DirectoryDetails extends Activity {
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.directory_details);
		
		// Retrieve brother data from intent.
		final Brother brother = (Brother) getIntent().getParcelableExtra(getString(R.string.dir_brother_data));
		
		// Set all labels.
		TextView nameLabel = (TextView) findViewById(R.id.name);
		String preferredName = brother.Preferred_Name;
		String firstName = preferredName.length() == 0 ? brother.First_Name : preferredName;
		nameLabel.setText(firstName + " " + brother.Last_Name); 
		
		TextView emailLabel = (TextView) findViewById(R.id.email);
		emailLabel.setText(brother.Email_Address);
		
		TextView phone = (TextView) findViewById(R.id.phone);
		phone.setText(brother.Phone_Number);
		
		TextView yearLabel = (TextView) findViewById(R.id.year);
		yearLabel.setText(brother.Expected_Graduation_Year);
		
		TextView pledgeClassLabel = (TextView) findViewById(R.id.pledge_class);
		pledgeClassLabel.setText(brother.Pledge_Class);
		
		TextView majorLabel = (TextView) findViewById(R.id.major);
		majorLabel.setText(brother.Major);
		

		// Enable SMS, Email, and Call buttons for this contact.
		Button smsButton = (Button) findViewById(R.id.text_button);
		smsButton.setOnClickListener(new OnClickListener () {
			@Override 
			public void onClick(View v) {
				try {
					Intent sendIntent = new Intent(Intent.ACTION_VIEW);
					sendIntent.setType("vnd.android-dir/mms-sms");
					sendIntent.putExtra("address", brother.Phone_Number);
					startActivity(sendIntent);
				} catch (Exception e) {
					Toast.makeText(getApplicationContext(),
						"SMS failed, please try again later!",
						Toast.LENGTH_LONG).show();
					e.printStackTrace();
				}
			}
		});
				
		
		Button emailButton = (Button) findViewById(R.id.email_button);
		emailButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
				emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String [] {brother.Email_Address});
				emailIntent.setType("text/plain");
				startActivity(emailIntent);
			}
		});
		
		Button callButton = (Button) findViewById(R.id.call_button);
		callButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				try {
			        Intent callIntent = new Intent(Intent.ACTION_DIAL);
			        callIntent.setData(Uri.parse("tel:" + brother.Phone_Number));
			        startActivity(callIntent);
			    } catch (ActivityNotFoundException e) {
			         e.printStackTrace();
			    }
			}
		});
	}
}
