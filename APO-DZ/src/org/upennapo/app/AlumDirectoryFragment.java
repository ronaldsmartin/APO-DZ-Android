package org.upennapo.app;

import android.content.Context;
import android.os.Bundle;

/**
 * @author Ronald Martin
 */
public class AlumDirectoryFragment extends DirectoryFragment {

    public static AlumDirectoryFragment newInstance(Context c) {
        AlumDirectoryFragment instance = new AlumDirectoryFragment();

        final String sheetKey = c.getString(R.string.alumni_directory_sheet_key);
        Bundle args = new Bundle();
        args.putString(SHEET_KEY, sheetKey);
        instance.setArguments(args);

        return instance;
    }
}
