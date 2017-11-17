package com.github.yeriomin.yalpstore;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

import com.github.yeriomin.playstoreapi.GooglePlayAPI;
import com.github.yeriomin.playstoreapi.SearchSuggestEntry;

import java.io.File;

public class YalpSuggestionProvider extends ContentProvider {

    @Override
    public boolean onCreate() {
        return true;
    }

    @Override
    public String getType(Uri uri) {
        return SearchManager.SUGGEST_MIME_TYPE;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        MatrixCursor cursor = new MatrixCursor(new String[] {
            BaseColumns._ID,
            SearchManager.SUGGEST_COLUMN_TEXT_1,
            SearchManager.SUGGEST_COLUMN_INTENT_DATA,
            SearchManager.SUGGEST_COLUMN_ICON_1
        });
        try {
            int i = 0;
            for (SearchSuggestEntry entry: new PlayStoreApiAuthenticator(getContext()).getApi().searchSuggest(uri.getLastPathSegment()).getEntryList()) {
                cursor.addRow(constructRow(entry, i++));
            }
        } catch (Throwable e) {
            Log.e(getClass().getSimpleName(), e.getClass().getName() + ": " + e.getMessage());
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

    private Object[] constructRow(SearchSuggestEntry entry, int id) {
        return entry.getType() == GooglePlayAPI.SEARCH_SUGGESTION_TYPE.APP.value ? constructAppRow(entry, id) : constructSuggestionRow(entry, id);
    }

    private Object[] constructAppRow(SearchSuggestEntry entry, int id) {
        File file = new BitmapManager(getContext()).downloadAndGetFile(entry.getImageContainer().getImageUrl());
        return new Object[] { id, entry.getTitle(), entry.getPackageNameContainer().getPackageName(), null != file ? Uri.fromFile(file) : R.drawable.ic_placeholder };
    }

    private Object[] constructSuggestionRow(SearchSuggestEntry entry, int id) {
        return new Object[] { id, entry.getSuggestedQuery(), entry.getSuggestedQuery(), R.drawable.ic_search };
    }
}
