/*
 * Yalp Store
 * Copyright (C) 2018 Sergey Yeriomin <yeriomin@gmail.com>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.github.yeriomin.yalpstore.view;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.github.yeriomin.playstoreapi.PropertiesDeviceInfoProvider;
import com.github.yeriomin.yalpstore.ContextUtil;
import com.github.yeriomin.yalpstore.DeviceInfoActivity;
import com.github.yeriomin.yalpstore.PreferenceUtil;
import com.github.yeriomin.yalpstore.R;
import com.github.yeriomin.yalpstore.SpoofDeviceManager;
import com.github.yeriomin.yalpstore.TokenDispenserMirrors;
import com.github.yeriomin.yalpstore.Util;
import com.github.yeriomin.yalpstore.YalpStoreApplication;
import com.github.yeriomin.yalpstore.model.LoginInfo;
import com.github.yeriomin.yalpstore.task.playstore.CheckLoginTask;
import com.github.yeriomin.yalpstore.task.playstore.PlayStoreTask;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class LoginDialogBuilder extends CredentialsDialogBuilder {

    static public final String USED_EMAILS_SET = "USED_EMAILS_SET";
    static public final String PREFERENCE_DISCLAIMER_IS_READ = "PREFERENCE_DISCLAIMER_IS_READ";

    private String previousEmail = "";
    private final Map<String, String> devices = new HashMap<>();
    private final Map<String, String> languages = new HashMap<>();

    public void setPreviousEmail(String previousEmail) {
        this.previousEmail = previousEmail;
    }

    public LoginDialogBuilder(Activity activity) {
        super(activity);
    }

    @Override
    public DialogWrapperAbstract create() {
        setLayout(R.layout.login_dialog_layout);
        setTitle(R.string.credentials_title);

        ((CheckBox) findViewById(R.id.account_toggle)).setOnCheckedChangeListener(new CheckboxListener(this, R.id.account_user) {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                super.onCheckedChanged(buttonView, isChecked);
                if (!isChecked && !PreferenceUtil.getBoolean(activity, PREFERENCE_DISCLAIMER_IS_READ)) {
                    showDisclaimer();
                }
            }
        });
        ((CheckBox) findViewById(R.id.device_toggle)).setOnCheckedChangeListener(new CheckboxWithSpinnerListener(this, R.id.device_list, devices) {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                super.onCheckedChanged(buttonView, isChecked);
                findViewById(R.id.device_details).setVisibility(isChecked ? View.GONE : View.VISIBLE);
            }

            @Override
            protected PrepareSpinnerTask getPrepareSpinnerTask() {
                return new DeviceSpinnerTask();
            }
        });
        findViewById(R.id.device_details).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String deviceDefinitionName = devices.get(((Spinner) findViewById(R.id.device_list)).getSelectedItem());
                if (!TextUtils.isEmpty(deviceDefinitionName)) {
                    activity.startActivity(new Intent(activity, DeviceInfoActivity.class).putExtra(DeviceInfoActivity.INTENT_DEVICE_NAME, deviceDefinitionName));
                }
            }
        });
        ((CheckBox) findViewById(R.id.language_toggle)).setOnCheckedChangeListener(new CheckboxWithSpinnerListener(this, R.id.language_list, languages) {
            @Override
            protected PrepareSpinnerTask getPrepareSpinnerTask() {
                return new LanguageSpinnerTask();
            }
        });

        AutoCompleteTextView editEmail = (AutoCompleteTextView) findViewById(R.id.email);
        editEmail.setAdapter(new ArrayAdapter<>(activity, android.R.layout.simple_dropdown_item_1line, getUsedEmails()));
        editEmail.setText(
            (YalpStoreApplication.user.appProvidedEmail() || TextUtils.isEmpty(YalpStoreApplication.user.getEmail()))
                ? this.previousEmail
                : YalpStoreApplication.user.getEmail()
        );
        findViewById(R.id.toggle_password_visibility).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean passwordVisible = !TextUtils.isEmpty((String) v.getTag());
                v.setTag(passwordVisible ? null : "tag");
                ((ImageView) v).setImageResource(passwordVisible ? R.drawable.ic_visibility_on : R.drawable.ic_visibility_off);
                ((EditText) findViewById(R.id.password)).setInputType(passwordVisible ? InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD : InputType.TYPE_CLASS_TEXT);
            }
        });

        setPositiveButton(android.R.string.ok, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                LoginInfo loginInfo = collectFormData();
                if (!loginInfo.appProvidedEmail() && (TextUtils.isEmpty(loginInfo.getEmail()) || TextUtils.isEmpty(loginInfo.getPassword()))) {
                    ContextUtil.toast(activity, R.string.error_credentials_empty);
                    return;
                }
                if (!TextUtils.isEmpty(loginInfo.getDeviceDefinitionName()) && !isDeviceDefinitionValid(loginInfo.getDeviceDefinitionName())) {
                    ContextUtil.toast(activity, R.string.error_invalid_device_definition);
                    return;
                }
                getCheckLoginTask(loginInfo).execute();
            }
        });
        setNegativeButton(android.R.string.cancel, null);
        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                PlayStoreTask.isShowingLoginDialog.set(false);
            }
        });
        return super.create();
    }

    @Override
    public DialogWrapperAbstract show() {
        DialogWrapperAbstract dialog = super.show();
        if (!TextUtils.isEmpty(previousEmail) || !YalpStoreApplication.user.appProvidedEmail()) {
            findViewById(R.id.account_toggle).performClick();
        }
        return dialog;
    }

    private LoginInfo collectFormData() {
        LoginInfo loginInfo = new LoginInfo();
        if (((CheckBox) findViewById(R.id.account_toggle)).isChecked()) {
            loginInfo.setTokenDispenserUrl(new TokenDispenserMirrors().get());
        } else {
            loginInfo.setEmail(((AutoCompleteTextView) findViewById(R.id.email)).getText().toString());
            loginInfo.setPassword(((EditText) findViewById(R.id.password)).getText().toString());
        }
        if (!((CheckBox) findViewById(R.id.device_toggle)).isChecked()) {
            String deviceDefinitionDisplayName = (String) ((Spinner) findViewById(R.id.device_list)).getSelectedItem();
            loginInfo.setDeviceDefinitionName(devices.get(deviceDefinitionDisplayName));
            loginInfo.setDeviceDefinitionDisplayName(deviceDefinitionDisplayName);
        }
        if (!((CheckBox) findViewById(R.id.language_toggle)).isChecked()) {
            loginInfo.setLocale(languages.get(((Spinner) findViewById(R.id.language_list)).getSelectedItem()));
        }
        return loginInfo;
    }

    private CheckLoginTask getCheckLoginTask(LoginInfo loginInfo) {
        CheckLoginTask task = new CheckLoginTask();
        task.setCaller(caller);
        task.setContext(activity);
        task.setLoginInfo(loginInfo);
        task.setPreviousEmail(previousEmail);
        task.prepareDialog(
            loginInfo.appProvidedEmail()
                ? R.string.dialog_message_logging_in_predefined
                : R.string.dialog_message_logging_in_provided_by_user
            ,
            R.string.dialog_title_logging_in
        );
        return task;
    }

    private void showDisclaimer() {
        new DialogWrapper(activity)
            .setMessage(R.string.credentials_message)
            .setTitle(R.string.dialog_title_system_app_warning)
            .setPositiveButton(android.R.string.ok, new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    PreferenceUtil.getDefaultSharedPreferences(activity).edit().putBoolean(PREFERENCE_DISCLAIMER_IS_READ, true).commit();
                }
            })
            .show()
        ;
    }

    private boolean isDeviceDefinitionValid(String spoofDevice) {
        PropertiesDeviceInfoProvider deviceInfoProvider = new PropertiesDeviceInfoProvider();
        deviceInfoProvider.setProperties(new SpoofDeviceManager(activity).getProperties(spoofDevice));
        deviceInfoProvider.setLocaleString(Locale.getDefault().toString());
        return deviceInfoProvider.isValid();
    }

    private List<String> getUsedEmails() {
        List<String> emails = new ArrayList<>(PreferenceUtil.getStringSet(activity, USED_EMAILS_SET));
        Collections.sort(emails);
        return emails;
    }

    private static class CheckboxListener implements CompoundButton.OnCheckedChangeListener {

        protected LoginDialogBuilder dialogBuilder;
        protected int layoutResId;

        public CheckboxListener(LoginDialogBuilder dialogBuilder, int layoutResId) {
            this.dialogBuilder = dialogBuilder;
            this.layoutResId = layoutResId;
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            dialogBuilder.findViewById(layoutResId).setVisibility(isChecked ? View.GONE : View.VISIBLE);
        }
    }

    abstract private static class CheckboxWithSpinnerListener extends CheckboxListener {

        private Map<String, String> valueKeyMap;

        abstract protected PrepareSpinnerTask getPrepareSpinnerTask();

        public CheckboxWithSpinnerListener(LoginDialogBuilder dialogBuilder, int layoutResId, Map<String, String> valueKeyMap) {
            super(dialogBuilder, layoutResId);
            this.valueKeyMap = valueKeyMap;
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            super.onCheckedChanged(buttonView, isChecked);
            Spinner spinner = (Spinner) dialogBuilder.findViewById(layoutResId);
            if (null == spinner.getAdapter() || spinner.getAdapter().isEmpty()) {
                PrepareSpinnerTask task = getPrepareSpinnerTask();
                task.setSpinner((Spinner) dialogBuilder.findViewById(layoutResId));
                task.setValueKeyMap(valueKeyMap);
                task.execute();
            }
        }
    }

    abstract private static class PrepareSpinnerTask extends AsyncTask<Void, Void, Map<String, String>> {

        private Map<String, String> valueKeyMap;

        protected WeakReference<Spinner> spinner = new WeakReference<>(null);

        public void setValueKeyMap(Map<String, String> valueKeyMap) {
            this.valueKeyMap = valueKeyMap;
        }

        public void setSpinner(Spinner spinner) {
            this.spinner = new WeakReference<>(spinner);
        }

        abstract protected Map<String, String> getValueKeyMap();

        @Override
        protected void onPostExecute(Map<String, String> stringStringMap) {
            super.onPostExecute(stringStringMap);
            if (null != spinner.get()) {
                List<String> values = new ArrayList<>(valueKeyMap.keySet());
                Collections.sort(values);
                spinner.get().setAdapter(new ArrayAdapter<>(spinner.get().getContext(), android.R.layout.simple_spinner_item, values));
            }
        }

        @Override
        protected Map<String, String> doInBackground(Void... voids) {
            valueKeyMap.clear();
            valueKeyMap.putAll(getValueKeyMap());
            return valueKeyMap;
        }
    }

    private static class DeviceSpinnerTask extends PrepareSpinnerTask {

        @Override
        protected Map<String, String> getValueKeyMap() {
            return Util.swapKeysValues(new SpoofDeviceManager(spinner.get().getContext()).getDevicesShort());
        }
    }

    private static class LanguageSpinnerTask extends PrepareSpinnerTask {

        @Override
        protected Map<String, String> getValueKeyMap() {
            Map<String, String> languages = new HashMap<>();
            for (Locale locale: Locale.getAvailableLocales()) {
                String displayName = locale.getDisplayName();
                displayName = displayName.substring(0, 1).toUpperCase(Locale.getDefault()) + displayName.substring(1);
                languages.put(locale.toString(), displayName);
            }
            return Util.swapKeysValues(languages);
        }
    }
}
