package com.github.yeriomin.yalpstore;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.List;

public class YalpSuggestionProvider extends ContentProvider {

    private PlayStoreApiWrapper api;

    @Override
    public boolean onCreate() {
        api = new PlayStoreApiWrapper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {
        return SearchManager.SUGGEST_MIME_TYPE;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        String query = uri.getLastPathSegment().toLowerCase();

        List<String> suggestions;
        try {
            suggestions = api.getSearchSuggestions(query);
        } catch (Throwable e) {
            suggestions = new ArrayList<>();
        }

        MatrixCursor cursor = new MatrixCursor(new String[] {
            BaseColumns._ID,
            SearchManager.SUGGEST_COLUMN_TEXT_1,
            SearchManager.SUGGEST_COLUMN_INTENT_DATA
        });
        for (int id = 0; id < suggestions.size(); id++) {
            cursor.addRow(new Object[] { id, suggestions.get(id), suggestions.get(id) });
        }
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
