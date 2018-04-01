package in.dragons.galaxy.builders;

import android.app.Dialog;
import android.content.Context;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;

import com.github.yeriomin.playstoreapi.AuthException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import in.dragons.galaxy.ContextUtil;
import in.dragons.galaxy.CredentialsEmptyException;
import in.dragons.galaxy.PlayStoreApiAuthenticator;
import in.dragons.galaxy.R;
import in.dragons.galaxy.Util;

public class UserProvidedAccountDialogBuilder extends CredentialsDialogBuilder {

    static private final String USED_EMAILS_SET = "USED_EMAILS_SET";

    private String previousEmail = "";

    public UserProvidedAccountDialogBuilder setPreviousEmail(String previousEmail) {
        this.previousEmail = previousEmail;
        return this;
    }

    public UserProvidedAccountDialogBuilder(Context context) {
        super(context);
    }

    @Override
    public Dialog show() {
        final Dialog ad = new Dialog(context);
        ad.setContentView(R.layout.credentials_dialog_layout);
        ad.setTitle(context.getString(R.string.credentials_title));
        ad.setCancelable(false);

        final AutoCompleteTextView editEmail = getEmailInput(ad);
        final EditText editPassword = (EditText) ad.findViewById(R.id.password);

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
            getUserCredentialsTask().execute(email, password);
        });

        ad.findViewById(R.id.toggle_password_visibility).setOnClickListener(v -> {
            boolean passwordVisible = !TextUtils.isEmpty((String) v.getTag());
            v.setTag(passwordVisible ? null : "tag");
            ((ImageView) v).setImageResource(passwordVisible ? R.drawable.ic_visibility_on : R.drawable.ic_visibility_off);
            editPassword.setInputType(passwordVisible ? InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD : InputType.TYPE_CLASS_TEXT);
        });

        ad.show();
        return ad;
    }

    private UserProvidedCredentialsTask getUserCredentialsTask() {
        UserProvidedCredentialsTask task = new UserProvidedCredentialsTask();
        task.setCaller(caller);
        task.setContext(context);
        task.prepareDialog(R.string.dialog_message_logging_in_provided_by_user, R.string.dialog_title_logging_in);
        return task;
    }

    private AutoCompleteTextView getEmailInput(Dialog ad) {
        AutoCompleteTextView editEmail = (AutoCompleteTextView) ad.findViewById(R.id.email);
        editEmail.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, getUsedEmails()));
        editEmail.setText(PreferenceManager.getDefaultSharedPreferences(context).getString(PlayStoreApiAuthenticator.PREFERENCE_EMAIL, this.previousEmail));
        return editEmail;
    }

    private List<String> getUsedEmails() {
        List<String> emails = new ArrayList<>(Util.getStringSet(context, USED_EMAILS_SET));
        Collections.sort(emails);
        return emails;
    }

    private static class UserProvidedCredentialsTask extends CredentialsDialogBuilder.CheckCredentialsTask {

        private String previousEmail;

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
    }
}
