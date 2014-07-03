package org.upennapo.app;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

public class DirectoryFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    public static final String SHEET_KEY = "SHEET_KEY";
    private static final String LIST_KEY = "DIRECTORY_LIST";

    protected ArrayList<Brother> mBrothers;
    private String mScriptUrl;
    private String mSheetKey;

    private View mView;
    private SwipeRefreshLayout mSwipeRefreshLayout;


    public DirectoryFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retrieve the arguments passed by the MainActivity
        this.mSheetKey = getArguments().getString(SHEET_KEY);
        this.mScriptUrl = getString(R.string.directory_script) + mSheetKey;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the View
        mView = inflater.inflate(R.layout.fragment_directory, container, false);

        init(savedInstanceState);

        return mView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(LIST_KEY, mBrothers);
        super.onSaveInstanceState(outState);
    }

    protected void init(Bundle savedInstanceState) {
        // Set up PullToRefresh.
        this.mSwipeRefreshLayout = (SwipeRefreshLayout) mView.findViewById(R.id.swipe_layout);
        this.mSwipeRefreshLayout.setColorScheme(
                R.color.apo_blue, R.color.apo_yellow, R.color.apo_blue, R.color.apo_yellow);
        this.mSwipeRefreshLayout.setOnRefreshListener(this);

        // Retrieve directory data from internet, memory, or Bundle.
        if (savedInstanceState == null) {
            if (DataManager.isNetworkAvailable(getActivity())) {
                // Make an asynchronous request for the JSON using the URL
                new AsyncBrotherLoader().execute(mScriptUrl, mSheetKey, "false");
            } else {
                // Notify the user that there is no connection. Tell them to try later.
                Toast noConnectionToast = Toast.makeText(getActivity(),
                        R.string.no_internet_toast_msg,
                        Toast.LENGTH_LONG);
                noConnectionToast.show();
            }
        } else {
            mBrothers = savedInstanceState.getParcelableArrayList(LIST_KEY);
            updateListView();
        }
    }

    /**
     * Show the ActionBar progress bar.
     */
    private void showProgressBar() {
        getActivity().setProgressBarVisibility(true);
    }

    /**
     * Set the progress value of the ActionBar progress bar.
     *
     * @param value of progress in range [0, 10000]
     */
    private void updateProgressValue(int value) {
        getActivity().setProgress(value);
    }


    /**
     * Hide the ActionBar progress bar.
     */
    private void hideProgressBar() {
        getActivity().setProgressBarVisibility(false);
    }

    protected AdapterView.OnItemClickListener onItemClickListener() {
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Parcel the brother at this index to the details view.
                Intent detailPage = new Intent(getActivity(), DirectoryDetailsActivity.class);
                detailPage.putExtra(DirectoryDetailsActivity.TAG_BROTHER_DATA, mBrothers.get(position));
                getActivity().startActivity(detailPage);
            }
        };
    }

    private void updateListView() {
        AlphabeticalAdapter adapter =
                new AlphabeticalAdapter(getActivity(), mBrothers);
        final ListView list = (ListView) mView.findViewById(R.id.name_list);
        list.setAdapter(adapter);
        list.setItemsCanFocus(false);
        list.setOnItemClickListener(onItemClickListener());
    }

    @Override
    public void onRefresh() {
        // Make an asynchronous request for the JSON using the URL
        mSwipeRefreshLayout.setRefreshing(true);
        new AsyncBrotherLoader().execute(mScriptUrl, mSheetKey, "true");
    }

    private class AsyncBrotherLoader extends AsyncTask<String, Void, Brother[]> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Activate the progress view in the action bar.
            // Progress bar code is due to http://guides.thecodepath.com/android/Handling-ProgressBars
            showProgressBar();

            updateProgressValue(1000);
        }

        @Override
        protected Brother[] doInBackground(String... params) {
            return DataManager.getDirectoryData(params[0], params[1],
                    getActivity(), "true".equals(params[2]));
        }

        @Override
        protected void onPostExecute(Brother[] result) {
            if (result == null) {
                // If there is an error getting the result, display an alert.
                Toast failureAlert = Toast.makeText(getActivity(),
                        "Unable to load at this time.", Toast.LENGTH_LONG);
                failureAlert.show();
            } else {
                Arrays.sort(result);
                mBrothers = new ArrayList<Brother>(Arrays.asList(result));
                updateListView();
            }
            updateProgressValue(10000);
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }
}

