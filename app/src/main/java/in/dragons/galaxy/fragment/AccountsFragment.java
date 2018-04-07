package in.dragons.galaxy.fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.ImageViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.percolate.caffeine.ViewUtils;
import com.squareup.picasso.Picasso;

import in.dragons.galaxy.CircleTransform;
import in.dragons.galaxy.PlayStoreApiAuthenticator;
import in.dragons.galaxy.R;
import in.dragons.galaxy.activities.LoginActivity;

public class AccountsFragment extends UtilFragment {

    private SharedPreferences sharedPreferences;
    private String Email;
    private View v;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRetainInstance(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (v != null) {
            if ((ViewGroup) v.getParent() != null)
                ((ViewGroup) v.getParent()).removeView(v);
            return v;
        }

        v = inflater.inflate(R.layout.app_acc_inc, container, false);
        getActivity().setTitle(R.string.action_accounts);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        Email = sharedPreferences.getString(PlayStoreApiAuthenticator.PREFERENCE_EMAIL, "");

        if (isLoggedIn() && isGoogle()) {
            drawGoogle();
        } else if (isLoggedIn() && isDummy())
            drawDummy();
        setFab();

        return v;
    }

    public void drawDummy() {
        ViewUtils.findViewById(v, R.id.dummy_container).setVisibility(View.VISIBLE);
        ViewUtils.findViewById(v, R.id.no_dummy).setVisibility(View.GONE);

        ImageViewCompat.setImageTintList(ViewUtils.findViewById(v, R.id.dummy_ind),
                ColorStateList.valueOf((getResources().getColor(R.color.colorRed))));

        TextView dummyEmail = ViewUtils.findViewById(v, R.id.dummy_email);
        dummyEmail.setText(Email);

        setText(R.id.dummy_gsf, R.string.device_gsfID,
                PreferenceFragment.getString(getActivity(),
                        PlayStoreApiAuthenticator.PREFERENCE_GSF_ID));

        Button logout = ViewUtils.findViewById(v, R.id.account_logout);
        logout.setOnClickListener(v -> showLogOutDialog());
    }

    public void drawGoogle() {
        ViewUtils.findViewById(v, R.id.google_container).setVisibility(View.VISIBLE);
        ViewUtils.findViewById(v, R.id.no_google).setVisibility(View.GONE);

        ImageViewCompat.setImageTintList(ViewUtils.findViewById(v, R.id.google_ind),
                ColorStateList.valueOf((getResources().getColor(R.color.colorGreen))));

        ViewUtils.setText(v, R.id.google_name, sharedPreferences.getString("GOOGLE_NAME", ""));
        ViewUtils.setText(v, R.id.google_email, Email);

        setText(R.id.google_gsf, R.string.device_gsfID,
                PreferenceFragment.getString(getActivity(), PlayStoreApiAuthenticator.PREFERENCE_GSF_ID));

        Button button = ViewUtils.findViewById(v, R.id.google_logout);
        button.setOnClickListener(v -> showLogOutDialog());

        if (isConnected())
            loadAvatar(PreferenceFragment.getString(getActivity(), "GOOGLE_URL"));
    }

    public void setFab() {
        FloatingActionButton dummyFab = ViewUtils.findViewById(v, R.id.dummy_login);
        dummyFab.setOnClickListener(v -> logInWithPredefinedAccount());

        FloatingActionButton googleFab = ViewUtils.findViewById(v, R.id.google_login);
        googleFab.setOnClickListener(view -> logInWithGoogleAccount());
    }

    public void loadAvatar(String Url) {
        Picasso.with(getActivity())
                .load(Url)
                .placeholder(R.drawable.ic_user_placeholder)
                .transform(new CircleTransform())
                .into(((ImageView) v.findViewById(R.id.google_avatar)));
    }

    protected void setText(int viewId, String text) {
        TextView textView = ViewUtils.findViewById(v, viewId);
        if (null != textView)
            textView.setText(text);
    }

    protected void setText(int viewId, int stringId, Object... text) {
        setText(viewId, this.getString(stringId, text));
    }

    AlertDialog showLogOutDialog() {
        return new AlertDialog.Builder(getActivity())
                .setMessage(R.string.dialog_message_logout)
                .setTitle(R.string.dialog_title_logout)
                .setPositiveButton(android.R.string.yes, (dialogInterface, i) -> {
                    checkOut();
                    new PlayStoreApiAuthenticator(getActivity().getApplicationContext()).logout();
                    dialogInterface.dismiss();
                    getActivity().finish();
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }
}