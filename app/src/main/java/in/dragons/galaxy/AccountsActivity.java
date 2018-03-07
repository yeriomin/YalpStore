package in.dragons.galaxy;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.ImageViewCompat;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.percolate.caffeine.ViewUtils;
import com.squareup.picasso.Picasso;

public class AccountsActivity extends GalaxyActivity {

    AccountTypeDialogBuilder accountTypeDialogBuilder = new AccountTypeDialogBuilder(this);
    ImageView accInd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout contentFrameLayout = ViewUtils.findViewById(this, R.id.content_frame);
        getLayoutInflater().inflate(R.layout.app_acc_inc, contentFrameLayout);

        notifyConnected(this);

        Email = sharedPreferences.getString(PlayStoreApiAuthenticator.PREFERENCE_EMAIL, "");

        if (isValidEmail(Email) && isConnected()) {
            drawGoogle();
        } else if (isDummyEmail())
            drawDummy();

        setFab();
    }

    public void drawDummy() {
        ViewUtils.findViewById(this, R.id.dummy_container).setVisibility(View.VISIBLE);
        ViewUtils.findViewById(this, R.id.no_dummy).setVisibility(View.GONE);

        ImageViewCompat.setImageTintList(ViewUtils.findViewById(this, R.id.dummy_ind),
                ColorStateList.valueOf((getResources().getColor(R.color.colorRed))));

        TextView dummyEmail = ViewUtils.findViewById(this, R.id.dummy_email);
        dummyEmail.setText(Email);

        setText(R.id.dummy_gsf, R.string.device_gsfID, sharedPreferences.getString(PlayStoreApiAuthenticator.PREFERENCE_GSF_ID, ""));

        Button logout = ViewUtils.findViewById(this, R.id.account_logout);
        logout.setOnClickListener(v -> showLogOutDialog());

        Button switched = ViewUtils.findViewById(this, R.id.account_switch);
        switched.setOnClickListener(v -> accountTypeDialogBuilder.logInWithPredefinedAccount());
    }

    public void drawGoogle() {
        if (Email != "") {
            ViewUtils.findViewById(this, R.id.google_container).setVisibility(View.VISIBLE);
            ViewUtils.findViewById(this, R.id.no_google).setVisibility(View.GONE);

            ImageViewCompat.setImageTintList(ViewUtils.findViewById(this, R.id.google_ind),
                    ColorStateList.valueOf((getResources().getColor(R.color.colorGreen))));

            TextView googleName = ViewUtils.findViewById(this, R.id.google_name);
            googleName.setText(sharedPreferences.getString("GOOGLE_NAME", ""));

            TextView googleEmail = ViewUtils.findViewById(this, R.id.google_email);
            googleEmail.setText(Email);

            setText(R.id.google_gsf, R.string.device_gsfID, sharedPreferences.getString(PlayStoreApiAuthenticator.PREFERENCE_GSF_ID, ""));

            Button button = ViewUtils.findViewById(this, R.id.google_logout);
            button.setOnClickListener(v -> showLogOutDialog());

            loadAvatar(sharedPreferences.getString("GOOGLE_URL", ""));
        }
    }

    public void setFab() {
        FloatingActionButton dummyFab = ViewUtils.findViewById(this, R.id.dummy_login);
        dummyFab.setOnClickListener(view -> accountTypeDialogBuilder.logInWithPredefinedAccount());

        FloatingActionButton googleFab = ViewUtils.findViewById(this, R.id.google_login);
        googleFab.setOnClickListener(view -> accountTypeDialogBuilder.showCredentialsDialog());
    }

    public void loadAvatar(String Url) {
        Picasso.with(this)
                .load(Url)
                .placeholder(R.drawable.ic_user_placeholder)
                .transform(new CircleTransform())
                .into(((ImageView) findViewById(R.id.google_avatar)));
    }

    protected void setText(int viewId, String text) {
        TextView textView = ViewUtils.findViewById(this, viewId);
        if (null != textView)
            textView.setText(text);
    }

    protected void setText(int viewId, int stringId, Object... text) {
        setText(viewId, this.getString(stringId, text));
    }
}
