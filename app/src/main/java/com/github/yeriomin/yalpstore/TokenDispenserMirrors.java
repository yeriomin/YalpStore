package com.github.yeriomin.yalpstore;

import java.util.Random;

public class TokenDispenserMirrors {

    static private String[] mirrors = new String[] {
        "http://route-play-store-token-dispenser.1d35.starter-us-east-1.openshiftapps.com",
        "http://route-token-dispenser.193b.starter-ca-central-1.openshiftapps.com",
        "http://token-dispenser.duckdns.org:8080"
    };

    static public String get() {
        return mirrors[new Random().nextInt(mirrors.length)];
    }
}
