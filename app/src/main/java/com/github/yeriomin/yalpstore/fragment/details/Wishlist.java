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

package com.github.yeriomin.yalpstore.fragment.details;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;

import com.github.yeriomin.yalpstore.R;
import com.github.yeriomin.yalpstore.WishlistActivity;
import com.github.yeriomin.yalpstore.YalpStoreActivity;
import com.github.yeriomin.yalpstore.YalpStoreApplication;
import com.github.yeriomin.yalpstore.fragment.Abstract;
import com.github.yeriomin.yalpstore.model.App;
import com.github.yeriomin.yalpstore.task.playstore.WishlistToggleTask;
import com.github.yeriomin.yalpstore.task.playstore.WishlistUpdateTask;

import java.util.List;

public class Wishlist extends Abstract {

    public Wishlist(YalpStoreActivity activity, App app) {
        super(activity, app);
    }

    @Override
    public void draw() {
        ImageView wishlistButton = activity.findViewById(R.id.wishlist);
        if (app.isInstalled()) {
            wishlistButton.setVisibility(View.GONE);
            return;
        }
        new DetailsWishlistUpdateTask(activity, app.getPackageName()).execute();
        initWishlistButton(wishlistButton, activity, app.getPackageName());
        wishlistButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                activity.startActivity(new Intent(activity, WishlistActivity.class));
                return true;
            }
        });
    }

    static private void initWishlistButton(ImageView wishlistButton, final YalpStoreActivity activity, final String packageName) {
        wishlistButton.setVisibility(View.VISIBLE);
        wishlistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setOnClickListener(null);
                DetailsWishlistToggleTask task = new DetailsWishlistToggleTask(activity);
                task.setPackageName(packageName);
                task.execute();
            }
        });
    }

    static private class DetailsWishlistToggleTask extends WishlistToggleTask {

        public DetailsWishlistToggleTask(YalpStoreActivity activity) {
            setContext(activity);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            YalpStoreActivity activity = (YalpStoreActivity) context;
            ImageView wishlistButton = activity.findViewById(R.id.wishlist);
            wishlistButton.setImageResource(YalpStoreApplication.wishlist.contains(packageName) ? R.drawable.ic_wishlist_tick : R.drawable.ic_wishlist_plus);
            initWishlistButton(wishlistButton, activity, packageName);
        }
    }

    static private class DetailsWishlistUpdateTask extends WishlistUpdateTask {

        private String packageName;

        public DetailsWishlistUpdateTask(YalpStoreActivity activity, String packageName) {
            this.packageName = packageName;
            setContext(activity);
        }

        @Override
        protected void onPostExecute(List<String> result) {
            super.onPostExecute(result);
            ((ImageView) ((YalpStoreActivity) context).findViewById(R.id.wishlist))
                .setImageResource(YalpStoreApplication.wishlist.contains(packageName) ? R.drawable.ic_wishlist_tick : R.drawable.ic_wishlist_plus);
        }
    }
}
