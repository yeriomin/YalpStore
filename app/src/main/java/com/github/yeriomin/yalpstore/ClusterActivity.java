package com.github.yeriomin.yalpstore;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

public class ClusterActivity extends EndlessScrollActivity {

    static private final String INTENT_URL = "INTENT_URL";
    static private final String INTENT_TITLE = "INTENT_TITLE";

    private String clusterUrl;
    private ClusterIterator clusterIterator;

    static public void start(Context context, String url, String title) {
        Intent intent = new Intent(context, ClusterActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra(ClusterActivity.INTENT_URL, url);
        intent.putExtra(ClusterActivity.INTENT_TITLE, title);
        context.startActivity(intent);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (TextUtils.isEmpty(intent.getStringExtra(ClusterActivity.INTENT_URL))
            || TextUtils.isEmpty(intent.getStringExtra(ClusterActivity.INTENT_TITLE))
        ) {
            Log.w(getClass().getName(), "No cluster url or title provided in the intent");
            finish();
            return;
        }

        setTitle(intent.getStringExtra(ClusterActivity.INTENT_TITLE));
        clusterUrl = intent.getStringExtra(ClusterActivity.INTENT_URL);
        clearApps();
        loadApps();
    }

    @Override
    public void loadApps() {
        ClusterTask task = new ClusterTask(clusterIterator) {

            @Override
            protected void onPostExecute(Throwable e) {
                super.onPostExecute(e);
                if (null == ClusterActivity.this.clusterIterator) {
                    clusterIterator = iterator;
                }
                addApps(apps);
                apps.clear();
            }
        };
        prepareTask(task).execute(clusterUrl);
    }
}
