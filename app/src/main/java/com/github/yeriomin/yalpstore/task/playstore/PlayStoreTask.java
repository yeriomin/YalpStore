package com.github.yeriomin.yalpstore.task.playstore;

import android.util.Log;
import android.widget.TextView;

import com.github.yeriomin.playstoreapi.AuthException;
import com.github.yeriomin.yalpstore.AccountTypeDialogBuilder;
import com.github.yeriomin.yalpstore.ContextUtil;
import com.github.yeriomin.yalpstore.CredentialsEmptyException;
import com.github.yeriomin.yalpstore.FirstLaunchChecker;
import com.github.yeriomin.yalpstore.PlayStoreApiAuthenticator;
import com.github.yeriomin.yalpstore.R;
import com.github.yeriomin.yalpstore.task.TaskWithProgress;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import javax.net.ssl.SSLHandshakeException;

abstract public class PlayStoreTask<T> extends TaskWithProgress<T> {

    protected Throwable exception;
    protected TextView errorView;

    public void setErrorView(TextView errorView) {
        this.errorView = errorView;
    }

    public Throwable getException() {
        return exception;
    }

    public boolean success() {
        return null == exception;
    }

    @Override
    protected void onPostExecute(T result) {
        super.onPostExecute(result);
        if (exception != null) {
            processException(exception);
        }
    }

    protected void processException(Throwable e) {
        Log.d(getClass().getName(), e.getClass().getName() + " caught during a google api request: " + e.getMessage());
        if (e instanceof AuthException) {
            processAuthException((AuthException) e);
        } else if (e instanceof IOException) {
            processIOException((IOException) e);
        } else {
            Log.e(getClass().getName(), "Unknown exception " + e.getClass().getName() + " " + e.getMessage());
            e.printStackTrace();
        }
    }

    protected void processIOException(IOException e) {
        String message;
        if (noNetwork(e)) {
            message = this.context.getString(R.string.error_no_network);
        } else {
            message = this.context.getString(R.string.error_network_other, e.getClass().getName() + " " + e.getMessage());
        }
        if (null != this.errorView) {
            this.errorView.setText(message);
        } else {
            ContextUtil.toastLong(this.context, message);
        }
    }

    protected void processAuthException(AuthException e) {
        if (!ContextUtil.isAlive(context)) {
            Log.e(getClass().getName(), "AuthException happened and the provided context is not ui capable");
            return;
        }
        AccountTypeDialogBuilder builder = new AccountTypeDialogBuilder(this.context);
        builder.setCaller(this);
        if (e instanceof CredentialsEmptyException) {
            Log.i(getClass().getName(), "Credentials empty");
            if (new FirstLaunchChecker(context).isFirstLogin()) {
                Log.i(getClass().getName(), "First launch, so using built-in account");
                builder.logInWithPredefinedAccount();
                ContextUtil.toast(context, R.string.first_login_message);
                return;
            }
        } else {
            ContextUtil.toast(this.context, R.string.error_incorrect_password);
            new PlayStoreApiAuthenticator(context).logout();
        }
        builder.show();
    }

    static protected boolean noNetwork(Throwable e) {
        return e instanceof UnknownHostException
            || e instanceof SSLHandshakeException
            || e instanceof ConnectException
            || e instanceof SocketException
            || e instanceof SocketTimeoutException
        ;
    }
}
