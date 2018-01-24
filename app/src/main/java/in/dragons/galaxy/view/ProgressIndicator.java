package in.dragons.galaxy.view;

import android.view.View;

import in.dragons.galaxy.R;

public class ProgressIndicator extends ListItem {

    @Override
    public void draw() {
        view.findViewById(R.id.separator).setVisibility(View.GONE);
        view.findViewById(R.id.app).setVisibility(View.GONE);
        view.findViewById(R.id.progress).setVisibility(View.VISIBLE);
    }
}
