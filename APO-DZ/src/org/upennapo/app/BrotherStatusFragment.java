package org.upennapo.app;


import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;


public class BrotherStatusFragment extends Fragment implements OnRefreshListener {

    // Constants
    public static final String URL_KEY = "SPREADSHEET_URL";

    // Brother to Display
    private User user;
    private String firstName, lastName, spreadsheetUrl;

    private PullToRefreshLayout mPullToRefreshLayout;

    private static int completionStatusStringId(boolean complete) {
        if (complete)
            return R.string.req_complete;
        else return R.string.req_incomplete;
    }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		SharedPreferences prefs = getActivity().getSharedPreferences("LoginActivity", Context.MODE_PRIVATE);
        this.firstName = prefs.getString(LoginActivity.USER_FIRSTNAME_KEY, "");
        this.lastName = prefs.getString(LoginActivity.USER_LASTNAME_KEY, "");
        this.spreadsheetUrl = getArguments().getString(URL_KEY);

		getActivity().setProgressBarVisibility(true);
		if (ReadJSON.isNetworkAvailable(getActivity())) {
            updateUserData();
        } else {
            // Notify the user that there is no connection. Tell them to try later.
            Toast noConnectionToast = Toast.makeText(getActivity(),
                    "Oops(ilon), there's no internet! Try again later.",
                    Toast.LENGTH_LONG);
            noConnectionToast.show();
        }

        View view = inflater.inflate(R.layout.fragment_brother_status, container, false);

        // Now find the PullToRefreshLayout to setup
        this.mPullToRefreshLayout = (PullToRefreshLayout) view.findViewById(R.id.ptr_layout);
        // Now setup the PullToRefreshLayout
        ActionBarPullToRefresh.from(getActivity())
                // Mark All Children as pullable
                .allChildrenArePullable()
                        // Set a OnRefreshListener
                .listener(this)
                        // Finally commit the setup to our PullToRefreshLayout
                .setup(mPullToRefreshLayout);

        return view;
    }

    @Override
    public void onRefreshStarted(View view) {
        updateUserData();
    }

    private void updateUserData() {
        AsyncUserDataLoader loader = new AsyncUserDataLoader() {
            @Override
            protected void onPostExecute(User result) {
                super.onPostExecute(result);
                mPullToRefreshLayout.setRefreshComplete();
            }
        };
        loader.execute(spreadsheetUrl, firstName, lastName);
    }

    @SuppressWarnings("ResourceType")
    private void updateView() {
        if (this.user == null) {
            // Notify the user and do not update the view if the user wasn't on the spreadsheet.
            AlertDialog.Builder nullUserAlert = new AlertDialog.Builder(getActivity());
            nullUserAlert.setTitle("User data not found");
            nullUserAlert.setMessage("We were unable to find your APO record. " +
                    "Please log in with your name exactly as it appears on the Spreadsheet.");
            nullUserAlert.show();
            return;
        }

        // Update Status Labels
        TextView nameLabel = (TextView) getActivity().findViewById(R.id.name_label);
        nameLabel.setText(this.user.First_Name + " " + this.user.Last_Name);

        TextView statusLabel = (TextView) getActivity().findViewById(R.id.status);
        statusLabel.setText(this.user.Status);

        TextView allReqStatusLabel = (TextView) getActivity().findViewById(R.id.all_reqs_status);
        allReqStatusLabel.setText(completionStatusStringId(this.user.Complete));


        // Update Service Status
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


        // Update Membership Status
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


        // Update Fellowship Status
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
	    	return ReadJSON.getBrotherData(params[0], params[1], params[2]);
	    }
	    
	    @Override
	    protected void onPostExecute(User result) {    
	        BrotherStatusFragment.this.user = result;
	        getActivity().setProgress(5000);
	        updateView();
	        getActivity().setProgress(10000);
	    }	    
	}
}
