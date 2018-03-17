package com.github.yeriomin.yalpstore.view;

import android.app.Activity;
import android.content.Context;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.github.yeriomin.yalpstore.ContextUtil;
import com.github.yeriomin.yalpstore.PlayStoreApiAuthenticator;
import com.github.yeriomin.yalpstore.R;
import com.github.yeriomin.yalpstore.Util;
import com.github.yeriomin.yalpstore.task.playstore.UserProvidedCredentialsTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UserProvidedAccountDialogBuilder extends CredentialsDialogBuilder {

    static public final String USED_EMAILS_SET = "USED_EMAILS_SET";

    private String previousEmail = "";

    public UserProvidedAccountDialogBuilder setPreviousEmail(String previousEmail) {
        this.previousEmail = previousEmail;
        return this;
    }

    public UserProvidedAccountDialogBuilder(Activity activity) {
        super(activity);
    }

    @Override
    public DialogWrapperAbstract show() {
        final DialogWrapperAbstract ad = new DialogWrapper(activity);
        ad.setLayout(R.layout.credentials_dialog_layout);
        ad.setTitle(R.string.credentials_title);
        ad.setCancelable(false);

        final AutoCompleteTextView editEmail = getEmailInput(ad);
        final EditText editPassword = (EditText) ad.findViewById(R.id.password);

        ad.findViewById(R.id.button_exit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        });

        ad.findViewById(R.id.button_ok).setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context c = view.getContext();
                String email = editEmail.getText().toString();
                String password = editPassword.getText().toString();
                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                    ContextUtil.toast(c.getApplicationContext(), R.string.error_credentials_empty);
                    return;
                }
                ad.dismiss();
                getUserCredentialsTask().execute(email, password);
            }
        });

        ad.findViewById(R.id.toggle_password_visibility).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean passwordVisible = !TextUtils.isEmpty((String) v.getTag());
                v.setTag(passwordVisible ? null : "tag");
                ((ImageView) v).setImageResource(passwordVisible ? R.drawable.ic_visibility_on : R.drawable.ic_visibility_off);
                editPassword.setInputType(passwordVisible ? InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD : InputType.TYPE_CLASS_TEXT);
            }
        });

        ad.show();
        return ad;
    }

    private UserProvidedCredentialsTask getUserCredentialsTask() {
        UserProvidedCredentialsTask task = new UserProvidedCredentialsTask();
        task.setCaller(caller);
        task.setContext(activity);
        task.prepareDialog(R.string.dialog_message_logging_in_provided_by_user, R.string.dialog_title_logging_in);
        return task;
    }

    private AutoCompleteTextView getEmailInput(DialogWrapperAbstract ad) {
        AutoCompleteTextView editEmail = (AutoCompleteTextView) ad.findViewById(R.id.email);
        editEmail.setAdapter(new ArrayAdapter<>(activity, android.R.layout.simple_dropdown_item_1line, getUsedEmails()));
        editEmail.setText(PreferenceManager.getDefaultSharedPreferences(activity).getString(PlayStoreApiAuthenticator.PREFERENCE_EMAIL, this.previousEmail));
        return editEmail;
    }

    private List<String> getUsedEmails() {
        List<String> emails = new ArrayList<>(Util.getStringSet(activity, USED_EMAILS_SET));
        Collections.sort(emails);
        return emails;
    }
}
