package org.upennapo.app.activity;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import org.upennapo.app.R;
import org.upennapo.app.fragment.DirectoryFragment;

public class DirectoryActivity extends FragmentActivity {

    public static final String SHEET_KEY = DirectoryFragment.SHEET_KEY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_directory);

        final String directoryName = getIntent().getStringExtra(SHEET_KEY);
        final ActionBar actionBar = getActionBar();

        if (actionBar != null) {
            actionBar.setTitle(directoryName);
            actionBar.setLogo(R.drawable.ic_action_people);
        }

        if (savedInstanceState == null) {
            DirectoryFragment directory = new DirectoryFragment();

            Bundle args = new Bundle();
            args.putString(SHEET_KEY, directoryName);
            directory.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, directory)
                    .commit();
        }
    }

    @Override
    public boolean onNavigateUp() {
        finish();
        return true;
    }
}
