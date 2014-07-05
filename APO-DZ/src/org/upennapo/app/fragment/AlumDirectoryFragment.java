package org.upennapo.app.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import org.upennapo.app.R;
import org.upennapo.app.model.Brother;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;


/**
 * An extension of DirectoryFragment that loads directory from all directory sources, using
 * ActionItems to toggle which source is displayed.
 *
 * @author Ronald Martin
 */
public class AlumDirectoryFragment extends DirectoryFragment {

    public static final String TAG = "AlumDirectoryFragment";


    /**
     * Keys allow storage and retrieval of data during Fragment lifecycle.
     */
    private static final String LIST_BROS = "LIST_BROS";
    private static final String LIST_ALUM = "LIST_ALUM";

    /**
     * Store entries independently of directory list. This allows us to dynamically pick which class
     * of entries to view (via ActionItems).
     */
    private ArrayList<Brother> mStudentList;
    private ArrayList<Brother> mAlumList;

    /**
     * Returns new instance of AlumDirectoryFragment with arguments set.
     *
     * @param context - context used to access String resources
     * @return primed instance of AlumDirectoryFragment
     */
    public static AlumDirectoryFragment newInstance(Context context) {
        AlumDirectoryFragment instance = new AlumDirectoryFragment();

        final String sheetKey = context.getString(R.string.alumni_directory_sheet_key);
        Bundle args = new Bundle();
        args.putString(SHEET_KEY, sheetKey);
        instance.setArguments(args);

        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        // If possible, retrieve saved data.
        if (savedInstanceState == null) {
            mAlumList = new ArrayList<Brother>();
            mStudentList = new ArrayList<Brother>();
        } else {
            mAlumList = savedInstanceState.getParcelableArrayList(LIST_ALUM);
            mStudentList = savedInstanceState.getParcelableArrayList(LIST_BROS);
        }

        super.init(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.directory, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_show_alumni:
                if (getAdapter() != null && getAdapter().getCount() != mAlumList.size()) {
                    setDirectoryList(mAlumList);
                    updateListView();
                }
                return true;

            case R.id.menu_show_brothers:
                if (getAdapter() != null && getAdapter().getCount() != mStudentList.size()) {
                    setDirectoryList(mStudentList);
                    updateListView();
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(LIST_BROS, mStudentList);
        outState.putParcelableArrayList(LIST_ALUM, mAlumList);
    }

    @Override
    protected void loadData(final boolean forceDownload) {
        // Populate list with alumni data and save a reference to it.
        new AsyncBrotherLoader() {
            @Override
            protected void onPostExecute(Brother[] result) {
                // Use dependency injection to save a reference to the alumni list.
                super.onPostExecute(result);
                mAlumList = getDirectoryList();
            }
        }.execute(getString(R.string.alumni_directory_sheet_key), "" + forceDownload);

        // Load and save brother and pledge data, too.
        mStudentList.clear();
        loadDataInBackground(getString(R.string.brother_directory_sheet_key), forceDownload);
        loadDataInBackground(getString(R.string.pledge_directory_sheet_key), forceDownload);
        Collections.sort(mStudentList);
    }

    /**
     * Asynchronously load directory data and save it to the secondary (student directory) list.
     *
     * @param sheetKey      for the data to download
     * @param forceDownload whether or not to force downloading over the Internet
     */
    private void loadDataInBackground(String sheetKey, boolean forceDownload) {
        // Use dependency injection to override default AsyncBrotherLoader behavior.
        new AsyncBrotherLoader() {
            @Override
            protected void onPostExecute(Brother[] result) {
                if (result == null) {
                    Toast.makeText(getActivity(), R.string.no_internet_toast_msg, Toast.LENGTH_LONG);
                } else {
                    mStudentList.addAll(Arrays.asList(result));
                    Collections.sort(mStudentList);
                }
            }
        }.execute(sheetKey, "" + forceDownload);
    }
}
