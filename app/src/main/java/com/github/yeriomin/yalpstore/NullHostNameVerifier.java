package com.github.yeriomin.yalpstore;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

class NullHostNameVerifier implements HostnameVerifier {

    @Override
    public boolean verify(String hostname, SSLSession session) {
        return true;
    }
}
