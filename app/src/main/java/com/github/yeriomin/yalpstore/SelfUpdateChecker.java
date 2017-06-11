package com.github.yeriomin.yalpstore;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.github.yeriomin.playstoreapi.AndroidAppDeliveryData;
import com.github.yeriomin.yalpstore.model.App;

import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

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
        button.setVisibility(View.VISIBLE);
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

        downloader.download(app, deliveryData, null);
    }

    private String getHtml() throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(FDROID_APP_PAGE).openConnection();
        InputStream in = connection.getInputStream();
        return new String(toByteArray(in));
    }

    private byte[] toByteArray(InputStream in) throws IOException {
        byte[] buffer = new byte[2048];
        int bytesRead;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        while ((bytesRead = in.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, bytesRead);
        }
        return byteArrayOutputStream.toByteArray();
    }
}
