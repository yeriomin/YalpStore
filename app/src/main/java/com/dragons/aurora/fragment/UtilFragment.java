package com.dragons.aurora.fragment;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.TextView;

import com.dragons.aurora.PlayStoreApiAuthenticator;
import com.dragons.aurora.R;
import com.dragons.aurora.activities.LoginActivity;
import com.percolate.caffeine.PhoneUtils;
import com.percolate.caffeine.ViewUtils;

public abstract class UtilFragment extends AccountsHelper {

    protected boolean isLoggedIn() {
        return PreferenceFragment.getBoolean(getActivity(), "LOGGED_IN");
    }

    protected boolean isDummy() {
        return PreferenceFragment.getBoolean(getActivity(), "DUMMY_ACC");
    }

    protected boolean isGoogle() {
        return PreferenceFragment.getBoolean(getActivity(), "GOOGLE_ACC");
    }

    protected boolean isConnected(Context c) {
        return PhoneUtils.isNetworkAvailable(c);
    }

    protected void hide(View v, int viewID) {
        ViewUtils.findViewById(v, viewID).setVisibility(View.GONE);
    }

    protected void show(View v, int viewID) {
        ViewUtils.findViewById(v, viewID).setVisibility(View.VISIBLE);
    }

    protected void setText(View v, int viewId, String text) {
        TextView textView = ViewUtils.findViewById(v, viewId);
        if (null != textView)
            textView.setText(text);
    }

    protected void setText(View v, int viewId, int stringId, Object... text) {
        setText(v, viewId, v.getResources().getString(stringId, text));
    }

    protected void LoginFirst() {
        new AlertDialog.Builder(getActivity())
                .setTitle("Logged Out ?")
                .setMessage(R.string.header_usr_noEmail)
                .setPositiveButton("Login", (dialogInterface, i) -> startActivity(new Intent(getActivity(), LoginActivity.class)))
                .setCancelable(false)
                .show();
    }

    protected void refreshMyToken() {
        RefreshTokenTask task = new RefreshTokenTask();
        task.setCaller(playStoreTask);
        task.setContext(this.getActivity());
        task.execute();
    }

    public void switchDummy() {
        LoginTask task = new LoginTask();
        task.setCaller(playStoreTask);
        task.setContext(getActivity());
        task.prepareDialog(R.string.dialog_message_switching_in_predefined, R.string.dialog_title_switching);
        task.execute();
    }

    public void switchGoogle() {
        new PlayStoreApiAuthenticator(getActivity()).logout();
        logInWithGoogleAccount();
    }
}
