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

package com.github.yeriomin.yalpstore.task.playstore;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.yeriomin.yalpstore.R;
import com.github.yeriomin.yalpstore.SpoofDeviceManager;
import com.github.yeriomin.yalpstore.YalpStoreApplication;
import com.github.yeriomin.yalpstore.model.ImageSource;
import com.github.yeriomin.yalpstore.task.LoadCircularImageTask;
import com.github.yeriomin.yalpstore.task.LoadImageTask;

import java.lang.ref.WeakReference;
import java.util.Map;

public class NavHeaderUpdateTask extends UserProfileTask {

    private WeakReference<ImageView> avatarViewRef = new WeakReference<>(null);
    private WeakReference<TextView> userNameViewRef = new WeakReference<>(null);
    private WeakReference<TextView> deviceViewRef = new WeakReference<>(null);

    public void setAvatarView(ImageView avatarView) {
        this.avatarViewRef = new WeakReference<>(avatarView);
    }

    public void setUserNameView(TextView userNameView) {
        this.userNameViewRef = new WeakReference<>(userNameView);
    }

    public void setDeviceView(TextView deviceView) {
        this.deviceViewRef = new WeakReference<>(deviceView);
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        if (null == avatarViewRef.get() || null == userNameViewRef.get() || null == deviceViewRef.get()) {
            return;
        }
        if (YalpStoreApplication.user.isLoggedIn()) {
            Map<String, String> devices = new SpoofDeviceManager(context).getDevices();
            if (!TextUtils.isEmpty(YalpStoreApplication.user.getDeviceDefinitionName())
                && devices.containsKey(YalpStoreApplication.user.getDeviceDefinitionName())
                ) {
                deviceViewRef.get().setText(devices.get(YalpStoreApplication.user.getDeviceDefinitionName()));
                deviceViewRef.get().setVisibility(View.VISIBLE);
            } else {
                deviceViewRef.get().setVisibility(View.INVISIBLE);
            }
            userNameViewRef.get().setText(YalpStoreApplication.user.getUserName());
            new LoadCircularImageTask(avatarViewRef.get())
                .setCropCircle(true)
                .setFadeInMillis(200)
                .setImageSource(new ImageSource(YalpStoreApplication.user.getUserPicUrl()))
                .executeOnExecutorIfPossible()
            ;
        } else {
            avatarViewRef.get().setImageResource(R.drawable.ic_placeholder);
            userNameViewRef.get().setText(R.string.auth_empty);
            deviceViewRef.get().setVisibility(View.INVISIBLE);
        }
    }
}
