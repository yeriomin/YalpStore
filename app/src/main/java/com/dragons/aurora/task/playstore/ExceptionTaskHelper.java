package com.dragons.aurora.task.playstore;

import android.text.TextUtils;
import android.util.Log;

import com.dragons.aurora.ContextUtil;
import com.dragons.aurora.CredentialsEmptyException;
import com.dragons.aurora.PlayStoreApiAuthenticator;
import com.dragons.aurora.R;
import com.dragons.aurora.fragment.AppListFragment;
import com.dragons.aurora.fragment.PreferenceFragment;
import com.dragons.aurora.playstoreapiv2.AuthException;
import com.percolate.caffeine.ToastUtils;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import javax.net.ssl.SSLHandshakeException;

public abstract class ExceptionTaskHelper extends AppListFragment {

    protected Throwable exception;

    protected static boolean noNetwork(Throwable e) {
        return e instanceof UnknownHostException
                || e instanceof SSLHandshakeException
                || e instanceof ConnectException
                || e instanceof SocketException
                || e instanceof SocketTimeoutException
                || (null != e && null != e.getCause() && noNetwork(e.getCause()))
                ;
    }

    protected boolean success() {
        return null == exception;
    }

    protected void processException(Throwable e) {
        Log.d(getClass().getSimpleName(), e.getClass().getName() + " caught during a google api request: " + e.getMessage());
        if (e instanceof AuthException) {
            processAuthException((AuthException) e);
        } else if (e instanceof IOException) {
            processIOException((IOException) e);
        } else {
            Log.e(getClass().getSimpleName(), "Unknown exception " + e.getClass().getName() + " " + e.getMessage());
            e.printStackTrace();
        }
    }

    protected void processIOException(IOException e) {
        String message;
        if (noNetwork(e) && this.getActivity() != null) {
            message = this.getActivity().getString(R.string.error_no_network);
        } else {
            message = TextUtils.isEmpty(e.getMessage())
                    ? this.getActivity().getString(R.string.error_network_other, e.getClass().getName())
                    : e.getMessage()
            ;
        }
        ContextUtil.toastLong(this.getActivity(), message);
    }

    protected void processAuthException(AuthException e) {
        if (e instanceof CredentialsEmptyException) {
            Log.i(getClass().getSimpleName(), "Credentials empty");
        } else if (e.getCode() == 401 && PreferenceFragment.getBoolean(this.getActivity(), PlayStoreApiAuthenticator.PREFERENCE_APP_PROVIDED_EMAIL)) {
            Log.i(getClass().getSimpleName(), "Token is stale");
            refreshMyToken();
        } else {
            ToastUtils.quickToast(getActivity(), e.getMessage());
            ContextUtil.toast(this.getActivity(), R.string.error_incorrect_password);
            new PlayStoreApiAuthenticator(this.getActivity()).logout();
        }
    }
}