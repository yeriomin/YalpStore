package in.dragons.galaxy.downloader;

import android.util.Pair;
import android.view.View;

import java.lang.ref.WeakReference;

import in.dragons.galaxy.NumberProgressBar;
import in.dragons.galaxy.task.RepeatingTask;

public class DownloadProgressBarUpdater extends RepeatingTask {

    private String packageName;
    private WeakReference<NumberProgressBar> progressBarRef = new WeakReference<>(null);

    public DownloadProgressBarUpdater(String packageName, NumberProgressBar progressBar) {
        this.packageName = packageName;
        progressBarRef = new WeakReference<>(progressBar);
    }

    @Override
    protected boolean shouldRunAgain() {
        DownloadState state = DownloadState.get(packageName);
        return null != state && !state.isEverythingFinished();
    }

    @Override
    protected void payload() {
        NumberProgressBar progressBar = progressBarRef.get();
        if (null == progressBar) {
            return;
        }
        DownloadState state = DownloadState.get(packageName);
        if (null == state || state.isEverythingFinished()) {
            progressBar.setVisibility(View.GONE);
            return;
        }
        Pair<Integer, Integer> progress = state.getProgress();
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setProgress(progress.first);
        progressBar.setMax(progress.second);
    }
}
