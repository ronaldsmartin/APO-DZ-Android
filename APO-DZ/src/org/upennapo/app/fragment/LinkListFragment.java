package org.upennapo.app.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.upennapo.app.R;
import org.upennapo.app.adapter.LinkAdapter;


/**
 * A fragment representing a list of Links.
 * <p/>
 * <p/>
 * Activities containing this fragment MUST implement the Callbacks
 * interface.
 */
public class LinkListFragment extends ListFragment {

    public static final String TITLES = "LINK_TITLES";
    public static final String DESCRIPTIONS = "LINK_DESCRIPTIONS";
    public static final String TARGETS = "LINK_TARGETS";

    private String[] mLinkTargets;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public LinkListFragment() {
    }

    public static LinkListFragment newBrotherLinksInstance(Context context) {
        Bundle args = new Bundle();
        args.putStringArray(TITLES, context.getResources().getStringArray(R.array.brother_link_titles));
        args.putStringArray(TARGETS, context.getResources().getStringArray(R.array.brother_link_targets));

        LinkListFragment instance = new LinkListFragment();
        instance.setArguments(args);

        return instance;
    }

    public static LinkListFragment newAlumLinksInstance(Context context) {
        Bundle args = new Bundle();
        args.putStringArray(TITLES, context.getResources().getStringArray(R.array.alumni_res_titles));
        args.putStringArray(DESCRIPTIONS, context.getResources().getStringArray(R.array.alumni_res_descriptions));
        args.putStringArray(TARGETS, context.getResources().getStringArray(R.array.alumni_res_targets));

        LinkListFragment instance = new LinkListFragment();
        instance.setArguments(args);

        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLinkTargets = getArguments().getStringArray(TARGETS);
        setListAdapter(new LinkAdapter(getActivity(), R.layout.fragment_links, getArguments()));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_links, container, false);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        final String targetUrl = mLinkTargets[position];
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(targetUrl)));
    }
}
