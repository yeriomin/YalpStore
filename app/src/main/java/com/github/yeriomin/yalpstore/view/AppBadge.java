package com.github.yeriomin.yalpstore.view;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.yeriomin.yalpstore.LoadIconTask;
import com.github.yeriomin.yalpstore.R;
import com.github.yeriomin.yalpstore.model.App;

import java.util.WeakHashMap;

public abstract class AppBadge extends ListItem {

    static private WeakHashMap<Integer, LoadIconTask> tasks = new WeakHashMap<>();

    protected App app;

    public void setApp(App app) {
        this.app = app;
    }

    public App getApp() {
        return app;
    }

    @Override
    public void draw() {
        view.findViewById(R.id.separator).setVisibility(View.GONE);
        view.findViewById(R.id.app).setVisibility(View.VISIBLE);

        ((TextView) view.findViewById(R.id.text1)).setText(app.getDisplayName());

        drawIcon((ImageView) view.findViewById(R.id.icon));
    }

    private void drawIcon(ImageView imageView) {
        String tag = (String) imageView.getTag();
        if (!TextUtils.isEmpty(tag) && tag.equals(app.getPackageName())) {
            return;
        }
        imageView.setTag(app.getPackageName());
        LoadIconTask task = new LoadIconTask(imageView);
        LoadIconTask previousTask = tasks.get(imageView.hashCode());
        if (null != previousTask) {
            previousTask.cancel(true);
        }
        tasks.put(imageView.hashCode(), task);
        task.execute(app.getIconInfo());
    }

    protected void setText(int viewId, String text) {
        TextView textView = (TextView) view.findViewById(viewId);
        if (!TextUtils.isEmpty(text)) {
            textView.setText(text);
            textView.setVisibility(View.VISIBLE);
        } else {
            textView.setVisibility(View.GONE);
        }
    }
}
