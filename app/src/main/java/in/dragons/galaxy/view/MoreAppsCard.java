package in.dragons.galaxy.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import in.dragons.galaxy.R;
import in.dragons.galaxy.activities.CategoryAppsActivity;
import in.dragons.galaxy.adapters.MApps_Adapter;

public class MoreAppsCard extends RelativeLayout {

    String title, category, json_file;
    TextView card_title;
    Button more_apps;
    RecyclerView apps_recycler;

    public MoreAppsCard(Context context) {
        super(context);
        init(context, null);
    }

    public MoreAppsCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        View view = inflate(context, R.layout.more_apps_card_adapter, this);
        card_title = view.findViewById(R.id.m_apps_title);
        more_apps = view.findViewById(R.id.m_apps_more);
        apps_recycler = view.findViewById(R.id.m_apps_recycler);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.MoreAppsCard, 0, 0);
        try {
            title = a.getString(R.styleable.MoreAppsCard_CardTitle);
            category = a.getString(R.styleable.MoreAppsCard_AppsCategory);
            json_file = a.getString(R.styleable.MoreAppsCard_JsonName);
        } finally {
            a.recycle();
        }
        if (category != null && json_file != null) {
            JsonParser(context, json_file);
        }
        if (title != null) {
            card_title.setText(title);
        } else {
            card_title.setText("Title Attr not found");
        }
        more_apps.setOnClickListener(v -> CategoryAppsActivity.start(context, category));

    }

    private void JsonParser(Context context, String json_file) {
        RequestQueue mRequestQueue = Volley.newRequestQueue(context);
        List<MApps_Adapter.MAppsHolder> MApps_Adapter = new ArrayList<>();
        String JSON_PATH = "https://raw.githubusercontent.com/GalaxyStore/MetaData/master/" + json_file + ".json";
        JsonArrayRequest req = new JsonArrayRequest(JSON_PATH,
                response -> {
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject inst = (JSONObject) response.get(i);
                            MApps_Adapter adapter = new MApps_Adapter(MApps_Adapter, context);
                            MApps_Adapter.MAppsHolder apps = new MApps_Adapter.MAppsHolder(
                                    inst.getString("title"),
                                    inst.getString("id"),
                                    inst.getString("icon"));
                            MApps_Adapter.add(apps);
                            apps_recycler.setAdapter(adapter);
                            apps_recycler.setLayoutManager(new LinearLayoutManager(context,
                                    LinearLayoutManager.HORIZONTAL, false));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.w("JSON_ERROR", "Error: " + e.getMessage());
                    }
                }, error -> Log.w("JSON_ERROR", "Error: " + error.getMessage()));
        mRequestQueue.add(req);
    }
}
