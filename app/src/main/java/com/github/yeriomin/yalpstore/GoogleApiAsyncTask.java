package com.github.yeriomin.yalpstore;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.github.yeriomin.playstoreapi.AuthException;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import javax.net.ssl.SSLHandshakeException;

abstract class GoogleApiAsyncTask extends AsyncTask<String, Void, Throwable> {

    protected Context context;
    protected int progressDialogTitleId;
    protected int progressDialogMessageId;
    protected ProgressDialog progressDialog;
    protected TextView errorView;
    protected GoogleApiAsyncTask taskClone;

    public void setContext(Context context) {
        this.context = context;
    }

    public void prepareDialog(int messageId, int titleId) {
        this.progressDialogTitleId = titleId;
        this.progressDialogMessageId = messageId;
    }

    public void setErrorView(TextView errorView) {
        this.errorView = errorView;
    }

    public void setTaskClone(GoogleApiAsyncTask taskClone) {
        this.taskClone = taskClone;
    }

    @Override
    protected void onPreExecute() {
        if (progressDialogMessageId > 0 && progressDialogTitleId > 0) {
            this.progressDialog = ProgressDialog.show(
                this.context,
                this.context.getString(this.progressDialogTitleId),
                this.context.getString(this.progressDialogMessageId),
                true
            );
        }
    }

    @Override
    protected void onPostExecute(Throwable e) {
        if (null != this.progressDialog) {
            this.progressDialog.dismiss();
        }
        if (e instanceof RuntimeException && null != e.getCause()) {
            e = e.getCause();
        }
        if (e instanceof AuthException) {
            if (e instanceof CredentialsEmptyException) {
                Log.w(getClass().getName(), "Credentials empty");
            } else {
                toast(this.context, R.string.error_incorrect_password);
                new PlayStoreApiWrapper(this.context).forceTokenRefresh();
            }
            AccountTypeDialogBuilder builder = new AccountTypeDialogBuilder(this.context);
            builder.setTaskClone(this.taskClone);
            builder.show();
        } else if (e instanceof IOException) {
            String message;
            if (e instanceof UnknownHostException
                || e instanceof SSLHandshakeException
                || e instanceof ConnectException
                || e instanceof SocketException
                || e instanceof SocketTimeoutException) {
                message = this.context.getString(R.string.error_no_network);
            } else {
                message = this.context.getString(R.string.error_network_other, e.getClass().getName() + " " + e.getMessage());
            }
            if (null != this.errorView) {
                this.errorView.setText(message);
            } else {
                toast(this.context, message);
            }
        } else if (e != null) {
            Log.e(getClass().getName(), "Unknown exception " + e.getClass().getName() + " " + e.getMessage());
            e.printStackTrace();
        }
    }

    static protected void toast(Context c, String message) {
        Toast.makeText(
            c.getApplicationContext(),
            message,
            Toast.LENGTH_LONG
        ).show();
    }

    static protected void toast(Context c, int stringId, String... stringArgs) {
        toast(c, c.getString(stringId, (Object[]) stringArgs));
    }
}
