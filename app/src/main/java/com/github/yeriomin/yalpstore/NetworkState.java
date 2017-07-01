package com.github.yeriomin.yalpstore;

import android.text.TextUtils;
import android.util.Log;

import eu.chainfire.libsuperuser.Shell;

public class NetworkState {

    static public boolean isVpn() {
        for (String line: Shell.SH.run("ls -1 /sys/class/net")) {
            String networkName = line.trim();
            if (networkName.startsWith("tun") || networkName.startsWith("ppp")) {
                Log.i(NetworkState.class.getName(), "VPN seems to be on: " + networkName);
                return true;
            }
        }
        return false;
    }

    static public boolean isWifi() {
        for (String line: Shell.SH.run("cat /proc/net/wireless")) {
            String trimmed = line.trim();
            if (trimmed.startsWith("Inter") || trimmed.startsWith("face")) {
                continue;
            }
            for (String component: TextUtils.split(trimmed, " ")) {
                if (component.endsWith(":")) {
                    continue;
                }
                component = component.replace(".", "");
                boolean isZero = Util.parseInt(component, 0) == 0;
                if (!isZero) {
                    return true;
                }
            }
        }
        return false;
    }
}
