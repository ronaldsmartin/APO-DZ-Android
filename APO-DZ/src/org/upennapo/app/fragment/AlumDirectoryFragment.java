package org.upennapo.app.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
public class AlumDirectoryFragment extends DirectoryFragment implements View.OnClickListener {

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
    private ArrayList<Brother> mStudentList, mAlumList;

    private Button mShowAlumBtn, mShowStudentBtn;

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
        setHasOptionsMenu(false);

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
        mShowAlumBtn = (Button) view.findViewById(R.id.btn_show_alum);
        mShowStudentBtn = (Button) view.findViewById(R.id.btn_show_students);

        mShowAlumBtn.setOnClickListener(this);
        mShowStudentBtn.setOnClickListener(this);
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
                if (getActivity() != null && result == null) {
                    Toast.makeText(getActivity(),
                            R.string.no_internet_toast_msg,
                            Toast.LENGTH_LONG)
                            .show();
                } else {
                    mStudentList.addAll(Arrays.asList(result));
                    Collections.sort(mStudentList);
                }
            }
        }.execute(sheetKey, "" + forceDownload);
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_show_alum:
                if (getAdapter() != null && getAdapter().getCount() != mAlumList.size()) {
                    setDirectoryList(mAlumList);
                    updateListView();
                    mShowAlumBtn.setTextColor(getResources().getColor(R.color.accent_fallback_light));
                    mShowStudentBtn.setTextColor(getResources().getColor(android.R.color.white));
                }
                break;
            case R.id.btn_show_students:
                if (getAdapter() != null && getAdapter().getCount() != mStudentList.size()) {
                    setDirectoryList(mStudentList);
                    updateListView();
                    mShowStudentBtn.setTextColor(getResources().getColor(R.color.accent_fallback_light));
                    mShowAlumBtn.setTextColor(getResources().getColor(android.R.color.white));
                }
                break;
        }
    }
}
