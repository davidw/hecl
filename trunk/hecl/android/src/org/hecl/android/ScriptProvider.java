/*
 * Copyright (C) 2007 DedaSys LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/* Repurposed from the NotePad tutorial. */

package org.hecl.android;

import android.content.ContentProvider;

import android.content.ContentUris;
import android.content.UriMatcher;
import android.content.ContentValues;
import android.content.Resources;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import java.util.HashMap;

/**
 * Provides access to a database of notes. Each note has a title, the note
 * itself, a creation date and a modified data.
 *
 */
public class ScriptProvider extends ContentProvider {

    protected static final String CREATED_DATE = "created";
    protected static final String MODIFIED_DATE = "modified";
    protected static final String TITLE = "title";
    protected static final String _ID = "_id";
    protected static final String SCRIPT = "script";
    public static final Uri CONTENT_URI =
	Uri.parse("content://org.hecl.android.Scripts/scripts");

    private SQLiteDatabase db;

    private static final String TAG = "ScriptProvider";
    private static final String DATABASE_NAME = "hecl_script.db";
    private static final int DATABASE_VERSION = 2;

    private static HashMap<String, String> SCRIPT_LIST_PROJECTION_MAP;

    private static final int SCRIPTS = 1;
    private static final int SCRIPT_ID = 2;

    private static final UriMatcher URL_MATCHER;

    private static class DatabaseHelper extends SQLiteOpenHelper {

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE scripts (_id INTEGER PRIMARY KEY,"
		       + "title TEXT, script TEXT, created INTEGER,"
		       + "modified INTEGER" + ");");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	    /* FIXME - not needed at this point in time. */
            onCreate(db);
        }
    }

    @Override
    public boolean onCreate() {
        DatabaseHelper dbHelper = new DatabaseHelper();
        db = dbHelper.openDatabase(getContext(), DATABASE_NAME, null, DATABASE_VERSION);
        return (db == null) ? false : true;
    }

    @Override
    public Cursor query(Uri url, String[] projection, String selection,
			String[] selectionArgs, String sort) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        switch (URL_MATCHER.match(url)) {
        case SCRIPTS:
            qb.setTables("scripts");
            qb.setProjectionMap(SCRIPT_LIST_PROJECTION_MAP);
            break;

        case SCRIPT_ID:
            qb.setTables("scripts");
            qb.appendWhere("_id=" + url.getPathSegments().get(1));
            break;

        default:
            throw new IllegalArgumentException("Unknown URL " + url);
        }

        // If no sort order is specified use the default
        String orderBy;
        if (TextUtils.isEmpty(sort)) {
            orderBy = "title";
        } else {
            orderBy = sort;
        }

        Cursor c = qb.query(db, projection, selection, selectionArgs, null,
			    null, orderBy);
        c.setNotificationUri(getContext().getContentResolver(), url);
        return c;
    }

    @Override
    public String getType(Uri url) {
        switch (URL_MATCHER.match(url)) {
        case SCRIPTS:
            return "vnd.android.cursor.dir/vnd.hecl.script";

        case SCRIPT_ID:
            return "vnd.android.cursor.item/vnd.hecl.script";

        default:
            throw new IllegalArgumentException("Unknown URL " + url);
        }
    }

    @Override
    public Uri insert(Uri url, ContentValues initialValues) {
        long rowID;
        ContentValues values;
        if (initialValues != null) {
            values = new ContentValues(initialValues);
        } else {
            values = new ContentValues();
        }

        if (URL_MATCHER.match(url) != SCRIPTS) {
            throw new IllegalArgumentException("Unknown URL " + url);
        }

        Long now = Long.valueOf(System.currentTimeMillis());
        Resources r = Resources.getSystem();

        // Make sure that the fields are all set
        if (values.containsKey(CREATED_DATE) == false) {
            values.put(CREATED_DATE, now);
        }

        if (values.containsKey(MODIFIED_DATE) == false) {
            values.put(MODIFIED_DATE, now);
        }

        if (values.containsKey(TITLE) == false) {
            values.put(TITLE, "");
        }

        if (values.containsKey(SCRIPT) == false) {
            values.put(SCRIPT, "");
        }

        rowID = db.insert("scripts", "script", values);
        if (rowID > 0) {
            Uri uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
            getContext().getContentResolver().notifyChange(uri, null);
            return uri;
        }

        throw new SQLException("Failed to insert row into " + url);
    }

    @Override
    public int delete(Uri url, String where, String[] whereArgs) {
        int count;
        long rowId = 0;
        switch (URL_MATCHER.match(url)) {
        case SCRIPTS:
            count = db.delete("note_pad", where, whereArgs);
            break;

        case SCRIPT_ID:
            String segment = url.getPathSegments().get(1);
            rowId = Long.parseLong(segment);
            count = db
                    .delete("notes", "_id="
                            + segment
                            + (!TextUtils.isEmpty(where) ? " AND (" + where
                                    + ')' : ""), whereArgs);
            break;

        default:
            throw new IllegalArgumentException("Unknown URL " + url);
        }

        getContext().getContentResolver().notifyChange(url, null);
        return count;
    }

    @Override
    public int update(Uri url, ContentValues values, String where, String[] whereArgs) {
        int count;
        switch (URL_MATCHER.match(url)) {
        case SCRIPTS:
            count = db.update("notes", values, where, whereArgs);
            break;

        case SCRIPT_ID:
            String segment = url.getPathSegments().get(1);
            count = db
                    .update("notes", values, "_id="
                            + segment
                            + (!TextUtils.isEmpty(where) ? " AND (" + where
                                    + ')' : ""), whereArgs);
            break;

        default:
            throw new IllegalArgumentException("Unknown URL " + url);
        }

        getContext().getContentResolver().notifyChange(url, null);
        return count;
    }

    static {
        URL_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
        URL_MATCHER.addURI("org.hecl.android.Scripts", "scripts", SCRIPTS);
        URL_MATCHER.addURI("org.hecl.android.Scripts", "scripts/#", SCRIPT_ID);

        SCRIPT_LIST_PROJECTION_MAP = new HashMap<String, String>();
        SCRIPT_LIST_PROJECTION_MAP.put(_ID, "_id");
        SCRIPT_LIST_PROJECTION_MAP.put(TITLE, "title");
        SCRIPT_LIST_PROJECTION_MAP.put(SCRIPT, "script");
        SCRIPT_LIST_PROJECTION_MAP.put(CREATED_DATE, "created");
        SCRIPT_LIST_PROJECTION_MAP.put(MODIFIED_DATE, "modified");
    }
}
