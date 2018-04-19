package com.dragons.aurora.fragment;

import android.app.DownloadManager;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.percolate.caffeine.ToastUtils;
import com.percolate.caffeine.ViewUtils;

import java.util.Collections;

import com.dragons.aurora.AuroraApplication;
import com.dragons.aurora.PlayStoreApiAuthenticator;
import com.dragons.aurora.R;
import com.dragons.aurora.UpdateAllReceiver;
import com.dragons.aurora.UpdateChecker;
import com.dragons.aurora.activities.AuroraActivity;
import com.dragons.aurora.adapters.AppListAdapter;
import com.dragons.aurora.model.App;
import com.dragons.aurora.task.playstore.ForegroundUpdatableAppsTaskHelper;
import com.dragons.aurora.view.ListItem;
import com.dragons.aurora.view.UpdatableAppBadge;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static android.content.Context.DOWNLOAD_SERVICE;

public class UpdatableAppsFragment extends ForegroundUpdatableAppsTaskHelper {

    private DownloadManager.Query query;
    private DownloadManager dm;
    private View v;
    private Disposable loadApps;
    private SwipeRefreshLayout swipeRefreshLayout;
    public static int updates = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setHasOptionsMenu(true);
        this.setRetainInstance(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (v != null) {
            if ((ViewGroup) v.getParent() != null)
                ((ViewGroup) v.getParent()).removeView(v);
            return v;
        }

        v = inflater.inflate(R.layout.app_updatable_inc, container, false);

        swipeRefreshLayout = ViewUtils.findViewById(v, R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            if (isLoggedIn())
                loadUpdatableApps();
            else {
                //LoginFirst();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        setupListView(v, R.layout.two_line_list_item_with_icon);
        setupDelta();

        getListView().setOnItemClickListener((parent, view, position, id) -> grabDetails(position));
        registerForContextMenu(getListView());
        updateInteger();
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isLoggedIn() && updatableApps.isEmpty())
            loadUpdatableApps();
        else if (!updatableApps.isEmpty())
            setText(R.id.updates_txt, R.string.list_update_all_txt, updatableApps.size());
        else if (!isLoggedIn())
            ToastUtils.quickToast(getActivity(), "You need to Login First", true);
        else {
            new UpdateAllReceiver((AuroraActivity) getActivity());
            checkAppListValidity();
        }
        updateInteger();
    }

    public void updateInteger() {
        updates = updatableApps.size();
        if (updatableApps.size() >= 99){
            updates = 99;
        }
    }

    public void loadUpdatableApps() {
        swipeRefreshLayout.setRefreshing(true);
        loadApps = Observable.fromCallable(() -> getUpdatableApps(new PlayStoreApiAuthenticator(this.getActivity()).getApi()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((appList) -> {
                    if (v != null) {
                        clearApps();
                        Collections.sort(appList);
                        addApps(appList);

                        swipeRefreshLayout.setRefreshing(false);

                        if (success() && appList.isEmpty())
                            ViewUtils.findViewById(v, R.id.unicorn).setVisibility(View.VISIBLE);
                        else {
                            setText(R.id.updates_txt, R.string.list_update_all_txt, appList.size());
                            setupButtons();
                        }
                    }
                }, this::processException);
    }

    @Override
    public void removeApp(String packageName) {
        super.removeApp(packageName);
        if (updatableApps.isEmpty()) {
            v.findViewById(R.id.unicorn).setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    protected void clearApps() {
        ((AppListAdapter) getListView().getAdapter()).clear();
    }

    @Override
    protected ListItem getListItem(App app) {
        UpdatableAppBadge appBadge = new UpdatableAppBadge();
        appBadge.setApp(app);
        return appBadge;
    }

    public void launchUpdateAll() {
        ((AuroraApplication) getActivity().getApplicationContext()).setBackgroundUpdating(true);
        new UpdateChecker().onReceive(UpdatableAppsFragment.this.getActivity(), getActivity().getIntent());
        ViewUtils.findViewById(v, R.id.update_all).setVisibility(View.GONE);
        ViewUtils.findViewById(v, R.id.update_cancel).setVisibility(View.VISIBLE);
    }

    public void setupButtons() {
        Button update = ViewUtils.findViewById(v, R.id.update_all);
        Button cancel = ViewUtils.findViewById(v, R.id.update_cancel);
        TextView txt = ViewUtils.findViewById(v, R.id.updates_txt);

        update.setVisibility(View.VISIBLE);

        update.setOnClickListener(v -> {
            launchUpdateAll();
            update.setVisibility(View.GONE);
            cancel.setVisibility(View.VISIBLE);
            txt.setText(R.string.list_updating);
        });

        cancel.setOnClickListener(v -> {
            query = new DownloadManager.Query();
            query.setFilterByStatus(DownloadManager.STATUS_PENDING | DownloadManager.STATUS_RUNNING);
            dm = (DownloadManager) this.getActivity().getSystemService(DOWNLOAD_SERVICE);
            Cursor c = dm.query(query);
            while (c.moveToNext()) {
                dm.remove(c.getLong(c.getColumnIndex(DownloadManager.COLUMN_ID)));
            }
            update.setVisibility(View.VISIBLE);
            cancel.setVisibility(View.GONE);
            setText(R.id.updates_txt, R.string.list_update_all_txt, updatableApps.size());
        });
    }

    public void setupDelta() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        TextView delta = ViewUtils.findViewById(v, R.id.updates_setting);
        delta.setText(sharedPreferences.getBoolean("PREFERENCE_DOWNLOAD_DELTAS", true) ? R.string.delta_enabled : R.string.delta_disabled);
        delta.setVisibility(View.VISIBLE);
    }

    public static UpdatableAppsFragment newInstance() {
        return new UpdatableAppsFragment();
    }
}
