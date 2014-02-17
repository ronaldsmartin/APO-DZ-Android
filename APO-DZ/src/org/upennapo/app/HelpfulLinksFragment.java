package org.upennapo.app;

import com.commonsware.cwac.merge.MergeAdapter;

import android.support.v4.app.Fragment;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
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
 * @author Dean Wilhelmi
 * Based on tutorial from http://www.javacodegeeks.com/2013/06/android-asynctask-listview-json.html
 */

public class HelpfulLinksFragment extends Fragment {
	/**
     * The fragment argument representing the section number for this
     * fragment.
     */
    public static final String ARG_SECTION_NUMBER = "section_number";
    public static final float HELPFUL_LINKS_HEADER_SIZE = 30;
    public int HEADER_TEXT_COLOR;
    public int HEADER_BACKGROUND_COLOR;
    

    public HelpfulLinksFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	
    	View view = inflater.inflate(R.layout.fragment_helpful_links, container, false);
    	this.HEADER_TEXT_COLOR = getActivity().getResources().getColor(R.color.apo_yellow);
    	this.HEADER_BACKGROUND_COLOR = getActivity().getResources().getColor(R.color.apo_blue);
    	
    	ListView sheetList = (ListView)view.findViewById(R.id.sheet_list);
    	
    	
    	String[] sheets = getResources().getStringArray(R.array.sheet_links);
    	ArrayAdapter<String> sheetsAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_activated_1, sheets);
    	
    	String[] forms = getResources().getStringArray(R.array.form_links);
    	ArrayAdapter<String> formsAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_activated_1, forms);
    	
    	String[] sites = getResources().getStringArray(R.array.site_links);
    	ArrayAdapter<String> sitesAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_activated_1, sites);
    	
    	String[] socials = getResources().getStringArray(R.array.social_links);
    	ArrayAdapter<String> socialsAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_activated_1, socials);
    	
    	
    	TextView sheetHeaderView = new TextView(getActivity());
    	sheetHeaderView.setText(R.string.sheet_list_header);
    	sheetHeaderView.setTextSize(HELPFUL_LINKS_HEADER_SIZE);
    	sheetHeaderView.setGravity(Gravity.CENTER);
    	sheetHeaderView.setTextColor(HEADER_TEXT_COLOR);
    	sheetHeaderView.setBackgroundColor(HEADER_BACKGROUND_COLOR);
    	
    	TextView formHeaderView = new TextView(getActivity());
    	formHeaderView.setText(R.string.form_list_header);
    	formHeaderView.setTextSize(HELPFUL_LINKS_HEADER_SIZE);
    	formHeaderView.setGravity(Gravity.CENTER);
    	formHeaderView.setTextColor(HEADER_TEXT_COLOR);
    	formHeaderView.setBackgroundColor(HEADER_BACKGROUND_COLOR);
    	
    	TextView siteHeaderView = new TextView(getActivity());
    	siteHeaderView.setText(R.string.site_list_header);
    	siteHeaderView.setTextSize(HELPFUL_LINKS_HEADER_SIZE);
    	siteHeaderView.setGravity(Gravity.CENTER);
    	siteHeaderView.setTextColor(HEADER_TEXT_COLOR);
    	siteHeaderView.setBackgroundColor(HEADER_BACKGROUND_COLOR);
    	
    	TextView socialHeaderView = new TextView(getActivity());
    	socialHeaderView.setText(R.string.social_list_header);
    	socialHeaderView.setTextSize(HELPFUL_LINKS_HEADER_SIZE);
    	socialHeaderView.setGravity(Gravity.CENTER);
    	socialHeaderView.setTextColor(HEADER_TEXT_COLOR);
    	socialHeaderView.setBackgroundColor(HEADER_BACKGROUND_COLOR);
    	
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
    			// When clicked perform some action...
    			switch (position){
    			// start Spreadsheets
    			case 1:
    				Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.brother_sheet_url)));
                    startActivity(i);
                    break;
    			case 2:
    				Intent i2 = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.pledge_sheet_url)));
                    startActivity(i2);
                    break;
    			case 3:
    				Intent i3 = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.biglittle_sheet_url)));
                    startActivity(i3);
                    break;
    			case 4:
    				Intent i4 = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.food_group_sheet)));
                    startActivity(i4);
                    break;
                // start Forms
    			case 6:
    				Intent i6 = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.service_reporting_form)));
                    startActivity(i6);
                    break;
    			case 7:
    				Intent i7 = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.service_reflection_form)));
                    startActivity(i7);
                    break;
    			case 8:
    				Intent i8 = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.fellowship_hosting_form)));
                    startActivity(i8);
                    break;
    			case 9:
    				Intent i9 = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.fellowship_reporting_form)));
                    startActivity(i9);
                    break;
    			case 10:
    				Intent i10 = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.food_group_reporting_form)));
                    startActivity(i10);
                    break;
    			case 11:
    				Intent i11 = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.biglittle_reporting_form)));
                    startActivity(i11);
                    break;
    			case 12:
    				Intent i12 = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.merit_reporting_form)));
                    startActivity(i12);
                    break;
    			case 13:
    				Intent i13 = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.reimbursement_form)));
                    startActivity(i13);
                    break;
    			case 14:
    				Intent i14 = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.board_feedback_form)));
                    startActivity(i14);
                    break;
                // Start Sites
    			case 16:
    				Intent i16 = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.service_calendar_url)));
                    startActivity(i16);
                    break;
    			case 17:
    				Intent i17 = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.national_site_url)));
                    startActivity(i17);
                    break;
    			case 18:
    				Intent i18 = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.chapter_site_url)));
                    startActivity(i18);
                    break;
    			case 19:
    				Intent i19 = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.old_service_calendar_url)));
                    startActivity(i19);
                    break;
                // Start Social
    			case 21:
    				Intent i21 = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.youtube_url)));
                    startActivity(i21);
                    break;
    			case 22:
    				Intent i22 = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.instagram_url)));
                    startActivity(i22);
                    break;
    			case 23:
    				Intent i23 = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.tumblr_url)));
                    startActivity(i23);
                    break;
    			case 24:
    				Intent i24 = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.apoutofcontext_url)));
                    startActivity(i24);
                    break;
    			}
    		}
		});
    	
//    	String[] sites = getResources().getStringArray(R.array.site_links);
//    	ArrayAdapter<String> sitesAdapter = new ArrayAdapter<String>(getActivity(),
//                android.R.layout.simple_list_item_activated_1, sites);
//    	siteList.setAdapter(sitesAdapter);
//    	
//    	String[] socials = getResources().getStringArray(R.array.social_links);
//    	ArrayAdapter<String> socialsAdapter = new ArrayAdapter<String>(getActivity(),
//                android.R.layout.simple_list_item_activated_1, socials);
//    	socialList.setAdapter(socialsAdapter);
    	
//    	formList.setAdapter(new ArrayAdapter<String>(getActivity(),
//                android.R.layout.simple_list_item_activated_1, R.array.form_links));
//    	siteList.setAdapter(new ArrayAdapter<String>(getActivity(),
//                android.R.layout.simple_list_item_activated_1, R.array.site_links));
//    	socialList.setAdapter(new ArrayAdapter<String>(getActivity(),
//                android.R.layout.simple_list_item_activated_1, R.array.social_links));
    	
    	
        return view;
    }
}
