package com.dragons.aurora.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dragons.aurora.PlayStoreApiAuthenticator;
import com.dragons.aurora.R;
import com.dragons.aurora.adapters.InstalledAppsAdapter;
import com.dragons.aurora.model.App;
import com.dragons.aurora.task.playstore.ForegroundUpdatableAppsTaskHelper;
import com.percolate.caffeine.ViewUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class InstalledAppsFragment extends ForegroundUpdatableAppsTaskHelper {

    private View v;
    private Disposable loadApps;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;

    public static InstalledAppsFragment newInstance() {
        return new InstalledAppsFragment();
    }

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

        v = inflater.inflate(R.layout.app_installed_inc, container, false);

        swipeRefreshLayout = ViewUtils.findViewById(v, R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            if (isLoggedIn())
                loadMarketApps();
            else
                swipeRefreshLayout.setRefreshing(false);
        });

        recyclerView = v.findViewById(R.id.installed_apps_list);
        registerForContextMenu(recyclerView);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isLoggedIn() && allMarketApps.isEmpty())
            loadMarketApps();
        else {
            checkAppListValidity();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        swipeRefreshLayout.setRefreshing(false);
    }

    protected void setupListView(List<App> appsToAdd) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getActivity());
        InstalledAppsAdapter installedAppsAdapter = new InstalledAppsAdapter(getActivity(), appsToAdd);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), layoutManager.getOrientation());

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setAdapter(installedAppsAdapter);
    }

    public void loadMarketApps() {
        if (isDummy())
            refreshMyToken();
        swipeRefreshLayout.setRefreshing(true);
        loadApps = Observable.fromCallable(() -> getInstalledApps(new PlayStoreApiAuthenticator(this.getActivity()).getApi()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((appList) -> {
                    if (v != null) {
                        clearApps();
                        appList = new ArrayList<>(new HashSet<>(appList));
                        Collections.sort(appList);
                        setupListView(appList);
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, this::processException);
    }

}