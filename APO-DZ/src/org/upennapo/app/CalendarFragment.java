/**
 *
 */
package org.upennapo.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

/**
 * CalendarFragment uses a WebView to load a Google Calendar.
 *
 * @author Ronald Martin
 */
public class CalendarFragment extends Fragment {

    public static final String URL_KEY = "url";
    private WebView mWebView;


    public CalendarFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        init(savedInstanceState);

        return mWebView;
    }

    private void init(Bundle savedInstanceState) {
        mWebView = new WebView(getActivity());
        mWebView.getSettings().setJavaScriptEnabled(true);
        if (savedInstanceState == null) {
            mWebView.loadUrl(getArguments().getString(URL_KEY));
        } else {
            mWebView.restoreState(savedInstanceState);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        mWebView.saveState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
    }
}
