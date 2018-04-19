package com.dragons.aurora;

import android.widget.AbsListView;
import android.widget.ListView;

abstract public class ScrollEdgeListener implements ListView.OnScrollListener {

    private int lastLastitem;

    abstract protected void loadMore();

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        int lastItem = firstVisibleItem + visibleItemCount;
        boolean loadMore = lastItem >= totalItemCount;
        if (totalItemCount > 0 && loadMore && lastLastitem != lastItem) {
            lastLastitem = lastItem;
            loadMore();
        }
    }
}
