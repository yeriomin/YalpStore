package com.github.yeriomin.playstoreapi;

import java.util.Arrays;
import java.util.Properties;

public class PropertiesDeviceInfoProvider implements DeviceInfoProvider {

    private Properties properties;
    private String localeString;

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public void setLocaleString(String localeString) {
        this.localeString = localeString;
    }

    public int getSdkVersion() {
        return Integer.parseInt(this.properties.getProperty("Build.VERSION.SDK_INT"));
    }

    public String getUserAgentString() {
        return "Android-Finsky/7.1.15 ("
            + "api=3"
            + ",versionCode=80711500"
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
                            .setSdkVersion(Integer.getInteger(this.properties.getProperty("Build.VERSION.SDK_INT")))
                            .setModel(this.properties.getProperty("Build.MODEL"))
                            .setManufacturer(this.properties.getProperty("Build.MANUFACTURER"))
                            .setBuildProduct(this.properties.getProperty("Build.PRODUCT"))
                            .setClient(this.properties.getProperty("Client"))
                            .setOtaInstalled(Boolean.getBoolean(this.properties.getProperty("OtaInstalled")))
                            .setTimestamp(System.currentTimeMillis() / 1000)
                            .setGoogleServices(Integer.getInteger(this.properties.getProperty("GSF.version")))
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
            .setTouchScreen(Integer.getInteger(this.properties.getProperty("TouchScreen")))
            .setKeyboard(Integer.getInteger(this.properties.getProperty("Keyboard")))
            .setNavigation(Integer.getInteger(this.properties.getProperty("Navigation")))
            .setScreenLayout(Integer.getInteger(this.properties.getProperty("ScreenLayout")))
            .setHasHardKeyboard(Boolean.getBoolean(this.properties.getProperty("HasHardKeyboard")))
            .setHasFiveWayNavigation(Boolean.getBoolean(this.properties.getProperty("HasFiveWayNavigation")))
            .setScreenDensity(Integer.getInteger(this.properties.getProperty("Screen.Density")))
            .setScreenWidth(Integer.getInteger(this.properties.getProperty("Screen.Width")))
            .setScreenHeight(Integer.getInteger(this.properties.getProperty("Screen.Height")))
            .addAllNativePlatform(Arrays.asList(this.properties.getProperty("Platforms").split(",")))
            .addAllSystemSharedLibrary(Arrays.asList(this.properties.getProperty("SharedLibraries").split(",")))
            .addAllSystemAvailableFeature(Arrays.asList(this.properties.getProperty("Features").split(",")))
            .addAllSystemSupportedLocale(Arrays.asList(this.properties.getProperty("Locales").split(",")))
            .setGlEsVersion(Integer.getInteger(this.properties.getProperty("GL.Version")))
            .addAllGlExtension(Arrays.asList(this.properties.getProperty("GL.Extensions").split(",")))
            .build();
    }
}
