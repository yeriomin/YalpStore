package in.dragons.galaxy.task.playstore;

import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.dragons.aurora.playstoreapiv2.AuthException;
import com.dragons.aurora.playstoreapiv2.BulkDetailsEntry;
import com.dragons.aurora.playstoreapiv2.GooglePlayAPI;
import com.percolate.caffeine.ToastUtils;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.SSLHandshakeException;

import in.dragons.galaxy.BlackWhiteListManager;
import in.dragons.galaxy.BuildConfig;
import in.dragons.galaxy.ContextUtil;
import in.dragons.galaxy.CredentialsEmptyException;
import in.dragons.galaxy.PlayStoreApiAuthenticator;
import in.dragons.galaxy.R;
import in.dragons.galaxy.activities.GalaxyActivity;
import in.dragons.galaxy.fragment.AppListFragment;
import in.dragons.galaxy.fragment.PreferenceFragment;
import in.dragons.galaxy.model.App;
import in.dragons.galaxy.model.AppBuilder;
import in.dragons.galaxy.task.AppListValidityCheckTask;
import in.dragons.galaxy.task.InstalledAppsTask;

public abstract class ForegroundUpdatableAppsTaskHelper extends AppListFragment {

    protected View view;
    protected TextView errorView;
    protected View progressIndicator;
    protected Throwable exception;

    protected List<App> updatableApps = new ArrayList<>();
    protected List<App> allMarketApps = new ArrayList<>();

    protected List<App> getInstalledApps(GooglePlayAPI api) throws IOException {
        api.toc();
        allMarketApps.clear();
        Map<String, App> installedApps = getInstalledApps();
        for (App appFromMarket : getAppsFromPlayStore(api, filterBlacklistedApps(installedApps).keySet())) {
            String packageName = appFromMarket.getPackageName();
            if (TextUtils.isEmpty(packageName) || !installedApps.containsKey(packageName)) {
                continue;
            }
            App installedApp = installedApps.get(packageName);
            appFromMarket = addInstalledAppInfo(appFromMarket, installedApp);

            allMarketApps.add(appFromMarket);
        }

        return allMarketApps;
    }

    protected List<App> getUpdatableApps(GooglePlayAPI api) throws IOException {
        api.toc();
        updatableApps.clear();
        Map<String, App> installedApps = getInstalledApps();
        for (App appFromMarket : getAppsFromPlayStore(api, filterBlacklistedApps(installedApps).keySet())) {
            String packageName = appFromMarket.getPackageName();
            if (TextUtils.isEmpty(packageName) || !installedApps.containsKey(packageName)) {
                continue;
            }
            App installedApp = installedApps.get(packageName);
            appFromMarket = addInstalledAppInfo(appFromMarket, installedApp);
            if (installedApp.getVersionCode() < appFromMarket.getVersionCode()) {
                updatableApps.add(appFromMarket);
            }
            allMarketApps.add(appFromMarket);
        }
        if (!new BlackWhiteListManager(this.getActivity()).isUpdatable(BuildConfig.APPLICATION_ID)) {
            return updatableApps;
        }
        return updatableApps;
    }

    protected List<App> getAppsFromPlayStore(GooglePlayAPI api, Collection<String> packageNames) throws IOException {
        List<App> appsFromPlayStore = new ArrayList<>();
        boolean builtInAccount = PreferenceFragment.getBoolean(this.getActivity(), PlayStoreApiAuthenticator.PREFERENCE_APP_PROVIDED_EMAIL);
        for (App app : getRemoteAppList(api, new ArrayList<>(packageNames))) {
            if (!builtInAccount || app.isFree()) {
                appsFromPlayStore.add(app);
            }
        }
        return appsFromPlayStore;
    }

    protected List<App> getRemoteAppList(GooglePlayAPI api, List<String> packageNames) throws IOException {
        List<App> apps = new ArrayList<>();
        for (BulkDetailsEntry details : api.bulkDetails(packageNames).getEntryList()) {
            if (!details.hasDoc()) {
                continue;
            }
            apps.add(AppBuilder.build(details.getDoc()));
        }
        Collections.sort(apps);
        return apps;
    }

    protected Map<String, App> getInstalledApps() {
        InstalledAppsTask task = new InstalledAppsTask();
        task.setContext(this.getActivity());
        task.setIncludeSystemApps(true);
        return task.getInstalledApps(false);
    }

    protected App addInstalledAppInfo(App appFromMarket, App installedApp) {
        if (null != installedApp) {
            appFromMarket.setPackageInfo(installedApp.getPackageInfo());
            appFromMarket.setVersionName(installedApp.getVersionName());
            appFromMarket.setDisplayName(installedApp.getDisplayName());
            appFromMarket.setSystem(installedApp.isSystem());
            appFromMarket.setInstalled(true);
        }
        return appFromMarket;
    }

    protected Map<String, App> filterBlacklistedApps(Map<String, App> apps) {
        Set<String> packageNames = new HashSet<>(apps.keySet());
        if (PreferenceManager.getDefaultSharedPreferences(this.getActivity()).getString(PreferenceFragment.PREFERENCE_UPDATE_LIST_WHITE_OR_BLACK, PreferenceFragment.LIST_BLACK).equals(PreferenceFragment.LIST_BLACK)) {
            packageNames.removeAll(new BlackWhiteListManager(this.getActivity()).get());
        } else {
            packageNames.retainAll(new BlackWhiteListManager(this.getActivity()).get());
        }
        Map<String, App> result = new HashMap<>();
        for (App app : apps.values()) {
            if (packageNames.contains(app.getPackageName())) {
                result.put(app.getPackageName(), app);
            }
        }
        return result;
    }

    //Exception Handling
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
            ToastUtils.quickToast(getActivity(),e.getMessage());
            //ContextUtil.toast(this.getActivity(), R.string.error_incorrect_password);
            //new PlayStoreApiAuthenticator(this.getActivity()).logout();
        }
    }

    protected static boolean noNetwork(Throwable e) {
        return e instanceof UnknownHostException
                || e instanceof SSLHandshakeException
                || e instanceof ConnectException
                || e instanceof SocketException
                || e instanceof SocketTimeoutException
                || (null != e && null != e.getCause() && noNetwork(e.getCause()))
                ;
    }

    protected void checkAppListValidity() {
        AppListValidityCheckTask task = new AppListValidityCheckTask((GalaxyActivity) this.getActivity());
        task.setRespectUpdateBlacklist(true);
        task.setIncludeSystemApps(true);
        task.execute();
    }

}