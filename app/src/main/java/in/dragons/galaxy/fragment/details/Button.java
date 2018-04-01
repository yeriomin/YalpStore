package in.dragons.galaxy.fragment.details;

import android.content.pm.PackageManager;
import android.view.View;

import in.dragons.galaxy.activities.GalaxyActivity;
import in.dragons.galaxy.model.App;

public abstract class Button extends Abstract {

    protected View button;
    protected Boolean isMisc;

    public Button(GalaxyActivity activity, App app) {
        super(activity, app);
        this.button = getButton();
        this.isMisc = getMisc();
    }

    abstract protected View getButton();

    abstract protected Boolean getMisc();

    abstract protected boolean shouldBeVisible();

    abstract protected void onButtonClick(View v, Boolean isMisc);

    @Override
    public void draw() {
        if (null == button) {
            return;
        }
        button.setEnabled(true);
        button.setVisibility(shouldBeVisible() ? View.VISIBLE : View.GONE);
        button.setOnClickListener(view -> onButtonClick(view, isMisc));
    }

    void disable(int stringId) {
        if (null == button) {
            return;
        }
        ((android.widget.Button) button).setText(stringId);
        button.setEnabled(false);
    }

    protected boolean isInstalled() {
        try {
            activity.getPackageManager().getPackageInfo(app.getPackageName(), 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}
