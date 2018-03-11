package com.github.yeriomin.yalpstore.task;

import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import com.github.yeriomin.yalpstore.model.ImageSource;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class AvatarTask extends LoadImageTask {

    private static final String PREFERENCE_KEY = "AVATAR_URL_";
    private static final String PICASA_URL = "http://picasaweb.google.com/data/entry/api/user/%s?alt=json";

    private String email;

    public AvatarTask(String email, ImageView avatarView) {
        super(avatarView);
        this.email = email;
    }

    @Override
    protected Void doInBackground(ImageSource... params) {
        String url = loadSavedAvatarUrl();
        if (!TextUtils.isEmpty(url)) {
            url = getAvatarUrl(getJsonString(String.format(PICASA_URL, email)));
            save(url);
        }
        if (TextUtils.isEmpty(url)) {
            return null;
        }
        ImageSource imageSource = new ImageSource(url);
        return super.doInBackground(imageSource);
    }

    private String getAvatarUrl(String jsonString) {
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            if (!jsonObject.has("entry")
                || !jsonObject.getJSONObject("entry").has("gphoto$thumbnail")
                || !jsonObject.getJSONObject("entry").getJSONObject("gphoto$thumbnail").has("$t")
            ) {
                Log.w(getClass().getSimpleName(), "Required property not found, unexpected json structure");
                return null;
            }
            return jsonObject.getJSONObject("entry").getJSONObject("gphoto$thumbnail").getString("$t");
        } catch (JSONException e) {
            Log.w(getClass().getSimpleName(), "JSON invalid: " + e.getMessage());
        }
        return null;
    }

    private String getJsonString(String url) {
        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) new URL(url).openConnection();
            InputStream inputStream = urlConnection.getInputStream();
            BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder sb = new StringBuilder(inputStream.available());
            String line;
            while ((line = r.readLine()) != null) {
                sb.append(line).append('\n');
            }
            return sb.toString();
        } catch (MalformedURLException e) {
            Log.w(getClass().getSimpleName(), "Could not build a valid picasa url with email " + email + " " + e.getMessage());
        } catch (IOException e) {
            Log.w(getClass().getSimpleName(), "Could not get picasa user info for email " + email + " " + e.getMessage());
        } finally {
            if (null != urlConnection) {
                urlConnection.disconnect();
            }
        }
        return null;
    }

    private void save(String url) {
        PreferenceManager.getDefaultSharedPreferences(imageView.getContext()).edit().putString(PREFERENCE_KEY + email, url).apply();
    }

    private String loadSavedAvatarUrl() {
        return PreferenceManager.getDefaultSharedPreferences(imageView.getContext()).getString(PREFERENCE_KEY + email, "");
    }
}
