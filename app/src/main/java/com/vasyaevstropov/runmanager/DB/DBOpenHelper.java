package com.vasyaevstropov.runmanager.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.vasyaevstropov.runmanager.Models.Coordinates;

public class DBOpenHelper extends SQLiteOpenHelper {

    public DBOpenHelper(Context context) {
        super(context, "DBcoordinates", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " +
                Coordinates.TABLE_NAME_SPEEDTABLE +"( "+
                Coordinates.COLUMN_ID+" INTEGER PRIMARY KEY AUTOINCREMENT," +
                Coordinates.COLUMN_NUMB_RECORD + " TEXT, " +
                Coordinates.COLUMN_NAME_RECORD + " TEXT, " +
                Coordinates.COLUMN_LONGITUDE +" TEXT, " +
                Coordinates.COLUMN_LATITUDE + " TEXT, " +
                Coordinates.COLUMN_SPEED + " TEXT, " +
                Coordinates.COLUMN_TIME + " TEXT);");

        db.execSQL("CREATE TABLE " +
                Coordinates.TABLE_NAME_SEGMENT +"(" +
                Coordinates.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                Coordinates.COLUMN_DAYOFWEEK + " TEXT, " +
                Coordinates.COLUMN_DATE + " TEXT, " +
                Coordinates.COLUMN_DISTANCE + " TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Coordinates.TABLE_NAME_SPEEDTABLE);
        db.execSQL("DROP TABLE IF EXISTS " + Coordinates.TABLE_NAME_SEGMENT);
        onCreate(db);
    }

    public long insertStudent(Coordinates coordinates){
        long id =0;

        SQLiteDatabase db = getWritableDatabase();

            ContentValues cv = new ContentValues();
            cv.put(Coordinates.COLUMN_NUMB_RECORD, String.valueOf(getLastNumberRecord(db)));
            cv.put(Coordinates.COLUMN_NAME_RECORD,"");
            cv.put(Coordinates.COLUMN_LONGITUDE, String.valueOf(coordinates.getLongitude()));
            cv.put(Coordinates.COLUMN_LATITUDE, String.valueOf(coordinates.getLatitude()));
            cv.put(Coordinates.COLUMN_SPEED, String.valueOf(coordinates.getSpeed()));
            cv.put(Coordinates.COLUMN_TIME, String.valueOf(coordinates.getTime()));

            id = db.insert(Coordinates.TABLE_NAME_SPEEDTABLE, null, cv);

        return id;
    }

    private void writeToSegmentTable(Coordinates coordinates) {
        SQLiteDatabase db = getWritableDatabase();

        try{
            ContentValues cv = new ContentValues();
            cv.put(Coordinates.COLUMN_DAYOFWEEK,coordinates.getDayOfWeek()); //Воскресенье показівает как первый день.
            cv.put(Coordinates.COLUMN_DATE,coordinates.getDate()); //текущий день месяца показывает
            cv.put(Coordinates.COLUMN_DISTANCE, coordinates.getDistance());
            db.insert(Coordinates.TABLE_NAME_SEGMENT, null, cv);
        }catch (Exception e){}

    }

    public int getLastNumberRecord(SQLiteDatabase db) { //получаем последний ИД записанный в Segmenttable
        int lastNumberRecord =1;

        Cursor c = db.rawQuery("SELECT * FROM "+Coordinates.TABLE_NAME_SEGMENT,null);

        if (c.moveToLast()) {
            int columnID = c.getColumnIndex(Coordinates.COLUMN_ID);
            lastNumberRecord = Integer.parseInt(c.getString(columnID));
            ++lastNumberRecord;
        }
        try{
            c.close();
        }catch (Exception e){e.printStackTrace();}

        return lastNumberRecord;
    }

}
