package com.github.yeriomin.yalpstore;

import java.util.HashMap;
import java.util.Map;

public class InstallationState {

    enum STATUS {
        PROCESSING,
        SUCCESS,
        FAILURE
    }

    static private final Map<String, STATUS> apps = new HashMap<>();

    static public boolean isInstalling(String packageName) {
        return apps.keySet().contains(packageName) && apps.get(packageName).equals(STATUS.PROCESSING);
    }

    static public boolean isInstalled(String packageName) {
        return apps.keySet().contains(packageName) && apps.get(packageName).equals(STATUS.SUCCESS);
    }

    static public void setInstalling(String packageName) {
        apps.put(packageName, STATUS.PROCESSING);
    }

    static public void setSuccess(String packageName) {
        apps.put(packageName, STATUS.SUCCESS);
    }

    static public void setFailure(String packageName) {
        apps.put(packageName, STATUS.FAILURE);
    }
}
