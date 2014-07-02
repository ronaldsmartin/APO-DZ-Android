package org.upennapo.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SectionIndexer;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

class AlphabeticalAdapter extends ArrayAdapter<Brother> implements SectionIndexer {

    private HashMap<String, Integer> alphaIndexer;
    private String[] sections;

    public AlphabeticalAdapter(Context context, ArrayList<Brother> brothers) {
        super(context, R.layout.item_brother, R.id.txt_name, brothers);
        init(brothers);
    }

    private void init(List<Brother> data) {
        alphaIndexer = new HashMap<String, Integer>();
        for (int i = 0; i < data.size(); i++) {
            String s = data.get(i).toString().substring(0, 1).toUpperCase();
            if (!alphaIndexer.containsKey(s))
                alphaIndexer.put(s, i);
        }

        Set<String> sectionLetters = alphaIndexer.keySet();
        ArrayList<String> sectionList = new ArrayList<String>(sectionLetters);
        Collections.sort(sectionList);
        sections = new String[sectionList.size()];
        for (int i = 0; i < sectionList.size(); i++)
            sections[i] = sectionList.get(i);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_brother, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.nameLabel = (TextView) convertView.findViewById(R.id.txt_name);
            viewHolder.pledgeClassLabel = (TextView) convertView.findViewById(R.id.txt_pledge_class);
            viewHolder.phoneNumLabel = (TextView) convertView.findViewById(R.id.txt_phone);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final Brother brother = getItem(position);
        viewHolder.nameLabel.setText(brother.toString());
        viewHolder.pledgeClassLabel.setText(brother.Pledge_Class);
        viewHolder.phoneNumLabel.setText(brother.Phone_Number);
        return convertView;
    }

    public int getPositionForSection(int section) {
        return alphaIndexer.get(sections[section]);
    }

    public int getSectionForPosition(int position) {
        return 0;
    }

    public Object[] getSections() {
        return sections;
    }

    private static class ViewHolder {
        TextView nameLabel;
        TextView pledgeClassLabel;
        TextView phoneNumLabel;
    }
}
