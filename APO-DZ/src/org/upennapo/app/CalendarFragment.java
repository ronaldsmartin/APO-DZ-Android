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
 * @author Ronald Martin
 *
 */
public class CalendarFragment extends Fragment {
	
	public static final String URL_KEY = "url";
    private WebView mWebView;


    public CalendarFragment() {
        mWebView = new WebView(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        init(savedInstanceState);

        return mWebView;
    }

    private void init(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            mWebView.getSettings().setJavaScriptEnabled(true);
            mWebView.loadUrl(getArguments().getString(URL_KEY));
        } else {
            mWebView.restoreState(savedInstanceState);
        }
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
    }
}
