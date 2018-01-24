package in.dragons.galaxy;

import android.content.Context;

public abstract class DownloadManagerAbstract implements DownloadManagerInterface {

    protected Context context;

    public DownloadManagerAbstract(Context context) {
        this.context = context;
    }

    static protected String getErrorString(Context context, int reason) {
        int stringId;
        switch (reason) {
            case DownloadManagerInterface.ERROR_CANNOT_RESUME:
                stringId = R.string.download_manager_ERROR_CANNOT_RESUME;
                break;
            case DownloadManagerInterface.ERROR_DEVICE_NOT_FOUND:
                stringId = R.string.download_manager_ERROR_DEVICE_NOT_FOUND;
                break;
            case DownloadManagerInterface.ERROR_FILE_ERROR:
                stringId = R.string.download_manager_ERROR_FILE_ERROR;
                break;
            case DownloadManagerInterface.ERROR_HTTP_DATA_ERROR:
                stringId = R.string.download_manager_ERROR_HTTP_DATA_ERROR;
                break;
            case DownloadManagerInterface.ERROR_INSUFFICIENT_SPACE:
                stringId = R.string.download_manager_ERROR_INSUFFICIENT_SPACE;
                break;
            case DownloadManagerInterface.ERROR_TOO_MANY_REDIRECTS:
                stringId = R.string.download_manager_ERROR_TOO_MANY_REDIRECTS;
                break;
            case DownloadManagerInterface.ERROR_UNHANDLED_HTTP_CODE:
                stringId = R.string.download_manager_ERROR_UNHANDLED_HTTP_CODE;
                break;
            case DownloadManagerInterface.ERROR_BLOCKED:
                stringId = R.string.download_manager_ERROR_BLOCKED;
                break;
            case DownloadManagerInterface.ERROR_UNKNOWN:
            default:
                stringId = R.string.download_manager_ERROR_UNKNOWN;
                break;
        }
        return context.getString(stringId);
    }

    @Override
    public void cancel(long downloadId) {
        DownloadState state = DownloadState.get(downloadId);
        if (null != state) {
            state.setCancelled(downloadId);
        }
    }
}
