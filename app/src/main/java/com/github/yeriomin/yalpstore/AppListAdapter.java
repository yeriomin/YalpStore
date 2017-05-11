package com.github.yeriomin.yalpstore;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.github.yeriomin.yalpstore.view.ListItem;


public class AppListAdapter extends ArrayAdapter<ListItem> {

    private int resourceId;
    private LayoutInflater inflater;

    public AppListAdapter(Context context, int resourceId) {
        super(context, resourceId);
        this.resourceId = resourceId;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null == convertView ? inflater.inflate(resourceId, parent, false) : convertView;
        ListItem listItem = getItem(position);
        listItem.setView(view);
        listItem.draw();
        return view;
    }
}
