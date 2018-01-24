package in.dragons.galaxy;

import android.util.Pair;
import android.view.View;
import android.widget.ProgressBar;

import java.lang.ref.WeakReference;

public class DownloadProgressBarUpdater extends RepeatingTask {

    private String packageName;
    private WeakReference<ProgressBar> progressBarRef = new WeakReference<>(null);

    public DownloadProgressBarUpdater(String packageName, ProgressBar progressBar) {
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
        ProgressBar progressBar = progressBarRef.get();
        if (null == progressBar) {
            return;
        }
        DownloadState state = DownloadState.get(packageName);
        if (null == state || state.isEverythingFinished()) {
            progressBar.setVisibility(View.GONE);
            progressBar.setIndeterminate(true);
            return;
        }
        Pair<Integer, Integer> progress = state.getProgress();
        progressBar.setIndeterminate(false);
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setProgress(progress.first);
        progressBar.setMax(progress.second);
    }
}
