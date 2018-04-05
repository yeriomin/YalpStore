package in.dragons.galaxy.view;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
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

import in.dragons.galaxy.adapters.CommunityBasedAppsAdapter;

public class CommunityBasedApps extends RecyclerView {

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
        List<CommunityBasedAppsAdapter.FeaturedHolder> FeaturedAppsHolder = new ArrayList<>();
        String JSON_PATH = "https://raw.githubusercontent.com/GalaxyStore/MetaData/master/community_apps.json";
        JsonArrayRequest req = new JsonArrayRequest(JSON_PATH,
                response -> {
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject inst = (JSONObject) response.get(i);
                            CommunityBasedAppsAdapter adapter = new CommunityBasedAppsAdapter(FeaturedAppsHolder, context);
                            CommunityBasedAppsAdapter.FeaturedHolder apps = new CommunityBasedAppsAdapter.FeaturedHolder(
                                    inst.getString("app_name"),
                                    inst.getString("app_packagename"),
                                    inst.getString("app_icon"));
                            FeaturedAppsHolder.add(apps);
                            setAdapter(adapter);
                            setLayoutManager(new GridLayoutManager(context, 3));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.w("JSON_ERROR", "Error: " + e.getMessage());
                    }
                }, error -> Log.w("JSON_ERROR", "Error: " + error.getMessage()));
        mRequestQueue.add(req);
    }


}
