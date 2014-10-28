package com.vkuzub.taximeter.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.vkuzub.taximeter.main.HistoryActivity;

/**
 * Created by Vyacheslav on 28.10.2014.
 */
public class DBHelper extends SQLiteOpenHelper {

    public static final String tableName = "history";
    public static final int dbVersion = 1;

    private final String CREATE_TABLE = "create table " + tableName + "("
            + "id integer primary key autoincrement," + "date text," + "sum text," + "distance text" + ");";

    public DBHelper(Context context, SQLiteDatabase.CursorFactory factory) {
        super(context, tableName, factory, dbVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
