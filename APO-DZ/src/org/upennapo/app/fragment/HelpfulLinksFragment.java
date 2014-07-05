package org.upennapo.app.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.commonsware.cwac.merge.MergeAdapter;

import org.upennapo.app.R;

/**
 * @author Dean Wilhelmi & Ronald Martin
 *         Based on tutorial from http://www.javacodegeeks.com/2013/06/android-asynctask-listview-json.html
 */

public class HelpfulLinksFragment extends Fragment {

    public HelpfulLinksFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_helpful_links, container, false);
        ListView sheetList = (ListView) view.findViewById(R.id.sheet_list);

        // Populate the cells with the names of the links
        final String[] sheets = getResources().getStringArray(R.array.sheet_link_names);
        ArrayAdapter<String> sheetsAdapter = new ArrayAdapter<String>(getActivity(),
                R.layout.helpful_links_text, R.id.centered_text, sheets);

        final String[] forms = getResources().getStringArray(R.array.form_link_names);
        ArrayAdapter<String> formsAdapter = new ArrayAdapter<String>(getActivity(),
                R.layout.helpful_links_text, R.id.centered_text, forms);

        final String[] sites = getResources().getStringArray(R.array.site_link_names);
        ArrayAdapter<String> sitesAdapter = new ArrayAdapter<String>(getActivity(),
                R.layout.helpful_links_text, R.id.centered_text, sites);

        final String[] socials = getResources().getStringArray(R.array.social_link_names);
        ArrayAdapter<String> socialsAdapter = new ArrayAdapter<String>(getActivity(),
                R.layout.helpful_links_text, R.id.centered_text, socials);

        // Set text for headers.
        TextView sheetHeaderView = (TextView) inflater.inflate(R.layout.list_header, null);
        sheetHeaderView.setText(R.string.sheet_list_header);

        TextView formHeaderView = (TextView) inflater.inflate(R.layout.list_header, null);
        formHeaderView.setText(R.string.form_list_header);

        TextView siteHeaderView = (TextView) inflater.inflate(R.layout.list_header, null);
        siteHeaderView.setText(R.string.site_list_header);

        TextView socialHeaderView = (TextView) inflater.inflate(R.layout.list_header, null);
        socialHeaderView.setText(R.string.social_list_header);


        // Convert Adapter to a TableView
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

        // Set links to open the corresponding url in the String array.
        sheetList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // When clicked open the URL specified by this intent.
                final String[] links = getResources().getStringArray(R.array.helpful_links);
                String url = links[position];
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(i);
            }
        });

        return view;
    }
}
