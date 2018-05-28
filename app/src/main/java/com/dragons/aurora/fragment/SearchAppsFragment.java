package com.dragons.aurora.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;

import com.dragons.aurora.AppListIterator;
import com.dragons.aurora.PlayStoreApiAuthenticator;
import com.dragons.aurora.R;
import com.dragons.aurora.activities.AuroraActivity;
import com.dragons.aurora.adapters.EndlessAppsAdapter;
import com.dragons.aurora.model.App;
import com.dragons.aurora.playstoreapiv2.SearchIterator;
import com.dragons.aurora.task.playstore.SearchTask;
import com.dragons.aurora.view.AdaptiveToolbar;

import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class SearchAppsFragment extends SearchTask {

    @BindView(R.id.search_apps_list)
    RecyclerView recyclerView;
    @BindView(R.id.adaptive_toolbar)
    AdaptiveToolbar adaptiveToolbar;
    @BindView(R.id.progress)
    ProgressBar progressBar;

    private String title;
    private boolean setLooper = true;
    private boolean loading = true;
    private int oldItems, visibleItems, totalItems;
    private View view;
    private AppListIterator iterator;
    private EndlessAppsAdapter endlessAppsAdapter;
    private Disposable disposable;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_search_list, container, false);
        ButterKnife.bind(this, view);
        adaptiveToolbar.getAction_icon().setOnClickListener((v -> this.getActivity().onBackPressed()));
        adaptiveToolbar.getTitle0().setText(title);
        adaptiveToolbar.getTitle1().setVisibility(View.GONE);
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
            String query = arguments.getString("SearchQuery");
            title = arguments.getString("SearchTitle");
            iterator = setupIterator(query);
            fetchSearchAppsList(false);
        } else
            Log.e(this.getClass().getName(), "No category id provided");
    }

    protected AppListIterator setupIterator(String query) {
        AppListIterator iterator;
        try {
            iterator = new AppListIterator(new SearchIterator(new PlayStoreApiAuthenticator(getContext()).getApi(), query));
            iterator.setFilter(new FilterMenu((AuroraActivity) getContext()).getFilterPreferences());
            return iterator;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    protected void setupListView(List<App> appsToAdd) {
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this.getActivity());
        endlessAppsAdapter = new EndlessAppsAdapter(getActivity(), appsToAdd);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(this.getActivity(), R.anim.layout_anim));
        recyclerView.setAdapter(endlessAppsAdapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if ((visibleItems + oldItems) >= totalItems + 2)
                    setLooper = false;
                if (dy > 0) {
                    visibleItems = mLayoutManager.getChildCount();
                    totalItems = mLayoutManager.getItemCount();
                    oldItems = mLayoutManager.findFirstVisibleItemPosition();

                    if (loading && !setLooper) {
                        if ((visibleItems + oldItems) >= totalItems - 2) {
                            loading = false;
                            fetchSearchAppsList(true);
                        }
                    }
                }
            }
        });
        getLooper();
    }

    public void fetchSearchAppsList(boolean loadMore) {
        disposable = Observable.fromCallable(() -> getResult(iterator))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(appList -> {
                    if (view != null) {
                        if (loadMore) {
                            loading = true;
                            addApps(appList);
                        } else
                            setupListView(appList);
                    }
                }, this::processException);
    }

    public void addApps(List<App> appsToAdd) {
        if (!appsToAdd.isEmpty()) {
            for (App app : appsToAdd)
                endlessAppsAdapter.add(app);
            endlessAppsAdapter.notifyItemInserted(endlessAppsAdapter.getItemCount() - 1);
        }
        getLooper();
    }

    public void getLooper() {
        if (iterator.hasNext() && setLooper)
            fetchSearchAppsList(true);
    }
}