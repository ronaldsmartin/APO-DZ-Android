package org.upennapo.app;

import android.app.Activity;
import android.content.Context;
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

    /**
     * Specifies the directory type {Brother, Pledge}. Used to store and retrieve directory data.
     */
    private String mSheetKey;

    /**
     * Directory data
     */
    private ArrayList<Brother> mDirectoryList;

    private AlphabeticalAdapter mAdapter;
    private ListView mListView;
    private SwipeRefreshLayout mSwipeRefreshLayout;


    /**
     * Empty constructor for Fragment intialization
     */
    public DirectoryFragment() {
    }

    /**
     * Returns new instance of DirectoryFragment with arguments set to load Active Brother
     * directory.
     *
     * @param context used to access String resources
     * @return primed instance of DirectoryFragment
     */
    public static DirectoryFragment newBrotherDirectoryInstance(Context context) {
        DirectoryFragment instance = new DirectoryFragment();

        Bundle broStatusArgs = new Bundle();
        broStatusArgs.putString(DirectoryFragment.SHEET_KEY,
                context.getString(R.string.brother_directory_sheet_key));
        instance.setArguments(broStatusArgs);

        return instance;
    }

    /**
     * Returns new instance of DirectoryFragment with arguments set to load pledge directory.
     *
     * @param context used to access String resources
     * @return primed instance of DirectoryFragment
     */
    public static DirectoryFragment newPledgeDirectoryInstance(Context context) {
        DirectoryFragment instance = new DirectoryFragment();

        Bundle broStatusArgs = new Bundle();
        broStatusArgs.putString(DirectoryFragment.SHEET_KEY,
                context.getString(R.string.pledge_directory_sheet_key));
        instance.setArguments(broStatusArgs);

        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        // Retrieve the arguments passed by the MainActivity
        this.mSheetKey = getArguments().getString(SHEET_KEY);

        // Prepare Adapter
        if (savedInstanceState == null) {
            this.mDirectoryList = new ArrayList<Brother>();
        } else {
            this.mDirectoryList = savedInstanceState.getParcelableArrayList(LIST_KEY);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the View
        final View mView = inflater.inflate(R.layout.fragment_directory, container, false);

        // Set up PullToRefresh.
        mSwipeRefreshLayout = (SwipeRefreshLayout) mView.findViewById(R.id.swipe_layout);
        mSwipeRefreshLayout.setColorScheme(
                R.color.apo_blue, R.color.apo_yellow, R.color.apo_blue, R.color.apo_yellow);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        // Set up ListView
        mListView = (ListView) mView.findViewById(R.id.name_list);
        mListView.setItemsCanFocus(false);
        mListView.setOnItemClickListener(onItemClickListener());

        init(savedInstanceState);

        return mView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        activity.setProgressBarIndeterminateVisibility(true);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(LIST_KEY, mDirectoryList);
    }

    protected void init(Bundle savedInstanceState) {
        // Retrieve directory data from internet or memory.
        if (savedInstanceState == null) {
            if (DataManager.isNetworkAvailable(getActivity())) {
                // Make an asynchronous request for the JSON using the URL
                loadData(false);
            } else {
                // Notify the user that there is no connection. Tell them to try later.
                Toast noConnectionToast = Toast.makeText(getActivity(),
                        R.string.no_internet_toast_msg,
                        Toast.LENGTH_LONG);
                noConnectionToast.show();
            }
        }
        updateListView();
    }

    private AdapterView.OnItemClickListener onItemClickListener() {
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Parcel the brother at this index to the details view.
                Intent detailPage = new Intent(getActivity(), DirectoryDetailsActivity.class);
                detailPage.putExtra(DirectoryDetailsActivity.TAG_BROTHER_DATA, mDirectoryList.get(position));
                getActivity().startActivity(detailPage);
            }
        };
    }

    protected void updateListView() {
        // We create a new adapter so that it can reinitialize the fastScroll section index.
        if (getActivity() != null) {
            mAdapter = new AlphabeticalAdapter(getActivity(), mDirectoryList);
            mListView.setAdapter(mAdapter);
            getActivity().setProgressBarIndeterminateVisibility(false);
        }

        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onRefresh() {
        // Make an asynchronous request for the JSON using the URL
        mSwipeRefreshLayout.setRefreshing(true);
        loadData(true);
    }

    /**
     * Asynchronously load directory data and update the ListView.
     *
     * @param forceDownload whether or not to load data from the Internet.
     */
    protected void loadData(boolean forceDownload) {
        getActivity().setProgressBarIndeterminateVisibility(!forceDownload);
        new AsyncBrotherLoader().execute(mSheetKey, "" + forceDownload);
    }

    /**
     * Getters & Setters *
     */

    public ArrayList<Brother> getDirectoryList() {
        return mDirectoryList;
    }

    public void setDirectoryList(ArrayList<Brother> directoryList) {
        this.mDirectoryList = directoryList;
    }

    public AlphabeticalAdapter getAdapter() {
        return mAdapter;
    }

    /**
     * AsyncTask used to load directory data and populate the ListView.
     * Params are Strings, unfortunately.
     */
    protected class AsyncBrotherLoader extends AsyncTask<String, Void, Brother[]> {

        @Override
        protected Brother[] doInBackground(String... params) {
            return DataManager.getDirectoryData(params[0], getActivity(), "true".equals(params[1]));
        }

        @Override
        protected void onPostExecute(Brother[] result) {
            super.onPostExecute(result);

            // Short circuit if our context is gone.
            if (getActivity() == null) return;

            if (result == null) {
                // If there is an error getting the result, display an alert.
                Toast failureAlert = Toast.makeText(getActivity(),
                        "Something went wrong. Try refreshing.", Toast.LENGTH_LONG);
                failureAlert.show();
            } else {
                Arrays.sort(result);
                mDirectoryList.clear();
                mDirectoryList.addAll(Arrays.asList(result));
                updateListView();
            }
        }
    }
}

