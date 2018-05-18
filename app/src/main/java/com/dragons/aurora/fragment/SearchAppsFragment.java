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
import io.reactivex.schedulers.Schedulers;

public class SearchAppsFragment extends SearchTask {

    @BindView(R.id.search_apps_list)
    RecyclerView recyclerView;
    @BindView(R.id.adaptive_toolbar)
    AdaptiveToolbar adaptiveToolbar;

    private String title;

    private boolean loading = true;
    private int oldItems, visibleItems, totalItems;

    private AppListIterator iterator;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_list, container, false);

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
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(this.getActivity(), R.anim.layout_anim));
        recyclerView.setAdapter(new EndlessAppsAdapter(getActivity(), appsToAdd));
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    visibleItems = mLayoutManager.getChildCount();
                    totalItems = mLayoutManager.getItemCount();
                    oldItems = mLayoutManager.findFirstVisibleItemPosition();

                    if (loading) {
                        if ((visibleItems + oldItems) >= totalItems - 2) {
                            loading = false;
                            fetchSearchAppsList(true);
                        }
                    }
                }
            }
        });
    }

    public void fetchSearchAppsList(boolean loadMore) {
        Observable.fromCallable(() -> getResult(iterator))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(appList -> {
                    if (loadMore) {
                        loading = true;
                        addApps(appList);
                    } else
                        setupListView(appList);
                }, this::processException);
    }

    public void addApps(List<App> appsToAdd) {
        EndlessAppsAdapter adapter = (EndlessAppsAdapter) recyclerView.getAdapter();
        for (App app : appsToAdd) {
            adapter.add(app);
        }
        adapter.notifyItemInserted(appsToAdd.size());
    }
}