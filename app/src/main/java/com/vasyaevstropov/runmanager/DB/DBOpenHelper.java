package com.vasyaevstropov.runmanager.DB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBOpenHelper extends SQLiteOpenHelper {

    public DBOpenHelper(Context context) {
        super(context, "DBcoordinates", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE speedtable ( id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "numberrecord INTEGER, " +
                "namerecord TEXT, " +
                "longitude TEXT, " +
                "latitude TEXT, " +
                "speed TEXT, " +
                "time TEXT);");

        db.execSQL("CREATE TABLE segmenttable ( id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "dayofweek TEXT, " +
                "date TEXT, " +
                "distance TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS speedtable");
        db.execSQL("DROP TABLE IF EXISTS segmenttable");
        onCreate(db);
    }
}
