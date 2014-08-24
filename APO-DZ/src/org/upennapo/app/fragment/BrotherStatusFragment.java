package org.upennapo.app.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.upennapo.app.R;
import org.upennapo.app.activity.LoginActivity;
import org.upennapo.app.model.DataManager;
import org.upennapo.app.model.User;


public class BrotherStatusFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener,
        View.OnClickListener {

    //region Constants
    public static final String STORAGE_KEY = "BROTHER_STATUS";
    public static final String LAST_UPDATED = "BROTHER_STATUS_LAST_UPDATED";
    public static final String ROW_KEY = "USER_ROW";
    private static final String URL_KEY = "SPREADSHEET_URL";
    private static final String USER_KEY = "USER";
    private static final String FIRST_NAME_KEY = "FIRST_NAME";
    private static final String LAST_NAME_KEY = "LAST_NAME";
    private static final String TAG_FAILED_SEARCH = "SEARCH_FAILED";
    //endregion

    /**
     * Flag is set in BrotherLoader when the result is null. This is used to ensure the user is only
     * notified of the failure once.
     */
    private boolean mFlagFailedSearch = false;
    // Brother to Display
    private User mUser;
    private String firstName, lastName, spreadsheetUrl;

    /**
     * The {@link android.support.v4.widget.SwipeRefreshLayout} that detects swipe gestures and
     * triggers callbacks in the app.
     */
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private View mProgressBar;

    public static BrotherStatusFragment newInstance(Context context) {
        BrotherStatusFragment instance = new BrotherStatusFragment();

        Bundle broStatusArgs = new Bundle();
        broStatusArgs.putString(BrotherStatusFragment.URL_KEY,
                context.getString(R.string.spreadsheet_url));

        instance.setArguments(broStatusArgs);
        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if (savedInstanceState != null) {
            mUser = savedInstanceState.getParcelable(USER_KEY);
            firstName = savedInstanceState.getString(FIRST_NAME_KEY);
            lastName = savedInstanceState.getString(LAST_NAME_KEY);
            spreadsheetUrl = savedInstanceState.getString(URL_KEY);
            mFlagFailedSearch = savedInstanceState.getBoolean(TAG_FAILED_SEARCH);
            updateViews();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_brother_status, container, false);

        // Attach ProgressBar
        this.mProgressBar = view.findViewById(R.id.brother_status_progress_bar);

        // Set up SwipeToRefresh
        this.mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_layout);
        this.mSwipeRefreshLayout.setColorSchemeResources(
                R.color.apo_blue, R.color.apo_yellow, R.color.apo_blue, R.color.apo_yellow);
        this.mSwipeRefreshLayout.setOnRefreshListener(this);

        // Attach buttons
        view.findViewById(R.id.btn_show_service_details).setOnClickListener(this);
        view.findViewById(R.id.btn_show_membership_details).setOnClickListener(this);
        view.findViewById(R.id.btn_show_fellowship_details).setOnClickListener(this);

        init(savedInstanceState, view);

        return view;
    }

    @Override
    public void onRefresh() {
        mSwipeRefreshLayout.setRefreshing(true);
        AsyncUserDataLoader loader = new AsyncUserDataLoader();
        loader.execute(spreadsheetUrl, firstName, lastName, "true");
    }

    private void init(Bundle savedInstanceState, View view) {
        if (savedInstanceState == null) {
            // Automatically fail the search for alumni.
            this.mFlagFailedSearch = LoginActivity.isAlumLoggedIn(getActivity())
                    || getActivity()
                    .getSharedPreferences(getString(R.string.app_global_storage_key), Context.MODE_PRIVATE)
                    .getBoolean(TAG_FAILED_SEARCH, false);
            if (mFlagFailedSearch) {
                mProgressBar.setVisibility(View.GONE);
                view.findViewById(R.id.status_fail_txt).setVisibility(View.VISIBLE);
            } else if (DataManager.isNetworkAvailable(getActivity())) {
                SharedPreferences prefs = getActivity().getSharedPreferences(getString(R.string.app_global_storage_key), Context.MODE_PRIVATE);
                this.firstName = prefs.getString(LoginActivity.USER_FIRSTNAME_KEY, "");
                this.lastName = prefs.getString(LoginActivity.USER_LASTNAME_KEY, "");
                this.spreadsheetUrl = getArguments().getString(URL_KEY);

                getUserData();
            } else {
                // Notify the user that there is no connection. Tell them to try later.
                Toast noConnectionToast = Toast.makeText(getActivity(),
                        R.string.no_internet_toast_msg,
                        Toast.LENGTH_LONG);
                noConnectionToast.show();

                // Setting the SwipeRefresh visibility to invisible allows users to access its
                // functionality without displaying its contents.
                this.mSwipeRefreshLayout.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(USER_KEY, mUser);
        outState.putString(FIRST_NAME_KEY, firstName);
        outState.putString(LAST_NAME_KEY, lastName);
        outState.putString(URL_KEY, spreadsheetUrl);
        outState.putBoolean(TAG_FAILED_SEARCH, mFlagFailedSearch);

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onClick(View view) {
        int sheetId = 0;
        switch (view.getId()) {
            case R.id.btn_show_service_details:
                sheetId = getResources().getInteger(R.integer.spreadsheet_sheet_num_service);
                break;

            case R.id.btn_show_membership_details:
                sheetId = getResources().getInteger(R.integer.spreadsheet_sheet_num_membership);
                break;

            case R.id.btn_show_fellowship_details:
                sheetId = getResources().getInteger(R.integer.spreadsheet_sheet_num_fellowship);
                break;
        }
        openUserSpreadsheetRow(sheetId);
    }

    /**
     * Open the spreadsheet web page for the sheet with sheetId at the current user's row.
     *
     * @param sheetId gid for the sheet to open on the brotherhood spreadsheet
     */
    private void openUserSpreadsheetRow(int sheetId) {
        final String url = DataManager.userSpreadsheetRowUrl(getActivity(), sheetId);
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
    }

    /**
     * Asynchronously load User data.
     */
    private void getUserData() {
        AsyncUserDataLoader loader = new AsyncUserDataLoader();
        loader.execute(spreadsheetUrl, firstName, lastName, "false");
    }

    /**
     * Update the view based on the result of the brother query.
     */
    private void updateViews() {
        if (this.mUser == null) {
            if (!mFlagFailedSearch) {
                // Notify the mUser and do not update all views if the mUser wasn't on the spreadsheet.
                AlertDialog.Builder nullUserAlert = new AlertDialog.Builder(getActivity());
                nullUserAlert.setTitle(R.string.dialog_user_not_found_title);
                nullUserAlert.setMessage(getString(R.string.dialog_user_not_found_msg));
                nullUserAlert.show();

                // Set that we have informed the user of the failure.
                mFlagFailedSearch = true;
            }

            TextView nameLabel = (TextView) getActivity().findViewById(R.id.name_label);
            nameLabel.setText(R.string.label_no_user_status);
            getActivity().findViewById(R.id.status_fail_txt).setVisibility(View.VISIBLE);
        } else {
            updateStatusLabels();
            updateServiceLabels();
            updateMembershipLabels();
            updateFellowshipLabels();
            mFlagFailedSearch = false;
            mSwipeRefreshLayout.setVisibility(View.VISIBLE);
        }
        storeUserSearchFail();
        mProgressBar.setVisibility(View.GONE);
        mSwipeRefreshLayout.setRefreshing(false);
    }

    /**
     * Remembers in SharedPreferences whether there is no spreadsheet data for the person with the
     * currently logged-in (user)name.
     */
    private void storeUserSearchFail() {
        if (getActivity() != null) {
            SharedPreferences.Editor editor = getActivity()
                    .getSharedPreferences(getString(R.string.app_global_storage_key), Context.MODE_PRIVATE)
                    .edit();
            editor.putBoolean(TAG_FAILED_SEARCH, mFlagFailedSearch);
            editor.apply();
        }
    }

    /**
     * Update labels associated with the mUser's meta-status.
     */
    private void updateStatusLabels() {
        TextView nameLabel = (TextView) getActivity().findViewById(R.id.name_label);
        nameLabel.setText(this.mUser.First_Name + " " + this.mUser.Last_Name);

        TextView statusLabel = (TextView) getActivity().findViewById(R.id.status);
        statusLabel.setText(this.mUser.Status);

        updateStatusText(R.id.all_reqs_status, this.mUser.Complete);
    }

    /**
     * Update labels associated with the mUser's service status.
     */
    private void updateServiceLabels() {
        updateStatusText(R.id.service_status, this.mUser.Service);

        TextView serviceHoursLabel = (TextView) getActivity().findViewById(R.id.hours_label);
        serviceHoursLabel.setText(this.mUser.Service_Hours + " of " + this.mUser.Required_Service_Hours);

        updateStatusText(R.id.large_group_status, this.mUser.Large_Group_Project);

        updateStatusText(R.id.publicity_status, this.mUser.Publicity);

        updateStatusText(R.id.service_hosting_status, this.mUser.Service_Hosting);
    }

    /**
     * Update labels associated with the mUser's membership status.
     */
    private void updateMembershipLabels() {
        updateStatusText(R.id.membership_status, this.mUser.Membership);

        TextView membershipPointsLabel = (TextView) getActivity().findViewById(R.id.membership_points);
        membershipPointsLabel.setText(this.mUser.Membership_Points + " of " + this.mUser.Required_Membership_Points);

        updateStatusText(R.id.brother_comp, this.mUser.Brother_Comp);

        updateStatusText(R.id.pledge_comp, this.mUser.Pledge_Comp);

        updateStatusText(R.id.membership_hosting,
                this.mUser.Service_Hosting && this.mUser.Fellowship_Hosting);
    }

    /**
     * Update labels associated with the mUser's Fellowship status.
     */
    private void updateFellowshipLabels() {
        updateStatusText(R.id.fellowship_status, this.mUser.Fellowship);

        TextView fellowshipPointsLabel = (TextView) getActivity().findViewById(R.id.fellowship_points);
        fellowshipPointsLabel.setText(this.mUser.Fellowship_Points + " of " + this.mUser.Required_Fellowship);

        updateStatusText(R.id.fellowship_hosting, this.mUser.Fellowship_Hosting);
    }

    /**
     * Set a TextView's text and color based on a status.
     *
     * @param viewId for the TextView to update
     * @param status that determines how to update the TextView
     */
    private void updateStatusText(int viewId, boolean status) {
        TextView textView = (TextView) getActivity().findViewById(viewId);
        if (status) {
            textView.setText(R.string.req_complete);
            textView.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        } else {
            textView.setText(R.string.req_incomplete);
        }
    }

    private class AsyncUserDataLoader extends AsyncTask<String, Void, User> {

        @Override
        protected User doInBackground(String... params) {
            return DataManager.getBrotherData(params[0], params[1], params[2],
                    getActivity(), "true".equalsIgnoreCase(params[3]));
        }

        @Override
        protected void onPostExecute(User result) {
            super.onPostExecute(result);

            // Update only if our context still exists.
            if (getActivity() != null) {
                BrotherStatusFragment.this.mUser = result;
                updateViews();
            }
        }
    }
}
