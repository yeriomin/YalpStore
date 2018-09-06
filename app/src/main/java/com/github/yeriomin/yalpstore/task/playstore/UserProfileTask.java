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

import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.github.yeriomin.playstoreapi.GooglePlayAPI;
import com.github.yeriomin.playstoreapi.Image;
import com.github.yeriomin.playstoreapi.UserProfile;
import com.github.yeriomin.yalpstore.SqliteHelper;
import com.github.yeriomin.yalpstore.YalpStoreApplication;
import com.github.yeriomin.yalpstore.model.LoginInfoDao;

import java.io.IOException;

public class UserProfileTask extends PlayStorePayloadTask<Void> {

    @Override
    protected Void getResult(GooglePlayAPI api, String... arguments) throws IOException {
        if (YalpStoreApplication.user.appProvidedEmail() || !TextUtils.isEmpty(YalpStoreApplication.user.getUserPicUrl())) {
            return null;
        }
        UserProfile userProfile = api.userProfile().getUserProfile();
        YalpStoreApplication.user.setUserName(userProfile.getName());
        for (Image image: userProfile.getImageList()) {
            if (image.getImageType() == GooglePlayAPI.IMAGE_TYPE_APP_ICON) {
                YalpStoreApplication.user.setUserPicUrl(image.getImageUrl());
            }
        }
        SQLiteDatabase db = new SqliteHelper(context).getWritableDatabase();
        new LoginInfoDao(db).insert(YalpStoreApplication.user);
        db.close();
        return null;
    }
}
