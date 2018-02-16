package in.dragons.galaxy.task.playstore;

import com.github.yeriomin.playstoreapi.GooglePlayAPI;

import java.io.IOException;

import in.dragons.galaxy.ContextUtil;
import in.dragons.galaxy.R;
import in.dragons.galaxy.model.App;

public class FlagTask extends PlayStorePayloadTask<Boolean> implements CloneableTask {

    private App app;
    private GooglePlayAPI.ABUSE reason;
    private String explanation;

    public FlagTask setApp(App app) {
        this.app = app;
        return this;
    }

    public FlagTask setReason(GooglePlayAPI.ABUSE reason) {
        this.reason = reason;
        return this;
    }

    public FlagTask setExplanation(String explanation) {
        this.explanation = explanation;
        return this;
    }

    @Override
    public FlagTask clone() {
        FlagTask task = new FlagTask();
        task.setContext(context);
        task.setApp(app);
        task.setReason(reason);
        task.setExplanation(explanation);
        return task;
    }

    @Override
    protected Boolean getResult(GooglePlayAPI api, String... arguments) throws IOException {
        return api.reportAbuse(app.getPackageName(), reason, explanation);
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);
        if (success()) {
            ContextUtil.toast(context, R.string.content_flagged);
        }
    }
}
