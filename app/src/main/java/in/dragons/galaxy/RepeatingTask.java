package in.dragons.galaxy;

import android.os.Handler;
import android.os.Looper;

abstract public class RepeatingTask {

    abstract protected boolean shouldRunAgain();
    abstract protected void payload();

    public void execute(final long interval) {
        new Handler(Looper.getMainLooper()).postDelayed(
            new Runnable() {
                @Override
                public void run() {
                    payload();
                    if (shouldRunAgain()) {
                        execute(interval);
                    }
                }
            },
            interval
        );
    }
}
