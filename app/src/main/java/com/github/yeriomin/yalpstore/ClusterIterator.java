package com.github.yeriomin.yalpstore;

import com.github.yeriomin.playstoreapi.DocV2;
import com.github.yeriomin.playstoreapi.ListResponse;
import com.github.yeriomin.playstoreapi.UrlIterator;

import java.util.ArrayList;
import java.util.List;

public class ClusterIterator extends AppListIterator {

    public ClusterIterator(UrlIterator iterator) {
        super(iterator);
    }

    @Override
    protected List<DocV2> getDocList() {
        ListResponse response = ((UrlIterator) iterator).next();
        if (response.getDocCount() == 0) {
            return new ArrayList<>();
        }
        return response.getDoc(0).getChildList();
    }
}
