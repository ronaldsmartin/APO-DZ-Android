package org.upennapo.app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

/**
 * @author Ronald Martin
 */
public class AlumDirectoryFragment extends DirectoryFragment {

    public static AlumDirectoryFragment newInstance(Context c) {
        AlumDirectoryFragment instance = new AlumDirectoryFragment();

        final String url = c.getString(R.string.alumni_directory_sheet_key),
                sheetKey = c.getString(R.string.alumni_directory_json_url);

        Bundle args = new Bundle();
        args.putString(DirectoryFragment.URL_KEY, url);
        args.putString(DirectoryFragment.SHEET_KEY, sheetKey);
        instance.setArguments(args);

        return instance;
    }

    @Override
    protected AdapterView.OnItemClickListener onItemClickListener() {
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Parcel the brother at this index to the details view.
                Intent detailPage = new Intent(getActivity(), DirectoryDetailsActivity.class);
                detailPage.putExtra(getString(R.string.dir_brother_data), mBrothers.get(position));
                getActivity().startActivity(detailPage);
            }
        };
    }
}
