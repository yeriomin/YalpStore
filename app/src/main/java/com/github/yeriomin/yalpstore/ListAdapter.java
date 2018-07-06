/*
 * Yalp Store
 * Copyright (C) 2018 Sergey Yeriomin <yeriomin@gmail.com>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.github.yeriomin.yalpstore;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;

import com.github.yeriomin.yalpstore.view.ListItem;

import java.util.Collection;

public class ListAdapter extends ArrayAdapter<ListItem> {

    private int resourceId;
    private LayoutInflater inflater;
    private Animation removeAnimation;

    public ListAdapter(Context context, int resourceId) {
        super(context, resourceId);
        this.resourceId = resourceId;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        removeAnimation = AnimationUtils.loadAnimation(context, android.R.anim.slide_out_right);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null == convertView ? inflater.inflate(resourceId, parent, false) : convertView;
        ListItem listItem = getItem(position);
        if (null != listItem && null != view) {
            listItem.setView(view);
            listItem.draw();
        }
        return view;
    }

    @Override
    public void addAll(Collection<? extends ListItem> collection) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            super.addAll(collection);
        } else {
            setNotifyOnChange(false);
            for (ListItem item: collection) {
                add(item);
            }
            setNotifyOnChange(true);
            notifyDataSetChanged();
        }
    }

    @Override
    public void remove(final ListItem listItem) {
        if (null == listItem || null == listItem.getView()) {
            return;
        }
        removeAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                ListAdapter.super.remove(listItem);
            }
        });
        listItem.getView().startAnimation(removeAnimation);
    }
}
