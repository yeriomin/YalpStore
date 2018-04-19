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

import com.dragons.aurora.adapters.FeaturedAdapter;

public class TopFreeGames extends CustomRecycler {

    String JSON_PATH = "https://raw.githubusercontent.com/GalaxyStore/MetaData/master/free_games.json";

    public TopFreeGames(Context context) {
        super(context);
        init(context, null, 0);
    }

    public TopFreeGames(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public TopFreeGames(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        setHorizontalScrollBarEnabled(true);
        JsonParser(context, JSON_PATH);
    }
}
