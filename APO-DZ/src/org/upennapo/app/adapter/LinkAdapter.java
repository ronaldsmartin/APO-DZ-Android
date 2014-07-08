package org.upennapo.app.adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.upennapo.app.R;
import org.upennapo.app.fragment.LinkListFragment;

/**
 * Custom adapter that sections arrays of links and targets.
 */
public class LinkAdapter extends ArrayAdapter<String> {

    private static final int NUM_VIEW_TYPES = 2;
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private String[] mTitles, mDescriptions, mTargets;

    public LinkAdapter(Context context, int layout, Bundle args) {
        super(context, layout);
        mTitles = args.getStringArray(LinkListFragment.TITLES);
        mDescriptions = args.getStringArray(LinkListFragment.DESCRIPTIONS);
        mTargets = args.getStringArray(LinkListFragment.TARGETS);
        super.addAll(mTitles);
    }

    @Override
    public int getViewTypeCount() {
        return NUM_VIEW_TYPES;
    }

    @Override
    public int getItemViewType(int position) {
        final String headerFlag = getContext().getString(R.string.link_header_flag);
        return headerFlag.equals(mTargets[position]) ? TYPE_HEADER : TYPE_ITEM;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        final int itemType = getItemViewType(position);
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            viewHolder = new ViewHolder();

            switch (itemType) {
                case TYPE_HEADER:
                    convertView = inflater.inflate(R.layout.list_header, parent, false);
                    viewHolder.textView = (TextView) convertView.findViewById(R.id.link_header);
                    break;

                case TYPE_ITEM:
                    convertView = inflater.inflate(R.layout.item_link_text, parent, false);
                    viewHolder.textView = (TextView) convertView.findViewById(R.id.main_text);
                    viewHolder.textViewSecondary = (TextView) convertView.findViewById(R.id.secondary_text);
                    break;
            }

            assert convertView != null;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.textView.setText(mTitles[position]);

        // If there is a description for this link, draw & display it.
        TextView description = viewHolder.textViewSecondary;
        if (mDescriptions != null && mDescriptions.length == mTitles.length
                && mDescriptions[position].length() > 0 && itemType != TYPE_HEADER) {
            description.setText(mDescriptions[position]);
            description.setVisibility(View.VISIBLE);
        } else if (description != null && description.getVisibility() == View.VISIBLE) {
            description.setVisibility(View.GONE);
        }

        return convertView;
    }

    @Override
    public boolean isEnabled(int position) {
        return !mTargets[position].equals(getContext().getString(R.string.link_header_flag));
    }

    private static class ViewHolder {
        TextView textView;
        TextView textViewSecondary;
    }
}
