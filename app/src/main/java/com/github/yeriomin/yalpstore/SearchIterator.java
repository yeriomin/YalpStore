package com.github.yeriomin.yalpstore;

import com.github.yeriomin.playstoreapi.DocV2;
import com.github.yeriomin.playstoreapi.SearchResponse;

import java.util.ArrayList;
import java.util.List;

class SearchIterator extends AppListIterator {

    public SearchIterator(com.github.yeriomin.playstoreapi.SearchIterator iterator) {
        super(iterator);
    }

    @Override
    protected List<DocV2> getDocList() {
        SearchResponse response = ((com.github.yeriomin.playstoreapi.SearchIterator) iterator).next();
        if (response.getDocCount() == 0) {
            return new ArrayList<>();
        }
        return response.getDoc(0).getChildList();
    }
}
