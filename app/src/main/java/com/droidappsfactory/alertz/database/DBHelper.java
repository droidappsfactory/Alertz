package com.droidappsfactory.alertz.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.droidappsfactory.alertz.Alertz;

/**
 * Created by tcsmans on 3/21/2017.
 */

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = Alertz.class.getSimpleName()+"Db.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TYPE_TXT = " TEXT ";
    private static final String TYPE_NUM = " INTEGER ";
    private static final String COMMA = ",";

    private static final String SQL_CREATE_Alertz_TABLE =
            "CREATE TABLE "+ DBColumns.TABLE_NAME+ " ("+
                    DBColumns._ID + " INTEGER PRIMARY KEY," +
                    DBColumns.TITLE + TYPE_TXT + COMMA +
                    DBColumns.DESC+TYPE_TXT+COMMA+
                    DBColumns.IMAGE_LINK+TYPE_TXT+COMMA+
                    DBColumns.VIDEO_LINK+TYPE_TXT+COMMA+
                    DBColumns.ENABLED+TYPE_NUM+COMMA+
                    DBColumns.TIME+ TYPE_NUM +" );";

    private static final String SQL_DELETE_Alertz_ENTRIES = "DROP TABLE IF EXISTS " + DBColumns.TABLE_NAME;



    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_Alertz_TABLE);

    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db,oldVersion,newVersion);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(SQL_DELETE_Alertz_ENTRIES);
        onCreate(sqLiteDatabase);
    }
}
