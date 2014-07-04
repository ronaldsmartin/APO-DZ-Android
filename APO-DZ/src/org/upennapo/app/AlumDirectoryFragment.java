package org.upennapo.app;

import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.ArrayList;


/**
 * An extension of DirectoryFragment that loads directory from all directory sources, using
 * ActionItems to toggle which source is displayed.
 *
 * @author Ronald Martin
 */
public class AlumDirectoryFragment extends DirectoryFragment {

    public static final String TAG = "AlumDirectoryFragment";

    private static final int[] MENU_IDs =
            {R.id.menu_about_app, R.id.menu_send_feedback, R.id.menu_switch_user};

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
     * @param c - context used to access String resources
     * @return primed instance of AlumDirectoryFragment
     */
    public static AlumDirectoryFragment newInstance(Context c) {
        AlumDirectoryFragment instance = new AlumDirectoryFragment();

        final String sheetKey = c.getString(R.string.alumni_directory_sheet_key);
        Bundle args = new Bundle();
        args.putString(SHEET_KEY, sheetKey);
        instance.setArguments(args);

        return instance;
    }

    /**
     * Concatenate two arrays, treating null as the empty array.
     *
     * @param arr1 first array of items to put in the new array
     * @param arr2 second array of items to put in the new array
     * @return the concatenation arr1 + arr2
     */
    private static Brother[] mergeArrays(Brother[] arr1, Brother[] arr2) {
        if (arr1 == null && arr2 == null) return null;
        else if (arr1 == null) return arr2;
        else if (arr2 == null) return arr1;
        else {
            Brother[] merge = new Brother[arr1.length + arr2.length];
            System.arraycopy(arr1, 0, merge, 0, arr1.length);
            System.arraycopy(arr2, 0, merge, arr1.length, arr2.length);

            return merge;
        }
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

        // Hide all menu items except the directory options.
        for (int id : MENU_IDs) {
            menu.findItem(id).setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_NEVER);
        }
        inflater.inflate(R.menu.directory, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_show_alumni:
                if (getAdapter().getCount() != mAlumList.size()) {
                    setDirectoryList(mAlumList);
                    updateListView();
                }
                return true;

            case R.id.menu_show_brothers:
                if (getAdapter().getCount() != mStudentList.size()) {
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
        getActivity().setProgressBarIndeterminateVisibility(!forceDownload);

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
                for (Brother brother : result)
                    mStudentList.add(brother);
            }
        }.execute(sheetKey, "" + forceDownload);
    }
}
