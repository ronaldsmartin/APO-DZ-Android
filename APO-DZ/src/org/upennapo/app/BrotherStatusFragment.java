package org.upennapo.app;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


public class BrotherStatusFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    // Constants
    public static final String STORAGE_KEY = "BROTHER_STATUS";
    public static final String URL_KEY = "SPREADSHEET_URL";
    private static final String USER_KEY = "USER";
    private static final String FIRST_NAME_KEY = "FIRST_NAME";
    private static final String LAST_NAME_KEY = "LAST_NAME";
    private static final String TAG_FAILED_SEARCH = "SEARCH_FAILED";
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

    public static BrotherStatusFragment newInstance(Context context) {
        BrotherStatusFragment instance = new BrotherStatusFragment();

        Bundle broStatusArgs = new Bundle();
        broStatusArgs.putString(BrotherStatusFragment.URL_KEY,
                context.getString(R.string.spreadsheet_url));

        instance.setArguments(broStatusArgs);
        return instance;
    }

    /**
     * Get the appropriate message resource ID for a completion status
     *
     * @param complete - the completion status to get the message for
     * @return the completion status message for this status
     */
    private static int completionStatusStringId(boolean complete) {
        if (complete)
            return R.string.req_complete;
        else return R.string.req_incomplete;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if (savedInstanceState != null)
            updateViews();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_brother_status, container, false);

        // Set up SwipeToRefresh
        this.mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_layout);
        this.mSwipeRefreshLayout.setColorScheme(
                R.color.apo_blue, R.color.apo_yellow, R.color.apo_blue, R.color.apo_yellow);
        this.mSwipeRefreshLayout.setOnRefreshListener(this);

        init(savedInstanceState);

        return view;
    }

    @Override
    public void onRefresh() {
        mSwipeRefreshLayout.setRefreshing(true);
        AsyncUserDataLoader loader = new AsyncUserDataLoader();
        loader.execute(spreadsheetUrl, firstName, lastName, "true");
    }

    private void init(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            if (DataManager.isNetworkAvailable(getActivity())) {
                SharedPreferences prefs = getActivity().getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
                this.firstName = prefs.getString(LoginActivity.USER_FIRSTNAME_KEY, "");
                this.lastName = prefs.getString(LoginActivity.USER_LASTNAME_KEY, "");
                this.spreadsheetUrl = getArguments().getString(URL_KEY);

                getActivity().setProgressBarIndeterminateVisibility(true);
                getUserData();
            } else {
                // Notify the user that there is no connection. Tell them to try later.
                Toast noConnectionToast = Toast.makeText(getActivity(),
                        R.string.no_internet_toast_msg,
                        Toast.LENGTH_LONG);
                noConnectionToast.show();

                getActivity().setProgressBarIndeterminateVisibility(false);
            }
        } else {
            mUser = savedInstanceState.getParcelable(USER_KEY);
            firstName = savedInstanceState.getString(FIRST_NAME_KEY);
            lastName = savedInstanceState.getString(LAST_NAME_KEY);
            spreadsheetUrl = savedInstanceState.getString(URL_KEY);
            mFlagFailedSearch = savedInstanceState.getBoolean(TAG_FAILED_SEARCH);
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

    /**
     * Asynchronously load User data.
     */
    private void getUserData() {
        AsyncUserDataLoader loader = new AsyncUserDataLoader();
        loader.execute(spreadsheetUrl, firstName, lastName, "false");
    }

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
        } else {
            updateStatusLabels();
            updateServiceLabels();
            updateMembershipLabels();
            updateFellowshipLabels();
            mFlagFailedSearch = false;
        }
        mSwipeRefreshLayout.setRefreshing(false);
        getActivity().setProgressBarIndeterminateVisibility(false);
    }

    /**
     * Update labels associated with the mUser's meta-status.
     */
    private void updateStatusLabels() {
        TextView nameLabel = (TextView) getActivity().findViewById(R.id.name_label);
        nameLabel.setText(this.mUser.First_and_Last_Name);

        TextView statusLabel = (TextView) getActivity().findViewById(R.id.status);
        statusLabel.setText(this.mUser.Status);

        TextView allReqStatusLabel = (TextView) getActivity().findViewById(R.id.all_reqs_status);
        allReqStatusLabel.setText(completionStatusStringId(this.mUser.Complete));
    }

    /**
     * Update labels associated with the mUser's service status.
     */
    private void updateServiceLabels() {
        TextView serviceStatusLabel = (TextView) getActivity().findViewById(R.id.service_status);
        serviceStatusLabel.setText(completionStatusStringId(this.mUser.Service));

        TextView serviceHoursLabel = (TextView) getActivity().findViewById(R.id.hours_label);
        serviceHoursLabel.setText(this.mUser.Service_Hours + " of " + this.mUser.Required_Service_Hours);

        TextView largeGroupStatusLabel = (TextView) getActivity().findViewById(R.id.large_group_status);
        largeGroupStatusLabel.setText(completionStatusStringId(this.mUser.Large_Group_Project));

        TextView publicityStatusLabel = (TextView) getActivity().findViewById(R.id.publicity_status);
        publicityStatusLabel.setText(completionStatusStringId(this.mUser.Publicity));

        TextView serviceHostingLabel = (TextView) getActivity().findViewById(R.id.service_hosting_status);
        serviceHostingLabel.setText(completionStatusStringId(this.mUser.Service_Hosting));
    }

    /**
     * Update labels associated with the mUser's membership status.
     */
    private void updateMembershipLabels() {
        TextView membershipStatusLabel = (TextView) getActivity().findViewById(R.id.membership_status);
        membershipStatusLabel.setText(completionStatusStringId(this.mUser.Membership));

        TextView membershipPointsLabel = (TextView) getActivity().findViewById(R.id.membership_points);
        membershipPointsLabel.setText(this.mUser.Membership_Points + " of " + this.mUser.Required_Membership_Points);

        TextView brotherCompLabel = (TextView) getActivity().findViewById(R.id.brother_comp);
        brotherCompLabel.setText(completionStatusStringId(this.mUser.Brother_Comp));

        TextView pledgeCompLabel = (TextView) getActivity().findViewById(R.id.pledge_comp);
        pledgeCompLabel.setText(completionStatusStringId(this.mUser.Pledge_Comp));

        TextView allHostingLabel = (TextView) getActivity().findViewById(R.id.membership_hosting);
        allHostingLabel.setText(completionStatusStringId(this.mUser.Service_Hosting && this.mUser.Fellowship_Hosting));
    }

    /**
     * Update labels associated with the mUser's Fellowship status.
     */
    private void updateFellowshipLabels() {
        TextView fellowshipStatusLabel = (TextView) getActivity().findViewById(R.id.fellowship_status);
        fellowshipStatusLabel.setText(completionStatusStringId(this.mUser.Fellowship));

        TextView fellowshipPointsLabel = (TextView) getActivity().findViewById(R.id.fellowship_points);
        fellowshipPointsLabel.setText(this.mUser.Fellowship_Points + " of " + this.mUser.Required_Fellowship);

        TextView fellowshipHostingLabel = (TextView) getActivity().findViewById(R.id.fellowship_hosting);
        fellowshipHostingLabel.setText(completionStatusStringId(this.mUser.Fellowship_Hosting));
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
            BrotherStatusFragment.this.mUser = result;
            updateViews();
        }
    }
}
