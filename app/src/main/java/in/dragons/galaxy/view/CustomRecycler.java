package in.dragons.galaxy.view;

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

import in.dragons.galaxy.adapters.FeaturedAdapter;

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

    public void JsonParser(Context context, String JSON_PATH) {
        RequestQueue mRequestQueue = Volley.newRequestQueue(context);
        List<FeaturedAdapter.FeaturedHolder> FeaturedAppsHolder = new ArrayList<>();
        JsonArrayRequest req = new JsonArrayRequest(JSON_PATH,
                response -> {
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject inst = (JSONObject) response.get(i);
                            FeaturedAdapter adapter = new FeaturedAdapter(FeaturedAppsHolder, context);
                            FeaturedAdapter.FeaturedHolder apps = new FeaturedAdapter
                                    .FeaturedHolder(inst.getString("title"),
                                    inst.getString("id"),
                                    inst.getString("developer"),
                                    inst.getString("icon"),
                                    inst.getDouble("rating"),
                                    inst.getString("price"));
                            FeaturedAppsHolder.add(apps);
                            setAdapter(adapter);
                            setLayoutManager(new LinearLayoutManager(context,
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
