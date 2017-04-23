package com.droidappsfactory.alertz.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.droidappsfactory.alertz.database.DBColumns;
import com.droidappsfactory.alertz.database.DBHelper;
import com.droidappsfactory.alertz.util.LogHelper;

/**
 * Created by tcsmans on 3/21/2017.
 */

public class AlertProvider extends ContentProvider {


    LogHelper logHelper = new LogHelper(AlertProvider.class.getSimpleName());

    private static final String AUTHORITY = "com.droidappsfactory.alertz.provider";
    private static final String ALERTZPATH = DBColumns.TABLE_NAME;

    public static final Uri CONTENT_ALERTZ_URI = Uri.parse("content://"+AUTHORITY+"/"+ ALERTZPATH);

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    private static final int alertzs = 1;
    private static final int alertz = 2;

    static {
        sUriMatcher.addURI(AUTHORITY, ALERTZPATH,alertzs);
        sUriMatcher.addURI(AUTHORITY, ALERTZPATH +"/*",alertz);
    }

    private DBHelper dbHelper;

    @Override
    public boolean onCreate() {
        dbHelper = new DBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] strings, @Nullable String s, @Nullable String[] strings1, @Nullable String s1) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor;

        switch (sUriMatcher.match(uri)){
            case alertz:

                cursor = db.query(DBColumns.TABLE_NAME,null,s,strings1,null,null,null);
                break;
            case alertzs:

                cursor = db.query(DBColumns.TABLE_NAME,null,null,null,null,null,null);

                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }




        //  logHelper.printLog(selection);



        logHelper.printLog("cursor returned size"+cursor.getCount());
        // make sure that potential listeners are getting notified
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        long rowId= 0;
        logHelper.printLog("Uri passed into insert"+uri);
        switch (sUriMatcher.match(uri)){
            case alertzs:
                rowId= dbHelper.getWritableDatabase().insert(DBColumns.TABLE_NAME,"",contentValues);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        logHelper.printLog("RowID "+rowId);
        getContext().getContentResolver().notifyChange(uri,null);
        if(rowId>0){
            return ContentUris.withAppendedId(uri,rowId);
        }else {
            throw new SQLException("Unable to insert the data");
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        int count = 0;
        switch (sUriMatcher.match(uri)){
            case alertz:

                count = db.delete(DBColumns.TABLE_NAME,s,strings);
                // count = db.delete(Columns.ArticleTable.TABLE_ARTICLE,selection,selectionArgs);
                break;

        }

        logHelper.printLog("no of rows deleted "+count);

        getContext().getContentResolver().notifyChange(uri,null);
        return count;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int count = 0;
        switch (sUriMatcher.match(uri)){
            case alertzs:
                break;
            case alertz:
                count = db.update(DBColumns.TABLE_NAME,contentValues,s,strings);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri,null);

        return count;
    }
}
