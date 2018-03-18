package com.github.yeriomin.yalpstore.task;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.View;

import com.github.yeriomin.yalpstore.ContextUtil;
import com.github.yeriomin.yalpstore.ThemeManager;

abstract public class TaskWithProgress<T> extends AsyncTask<String, Void, T> {

    protected Context context;
    protected ProgressDialog progressDialog;
    protected View progressIndicator;

    public View getProgressIndicator() {
        return progressIndicator;
    }

    public void setProgressIndicator(View progressIndicator) {
        this.progressIndicator = progressIndicator;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void prepareDialog(int messageId, int titleId) {
        ProgressDialog dialog = new ProgressDialog(context, new ThemeManager().getDialogThemeId(context));
        dialog.setTitle(context.getString(titleId));
        dialog.setMessage(context.getString(messageId));
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        this.progressDialog = dialog;
    }

    @Override
    protected void onPreExecute() {
        if (null != this.progressDialog && ContextUtil.isAlive(context)) {
            this.progressDialog.show();
        }
        if (null != progressIndicator) {
            progressIndicator.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onPostExecute(T result) {
        if (null != this.progressDialog && ContextUtil.isAlive(context) && progressDialog.isShowing()) {
            this.progressDialog.dismiss();
        }
        if (null != progressIndicator) {
            progressIndicator.setVisibility(View.GONE);
        }
    }
}
