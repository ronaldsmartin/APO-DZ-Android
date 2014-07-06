/**
 *
 */
package org.upennapo.app.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import org.upennapo.app.R;
import org.upennapo.app.model.DataManager;

/**
 * WebFragment uses a WebView to load a webpage. Classic.
 * This Fragment has JavaScript enabled by default.
 *
 * @author Ronald Martin
 */
public class WebFragment extends Fragment {

    public static final String URL_KEY = "url";
    private String mUrl;
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
        this.mUrl = getArguments().getString(URL_KEY);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (DataManager.isNetworkAvailable(getActivity())) {
            mWebView = new WebView(getActivity());
            mWebView.setWebViewClient(new NoRedirectWebViewClient());
            mWebView.getSettings().setJavaScriptEnabled(true);
            mWebView.loadUrl(mUrl);

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
                mWebView.loadUrl(mUrl);
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

    /**
     * Custom WebViewClient prevents hosts from overriding loads, thus circumventing Google
     * Calendar's tendency to prevent loading embeddable calendar feeds outside of iframes.
     */
    private class NoRedirectWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return false;
        }

        @Override
        public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
            return false;
        }
    }
}
