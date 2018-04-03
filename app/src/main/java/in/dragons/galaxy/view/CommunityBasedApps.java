package in.dragons.galaxy.view;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import in.dragons.galaxy.adapters.ComunityBasedAppsAdapter;

public class CommunityBasedApps extends RecyclerView {

    private String JSON_PATH = "https://raw.githubusercontent.com/GalaxyStore/MetaData/master/community_apps.json";
    private List<ComunityBasedAppsAdapter.FeaturedHolder> FeaturedAppsHolder;

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
        JsonParser(context);
    }

    private void JsonParser(Context context) {
        RequestQueue mRequestQueue = Volley.newRequestQueue(context);
        FeaturedAppsHolder = new ArrayList<>();
        JsonArrayRequest req = new JsonArrayRequest(JSON_PATH,
                response -> {
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject inst = (JSONObject) response.get(i);
                            FeaturedAppsHolder = new ArrayList<>();
                            ComunityBasedAppsAdapter adapter = new ComunityBasedAppsAdapter(FeaturedAppsHolder);
                            ComunityBasedAppsAdapter.FeaturedHolder apps = new ComunityBasedAppsAdapter.FeaturedHolder(inst.getString("app_name"), inst.getString("app_packagename"));
                            FeaturedAppsHolder.add(apps);
                            setAdapter(adapter);
                            setLayoutManager(new GridLayoutManager(context, 3));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.w("JSON_ERROR", "Error: " + e.getMessage());
                    }
                }, error -> {
            VolleyLog.d("Communication", "Error: " + error.getMessage());
            Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            Log.w("JSON_ERROR", "Error: " + error.getMessage());
        });
        mRequestQueue.add(req);
    }


}
