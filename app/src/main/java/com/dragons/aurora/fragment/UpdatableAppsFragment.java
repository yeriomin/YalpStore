package com.dragons.aurora.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.dragons.aurora.task.playstore.UpdatableAppsTaskHelper;
import com.github.florent37.shapeofview.shapes.RoundRectView;
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

public class UpdatableAppsFragment extends UpdatableAppsTaskHelper implements UpdatableRecyclerItemTouchHelper.UpdatableRecyclerItemTouchListener {

    public static int updates = 0;
    public static boolean recheck = false;
    public UpdatableAppsAdapter updatableAppsAdapter;
    private View view;
    private Disposable loadApps;
    private SwipeRefreshLayout swipeRefreshLayout;
    private UpdateAllReceiver updateAllReceiver;
    private Button update;
    private Button recheck_update;
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
                txt.setText(R.string.list_update_chk_txt);
                swipeRefreshLayout.setRefreshing(true);
                loadUpdatableApps();
            }
        });

        setupAutoUpdate();
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
        setupDelta();
    }

    @Override
    public void onStop() {
        super.onStop();
        (getActivity()).unregisterReceiver(updateAllReceiver);
        swipeRefreshLayout.setRefreshing(false);
    }

    protected void setupListView(List<App> appsToAdd) {
        RecyclerView recyclerView = view.findViewById(R.id.updatable_apps_list);
        recyclerView.setItemViewCacheSize(30);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        recyclerView.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(getContext(), R.anim.layout_anim));

        updatableAppsAdapter = new UpdatableAppsAdapter(getActivity(), appsToAdd);
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

    public void setupAutoUpdate() {
        RoundRectView autoUpdatesCard = view.findViewById(R.id.autoUpdatesCard);
        ImageView autoUpdatesClose = view.findViewById(R.id.autoUpdatesClose);
        Button autoUpdatesSwitch = view.findViewById(R.id.autoUpdatesSwitch);
        boolean shouldAsk = PreferenceFragment.getBoolean(getContext(), "PROMPT_UPDATE_INTERVAL");
        if (PreferenceFragment.getString(getContext(), "PREFERENCE_BACKGROUND_UPDATE_INTERVAL").equals("-1") && !shouldAsk) {
            autoUpdatesCard.setVisibility(View.VISIBLE);
        }

        autoUpdatesClose.setOnClickListener(click -> {
            autoUpdatesCard.setVisibility(View.GONE);
            PreferenceManager.getDefaultSharedPreferences(getActivity())
                    .edit()
                    .putBoolean("PROMPT_UPDATE_INTERVAL", true)
                    .apply();
        });

        autoUpdatesSwitch.setOnClickListener(click -> {
            PreferenceManager.getDefaultSharedPreferences(getActivity())
                    .edit()
                    .putString("PREFERENCE_BACKGROUND_UPDATE_INTERVAL", "86400000")
                    .apply();
            autoUpdatesCard.setVisibility(View.GONE);
        });
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
