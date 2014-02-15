/**
 * 
 */
package org.upennapo.app;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
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

    @Override
    public View onCreateView(LayoutInflater inflater,
    			ViewGroup container, Bundle savedInstanceState) {
        View calendarView = inflater.inflate(R.layout.fragment_calendar_tab, container, false);
        
        final String urlToLoad = getArguments().getString(URL_KEY);
        if (urlToLoad != null) {
            WebView calendarWebView = (WebView) calendarView.findViewById(R.id.calendar_webview);
            calendarWebView.getSettings().setJavaScriptEnabled(true);
            //calendarView.loadData("<html><meta name=\"viewport\" content=\"width=310\"/><iframe src=\"https://www.google.com/calendar/embed?title=APO%20Calendar&amp;showTitle=0&amp;showDate=0&amp;showPrint=0&amp;showTz=0&amp;mode=AGENDA&amp;height=600&amp;wkst=1&amp;bgcolor=%23ffffff&amp;src=uieecnqfvhg1ojnvsp0nf3rfek%40group.calendar.google.com&amp;color=%232F6309&amp;src=apoexec.vpfellowship%40gmail.com&amp;color=%231B887A&amp;src=u088i696n6jak2mpranj291v8k%40group.calendar.google.com&amp;color=%23B1365F&amp;src=bkap2sjjoo0gcu8blq3mn3bnic%40group.calendar.google.com&amp;color=%23B1440E&amp;src=fn14o6l0c7ckl211s92auufsgk%40group.calendar.google.com&amp;color=%236B3304&amp;ctz=America%2FNew_York\" style=\" border-width:0 \" width=\"800\" height=\"600\" frameborder=\"0\" scrolling=\"no\"></iframe>", "text/html", null);
            calendarWebView.loadUrl(urlToLoad);
        }

        return calendarView;
    }
    
    public void openServiceCalendar(View v) {
    	View calendarView = v.getRootView();
    	
    	WebView calendar = (WebView) calendarView.findViewById(R.id.calendar_webview);
    	calendar.getSettings().setJavaScriptEnabled(true);
    	calendar.loadUrl("https://sites.google.com/site/aposervicecalendar/");
    }
    
    public void openFellowshipCalendar(View v) {
    	View calendarView = v.getRootView();
    	
    	WebView calendar = (WebView) calendarView.findViewById(R.id.calendar_webview);
    	calendar.getSettings().setJavaScriptEnabled(true);
    	calendar.loadUrl("http://www.upennapo.org");
    }
    
    public void openPledgingCalendar(View v) {
    	View calendarView = v.getRootView();
    	
    	WebView calendar = (WebView) calendarView.findViewById(R.id.calendar_webview);
    	calendar.getSettings().setJavaScriptEnabled(true);
    	calendar.loadUrl("https://www.google.com/calendar/embed?showTitle=0&amp;showDate=0&amp&showPrint=0&amp&showTabs=0&amp&showTz=0&amp&mode=AGENDA&amp&height=600&amp&wkst=1&amp&bgcolor=%23FFFFFF&amp&src=uieecnqfvhg1ojnvsp0nf3rfek%40group.calendar.google.com&amp&color=%2323164E&amp&ctz=America%2FNew_York");
    }
}
