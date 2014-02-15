package org.upennapo.app;

import com.commonsware.cwac.merge.MergeAdapter;

import android.support.v4.app.Fragment;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class HelpfulLinksFragment extends Fragment {
	/**
     * The fragment argument representing the section number for this
     * fragment.
     */
    public static final String ARG_SECTION_NUMBER = "section_number";
    public static final float HELPFUL_LINKS_HEADER_SIZE = 30;
    public static final int HEADER_TEXT_COLOR = Color.WHITE;
    public static final int HEADER_BACKGROUND_COLOR = Color.BLUE;

    public HelpfulLinksFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	
    	View view = inflater.inflate(R.layout.fragment_helpful_links, container, false);
    	
    	
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
