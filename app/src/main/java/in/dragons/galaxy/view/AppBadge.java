package in.dragons.galaxy.view;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;

import in.dragons.galaxy.NetworkState;
import in.dragons.galaxy.PreferenceActivity;
import in.dragons.galaxy.R;
import in.dragons.galaxy.model.App;
import in.dragons.galaxy.model.ImageSource;
import in.dragons.galaxy.task.LoadImageTask;

public abstract class AppBadge extends ListItem {

    static private WeakHashMap<Integer, LoadImageTask> tasks = new WeakHashMap<>();

    protected App app;
    protected List<String> line2 = new ArrayList<>();
    protected List<String> line3 = new ArrayList<>();

    public void setApp(App app) {
        this.app = app;
    }

    public App getApp() {
        return app;
    }

    @Override
    public void draw() {
        view.findViewById(R.id.progress).setVisibility(View.GONE);
        view.findViewById(R.id.app).setVisibility(View.VISIBLE);

        ((TextView) view.findViewById(R.id.text1)).setText(app.getDisplayName());
        setText(R.id.text2, TextUtils.join(" • ", line2));
        setText(R.id.text3, TextUtils.join(" • ", line3));

        drawIcon((ImageView) view.findViewById(R.id.icon));

        if(app.isTestingProgramOptedIn())
            view.findViewById(R.id.beta_user).setVisibility(View.VISIBLE);
        if(app.isTestingProgramAvailable())
            view.findViewById(R.id.beta_avail).setVisibility(View.VISIBLE);
        if(app.isEarlyAccess())
            view.findViewById(R.id.early_access).setVisibility(View.VISIBLE);
    }

    private void drawIcon(ImageView imageView) {
        ImageSource imageSource = app.getIconInfo();
        if (null != imageSource.getApplicationInfo() && !noImages()) {
            imageView.setImageDrawable(imageView.getContext().getPackageManager().getApplicationIcon(imageSource.getApplicationInfo()));
        } else if(!noImages()) {
            Picasso
                    .with(view.getContext())
                    .load(imageSource.getUrl())
                    .placeholder(R.drawable.ic_placeholder)
                    .into(imageView);
        }
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

    private boolean noImages() {
        return NetworkState.isMetered(view.getContext()) && PreferenceActivity.getBoolean(view.getContext(), PreferenceActivity.PREFERENCE_NO_IMAGES);
    }
}
