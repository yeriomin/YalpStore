package in.dragons.galaxy.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.preference.PreferenceManager;
import android.support.v4.widget.ImageViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.yeriomin.playstoreapi.AuthException;
import com.percolate.caffeine.PhoneUtils;
import com.percolate.caffeine.ViewUtils;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import in.dragons.galaxy.CircleTransform;
import in.dragons.galaxy.ContextUtil;
import in.dragons.galaxy.CredentialsEmptyException;
import in.dragons.galaxy.GoogleAccountInfo;
import in.dragons.galaxy.PlayStoreApiAuthenticator;
import in.dragons.galaxy.R;
import in.dragons.galaxy.Util;
import in.dragons.galaxy.builders.AccountTypeDialogBuilder;
import in.dragons.galaxy.builders.CredentialsDialogBuilder;
import in.dragons.galaxy.builders.UserProvidedAccountDialogBuilder;
import in.dragons.galaxy.fragment.PreferenceFragment;
import in.dragons.galaxy.fragment.UtilFragment;
import in.dragons.galaxy.task.playstore.PlayStoreTask;

import static java.lang.Thread.sleep;

public class LoginActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    protected PlayStoreTask playStoreTask;
    private AutoCompleteTextView editEmail;
    private String Email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if (isConnected()) {
            init();
        }
        if (isLoggedIn()) {
            finish();
        }
    }

    private void init() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        Email = sharedPreferences.getString(PlayStoreApiAuthenticator.PREFERENCE_EMAIL, "");
        Button login_anonymous = findViewById(R.id.btn_ok_anm);
        editEmail = findViewById(R.id.emailg);
        login_anonymous.setOnClickListener(v -> {
            LoginTask task = new LoginTask();
            task.setCaller(playStoreTask);
            task.setContext(this);
            task.prepareDialog(R.string.dialog_message_logging_in_predefined, R.string.dialog_title_logging_in);
            task.execute();
        });
        EditText editPassword = findViewById(R.id.passwordg);
        Button login_google = findViewById(R.id.button_okg);
        login_google.setOnClickListener(view -> {
            Context c = view.getContext();
            String email = editEmail.getText().toString();
            String password = editPassword.getText().toString();
            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                ContextUtil.toast(c.getApplicationContext(), R.string.error_credentials_empty);
                return;
            }
            getUserCredentialsTask().execute(email, password);
        });
        ImageView toggle_password = findViewById(R.id.toggle_password_visibility);
        toggle_password.setOnClickListener(v -> {
            boolean passwordVisible = !TextUtils.isEmpty((String) v.getTag());
            v.setTag(passwordVisible ? null : "tag");
            ((ImageView) v).setImageResource(passwordVisible ? R.drawable.ic_visibility_on : R.drawable.ic_visibility_off);
            editPassword.setInputType(passwordVisible ? InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD : InputType.TYPE_CLASS_TEXT);
        });
    }

    private UserProvidedCredentialsTask getUserCredentialsTask() {
        UserProvidedCredentialsTask task = new UserProvidedCredentialsTask();
        task.setCaller(playStoreTask);
        task.setContext(this);
        task.prepareDialog(R.string.dialog_message_logging_in_provided_by_user, R.string.dialog_title_logging_in);
        return task;
    }

    @SuppressLint("StaticFieldLeak")
    private class UserProvidedCredentialsTask extends CredentialsDialogBuilder.CheckCredentialsTask {

        private String previousEmail;
        private String UNKNOWN = "Unknown user.";
        private String PICASAWEB = "picasaweb";
        private String Name, Url;

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
            Set<String> emailsSet = Util.getStringSet(context, "USED_EMAILS_SET");
            emailsSet.add(email);
            Util.putStringSet(context, "USED_EMAILS_SET", emailsSet);
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (success()) {
                PreferenceManager.getDefaultSharedPreferences(LoginActivity.this).edit().putBoolean("LOGGED_IN", true).apply();
                PreferenceManager.getDefaultSharedPreferences(LoginActivity.this).edit().putBoolean("GOOGLE_ACC", true).apply();
                PreferenceManager.getDefaultSharedPreferences(LoginActivity.this).edit().putBoolean("DUMMY_ACC", false).apply();
                setUser();
            }
        }

        @SuppressLint("StaticFieldLeak")
        private void setUser() {
            Email = PreferenceFragment.getString(LoginActivity.this, PlayStoreApiAuthenticator.PREFERENCE_EMAIL);
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

            PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("GOOGLE_NAME", Name).commit();
            PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("GOOGLE_URL", Url).commit();
            if (!Url.isEmpty()) {
                finish();
            }
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
            LoginActivity.this.finish();
        }
    }

    protected void setText(int viewId, String text) {
        TextView textView = ViewUtils.findViewById(this, viewId);
        if (null != textView)
            textView.setText(text);
    }

    protected void setText(int viewId, int stringId, Object... text) {
        setText(viewId, this.getString(stringId, text));
    }

    protected boolean isGoogle() {
        return PreferenceFragment.getBoolean(this, "GOOGLE_ACC");
    }

    protected boolean isLoggedIn() {
        return PreferenceFragment.getBoolean(this, "LOGGED_IN");
    }

    protected boolean isConnected() {
        return PhoneUtils.isNetworkAvailable(this);
    }
}
