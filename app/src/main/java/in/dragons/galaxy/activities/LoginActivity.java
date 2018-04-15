package in.dragons.galaxy.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;

import com.dragons.aurora.playstoreapiv2.AuthException;

import java.io.IOException;
import java.util.Set;

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
import in.dragons.galaxy.task.playstore.PlayStoreTask;

public class LoginActivity extends GalaxyActivity {

    protected PlayStoreTask playStoreTask;
    private String Email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        if (isConnected()) {
            init();
        }
        if (isLoggedIn()) {
            finish();
        }
    }

    private void init() {
        Button login_anonymous = findViewById(R.id.btn_ok_anm);
        CheckBox checkBox = findViewById(R.id.checkboxSave);
        EditText editPassword = findViewById(R.id.passwordg);
        Button login_google = findViewById(R.id.button_okg);
        AutoCompleteTextView editEmail = findViewById(R.id.emailg);

        login_anonymous.setOnClickListener(v -> {
            LoginTask task = new LoginTask();
            task.setCaller(playStoreTask);
            task.setContext(this);
            task.prepareDialog(R.string.dialog_message_logging_in_predefined, R.string.dialog_title_logging_in);
            task.execute();
        });
        login_google.setOnClickListener(view -> {
            Context c = view.getContext();
            String email = editEmail.getText().toString();
            String password = editPassword.getText().toString();
            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                ContextUtil.toast(c.getApplicationContext(), R.string.error_credentials_empty);
                return;
            }
            if (checkBox.isChecked()) {
                PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean("SEC_ACCOUNT", true).apply();
                PreferenceManager.getDefaultSharedPreferences(this).edit().putString("GOOGLE_EMAIL", email).apply();
                PreferenceManager.getDefaultSharedPreferences(this).edit().putString("GOOGLE_PASSWORD", password).apply();
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

            PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("GOOGLE_NAME", Name).apply();
            PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("GOOGLE_URL", Url).apply();

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
}
