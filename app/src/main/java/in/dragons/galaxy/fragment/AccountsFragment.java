package in.dragons.galaxy.fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
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

        if (isLoggedIn() && isGoogle())
            drawGoogle();
        else if (isLoggedIn() && isDummy())
            drawDummy();

        drawButtons();
        return v;
    }

    public void drawDummy() {
        TextView dummyEmail = ViewUtils.findViewById(v, R.id.account_email);
        dummyEmail.setText(Email);
        setAvatar(R.drawable.ic_dummy_avatar);
        setText(R.id.account_gsf, R.string.device_gsfID,
                PreferenceFragment.getString(getActivity(),
                        PlayStoreApiAuthenticator.PREFERENCE_GSF_ID));
        setText(R.id.account_name, "Dummy Account");
    }

    public void drawGoogle() {
        ViewUtils.setText(v, R.id.account_name, sharedPreferences.getString("GOOGLE_NAME", ""));
        ViewUtils.setText(v, R.id.account_email, Email);

        setText(R.id.account_gsf, R.string.device_gsfID,
                PreferenceFragment.getString(getActivity(),
                        PlayStoreApiAuthenticator.PREFERENCE_GSF_ID));

        if (isConnected())
            loadAvatar(PreferenceFragment.getString(getActivity(), "GOOGLE_URL"));
    }

    public void drawButtons() {
        Button logout = ViewUtils.findViewById(v, R.id.btn_logout);
        Button switchDummy = ViewUtils.findViewById(v, R.id.btn_switch);
        Button switchGoogle = ViewUtils.findViewById(v, R.id.btn_switchG);
        Button refreshToken = ViewUtils.findViewById(v, R.id.btn_refresh);
        TextView accWarn = ViewUtils.findViewById(v,R.id.acc_warn);

        if (isDummy()) {
            switchDummy.setVisibility(View.VISIBLE);
            refreshToken.setVisibility(View.VISIBLE);
            accWarn.setText(R.string.acc_dummy_detail);
        }

        if (isGoogle()) {
            switchGoogle.setVisibility(View.VISIBLE);
            accWarn.setText(R.string.acc_google_detail);
        }

        logout.setOnClickListener(view -> {
            showLogOutDialog();
        });

        switchGoogle.setOnClickListener(view -> {
            switchGoogle();
        });

        switchDummy.setOnClickListener(view -> switchDummy());
        refreshToken.setOnClickListener(view -> refreshMyToken());
    }

    public void setAvatar(int avatar) {
        ImageView avatar_view = v.findViewById(R.id.accounts_Avatar);
        avatar_view.setImageResource(avatar);
    }

    public void loadAvatar(String Url) {
        Picasso.with(getActivity())
                .load(Url)
                .placeholder(R.drawable.ic_dummy_avatar)
                .transform(new CircleTransform())
                .into(((ImageView) v.findViewById(R.id.accounts_Avatar)));
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
                    startActivity(new Intent(getContext(), LoginActivity.class));
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }
}