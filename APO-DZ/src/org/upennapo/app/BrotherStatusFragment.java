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

    // Brother to Display
    private User user;
    private String firstName, lastName, spreadsheetUrl;

    /**
     * The {@link android.support.v4.widget.SwipeRefreshLayout} that detects swipe gestures and
     * triggers callbacks in the app.
     */
    private SwipeRefreshLayout mSwipeRefreshLayout;

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
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(USER_KEY, user);
        outState.putString(FIRST_NAME_KEY, firstName);
        outState.putString(LAST_NAME_KEY, lastName);
        outState.putString(URL_KEY, spreadsheetUrl);

        super.onSaveInstanceState(outState);
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
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null)
            updateViews();
    }

    @Override
    public void onRefresh() {
        AsyncUserDataLoader loader = new AsyncUserDataLoader() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                if (!mSwipeRefreshLayout.isRefreshing()) {
                    mSwipeRefreshLayout.setRefreshing(true);
                }
            }

            @Override
            protected User doInBackground(String... params) {
                return ReadJSON.getBrotherData(params[0], params[1], params[2],
                        getActivity(), true);
            }

            @Override
            protected void onPostExecute(User result) {
                super.onPostExecute(result);
                BrotherStatusFragment.this.user = result;
                updateViews();
                BrotherStatusFragment.this.mSwipeRefreshLayout.setRefreshing(false);
            }
        };
        loader.execute(spreadsheetUrl, firstName, lastName);
    }

    private void init(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            if (ReadJSON.isNetworkAvailable(getActivity())) {
                SharedPreferences prefs = getActivity().getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
                this.firstName = prefs.getString(LoginActivity.USER_FIRSTNAME_KEY, "");
                this.lastName = prefs.getString(LoginActivity.USER_LASTNAME_KEY, "");
                this.spreadsheetUrl = getArguments().getString(URL_KEY);

                getActivity().setProgressBarVisibility(true);
                getUserData();
            } else {
                // Notify the user that there is no connection. Tell them to try later.
                Toast noConnectionToast = Toast.makeText(getActivity(),
                        "Oops(ilon), there's no internet! Try again later.",
                        Toast.LENGTH_LONG);
                noConnectionToast.show();

                getActivity().setProgressBarVisibility(false);
            }
        } else {
            user = savedInstanceState.getParcelable(USER_KEY);
            firstName = savedInstanceState.getString(FIRST_NAME_KEY);
            lastName = savedInstanceState.getString(LAST_NAME_KEY);
            spreadsheetUrl = savedInstanceState.getString(URL_KEY);
        }
    }

    /**
     * Asynchronously load User data.
     */
    private void getUserData() {
        AsyncUserDataLoader loader = new AsyncUserDataLoader();
        loader.execute(spreadsheetUrl, firstName, lastName);
    }

    @SuppressWarnings("ResourceType")
    private void updateViews() {
        if (this.user == null) {
            // Notify the user and do not update all views if the user wasn't on the spreadsheet.
            AlertDialog.Builder nullUserAlert = new AlertDialog.Builder(getActivity());
            nullUserAlert.setTitle("User data not found");
            nullUserAlert.setMessage("We were unable to find your APO record. " +
                    "Please log out and log back in with your name exactly as it appears on the Spreadsheet.");
            nullUserAlert.show();

            TextView nameLabel = (TextView) getActivity().findViewById(R.id.name_label);
            nameLabel.setText("Unable to find user data.");
        } else {
            updateStatusLabels();
            updateServiceLabels();
            updateMembershipLabels();
            updateFellowshipLabels();
        }
    }

    /**
     * Update labels associated with the user's meta-status.
     */
    private void updateStatusLabels() {
        TextView nameLabel = (TextView) getActivity().findViewById(R.id.name_label);
        nameLabel.setText(this.user.First_and_Last_Name);

        TextView statusLabel = (TextView) getActivity().findViewById(R.id.status);
        statusLabel.setText(this.user.Status);

        TextView allReqStatusLabel = (TextView) getActivity().findViewById(R.id.all_reqs_status);
        allReqStatusLabel.setText(completionStatusStringId(this.user.Complete));
    }

    /**
     * Update labels associated with the user's service status.
     */
    private void updateServiceLabels() {
        TextView serviceStatusLabel = (TextView) getActivity().findViewById(R.id.service_status);
        serviceStatusLabel.setText(completionStatusStringId(this.user.Service));

        TextView serviceHoursLabel = (TextView) getActivity().findViewById(R.id.hours_label);
        serviceHoursLabel.setText(this.user.Service_Hours + " of " + this.user.Required_Service_Hours);

        TextView largeGroupStatusLabel = (TextView) getActivity().findViewById(R.id.large_group_status);
        largeGroupStatusLabel.setText(completionStatusStringId(this.user.Large_Group_Project));

        TextView publicityStatusLabel = (TextView) getActivity().findViewById(R.id.publicity_status);
        publicityStatusLabel.setText(completionStatusStringId(this.user.Publicity));

        TextView serviceHostingLabel = (TextView) getActivity().findViewById(R.id.service_hosting_status);
        serviceHostingLabel.setText(completionStatusStringId(this.user.Service_Hosting));
    }

    /**
     * Update labels associated with the user's membership status.
     */
    private void updateMembershipLabels() {
        TextView membershipStatusLabel = (TextView) getActivity().findViewById(R.id.membership_status);
        membershipStatusLabel.setText(completionStatusStringId(this.user.Membership));

        TextView membershipPointsLabel = (TextView) getActivity().findViewById(R.id.membership_points);
        membershipPointsLabel.setText(this.user.Membership_Points + " of " + this.user.Required_Membership_Points);

        TextView brotherCompLabel = (TextView) getActivity().findViewById(R.id.brother_comp);
        brotherCompLabel.setText(completionStatusStringId(this.user.Brother_Comp));

        TextView pledgeCompLabel = (TextView) getActivity().findViewById(R.id.pledge_comp);
        pledgeCompLabel.setText(completionStatusStringId(this.user.Pledge_Comp));

        TextView allHostingLabel = (TextView) getActivity().findViewById(R.id.membership_hosting);
        allHostingLabel.setText(completionStatusStringId(this.user.Service_Hosting && this.user.Fellowship_Hosting));
    }

    /**
     * Update labels associated with the user's Fellowship status.
     */
    private void updateFellowshipLabels() {
        TextView fellowshipStatusLabel = (TextView) getActivity().findViewById(R.id.fellowship_status);
        fellowshipStatusLabel.setText(completionStatusStringId(this.user.Fellowship));

        TextView fellowshipPointsLabel = (TextView) getActivity().findViewById(R.id.fellowship_points);
        fellowshipPointsLabel.setText(this.user.Fellowship_Points + " of " + this.user.Required_Fellowship);

        TextView fellowshipHostingLabel = (TextView) getActivity().findViewById(R.id.fellowship_hosting);
        fellowshipHostingLabel.setText(completionStatusStringId(this.user.Fellowship_Hosting));
    }

    private class AsyncUserDataLoader extends AsyncTask<String, Void, User> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            getActivity().setProgress(2000);
        }

        @Override
        protected User doInBackground(String... params) {
            return ReadJSON.getBrotherData(params[0], params[1], params[2], getActivity(), false);
        }

        @Override
        protected void onPostExecute(User result) {
            super.onPostExecute(result);

            BrotherStatusFragment.this.user = result;
            getActivity().setProgress(5000);
            updateViews();
            getActivity().setProgress(10000);
        }
    }
}
