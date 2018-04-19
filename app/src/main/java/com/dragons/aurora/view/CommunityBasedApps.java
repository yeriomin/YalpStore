package com.dragons.aurora.view;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import com.dragons.aurora.R;
import com.dragons.aurora.adapters.CommunityBasedAppsAdapter;

public class CommunityBasedApps extends RelativeLayout {

    private RecyclerView cbased_recycler;

    public CommunityBasedApps(Context context) {
        super(context);
        init(context, null, 0);
    }

    public CommunityBasedApps(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public CommunityBasedApps(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        View view = inflate(context, R.layout.community_based_card, this);
        cbased_recycler = view.findViewById(R.id.cbased_recycler);
        JsonParser(context);
    }

    private void JsonParser(Context context) {
        List<CommunityBasedAppsAdapter.FeaturedHolder> FeaturedAppsHolder = new ArrayList<>();
        RequestQueue mRequestQueue = Volley.newRequestQueue(context);
        String JSON_PATH = "https://raw.githubusercontent.com/GalaxyStore/MetaData/master/community_apps.json";
        JsonArrayRequest req = new JsonArrayRequest(JSON_PATH,
                response -> {
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject inst = (JSONObject) response.get(i);
                            CommunityBasedAppsAdapter adapter = new CommunityBasedAppsAdapter(FeaturedAppsHolder);
                            CommunityBasedAppsAdapter.FeaturedHolder apps = new CommunityBasedAppsAdapter.FeaturedHolder(
                                    inst.getString("title"),
                                    inst.getString("id"),
                                    inst.getString("developer"),
                                    inst.getString("icon"),
                                    inst.getDouble("rating"),
                                    inst.getString("price"));
                            FeaturedAppsHolder.add(apps);
                            cbased_recycler.setAdapter(adapter);
                            cbased_recycler.setLayoutManager(new GridLayoutManager(context, 4));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.w("JSON_ERROR", "Error: " + e.getMessage());
                    }
                }, error -> Log.w("JSON_ERROR", "Error: " + error.getMessage()));
        mRequestQueue.add(req);
    }
}
