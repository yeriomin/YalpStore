package in.dragons.galaxy;

import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.ImageViewCompat;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.Properties;
import java.util.TimeZone;

public class AccountsActivity extends GalaxyActivity {

    AccountTypeDialogBuilder accountTypeDialogBuilder = new AccountTypeDialogBuilder(this);
    String deviceName;
    ImageView spoofed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.app_acc_inc, contentFrameLayout);

        notifyConnected(this);

        deviceName = sharedPreferences.getString(PreferenceActivity.PREFERENCE_DEVICE_TO_PRETEND_TO_BE, "");

        spoofed = (ImageView) findViewById(R.id.spoofed_indicator);

        if (isSpoofed())
            drawSpoofedDevice();
        else
            drawDevice();

        if (isValidEmail(Email) && isConnected()) {
            drawGoogle();
        } else if (isDummyEmail())
            drawDummy();

        setFab();
    }

    public boolean isSpoofed() {
        return (deviceName.contains("device-"));
    }

    public void drawDevice() {
        ImageViewCompat.setImageTintList(spoofed, ColorStateList.valueOf((getResources().getColor(R.color.colorGreen))));
        ((TextView) findViewById(R.id.device_model)).setText(Build.MODEL + " (" + Build.DEVICE + ")");
        ((TextView) findViewById(R.id.device_manufacturer)).setText("By : " + Build.MANUFACTURER);
        ((TextView) findViewById(R.id.device_architect)).setText("Board : " + Build.BOARD);
        ((TextView) findViewById(R.id.device_timezone)).setText("Timezone : " + (CharSequence) TimeZone.getDefault().getDisplayName());
        Display mDisplay = (this).getWindowManager().getDefaultDisplay();
        ((TextView) findViewById(R.id.device_resolution)).setText(mDisplay.getWidth() + " x " + mDisplay.getHeight());
        ((TextView) findViewById(R.id.device_architect)).setText("Board : " + Build.BOARD);
        ((TextView) findViewById(R.id.device_api)).setText("API Level " + Build.VERSION.SDK);
        ((TextView) findViewById(R.id.device_cpu)).setText(Build.CPU_ABI);
    }

    public void drawSpoofedDevice() {
        ImageViewCompat.setImageTintList(spoofed, ColorStateList.valueOf((getResources().getColor(R.color.colorRed))));
        Properties properties = new SpoofDeviceManager(this).getProperties(deviceName);
        String Model = properties.getProperty("UserReadableName");
        ((TextView) findViewById(R.id.device_model)).setText(Model.substring(0, Model.indexOf('(')) + " (" + properties.getProperty("Build.DEVICE") + ")");
        ((TextView) findViewById(R.id.device_manufacturer)).setText("By : " + properties.getProperty("Build.MANUFACTURER"));
        ((TextView) findViewById(R.id.device_architect)).setText("Board : " + properties.getProperty("Build.HARDWARE"));
        ((TextView) findViewById(R.id.device_timezone)).setText("Timezone : " + properties.getProperty("TimeZone"));
        ((TextView) findViewById(R.id.device_resolution)).setText(properties.getProperty("Screen.Width") + " x " + properties.getProperty("Screen.Height"));
        ((TextView) findViewById(R.id.device_api)).setText("API Level " + properties.getProperty("Build.VERSION.SDK_INT"));
        String Platforms = properties.getProperty("Platforms");
        ((TextView) findViewById(R.id.device_cpu)).setText(Platforms.substring(0, Platforms.indexOf(',')));
    }

    public void drawDummy() {
        ((LinearLayout) findViewById(R.id.dummy_container)).setVisibility(View.VISIBLE);
        ((LinearLayout) findViewById(R.id.dummy_action)).setVisibility(View.VISIBLE);
        ((LinearLayout) findViewById(R.id.no_dummy)).setVisibility(View.GONE);
        TextView dummyEmail = (TextView) findViewById(R.id.dummy_email);
        dummyEmail.setText(Email);
        TextView gsfIdView = (TextView) findViewById(R.id.dummy_gsf);
        gsfIdView.setText("GSF ID : " + sharedPreferences.getString(PlayStoreApiAuthenticator.PREFERENCE_GSF_ID, ""));

        Button logout = (Button) findViewById(R.id.account_logout);
        logout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showLogOutDialog();
            }
        });
        Button switched = (Button) findViewById(R.id.account_switch);
        switched.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                accountTypeDialogBuilder.logInWithPredefinedAccount();
            }
        });
    }

    public void drawGoogle() {
        if (Email != "") {
            ((LinearLayout) findViewById(R.id.google_container)).setVisibility(View.VISIBLE);
            ((LinearLayout) findViewById(R.id.google_action)).setVisibility(View.VISIBLE);
            ((LinearLayout) findViewById(R.id.no_google)).setVisibility(View.GONE);

            TextView googleName = (TextView) findViewById(R.id.google_name);
            googleName.setText(sharedPreferences.getString("GOOGLE_NAME", ""));

            TextView googleEmail = (TextView) findViewById(R.id.google_email);
            googleEmail.setText(Email);

            TextView gsfIdView = (TextView) findViewById(R.id.google_gsf);
            gsfIdView.setText("GSF ID : " + sharedPreferences.getString(PlayStoreApiAuthenticator.PREFERENCE_GSF_ID, ""));

            Button button = (Button) findViewById(R.id.google_logout);
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    showLogOutDialog();
                }
            });

            loadAvatar(sharedPreferences.getString("GOOGLE_URL", ""));
        }
    }


    public void setFab() {
        FloatingActionButton dummyfab = (FloatingActionButton) findViewById(R.id.dummy_login);
        dummyfab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                accountTypeDialogBuilder.logInWithPredefinedAccount();
            }
        });

        FloatingActionButton googlefab = (FloatingActionButton) findViewById(R.id.google_login);
        googlefab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                accountTypeDialogBuilder.showCredentialsDialog();
            }
        });
    }

    public void loadAvatar(String Url) {
        Picasso.with(this)
                .load(Url)
                .placeholder(R.drawable.ic_user_placeholder)
                .transform(new CircleTransform())
                .into(((ImageView) findViewById(R.id.google_avatar)));
    }
}
