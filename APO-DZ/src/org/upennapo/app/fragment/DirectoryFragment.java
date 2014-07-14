package org.upennapo.app.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.upennapo.app.R;
import org.upennapo.app.activity.DirectoryActivity;
import org.upennapo.app.activity.DirectoryDetailsActivity;
import org.upennapo.app.adapter.AlphabeticalAdapter;
import org.upennapo.app.model.Brother;
import org.upennapo.app.model.DataManager;

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
    private View mProgressBar, mReloadButton;


    /**
     * Empty constructor for Fragment intialization
     */
    public DirectoryFragment() {
    }

    /**
     * Returns new instance of DirectoryFragment with arguments set to load alum
     * directory.
     *
     * @param context used to access String resources
     * @return primed instance of DirectoryFragment
     */
    public static DirectoryFragment newAlumDirectoryInstance(Context context) {
        DirectoryFragment instance = new DirectoryFragment();

        Bundle args = new Bundle();
        args.putString(DirectoryFragment.SHEET_KEY,
                context.getString(R.string.alumni_directory_sheet_key));
        instance.setArguments(args);

        return instance;
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

        Bundle args = new Bundle();
        args.putString(DirectoryFragment.SHEET_KEY,
                context.getString(R.string.brother_directory_sheet_key));
        instance.setArguments(args);

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

        Bundle args = new Bundle();
        args.putString(DirectoryFragment.SHEET_KEY,
                context.getString(R.string.pledge_directory_sheet_key));
        instance.setArguments(args);

        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        // Retrieve the arguments passed by the MainActivity
        this.mSheetKey = getArguments().getString(SHEET_KEY);

        // Show Alum option in ActionBar on Brother tab only
        if (getString(R.string.brother_directory_sheet_key).equals(this.mSheetKey))
            setHasOptionsMenu(true);

        // If possible, retrieve saved data. Otherwise, initialize new list.
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
        final View view = inflater.inflate(R.layout.fragment_directory, container, false);

        init(savedInstanceState, view);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.directory, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_show_alumni:
                showAlumDirectory();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showAlumDirectory() {
        Intent intent = new Intent(getActivity(), DirectoryActivity.class);
        intent.putExtra(SHEET_KEY, getString(R.string.alumni_directory_sheet_key));
        startActivity(intent);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(LIST_KEY, mDirectoryList);
    }

    protected void init(Bundle savedInstanceState, View view) {
        // Attach ProgressBar and Button
        mProgressBar = view.findViewById(R.id.directory_progress_bar);
        mReloadButton = view.findViewById(R.id.directory_reload_button);

        // Set up PullToRefresh.
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_layout);
        mSwipeRefreshLayout.setColorScheme(
                R.color.apo_blue, R.color.apo_yellow, R.color.apo_blue, R.color.apo_yellow);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        // Set up ListView
        mListView = (ListView) view.findViewById(R.id.name_list);
        mListView.setItemsCanFocus(false);
        mListView.setOnItemClickListener(onItemClickListener());

        // Retrieve directory data from internet or memory.
        if (savedInstanceState == null) {
            loadData(false);
            mReloadButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    loadData(false);
                    mReloadButton.setVisibility(View.GONE);
                    mProgressBar.setVisibility(View.VISIBLE);
                }
            });
        }
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
        // Additionally, we need to toggle FastScroll so that the index will redraw.
        if (getActivity() != null) {
            mAdapter = new AlphabeticalAdapter(getActivity(), mDirectoryList);
            mListView.setFastScrollEnabled(false);


            mListView.setAdapter(mAdapter);

            mListView.setFastScrollEnabled(true);

            if (mReloadButton.getVisibility() == View.VISIBLE)
                mReloadButton.setVisibility(View.GONE);

            if (mSwipeRefreshLayout.getVisibility() == View.GONE) {
                mProgressBar.setVisibility(View.GONE);
                mSwipeRefreshLayout.setVisibility(View.VISIBLE);
            }
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

    public View getProgressBar() {
        return mProgressBar;
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
                Toast.makeText(getActivity(),
                        "Something went wrong. Try refreshing.",
                        Toast.LENGTH_SHORT)
                        .show();

                if (mSwipeRefreshLayout.getVisibility() == View.GONE) {
                    mProgressBar.setVisibility(View.GONE);
                    mReloadButton.setVisibility(View.VISIBLE);
                }
            } else {
                Arrays.sort(result);
                mDirectoryList.clear();
                mDirectoryList.addAll(Arrays.asList(result));
                updateListView();
            }
        }
    }
}

