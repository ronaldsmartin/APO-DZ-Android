package org.upennapo.app;

import java.util.Locale;

import com.commonsware.cwac.merge.MergeAdapter;

import android.R.style;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
/**
 * @author Dean Wilhelmi & Ronald Martin
 * Based on tutorial from http://www.javacodegeeks.com/2013/06/android-asynctask-listview-json.html
 */

public class HelpfulLinksFragment extends Fragment {
	/**
     * The fragment argument representing the section number for this
     * fragment.
     */
    public static final String ARG_SECTION_NUMBER = "section_number";
    public static final float HELPFUL_LINKS_HEADER_SIZE = 22;
    private int HEADER_TEXT_COLOR;
    private int HEADER_BACKGROUND_COLOR;
    

    public HelpfulLinksFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	
    	View view = inflater.inflate(R.layout.fragment_helpful_links, container, false);
    	this.HEADER_TEXT_COLOR = getActivity().getResources().getColor(R.color.apo_yellow);
    	this.HEADER_BACKGROUND_COLOR = Color.BLACK;
    	ListView sheetList = (ListView) view.findViewById(R.id.sheet_list);
    	
    	
    	final String[] sheets = getResources().getStringArray(R.array.sheet_links);
    	ArrayAdapter<String> sheetsAdapter = new ArrayAdapter<String>(getActivity(),
                R.layout.centered_textview, R.id.centered_text, sheets);

    	final String[] forms = getResources().getStringArray(R.array.form_links);
    	ArrayAdapter<String> formsAdapter = new ArrayAdapter<String>(getActivity(),
    			R.layout.centered_textview, R.id.centered_text, forms);
    	
    	final String[] sites = getResources().getStringArray(R.array.site_links);
    	ArrayAdapter<String> sitesAdapter = new ArrayAdapter<String>(getActivity(),
    			R.layout.centered_textview, R.id.centered_text, sites);
    	
    	final String[] socials = getResources().getStringArray(R.array.social_links);
    	ArrayAdapter<String> socialsAdapter = new ArrayAdapter<String>(getActivity(),
    			R.layout.centered_textview, R.id.centered_text, socials);
    	
    	// Set text for headers.
    	TextView sheetHeaderView = new TextView(getActivity());
    	sheetHeaderView.setText(R.string.sheet_list_header);
    	
    	TextView formHeaderView = new TextView(getActivity());
    	formHeaderView.setText(R.string.form_list_header);
    	
    	TextView siteHeaderView = new TextView(getActivity());
    	siteHeaderView.setText(R.string.site_list_header);
    	
    	TextView socialHeaderView = new TextView(getActivity());
    	socialHeaderView.setText(R.string.social_list_header);
    	
    	// Set the font and background for headers
    	final Locale l = Locale.getDefault();
    	final Typeface headerTypeface = Typeface.create("sans-serif-condensed", Typeface.BOLD);
    	final float scale = getResources().getDisplayMetrics().density;
    	final int padding = (int) (10*scale + 0.5f);
    	final TextView[] headers = {sheetHeaderView, formHeaderView, siteHeaderView, socialHeaderView};
    	for (TextView header : headers) {
    		final String headerName = header.getText().toString().toUpperCase(l);
    		header.setTypeface(headerTypeface);
    		header.setText(headerName);
    		header.setTextSize(HELPFUL_LINKS_HEADER_SIZE);
        	header.setGravity(Gravity.CENTER);
        	header.setTextColor(HEADER_TEXT_COLOR);
        	header.setBackgroundColor(HEADER_BACKGROUND_COLOR);
        	header.setPadding(0, padding, 0, padding);
    	}
    	
    	MergeAdapter myMergeAdapter = new MergeAdapter();
    	myMergeAdapter.addView(sheetHeaderView); 
    	myMergeAdapter.addAdapter(sheetsAdapter); 
    	myMergeAdapter.addView(formHeaderView); 
    	myMergeAdapter.addAdapter(formsAdapter);
    	myMergeAdapter.addView(siteHeaderView); 
    	myMergeAdapter.addAdapter(sitesAdapter); 
    	myMergeAdapter.addView(socialHeaderView); 
    	myMergeAdapter.addAdapter(socialsAdapter);
    	
    	sheetList.setAdapter(myMergeAdapter);
    	
    	
    	sheetList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
    		
    		public void onItemClick(AdapterView <?> parent, View view, int position, long id) {
    			// When clicked open the URL specified by this intent.
    			String url;
    			switch (position){
	    			// start Spreadsheets
	    			case 1:
	    				url = getString(R.string.brother_sheet_url);
	                    break;
	    			case 2:
	    				url = getString(R.string.pledge_sheet_url);
	                    break;
	    			case 3:
	    				url = getString(R.string.biglittle_sheet_url);
	                    break;
	    			case 4:
	    				url = getString(R.string.food_group_sheet);
	                    break;
	                // start Forms
	    			case 6:
	    				url = getString(R.string.service_reporting_form);
	                    break;
	    			case 7:
	    				url = getString(R.string.service_reflection_form);
	                    break;
	    			case 8:
	    				url = getString(R.string.fellowship_hosting_form);
	                    break;
	    			case 9:
	    				url = getString(R.string.fellowship_reporting_form);
	                    break;
	    			case 10:
	    				url = getString(R.string.food_group_reporting_form);
	                    break;
	    			case 11:
	    				url = getString(R.string.biglittle_reporting_form);
	                    break;
	    			case 12:
	    				url = getString(R.string.merit_reporting_form);
	                    break;
	    			case 13:
	    				url = getString(R.string.reimbursement_form);
	                    break;
	    			case 14:
	    				url = getString(R.string.board_feedback_form);
	                    break;
	                // Start Sites
	    			case 16:
	    				url = getString(R.string.service_calendar_url);
	                    break;
	    			case 17:
	    				url = getString(R.string.national_site_url);
	                    break;
	    			case 18:
	    				url = getString(R.string.chapter_site_url);
	                    break;
	    			case 19:
	    				url = getString(R.string.old_service_calendar_url);
	                    break;
	                // Start Social
	    			case 21:
	    				url = getString(R.string.youtube_url);
	                    break;
	    			case 22:
	    				url = getString(R.string.instagram_url);
	                    break;
	    			case 23:
	    				url = getString(R.string.tumblr_url);
	                    break;
	    			case 24:
	    				url = getString(R.string.apoutofcontext_url);
	                    break;
                    default:
                    	url = getString(R.string.chapter_site_url);
    			}
    			Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
    			startActivity(i);
    		}
		});  	
    	
        return view;
    }
}
