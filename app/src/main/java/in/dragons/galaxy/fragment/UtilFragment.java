package in.dragons.galaxy.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.dragons.aurora.playstoreapiv2.AuthException;
import com.percolate.caffeine.PhoneUtils;
import com.percolate.caffeine.ViewUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import in.dragons.galaxy.ContextUtil;
import in.dragons.galaxy.CredentialsEmptyException;
import in.dragons.galaxy.GoogleAccountInfo;
import in.dragons.galaxy.PlayStoreApiAuthenticator;
import in.dragons.galaxy.R;
import in.dragons.galaxy.Util;
import in.dragons.galaxy.activities.AccountsActivity;
import in.dragons.galaxy.activities.LoginActivity;
import in.dragons.galaxy.builders.AccountTypeDialogBuilder;
import in.dragons.galaxy.builders.CredentialsDialogBuilder;
import in.dragons.galaxy.builders.UserProvidedAccountDialogBuilder;
import in.dragons.galaxy.task.playstore.PlayStoreTask;

public abstract class UtilFragment extends Fragment {

    static private final String USED_EMAILS_SET = "USED_EMAILS_SET";
    protected PlayStoreTask playStoreTask;

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

    protected void checkOut() {
        PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putBoolean("LOGGED_IN", false).apply();
        PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putString("GOOGLE_NAME", "").apply();
        PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putString("GOOGLE_URL", "").apply();
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

    protected void setGooglePrefs(String email, String password) {
        PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putBoolean("SEC_ACCOUNT", true).apply();
        PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putString("GOOGLE_EMAIL", email).apply();
        PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putString("GOOGLE_PASSWORD", password).apply();
    }

    protected void removeGooglePrefs() {
        PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putBoolean("SEC_ACCOUNT", false).apply();
        PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putString("GOOGLE_EMAIL", "").apply();
        PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putString("GOOGLE_PASSWORD", "").apply();
    }

    private AutoCompleteTextView getEmailInput(Dialog ad) {
        AutoCompleteTextView editEmail = (AutoCompleteTextView) ad.findViewById(R.id.email);
        editEmail.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, getUsedEmails()));
        String previousEmail = "";
        editEmail.setText(PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(PlayStoreApiAuthenticator.PREFERENCE_EMAIL, previousEmail));
        return editEmail;
    }

    private List<String> getUsedEmails() {
        List<String> emails = new ArrayList<>(Util.getStringSet(getActivity(), USED_EMAILS_SET));
        Collections.sort(emails);
        return emails;
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

    public void withSavedGoogle() {
        String email = PreferenceFragment.getString(getActivity(), "GOOGLE_EMAIL");
        String password = PreferenceFragment.getString(getActivity(), "GOOGLE_PASSWORD");
        getUserCredentialsTask().execute(email, password);
    }

    public void logInWithGoogleAccount() {
        Dialog ad = new Dialog(getActivity());
        ad.setContentView(R.layout.credentials_dialog_layout);
        ad.setTitle(getActivity().getString(R.string.credentials_title));
        ad.setCancelable(false);

        AutoCompleteTextView editEmail = getEmailInput(ad);
        EditText editPassword = ad.findViewById(R.id.password);
        final CheckBox checkBox = ad.findViewById(R.id.checkboxSave);

        editEmail.setText("");

        ad.findViewById(R.id.button_exit).setOnClickListener(v -> ad.dismiss());
        ad.findViewById(R.id.button_ok).setOnClickListener(view -> {
            Context c = view.getContext();
            String email = editEmail.getText().toString();
            String password = editPassword.getText().toString();
            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                ContextUtil.toast(c.getApplicationContext(), R.string.error_credentials_empty);
                return;
            }
            ad.dismiss();

            if (checkBox.isChecked()) {
                setGooglePrefs(email, password);
            }
            getUserCredentialsTask().execute(email, password);
        });
        ad.findViewById(R.id.toggle_password_visibility).setOnClickListener(v -> {
            boolean passwordVisible = !TextUtils.isEmpty((String) v.getTag());
            v.setTag(passwordVisible ? null : "tag");
            ((ImageView) v).setImageResource(passwordVisible ? R.drawable.ic_visibility_on : R.drawable.ic_visibility_off);
            editPassword.setInputType(passwordVisible ? InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD : InputType.TYPE_CLASS_TEXT);
        });

        ad.show();
    }

    protected void LoginFirst() {
        new AlertDialog.Builder(getActivity())
                .setTitle("Logged Out ?")
                .setMessage(R.string.header_usr_noEmail)
                .setPositiveButton("Login", (dialogInterface, i) -> startActivity(new Intent(getActivity(), LoginActivity.class)))
                .setCancelable(false)
                .show();
    }

    private UserProvidedCredentialsTask getUserCredentialsTask() {
        UserProvidedCredentialsTask task = new UserProvidedCredentialsTask();
        task.setCaller(playStoreTask);
        task.setContext(getActivity());
        task.prepareDialog(R.string.dialog_message_logging_in_provided_by_user, R.string.dialog_title_logging_in);
        return task;
    }

    private static class RefreshTokenTask extends AccountTypeDialogBuilder.AppProvidedCredentialsTask {

        @Override
        public void setCaller(PlayStoreTask caller) {
            super.setCaller(caller);
        }

        @Override
        protected void payload() throws IOException {
            new PlayStoreApiAuthenticator(context).refreshToken();
        }

        @Override
        protected void onPostExecute(Void result) {
            InstalledAppsFragment.newInstance();
            UpdatableAppsFragment.newInstance();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class LoginTask extends AccountTypeDialogBuilder.AppProvidedCredentialsTask {

        @Override
        protected void payload() throws IOException {
            new PlayStoreApiAuthenticator(context).login();
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean("LOGGED_IN", true).apply();
            PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean("DUMMY_ACC", true).apply();
            PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean("GOOGLE_ACC", false).apply();

            AccountsActivity accountsActivity = (AccountsActivity) getActivity();
            accountsActivity.userChanged();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class UserProvidedCredentialsTask extends CredentialsDialogBuilder.CheckCredentialsTask {

        private String previousEmail;
        private String UNKNOWN = "Unknown user.";
        private String PICASAWEB = "picasaweb";
        private String Email, Name, Url;

        @Override
        protected CredentialsDialogBuilder getDialogBuilder() {
            return new UserProvidedAccountDialogBuilder(context).setPreviousEmail(previousEmail);
        }

        @Override
        protected Void doInBackground(String[] params) {
            if (params.length < 2
                    || params[0] == null
                    || params[1] == null
                    || TextUtils.isEmpty(params[0])
                    || TextUtils.isEmpty(params[1])
                    ) {
                exception = new CredentialsEmptyException();
                return null;
            }
            previousEmail = params[0];
            try {
                new PlayStoreApiAuthenticator(context).login(params[0], params[1]);
                addUsedEmail(params[0]);
            } catch (Throwable e) {
                if (e instanceof AuthException && null != ((AuthException) e).getTwoFactorUrl()) {
                    addUsedEmail(params[0]);
                }
                exception = e;
            }
            return null;
        }

        private void addUsedEmail(String email) {
            Set<String> emailsSet = Util.getStringSet(context, USED_EMAILS_SET);
            emailsSet.add(email);
            Util.putStringSet(context, USED_EMAILS_SET, emailsSet);
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (success()) {
                PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean("LOGGED_IN", true).apply();
                PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean("GOOGLE_ACC", true).apply();
                PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean("DUMMY_ACC", false).apply();
                setUser();
            }
        }

        @SuppressLint("StaticFieldLeak")
        private void setUser() {
            Email = PreferenceFragment.getString(context, PlayStoreApiAuthenticator.PREFERENCE_EMAIL);
            new GoogleAccountInfo(Email) {
                @Override
                public void onPostExecute(String result) {
                    parseRAW(result);
                }
            }.execute();
        }

        private void parseRAW(String rawData) {
            if (rawData.contains(PICASAWEB) && !rawData.contains(UNKNOWN)) {
                Name = rawData.substring(rawData.indexOf("<name>") + 6, rawData.indexOf("</name>"));
                Url = rawData.substring(rawData.indexOf("<gphoto:thumbnail>") + 18, rawData.lastIndexOf("</gphoto:thumbnail>"));
            } else {
                Name = Email;
                Url = "I dont fucking care";
            }

            PreferenceManager.getDefaultSharedPreferences(context).edit().putString("GOOGLE_NAME", Name).apply();
            PreferenceManager.getDefaultSharedPreferences(context).edit().putString("GOOGLE_URL", Url).apply();

            AccountsActivity accountsActivity = (AccountsActivity) getActivity();
            accountsActivity.userChanged();
        }
    }
}
