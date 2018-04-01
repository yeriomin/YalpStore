package in.dragons.galaxy.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.percolate.caffeine.ViewUtils;

import in.dragons.galaxy.CategoryManager;
import in.dragons.galaxy.PlayStoreApiAuthenticator;
import in.dragons.galaxy.R;
import in.dragons.galaxy.adapters.AllCategoriesAdapter;
import in.dragons.galaxy.adapters.TopCategoriesAdapter;
import in.dragons.galaxy.task.playstore.CategoryListTaskHelper;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class CategoryListFragment extends CategoryListTaskHelper {

    private View v;
    private CategoryManager manager;
    private Disposable loadApps;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        manager = new CategoryManager(this.getActivity());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (v != null) {
            if ((ViewGroup) v.getParent() != null)
                ((ViewGroup) v.getParent()).removeView(v);
            return v;
        }

        v = inflater.inflate(R.layout.app_category_inc, container, false);

        if (isLoggedIn()) {
            if (manager.categoryListEmpty())
                getAllCategories();
            else {
                setupAllCategories();
                setupTopCategories();
            }
        } else
            LoginFirst();

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isLoggedIn() && manager.categoryListEmpty())
            getAllCategories();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (loadApps != null && !loadApps.isDisposed())
            loadApps.dispose();
    }

    protected void setupTopCategories() {
        RecyclerView recyclerView = ViewUtils.findViewById(v, R.id.top_cat_view);
        RecyclerView.LayoutManager rlm = new LinearLayoutManager(this.getActivity());
        recyclerView.setLayoutManager(rlm);
        RecyclerView.Adapter rva = new TopCategoriesAdapter(this.getActivity(), getResources().getStringArray(R.array.topCategories));
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity(), LinearLayoutManager.HORIZONTAL, false) {
            @Override
            public boolean canScrollHorizontally() {
                return true;
            }

            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        recyclerView.setAdapter(rva);
        ViewUtils.findViewById(v, R.id.cat_container).setVisibility(View.VISIBLE);
    }

    protected void setupAllCategories() {
        RecyclerView recyclerView = ViewUtils.findViewById(v, R.id.all_cat_view);
        RecyclerView.LayoutManager rlm = new LinearLayoutManager(this.getActivity());
        recyclerView.setLayoutManager(rlm);
        RecyclerView.Adapter rva = new AllCategoriesAdapter(this.getActivity(), manager.getCategoriesFromSharedPreferences());
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity(), LinearLayoutManager.VERTICAL, false) {
            @Override
            public boolean canScrollHorizontally() {
                return false;
            }

            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        recyclerView.setAdapter(rva);
        ViewUtils.findViewById(v, R.id.cat_container).setVisibility(View.VISIBLE);
    }

    protected void getAllCategories() {
        if (isDummy())
            refreshMyToken();
        ViewUtils.findViewById(v, R.id.progress).setVisibility(View.VISIBLE);
        loadApps = Observable.fromCallable(() -> getResult(new PlayStoreApiAuthenticator(this.getActivity()).getApi(), manager))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(bindToLifecycle())
                .subscribe((result) -> {
                    if (result) {
                        ViewUtils.findViewById(v, R.id.progress).setVisibility(View.GONE);
                        setupTopCategories();
                        setupAllCategories();
                    }
                }, this::processException);
    }
}
