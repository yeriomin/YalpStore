package com.dragons.aurora.fragment;

import android.app.DownloadManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.dragons.aurora.AuroraApplication;
import com.dragons.aurora.BlackWhiteListManager;
import com.dragons.aurora.PlayStoreApiAuthenticator;
import com.dragons.aurora.R;
import com.dragons.aurora.UpdatableRecyclerItemTouchHelper;
import com.dragons.aurora.UpdateAllReceiver;
import com.dragons.aurora.UpdateChecker;
import com.dragons.aurora.adapters.UpdatableAppsAdapter;
import com.dragons.aurora.model.App;
import com.dragons.aurora.notification.CancelDownloadService;
import com.dragons.aurora.task.playstore.ForegroundUpdatableAppsTaskHelper;
import com.percolate.caffeine.ToastUtils;
import com.percolate.caffeine.ViewUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class UpdatableAppsFragment extends ForegroundUpdatableAppsTaskHelper implements UpdatableRecyclerItemTouchHelper.UpdatableRecyclerItemTouchListener {

    public static int updates = 0;
    public static boolean recheck = false;
    public UpdatableAppsAdapter updatableAppsAdapter;
    Button recheck_update;
    private DownloadManager.Query query;
    private DownloadManager dm;
    private View view;
    private Disposable loadApps;
    private SwipeRefreshLayout swipeRefreshLayout;
    private UpdateAllReceiver updateAllReceiver;
    private Button update;
    private Button cancel;
    private TextView txt;
    private List<App> updatableApps = new ArrayList<>(new HashSet<>());

    public static UpdatableAppsFragment newInstance() {
        return new UpdatableAppsFragment();
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

        if (view != null) {
            if ((ViewGroup) view.getParent() != null)
                ((ViewGroup) view.getParent()).removeView(view);
            return view;
        }

        view = inflater.inflate(R.layout.app_updatable_inc, container, false);
        initViews();

        swipeRefreshLayout.setOnRefreshListener(() -> {
            if (isLoggedIn() && isConnected(getContext()))
                loadUpdatableApps();
            else
                swipeRefreshLayout.setRefreshing(false);
        });

        recheck_update.setOnClickListener(click -> {
            if (isLoggedIn() && isConnected(getContext())) {
                hide(view, R.id.unicorn);
                swipeRefreshLayout.setRefreshing(true);
                loadUpdatableApps();
            }
        });

        setupDelta();
        return view;
    }

    private void initViews() {
        recheck_update = ViewUtils.findViewById(view, R.id.recheck_updates);
        update = ViewUtils.findViewById(view, R.id.update_all);
        cancel = ViewUtils.findViewById(view, R.id.update_cancel);
        txt = ViewUtils.findViewById(view, R.id.updates_txt);
        swipeRefreshLayout = ViewUtils.findViewById(view, R.id.swipe_refresh_layout);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (isLoggedIn() && updatableApps.isEmpty() || recheck) {
            recheck = false;
            loadUpdatableApps();
        } else if (updatableApps.size() > 0)
            setText(view, R.id.updates_txt, R.string.list_update_all_txt, updatableApps.size());
        else if (!isLoggedIn())
            ToastUtils.quickToast(getActivity(), "You need to Login First", true);
        else {
            new UpdateAllReceiver(this);
        }
        updateAllReceiver = new UpdateAllReceiver(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        (getActivity()).unregisterReceiver(updateAllReceiver);
        swipeRefreshLayout.setRefreshing(false);
    }

    protected void setupListView(List<App> appsToAdd) {
        RecyclerView recyclerView = view.findViewById(R.id.updatable_apps_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getActivity());
        updatableAppsAdapter = new UpdatableAppsAdapter(getActivity(), appsToAdd);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), layoutManager.getOrientation());

        recyclerView.setItemViewCacheSize(30);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setAdapter(updatableAppsAdapter);
        new ItemTouchHelper(
                new UpdatableRecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this))
                .attachToRecyclerView(recyclerView);
    }

    public void launchUpdateAll() {
        ((AuroraApplication) getActivity().getApplicationContext()).setBackgroundUpdating(true);
        new UpdateChecker().onReceive(UpdatableAppsFragment.this.getActivity(), getActivity().getIntent());
        hide(view, R.id.update_all);
        show(view, R.id.update_cancel);
    }

    public void setupButtons() {
        update.setVisibility(View.VISIBLE);
        update.setOnClickListener(v -> {
            launchUpdateAll();
            update.setVisibility(View.GONE);
            cancel.setVisibility(View.VISIBLE);
            txt.setText(R.string.list_updating);
        });

        cancel.setOnClickListener(v -> {
            for (App app : updatableApps) {
                getContext().startService(new Intent(getContext().getApplicationContext(), CancelDownloadService.class)
                        .putExtra(CancelDownloadService.PACKAGE_NAME, app.getPackageName()));
            }
            update.setVisibility(View.VISIBLE);
            cancel.setVisibility(View.GONE);
            setText(view, R.id.updates_txt, R.string.list_update_all_txt, updatableApps.size());
        });
    }

    public void removeButtons() {
        update.setVisibility(View.GONE);
        cancel.setVisibility(View.GONE);
    }

    public void setupDelta() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        TextView delta = ViewUtils.findViewById(view, R.id.updates_setting);
        delta.setText(sharedPreferences.getBoolean("PREFERENCE_DOWNLOAD_DELTAS", true) ? R.string.delta_enabled : R.string.delta_disabled);
        delta.setVisibility(View.VISIBLE);
    }

    public void updateInteger(int count) {
        if (count >= 99) {
            updates = 99;
        } else updates = count;
    }

    public void loadUpdatableApps() {
        swipeRefreshLayout.setRefreshing(true);
        loadApps = Observable.fromCallable(() -> getUpdatableApps(new PlayStoreApiAuthenticator(this.getActivity()).getApi()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((appList) -> {
                    if (view != null) {
                        updatableApps = new ArrayList<>(new HashSet<>(appList));
                        Collections.sort(updatableApps);
                        setupListView(updatableApps);
                        updateInteger(updatableApps.size());
                        swipeRefreshLayout.setRefreshing(false);

                        if (success() && updatableApps.isEmpty()) {
                            show(view, R.id.unicorn);
                            setText(view, R.id.updates_txt, R.string.list_update_all_txt, updatableApps.size());
                            removeButtons();
                        } else {
                            hide(view, R.id.unicorn);
                            setText(view, R.id.updates_txt, R.string.list_update_all_txt, updatableApps.size());
                            setupButtons();
                        }
                    }
                }, this::processException);
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof UpdatableAppsAdapter.ViewHolder) {
            new BlackWhiteListManager(getActivity())
                    .add(((UpdatableAppsAdapter.ViewHolder) viewHolder).app.getPackageName());

            updatableAppsAdapter.remove(position);

            if (updatableAppsAdapter.getItemCount() == 0) {
                view.findViewById(R.id.unicorn).setVisibility(View.VISIBLE);
                setText(view, R.id.updates_txt, R.string.list_update_all_txt, 0);
                updateInteger(0);
                removeButtons();
            } else
                view.findViewById(R.id.unicorn).setVisibility(View.GONE);
        }
    }
}
