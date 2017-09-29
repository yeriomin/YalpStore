package com.github.yeriomin.playstoreapi;

public interface DeviceInfoProvider {

    AndroidCheckinRequest generateAndroidCheckinRequest();
    DeviceConfigurationProto getDeviceConfigurationProto();
    String getUserAgentString();
    int getSdkVersion();
}
