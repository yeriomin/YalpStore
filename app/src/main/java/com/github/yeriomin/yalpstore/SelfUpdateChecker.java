package com.github.yeriomin.yalpstore;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.github.yeriomin.playstoreapi.AndroidAppDeliveryData;
import com.github.yeriomin.yalpstore.model.App;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SelfUpdateChecker extends AsyncTask<Void, Void, Void> {

    static private final String FDROID_APP_PAGE = "https://f-droid.org/repository/browse/?fdid=" + BuildConfig.APPLICATION_ID;

    private Context context;
    private FdroidLinkExtractor parser;
    private TextView button;

    public void setButton(TextView button) {
        this.button = button;
    }

    public SelfUpdateChecker(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        button.setText(R.string.about_self_update_checking);
    }

    @Override
    protected Void doInBackground(Void... params) {
        String html;
        try {
            html = getHtml();
        } catch (IOException e) {
            Log.w(getClass().getName(), "Could not download app page: " + e.getMessage());
            return null;
        }
        try {
            parser = new FdroidLinkExtractor(html);
        } catch (XmlPullParserException e) {
            Log.w(getClass().getName(), "HTML parsing error: " + e.getMessage());
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if (isVersionLatest()) {
            button.setText(R.string.about_self_update_unavailable);
        } else {
            button.setText(context.getString(R.string.about_self_update_available, parser.getLatestVersionCode()));
            button.setTextColor(0xFF5555FF);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    downloadLatest();
                }
            });
        }
    }

    private boolean isVersionLatest() {
        if (null != parser) {
            return parser.getLatestVersionCode() <= BuildConfig.VERSION_CODE;
        }
        return true;
    }

    private void downloadLatest() {
        App app = new App();
        app.getPackageInfo().packageName = BuildConfig.APPLICATION_ID;
        app.setDisplayName(context.getString(R.string.app_name));
        app.setVersionCode(parser.getLatestVersionCode());
        Downloader downloader = new Downloader(context);

        AndroidAppDeliveryData deliveryData = AndroidAppDeliveryData.newBuilder()
            .setDownloadUrl(parser.getLatestLink())
            .build();

        downloader.download(app, deliveryData);
    }

    private String getHtml() throws IOException{
        Response response = new OkHttpClient()
            .newCall(new Request.Builder().url(FDROID_APP_PAGE).build())
            .execute();
        if (!response.isSuccessful()) {
            throw new IOException("Unexpected code " + response.code());
        }
        return response.body().string();
    }

}
