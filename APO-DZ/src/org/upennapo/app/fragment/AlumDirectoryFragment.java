package org.upennapo.app.fragment;

import android.app.ActionBar;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.melnykov.fab.FloatingActionButton;

import org.upennapo.app.R;
import org.upennapo.app.model.Brother;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * An extension of {@link DirectoryFragment} that loads directory from all directory sources, using
 * a FloatingActionButton to toggle which source is displayed.
 *
 * @author Ronald Martin
 */
public class AlumDirectoryFragment extends DirectoryFragment {

    //region Constants
    /* Tag for debugging */
    public static final String TAG = "AlumDirectoryFragment";

    /* Keys allow storage and retrieval of data during Fragment lifecycle. */
    private static final String LIST_BROS = "LIST_BROS";
    private static final String LIST_ALUM = "LIST_ALUM";
    //endregion

    /* Store entries independently of directory list to allow toggling by user. */
    private ArrayList<Brother> mStudentList, mAlumList;

    //region Fragment lifecycle

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

        // If possible, retrieve saved data. Otherwise, initialize new lists.
        if (savedInstanceState == null) {
            mAlumList = new ArrayList<Brother>();
            mStudentList = new ArrayList<Brother>();
        } else {
            mAlumList = savedInstanceState.getParcelableArrayList(LIST_ALUM);
            mStudentList = savedInstanceState.getParcelableArrayList(LIST_BROS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alumni_directory, container, false);
        init(savedInstanceState, view);
        return view;
    }

    @Override
    protected void init(Bundle savedInstanceState, View view) {
        super.init(savedInstanceState, view);
        ButterKnife.inject(this, view);

        FloatingActionButton floatingActionButton =
                ButterKnife.findById(view, R.id.button_floating_action);
        floatingActionButton.attachToListView(getListView());

        getActionBar().setSubtitle(R.string.alumni);
    }

    @Override
    public void onResume() {
        super.onResume();

        // Set the appropriate ActionBar subtitle text.
        if (isShowingAlumDirectory()) getActionBar().setSubtitle(R.string.alumni);
        else if (isShowingStudentDirectory()) getActionBar().setSubtitle(R.string.students);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(LIST_BROS, mStudentList);
        outState.putParcelableArrayList(LIST_ALUM, mAlumList);
    }

    @Override
    public void onStop() {
        super.onStop();
        getActionBar().setSubtitle(null);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
    //endregion

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
                if (getActivity() != null && result == null) {
                    Toast.makeText(getActivity(),
                            R.string.no_internet_toast_msg,
                            Toast.LENGTH_LONG)
                            .show();
                } else if (result != null) {
                    mStudentList.addAll(Arrays.asList(result));
                    Collections.sort(mStudentList);
                }
            }
        }.execute(sheetKey, "" + forceDownload);
    }

    protected ActionBar getActionBar() {
        return getActivity().getActionBar();
    }

    @Override
    protected void updateListView() {
        super.updateListView();
        View view = getView();
        if (view != null) {
            View content = view.findViewById(R.id.content);
            if (content.getVisibility() == View.GONE) {
                getProgressBar().setVisibility(View.GONE);
                content.setVisibility(View.VISIBLE);
            }
        }
    }

    private boolean isShowingAlumDirectory() {
        return getAdapter() != null && mAlumList != null
                && getAdapter().getCount() == mAlumList.size();
    }

    private boolean isShowingStudentDirectory() {
        return getAdapter() != null && mStudentList != null
                && getAdapter().getCount() == mStudentList.size();
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.button_floating_action)
    /**
     * Switch which directory the adapter is currently showing.
     */
    public void toggleDirectoryList() {
        if (isShowingAlumDirectory()) {
            setDirectoryList(mStudentList);
            getActionBar().setSubtitle(R.string.students);
        } else if (isShowingStudentDirectory()) {
            setDirectoryList(mAlumList);
            getActionBar().setSubtitle(R.string.alumni);
        }
        updateListView();
    }
}
