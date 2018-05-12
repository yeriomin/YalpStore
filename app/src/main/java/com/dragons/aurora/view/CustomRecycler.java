package com.dragons.aurora.view;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import com.dragons.aurora.adapters.FeaturedAppsAdapter;

public class CustomRecycler extends RecyclerView {

    public CustomRecycler(Context context) {
        super(context);
    }

    public CustomRecycler(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomRecycler(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
