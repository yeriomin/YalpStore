package com.dragons.aurora.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.percolate.caffeine.ViewUtils;

import com.dragons.aurora.R;
import com.dragons.aurora.view.ListItem;


public class AppListAdapter extends ArrayAdapter<ListItem> {

    private int resourceId;
    private LayoutInflater inflater;

    public AppListAdapter(Context context, int resourceId) {
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
        ImageView menu3dot = ViewUtils.findViewById(view, R.id.menu_3dot);
        menu3dot.setOnClickListener(v -> view.showContextMenu());
        return view;
    }
}