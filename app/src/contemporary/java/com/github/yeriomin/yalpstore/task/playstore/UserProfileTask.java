package com.github.yeriomin.yalpstore.task.playstore;

import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.widget.ImageView;

import com.github.yeriomin.playstoreapi.GooglePlayAPI;
import com.github.yeriomin.playstoreapi.Image;
import com.github.yeriomin.playstoreapi.UserProfile;
import com.github.yeriomin.yalpstore.PreferenceUtil;
import com.github.yeriomin.yalpstore.model.ImageSource;
import com.github.yeriomin.yalpstore.task.LoadImageTask;

import java.io.IOException;

public class UserProfileTask extends PlayStorePayloadTask<String> {

    private static final String PREFERENCE_AVATAR_URL = "PREFERENCE_AVATAR_URL";

    private ImageView imageView;

    public UserProfileTask(ImageView imageView) {
        this.imageView = imageView;
        setContext(imageView.getContext());
    }

    @Override
    protected String getResult(GooglePlayAPI api, String... arguments) throws IOException {
        String avatarUrl = PreferenceUtil.getString(context, PREFERENCE_AVATAR_URL);
        if (!TextUtils.isEmpty(avatarUrl)) {
            return avatarUrl;
        }
        UserProfile userProfile = api.userProfile().getUserProfile();
        for (Image image: userProfile.getImageList()) {
            if (image.getImageType() == GooglePlayAPI.IMAGE_TYPE_APP_ICON) {
                return image.getImageUrl();
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if (success()) {
            PreferenceManager.getDefaultSharedPreferences(imageView.getContext()).edit().putString(PREFERENCE_AVATAR_URL, result).apply();
        }
        new LoadImageTask(imageView).setFadeInMillis(200).execute(new ImageSource(result));
    }
}
