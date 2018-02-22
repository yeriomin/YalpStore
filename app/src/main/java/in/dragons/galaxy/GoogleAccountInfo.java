package in.dragons.galaxy;

import android.os.AsyncTask;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GoogleAccountInfo extends AsyncTask<Void, Void, String> {

    private String URL;

    GoogleAccountInfo(String URL) {
        this.URL = "http://picasaweb.google.com/data/entry/api/user/" +URL;
    }

    @Override
    protected String doInBackground(Void... voids) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(URL)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
