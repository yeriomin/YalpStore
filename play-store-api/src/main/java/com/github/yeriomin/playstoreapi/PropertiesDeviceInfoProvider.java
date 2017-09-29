package com.github.yeriomin.playstoreapi;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class PropertiesDeviceInfoProvider implements DeviceInfoProvider {

    static private String[] requiredFields = new String[] {
        "UserReadableName",
        "Build.HARDWARE",
        "Build.RADIO",
        "Build.BOOTLOADER",
        "Build.FINGERPRINT",
        "Build.BRAND",
        "Build.DEVICE",
        "Build.VERSION.SDK_INT",
        "Build.MODEL",
        "Build.MANUFACTURER",
        "Build.PRODUCT",
        "TouchScreen",
        "Keyboard",
        "Navigation",
        "ScreenLayout",
        "HasHardKeyboard",
        "HasFiveWayNavigation",
        "GL.Version",
        "GSF.version",
        "Vending.version",
        "Screen.Density",
        "Screen.Width",
        "Screen.Height",
        "Platforms",
        "SharedLibraries",
        "Features",
        "Locales",
        "CellOperator",
        "SimOperator",
        "Roaming",
        "Client",
        "TimeZone",
        "GL.Extensions"
    };

    private Properties properties;
    private String localeString;

    /**
     * Time to report to the google server
     * Introduced to make tests reproducible
     */
    private long timeToReport = System.currentTimeMillis() / 1000;

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public void setLocaleString(String localeString) {
        this.localeString = localeString;
    }

    void setTimeToReport(long timeToReport) {
        this.timeToReport = timeToReport;
    }

    public boolean isValid() {
        return properties.keySet().containsAll(Arrays.asList(requiredFields));
    }

    public int getSdkVersion() {
        return Integer.parseInt(this.properties.getProperty("Build.VERSION.SDK_INT"));
    }

    public String getUserAgentString() {
        return "Android-Finsky/7.1.15 ("
            + "api=3"
            + ",versionCode=" + this.properties.getProperty("Vending.version")
            + ",sdk=" + this.properties.getProperty("Build.VERSION.SDK_INT")
            + ",device=" + this.properties.getProperty("Build.DEVICE")
            + ",hardware=" + this.properties.getProperty("Build.HARDWARE")
            + ",product=" + this.properties.getProperty("Build.PRODUCT")
            + ")";
    }

    public AndroidCheckinRequest generateAndroidCheckinRequest() {
        return AndroidCheckinRequest.newBuilder()
            .setId(0)
            .setCheckin(
                AndroidCheckinProto.newBuilder()
                    .setBuild(
                        AndroidBuildProto.newBuilder()
                            .setId(this.properties.getProperty("Build.FINGERPRINT"))
                            .setProduct(this.properties.getProperty("Build.HARDWARE"))
                            .setCarrier(this.properties.getProperty("Build.BRAND"))
                            .setRadio(this.properties.getProperty("Build.RADIO"))
                            .setBootloader(this.properties.getProperty("Build.BOOTLOADER"))
                            .setDevice(this.properties.getProperty("Build.DEVICE"))
                            .setSdkVersion(getInt("Build.VERSION.SDK_INT"))
                            .setModel(this.properties.getProperty("Build.MODEL"))
                            .setManufacturer(this.properties.getProperty("Build.MANUFACTURER"))
                            .setBuildProduct(this.properties.getProperty("Build.PRODUCT"))
                            .setClient(this.properties.getProperty("Client"))
                            .setOtaInstalled(Boolean.getBoolean(this.properties.getProperty("OtaInstalled")))
                            .setTimestamp(this.timeToReport)
                            .setGoogleServices(getInt("GSF.version"))
                    )
                    .setLastCheckinMsec(0)
                    .setCellOperator(this.properties.getProperty("CellOperator"))
                    .setSimOperator(this.properties.getProperty("SimOperator"))
                    .setRoaming(this.properties.getProperty("Roaming"))
                    .setUserNumber(0)
            )
            .setLocale(this.localeString)
            .setTimeZone(this.properties.getProperty("TimeZone"))
            .setVersion(3)
            .setDeviceConfiguration(getDeviceConfigurationProto())
            .setFragment(0)
            .build();
    }

    public DeviceConfigurationProto getDeviceConfigurationProto() {
        return DeviceConfigurationProto.newBuilder()
            .setTouchScreen(getInt("TouchScreen"))
            .setKeyboard(getInt("Keyboard"))
            .setNavigation(getInt("Navigation"))
            .setScreenLayout(getInt("ScreenLayout"))
            .setHasHardKeyboard(Boolean.getBoolean(this.properties.getProperty("HasHardKeyboard")))
            .setHasFiveWayNavigation(Boolean.getBoolean(this.properties.getProperty("HasFiveWayNavigation")))
            .setScreenDensity(getInt("Screen.Density"))
            .setScreenWidth(getInt("Screen.Width"))
            .setScreenHeight(getInt("Screen.Height"))
            .addAllNativePlatform(getList("Platforms"))
            .addAllSystemSharedLibrary(getList("SharedLibraries"))
            .addAllSystemAvailableFeature(getList("Features"))
            .addAllSystemSupportedLocale(getList("Locales"))
            .setGlEsVersion(getInt("GL.Version"))
            .addAllGlExtension(getList("GL.Extensions"))
            .build();
    }

    private int getInt(String key) {
        try {
            return Integer.parseInt(properties.getProperty(key));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private List<String> getList(String key) {
        return Arrays.asList(properties.getProperty(key).split(","));
    }
}
