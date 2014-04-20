/**
 * 
 */
package org.upennapo.app;

import android.support.v4.app.Fragment;
import android.annotation.SuppressLint;
import android.os.Bundle;
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
	
	public CalendarFragment() {
		
	}

    @SuppressLint("SetJavaScriptEnabled")
	@Override
    public View onCreateView(LayoutInflater inflater,
    			ViewGroup container, Bundle savedInstanceState) {
        View calendarView = inflater.inflate(R.layout.fragment_calendar_tab, container, false);
        
        final String urlToLoad = getArguments().getString(URL_KEY);
        if (urlToLoad != null) {
            WebView calendarWebView = (WebView) calendarView.findViewById(R.id.calendar_webview);
            calendarWebView.getSettings().setJavaScriptEnabled(true);
            calendarWebView.loadUrl(urlToLoad);
        }

        return calendarView;
    }
}
