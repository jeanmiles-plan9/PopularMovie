package com.example.popularmovie.app;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * UdaCity Android Nanodegree
 * Created by jeanmiles-plan9 on 2/5/16.
 */
public class SimpleExpandableListAdapter extends BaseExpandableListAdapter {
    private final Context context;
    private final ArrayList<String> listDataHeader;
    private final Map<String, List<String>> listDataChild;

    public SimpleExpandableListAdapter(Context context, ArrayList<String> listDataHeader, Map<String, List<String>> listDataItem) {
        this.context = context;
        this.listDataHeader = listDataHeader;
        this.listDataChild = listDataItem;
    }

    @Override
    public int getGroupCount() {
        return this.listDataHeader.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this.listDataChild.get(this.listDataHeader.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.listDataHeader.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this.listDataChild.get(this.listDataHeader.get(groupPosition))
                .get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.listview_group, null);
        }

        TextView listHeader = (TextView) convertView.findViewById(R.id.list_group_header);
        listHeader.setTypeface(null, Typeface.BOLD);
        listHeader.setText(headerTitle);

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition,
                             boolean isLastChild, View convertView,
                             ViewGroup parent) {
        final String childText = (String) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.listview_detail_item, null);
        }

        TextView listChild = (TextView) convertView.findViewById(R.id.trailer_title);
        listChild.setText(childText);

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }
}
