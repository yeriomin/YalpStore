package com.dragons.aurora.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.percolate.caffeine.ViewUtils;

import java.util.Collections;

import com.dragons.aurora.PlayStoreApiAuthenticator;
import com.dragons.aurora.R;
import com.dragons.aurora.task.playstore.ForegroundUpdatableAppsTaskHelper;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class InstalledAppsFragment extends ForegroundUpdatableAppsTaskHelper {

    private View v;
    private Disposable loadApps;
    private SwipeRefreshLayout swipeRefreshLayout;

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

        setupListView(v, R.layout.installed_list_item);

        swipeRefreshLayout = ViewUtils.findViewById(v, R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            if (isLoggedIn())
                loadMarketApps();
            else {
                LoginFirst();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        getListView().setOnItemClickListener((parent, view, position, id) -> {
            grabDetails(position);
        });

        registerForContextMenu(getListView());

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isLoggedIn() && allMarketApps.isEmpty())
            loadMarketApps();
        else if (!isLoggedIn())
            LoginFirst();
        else {
            checkAppListValidity();
        }
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
                        Collections.sort(appList);
                        addApps(appList);
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, this::processException);
    }

    @Override
    public void onStop() {
        super.onStop();
        swipeRefreshLayout.setRefreshing(false);
    }

    public static InstalledAppsFragment newInstance() {
        return new InstalledAppsFragment();
    }
}