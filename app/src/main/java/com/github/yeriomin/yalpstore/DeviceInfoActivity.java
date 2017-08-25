package com.github.yeriomin.yalpstore;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class DeviceInfoActivity extends YalpStoreActivity {

    public static final String INTENT_DEVICE_NAME = "INTENT_DEVICE_NAME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.deviceinfo_activity_layout);
        onNewIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String deviceName = intent.getStringExtra(INTENT_DEVICE_NAME);
        if (TextUtils.isEmpty(deviceName)) {
            Log.e(getClass().getName(), "No device name given");
            finish();
            return;
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(byteArrayOutputStream);
        new SpoofDeviceManager(this).getProperties(deviceName).list(printStream);
        ((TextView) findViewById(R.id.device_info)).setText(new String(byteArrayOutputStream.toByteArray()));
    }
}
