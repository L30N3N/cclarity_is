package de.dhbwmatinf19ai1.cclarityis.Rules;


import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Leon Nehring
 */

public class RulesAdapter extends BaseExpandableListAdapter {

    ArrayList<String> elements_title;
    HashMap<String, ArrayList<String>> elements_text;

    public RulesAdapter(ArrayList<String> elements_title, HashMap<String, ArrayList<String>> elements_text) {
        this.elements_text = elements_text;
        this.elements_title = elements_title;
    }

    @Override
    public int getGroupCount() {
        return elements_title.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return elements_text.get(elements_title.get(i)).size();
    }

    @Override
    public Object getGroup(int i) {
        return elements_title.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        return elements_text.get(elements_title.get(i)).get(i1);
    }

    @Override
    public long getGroupId(int i) {
        return 0;
    }

    @Override
    public long getChildId(int i, int i1) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
        view = LayoutInflater.from(viewGroup.getContext()).inflate(android.R.layout.simple_expandable_list_item_1, viewGroup, false);
        TextView textView = view.findViewById(android.R.id.text1);
        String group = String.valueOf(getGroup(i));
        textView.setText(group);
        textView.setTypeface(null, Typeface.BOLD);
        return view;
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
        view = LayoutInflater.from(viewGroup.getContext()).inflate(android.R.layout.simple_selectable_list_item, viewGroup, false);
        TextView textView = view.findViewById(android.R.id.text1);
        ViewGroup.LayoutParams params = textView.getLayoutParams();
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        textView.setLayoutParams(params);
        String child = String.valueOf(getChild(i, i1));
        textView.setText(child);

        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return false;
    }
}
