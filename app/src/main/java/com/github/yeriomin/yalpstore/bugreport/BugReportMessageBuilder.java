package com.github.yeriomin.yalpstore.bugreport;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;

import com.github.yeriomin.yalpstore.BuildConfig;
import com.github.yeriomin.yalpstore.R;
import com.github.yeriomin.yalpstore.selfupdate.Signature;

import java.util.HashMap;
import java.util.Map;

public class BugReportMessageBuilder extends BugReportPropertiesBuilder {

    private String identification;
    private String message;
    private String stackTrace;

    public BugReportMessageBuilder setIdentification(String identification) {
        this.identification = identification;
        return this;
    }

    public BugReportMessageBuilder setMessage(String message) {
        this.message = message;
        return this;
    }

    public BugReportMessageBuilder setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
        return this;
    }

    public BugReportMessageBuilder(Context context) {
        super(context);
        setFileName("message.txt");
    }

    @Override
    public BugReportBuilder build() {
        Map<String, String> properties = new HashMap<>();
        properties.put("userId", TextUtils.isEmpty(identification) ? "" : identification);
        properties.put("message", message);
        properties.put("versionCode", Integer.toString(BuildConfig.VERSION_CODE));
        properties.put("versionName", BuildConfig.VERSION_NAME);
        properties.put("deviceName", Build.DEVICE);
        properties.put("source", getSource());
        properties.put("topic", getTopic());
        setContent(buildProperties(properties));
        super.build();
        return this;
    }

    private String getSource() {
        if (Signature.isFdroid(context)) {
            return "fdroid";
        } else if (Signature.isGithub(context)) {
            return "github";
        }
        return "selfsigned";
    }

    private String getTopic() {
        if (!TextUtils.isEmpty(stackTrace)) {
            return "crash";
        } else if (!TextUtils.isEmpty(message) && message.contains(context.getString(R.string.sent_from_device_definition_dialog))) {
            return "device";
        } else {
            return "feedback";
        }
    }
}
