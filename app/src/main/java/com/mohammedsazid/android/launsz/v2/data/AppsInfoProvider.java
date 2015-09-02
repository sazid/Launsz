/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Mohammed Sazid-Al-Rashid
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.mohammedsazid.android.launsz.v2.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

public class AppsInfoProvider extends ContentProvider {

    // Constants for the content provider
    public static final String CONTENT_AUTHORITY = "com.mohammedsazid.android.launsz.v2.data.AppsInfoProvider";
    public static final String URL = "content://" + CONTENT_AUTHORITY;
    public static final Uri CONTENT_URI = Uri.parse(URL);
    // Constants for matching Uris
    private static final int APPSINFO_APPS = 1;
    private static final int APPSINFO_APP = 2;
    private static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(CONTENT_AUTHORITY, "/apps", APPSINFO_APPS);
        uriMatcher.addURI(CONTENT_AUTHORITY, "/apps/app/#", APPSINFO_APP);
    }

    private AppsInfoOpenHelper appsInfoOpenHelper;

    public AppsInfoProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // TODO: Implement this to handle requests to insert a new row.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public boolean onCreate() {
        try {
            appsInfoOpenHelper = new AppsInfoOpenHelper(getContext());
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        // TODO: Implement this to handle query requests from clients.
        SQLiteDatabase db = appsInfoOpenHelper.getReadableDatabase();
        Cursor cursor = null;

        // Set the sorting to be based on launch count (by default)
        if (sortOrder != null || sortOrder.isEmpty()) {
            sortOrder = LaunszContract.AppsInfo.COLUMN_LAUNCH_COUNT + " DESC";
        }

        switch (uriMatcher.match(uri)) {
            case APPSINFO_APPS:
                cursor = db.query(
                        LaunszContract.AppsInfo.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case APPSINFO_APP:
                String id = uri.getLastPathSegment();
                cursor = db.query(
                        LaunszContract.AppsInfo.TABLE_NAME,
                        projection,
                        LaunszContract.AppsInfo._ID + " = ?",
                        new String[]{id},
                        null,
                        null,
                        sortOrder
                );

                break;
        }

        if (cursor == null) {
            Log.e(AppsInfoProvider.class.getSimpleName(), "Could not load the cursor.");
        }

        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        SQLiteDatabase db = appsInfoOpenHelper.getWritableDatabase();

        // Return -1 if update was unsuccessful
        int updateCount = -1;

        switch (uriMatcher.match(uri)) {
            case APPSINFO_APPS:
                updateCount = db.update(
                        LaunszContract.AppsInfo.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs
                );

                break;
            case APPSINFO_APP:
                String id = uri.getLastPathSegment();

                updateCount = db.update(
                        LaunszContract.AppsInfo.TABLE_NAME,
                        values,
                        LaunszContract.AppsInfo._ID + " = ?",
                        new String[]{id}
                );

                break;
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return updateCount;
    }
}