package com.dragons.aurora.fragment.details;


import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.dragons.aurora.R;
import com.dragons.aurora.activities.AuroraActivity;
import com.dragons.aurora.model.App;
import com.percolate.caffeine.ViewUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.android.volley.VolleyLog.TAG;

public class ExodusPrivacy extends Abstract {

    public ExodusPrivacy(AuroraActivity activity, App app) {
        super(activity, app);
    }

    @Override
    public void draw() {
        getExodusReport(activity, "https://reports.exodus-privacy.eu.org/api/search/" + app.getPackageName());
    }

    private void getExodusReport(Context context, String EXODUS_PATH) {
        RequestQueue mRequestQueue = Volley.newRequestQueue(context);
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                EXODUS_PATH, null, response -> {
            try {
                JSONObject exodusReport = response.getJSONObject(app.getPackageName());
                JSONArray reportsArray = exodusReport.getJSONArray("reports");
                JSONObject trackersReport = reportsArray.getJSONObject(0);
                JSONArray trackers = trackersReport.getJSONArray("trackers");
                String appId = trackersReport.getString("id");
                drawExodus(trackers, appId);
                Log.w("EXODUS_PRIVACY", trackers.toString());
            } catch (JSONException e) {
                Log.w("EXODUS_PRIVACY", "Error occurred at Exodus Privacy");
            }
        }, error -> VolleyLog.d(TAG, "Error: " + error.getMessage()));
        mRequestQueue.add(jsonObjReq);
    }

    private void drawExodus(JSONArray appTrackers, String appId) {
        ViewUtils.findViewById(activity, R.id.exodus_card).setVisibility(View.VISIBLE);
        if (appTrackers.length() > 0) {
            setText(R.id.exodus_description, R.string.exodus_hasTracker, appTrackers.length());
        } else {
            setText(R.id.exodus_description, R.string.exodus_noTracker);
        }

        TextView viewMore = activity.findViewById(R.id.viewMore);
        viewMore.setPaintFlags(viewMore.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        viewMore.setOnClickListener(click -> activity.startActivity(new Intent(Intent.ACTION_VIEW,
                Uri.parse("https://reports.exodus-privacy.eu.org/reports/" + appId + "/"))));
    }
}
