package org.upennapo.app.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.upennapo.app.R;


/**
 * A fragment representing a list of Links.
 * <p/>
 * <p/>
 * Activities containing this fragment MUST implement the Callbacks
 * interface.
 */
public class LinkFragment extends ListFragment {

    private String[] mLinkTargets;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public LinkFragment() {
    }

    public static LinkFragment newInstance() {
        return new LinkFragment();
    }

    /**
     * This method converts dp unit to equivalent pixels, depending on device density.
     * Via http://stackoverflow.com/questions/4605527/converting-pixels-to-dp
     *
     * @param dp      A value in dp (density independent pixels) unit. Which we need to convert into pixels
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent px equivalent to dp depending on device density
     */
    public static float convertDpToPixel(float dp, Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return px;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final String[] linkTitles = getResources().getStringArray(R.array.alumni_res_titles);
        setListAdapter(new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, android.R.id.text1, linkTitles));

        mLinkTargets = getResources().getStringArray(R.array.alumni_res_targets);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_links, container, false);
    }

    @Override
    public ListView getListView() {
        return super.getListView();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        final String targetUrl = mLinkTargets[position];

        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(targetUrl)));
    }

    public static class LinkAdapter extends ArrayAdapter<String> {

        public LinkAdapter(Context context, int resource, String[] linkTitles) {
            super(context, resource);
        }

    }
}
