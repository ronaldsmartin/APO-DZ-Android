/**
 *
 */
package org.upennapo.app;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Toast;

/**
 * WebFragment uses a WebView to load a webpage. Classic.
 * This Fragment has JavaScript enabled by default.
 *
 * @author Ronald Martin
 */
public class WebFragment extends Fragment {

    public static final String URL_KEY = "url";
    private WebView mWebView;

    public WebFragment() {
    }

    /**
     * Get an instance of WebFragment that loads the Google Calendars.
     *
     * @param context in which to load the WebView
     * @return Google Calendar Fragment
     */
    public static WebFragment newCalendarInstance(Context context) {
        WebFragment instance = new WebFragment();

        final String url = context.getString(R.string.calendar_url);
        Bundle args = new Bundle();
        args.putString(URL_KEY, url);
        instance.setArguments(args);

        return instance;
    }

    /**
     * Get an instance of WebFragment that loads the APO 2048 game.
     *
     * @param context in which to load the WebView
     * @return 2048 Fragment
     */
    public static WebFragment new2048Instance(Context context) {
        WebFragment instance = new WebFragment();

        final String url = context.getString(R.string.apo_2048_url);
        Bundle args = new Bundle();
        args.putString(URL_KEY, url);
        instance.setArguments(args);

        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (DataManager.isNetworkAvailable(getActivity())) {
            mWebView = new WebView(getActivity());
            mWebView.getSettings().setJavaScriptEnabled(true);
            mWebView.loadUrl(getArguments().getString(URL_KEY));

            return mWebView;
        } else {
            Toast.makeText(getActivity(), R.string.no_internet_toast_msg, Toast.LENGTH_SHORT).show();
            return super.onCreateView(inflater, container, savedInstanceState);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_web, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_reload:
                mWebView.clearCache(true);
                mWebView.reload();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onDestroy() {
        if (mWebView != null) {
            // Prevent memory leaks.
            mWebView.clearCache(true);
            mWebView.destroy();
            mWebView = null;
        }
        super.onDestroy();
    }
}
