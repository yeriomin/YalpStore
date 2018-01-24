package in.dragons.galaxy.fragment.details;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import in.dragons.galaxy.DetailsActivity;
import in.dragons.galaxy.R;
import in.dragons.galaxy.model.App;

public class Share extends Abstract {

    static private String PLAYSTORE_LINK_PREFIX= "https://play.google.com/store/apps/details?id=";

    public Share(DetailsActivity activity, App app) {
        super(activity, app);
    }

    @Override
    public void draw() {
        ImageView share = (ImageView) activity.findViewById(R.id.share);
        share.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_SUBJECT, app.getDisplayName());
                i.putExtra(Intent.EXTRA_TEXT, PLAYSTORE_LINK_PREFIX + app.getPackageName());
                activity.startActivity(Intent.createChooser(i, activity.getString(R.string.details_share)));
            }
        });
    }
}
