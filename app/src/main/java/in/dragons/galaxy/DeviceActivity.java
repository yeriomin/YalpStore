package in.dragons.galaxy;

import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.ImageViewCompat;
import android.view.Display;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Properties;
import java.util.TimeZone;

public class DeviceActivity extends GalaxyActivity {

    AccountTypeDialogBuilder accountTypeDialogBuilder = new AccountTypeDialogBuilder(this);
    String deviceName;
    ImageView spoofed;
    Display mDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.app_device_inc, contentFrameLayout);

        deviceName = sharedPreferences.getString(PreferenceActivity.PREFERENCE_DEVICE_TO_PRETEND_TO_BE, "");
        spoofed = (ImageView) findViewById(R.id.spoofed_indicator);
        mDisplay = (this).getWindowManager().getDefaultDisplay();

        if (isSpoofed())
            drawSpoofedDevice();
        else
            drawDevice();

        setFab();
    }

    public boolean isSpoofed() {
        return (deviceName.contains("device-"));
    }

    public void drawDevice() {
        ImageViewCompat.setImageTintList(spoofed, ColorStateList.valueOf((getResources().getColor(R.color.colorGreen))));
        setText(R.id.device_model, R.string.device_model, Build.MODEL, Build.DEVICE);
        setText(R.id.device_manufacturer, R.string.device_manufacturer, Build.MANUFACTURER);
        setText(R.id.device_architect, R.string.device_board, Build.BOARD);
        setText(R.id.device_timezone, R.string.device_timezone, (CharSequence) TimeZone.getDefault().getDisplayName());
        setText(R.id.device_resolution, R.string.device_res, mDisplay.getWidth(), mDisplay.getHeight());
        setText(R.id.device_api, R.string.device_api, Build.VERSION.SDK);
        setText(R.id.device_cpu, R.string.device_cpu, Build.CPU_ABI);
    }

    public void drawSpoofedDevice() {
        ImageViewCompat.setImageTintList(spoofed, ColorStateList.valueOf((getResources().getColor(R.color.colorRed))));

        Properties properties = new SpoofDeviceManager(this).getProperties(deviceName);
        String Model = properties.getProperty("UserReadableName");

        setText(R.id.device_model, R.string.device_model, Model.substring(0, Model.indexOf('(')), properties.getProperty("Build.DEVICE"));
        setText(R.id.device_manufacturer, R.string.device_manufacturer, properties.getProperty("Build.MANUFACTURER"));
        setText(R.id.device_architect, R.string.device_board, properties.getProperty("Build.HARDWARE"));
        setText(R.id.device_timezone, R.string.device_timezone, properties.getProperty("TimeZone"));
        setText(R.id.device_resolution, R.string.device_res, properties.getProperty("Screen.Width"),properties.getProperty("Screen.Height"));
        setText(R.id.device_api, R.string.device_api,properties.getProperty("Build.VERSION.SDK_INT"));
        String Platforms = properties.getProperty("Platforms");
        setText(R.id.device_cpu, R.string.device_cpu, Platforms.substring(0, Platforms.indexOf(',')));
    }


    public void setFab() {
        FloatingActionButton changeDevice = (FloatingActionButton) findViewById(R.id.fab);
        changeDevice.setVisibility(View.VISIBLE);
        changeDevice.setImageResource(R.drawable.app_dev);
        changeDevice.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                //
            }
        });
    }

    protected void setText(int viewId, String text) {
        TextView textView = (TextView) this.findViewById(viewId);
        if (null != textView)
            textView.setText(text);
    }

    protected void setText(int viewId, int stringId, Object... text) {
        setText(viewId, this.getString(stringId, text));
    }
}
