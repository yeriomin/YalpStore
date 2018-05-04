package com.dragons.aurora.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.dragons.aurora.R;
import com.dragons.aurora.view.ListItem;
import com.percolate.caffeine.ViewUtils;


public class UpdatableAppListAdapter extends ArrayAdapter<ListItem> {

    private int resourceId;
    private LayoutInflater inflater;

    public UpdatableAppListAdapter(Context context, int resourceId) {
        super(context, resourceId);
        this.resourceId = resourceId;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        final View view = null == convertView ? inflater.inflate(resourceId, parent, false) : convertView;
        ListItem listItem = getItem(position);
        listItem.setView(view);
        listItem.draw();
        return view;
    }
}