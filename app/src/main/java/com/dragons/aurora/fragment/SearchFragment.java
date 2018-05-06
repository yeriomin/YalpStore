package com.dragons.aurora.fragment;

import android.annotation.TargetApi;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dragons.aurora.R;
import com.dragons.aurora.RecyclerItemTouchHelper;
import com.dragons.aurora.activities.SearchActivity;
import com.dragons.aurora.adapters.SearchHistoryAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class SearchFragment extends UtilFragment implements RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {

    SearchView searchToolbar;
    ArrayList<String> listHistory = new ArrayList<>();
    Set<String> setHistory = new HashSet<>();
    RecyclerView recyclerView;
    TextView emptyView;
    View view;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_search, container, false);
        searchToolbar = view.findViewById(R.id.search_apps);
        recyclerView = view.findViewById(R.id.searchHistory);
        emptyView = view.findViewById(R.id.emptyView);

        TextView clearAll = view.findViewById(R.id.clearAll);
        clearAll.setOnClickListener(v -> clearAll());

        RelativeLayout search_layout = view.findViewById(R.id.search_layout);
        search_layout.setOnClickListener(v -> {
            searchToolbar.setFocusable(true);
            searchToolbar.setIconified(false);
            searchToolbar.requestFocusFromTouch();
            searchToolbar.setQuery("", false);
        });
        addQueryTextListener(searchToolbar);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        setupSearchHistory();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    protected void addQueryTextListener(SearchView searchView) {
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        if (null != searchManager) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        }
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextChange(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.setQuery("", false);
                searchView.setIconified(true);
                searchView.clearFocus();
                setQuery(query);
                return false;
            }
        });

        searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int position) {
                return true;
            }

            @Override
            public boolean onSuggestionClick(int position) {
                Cursor cursor = searchView.getSuggestionsAdapter().getCursor();
                cursor.moveToPosition(position);
                String suggestion = cursor.getString(2);
                searchView.setQuery(suggestion, true);
                return false;
            }
        });
    }

    protected void setQuery(String query) {
        addHistory(query);
        Intent i = new Intent(getContext(), SearchActivity.class);
        i.setAction(Intent.ACTION_SEARCH);
        i.putExtra(SearchManager.QUERY, query);
        startActivity(i);
    }

    private void setupSearchHistory() {
        listHistory = getSharedValue();

        if (listHistory.isEmpty())
            toggleEmptyRecycle(true);
        else {
            toggleEmptyRecycle(false);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setNestedScrollingEnabled(false);
            recyclerView.setAdapter(new SearchHistoryAdapter(listHistory, getActivity()));
            new ItemTouchHelper(
                    new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this))
                    .attachToRecyclerView(recyclerView);
        }
    }

    public void addHistory(String query) {
        String date = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        String datedQuery = query + ":" + date;

        listHistory.add(datedQuery);
        setHistory.clear();
        setHistory.addAll(listHistory);
        putSharedValue(setHistory);
    }

    public void updateHistory() {
        setHistory.clear();
        setHistory.addAll(listHistory);
        putSharedValue(setHistory);

        recyclerView.getAdapter().notifyDataSetChanged();
        if (recyclerView.getAdapter().getItemCount() == 0) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        }
    }

    private ArrayList<String> getSharedValue() {
        Set<String> set = PreferenceManager
                .getDefaultSharedPreferences(getActivity())
                .getStringSet("SEARCH_HISTORY", null);

        if (set != null) {
            listHistory.clear();
            listHistory.addAll(set);
        }
        return listHistory;
    }

    private void putSharedValue(Set<String> setHistory) {
        PreferenceManager.getDefaultSharedPreferences(getActivity())
                .edit()
                .putStringSet("SEARCH_HISTORY", setHistory)
                .apply();
    }

    private void clearAll() {
        setHistory.clear();
        listHistory.clear();
        recyclerView.getAdapter().notifyDataSetChanged();
        putSharedValue(setHistory);
        toggleEmptyRecycle(true);
    }

    private void toggleEmptyRecycle(boolean toggle) {
        if (toggle) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof SearchHistoryAdapter.MyViewHolder) {
            String query = listHistory.get(viewHolder.getAdapterPosition());
            for (int j = listHistory.size() - 1; j >= 0; j--) {
                if (listHistory.get(j).contains(query)) {
                    listHistory.remove(j);
                    updateHistory();
                }
            }
        }
    }

}
