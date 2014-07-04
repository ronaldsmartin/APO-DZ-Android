package org.upennapo.app;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class DirectoryDetailsActivity extends Activity {

    public static final String TAG_BROTHER_DATA = "BROTHER";
    private Brother mBrother;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retrieve brother data from intent.
        mBrother = getIntent().getParcelableExtra(TAG_BROTHER_DATA);

        setContentView(R.layout.activity_directory_details);
        setupViews();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.directory_detail, menu);
        return true;
    }

    private void setupViews() {
        getActionBar().setTitle(mBrother.toString());

        // Set all labels.
        TextView emailLabel = (TextView) findViewById(R.id.email);
        emailLabel.setText(mBrother.Email_Address);

        TextView phone = (TextView) findViewById(R.id.phone);
        phone.setText(mBrother.Phone_Number);

        TextView yearLabel = (TextView) findViewById(R.id.year);
        yearLabel.setText(mBrother.Graduation_Year);

        TextView pledgeClassLabel = (TextView) findViewById(R.id.pledge_class);
        pledgeClassLabel.setText(mBrother.Pledge_Class);

        TextView majorLabel = (TextView) findViewById(R.id.major);
        majorLabel.setText(mBrother.Major);

        TextView schoolLabel = (TextView) findViewById(R.id.school);
        schoolLabel.setText(mBrother.School);

        // Enable the SMS button iff the device has the button feature.
        ImageButton smsButton = (ImageButton) findViewById(R.id.text_button);
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_TELEPHONY)) {
            smsButton.setEnabled(false);
        }
    }

    @Override
    public boolean onNavigateUp() {
        finish();
        return true;
    }

    /**
     * OnClick method for the SMS button.
     *
     * @param view the SMS ImageButton
     */
    public void onSmsBtnClick(View view) {
        try {
            Intent sendIntent = new Intent(Intent.ACTION_VIEW);
            sendIntent.setType("vnd.android-dir/mms-sms");
            sendIntent.putExtra("address", mBrother.Phone_Number);
            startActivity(sendIntent);
        } catch (Exception e) {
            Toast.makeText(this, "SMS failed, please try again later!", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    /**
     * Use an intent to add/edit this person in the default Contacts app.
     */
    public void onAddContactClick(MenuItem menuItem) {
        // Creates a new Intent to insert or edit a contact
        Intent intentInsertEdit = new Intent(Intent.ACTION_INSERT_OR_EDIT);

        // Sets the MIME type
        intentInsertEdit.setType(ContactsContract.Contacts.CONTENT_ITEM_TYPE);

        // Insert contact data.
        intentInsertEdit
                .putExtra(ContactsContract.Intents.Insert.NAME, mBrother.toString())
                .putExtra(ContactsContract.Intents.Insert.EMAIL, mBrother.Email_Address)
                .putExtra(ContactsContract.Intents.Insert.PHONE, mBrother.Phone_Number)
                .putExtra(ContactsContract.Intents.Insert.PHONE_TYPE,
                        ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);


        startActivity(intentInsertEdit);
    }

    public void onShoutoutBtnClick(View view) {
        final String url = getResources().getString(R.string.form_dz_shoutout);
        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(i);
    }
}
