package com.vasyaevstropov.runmanager.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.vasyaevstropov.runmanager.Models.Coordinates;

import java.util.ArrayList;

public class DBOpenHelper extends SQLiteOpenHelper {
    private Double long1, lat1, long2, lat2;


    public DBOpenHelper(Context context) {
        super(context, "DBcoordinates", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " +
                Coordinates.TABLE_NAME_SPEEDTABLE + "( " +
                Coordinates.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                Coordinates.COLUMN_NUMB_RECORD + " TEXT, " +
                Coordinates.COLUMN_NAME_RECORD + " TEXT, " +
                Coordinates.COLUMN_LONGITUDE + " TEXT, " +
                Coordinates.COLUMN_LATITUDE + " TEXT, " +
                Coordinates.COLUMN_SPEED + " TEXT, " +
                Coordinates.COLUMN_TIME + " TEXT);");

        db.execSQL("CREATE TABLE " +
                Coordinates.TABLE_NAME_SEGMENT + "(" +
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

    public long insertStudent(Coordinates coordinates) {
        long id = 0;

        SQLiteDatabase db = getWritableDatabase();
        try {
            ContentValues cv = new ContentValues();
            cv.put(Coordinates.COLUMN_NUMB_RECORD, String.valueOf(getLastNumberRecord(db)));
            cv.put(Coordinates.COLUMN_NAME_RECORD, "");
            cv.put(Coordinates.COLUMN_LONGITUDE, String.valueOf(coordinates.getLongitude()));
            cv.put(Coordinates.COLUMN_LATITUDE, String.valueOf(coordinates.getLatitude()));
            cv.put(Coordinates.COLUMN_SPEED, String.valueOf(coordinates.getSpeed()));
            cv.put(Coordinates.COLUMN_TIME, String.valueOf(coordinates.getTime()));
            id = db.insert(Coordinates.TABLE_NAME_SPEEDTABLE, null, cv);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return id;
    }

    public void writeToSegmentTable(Coordinates coordinates) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            ContentValues cv = new ContentValues();
            cv.put(Coordinates.COLUMN_DAYOFWEEK, coordinates.getDayOfWeek()); //Воскресенье показівает как первый день.
            cv.put(Coordinates.COLUMN_DATE, coordinates.getDate()); //текущий день месяца показывает
            cv.put(Coordinates.COLUMN_DISTANCE, coordinates.getDistance());
            db.insert(Coordinates.TABLE_NAME_SEGMENT, null, cv);
        } catch (Exception e) {
        }
    }

    public int getLastNumberRecord(SQLiteDatabase db) { //получаем последний ИД записанный в Segmenttable
        int lastNumberRecord = 1;
        try {
            Cursor c = db.rawQuery("SELECT * FROM " + Coordinates.TABLE_NAME_SEGMENT, null);
            if (c.moveToLast()) {
                int columnID = c.getColumnIndex(Coordinates.COLUMN_ID);
                lastNumberRecord = Integer.parseInt(c.getString(columnID));
                ++lastNumberRecord;
            }
            c.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return lastNumberRecord;
    }

    public PolylineOptions readDB(Integer numb) {
        PolylineOptions rectOptions = new PolylineOptions();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(Coordinates.TABLE_NAME_SPEEDTABLE, null, Coordinates.COLUMN_NUMB_RECORD + "=" + numb, null, null, null, null); //делаем выборку элементов со значением numberrecord (номер записи). В таблице segmenttable=numberrecord;
        if (c.moveToFirst()) {
            int longitude = c.getColumnIndex(Coordinates.COLUMN_LONGITUDE);
            int latitude = c.getColumnIndex(Coordinates.COLUMN_LATITUDE);
            long1 = Double.valueOf(c.getString(longitude)); //координаты самой первой точки
            lat1 = Double.valueOf(c.getString(latitude));  //координаты самой первой точки
            do {
                rectOptions.add(new LatLng(Double.valueOf(c.getString(latitude)), Double.valueOf(c.getString(longitude))));
                if (c.isLast()) {
                    long2 = Double.valueOf(c.getString(longitude)); //координаты последней точки
                    lat2 = Double.valueOf(c.getString(latitude));  //координаты последней точки
                }
            } while (c.moveToNext());
        }
        c.close();
        return rectOptions;
    }

    public ArrayList<Double> getFirstLastPoint(Integer numb) { //метод возвращяет координаты ПЕРВОЙ и ПОСЛЕДНЕЙ точки маршрута
        ArrayList<Double> arr = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        try {
            Cursor c = db.query(Coordinates.TABLE_NAME_SPEEDTABLE, null, Coordinates.COLUMN_NUMB_RECORD + "=" + numb, null, null, null, null); //делаем выборку элементов со значением numberrecord (номер записи). В таблице segmenttable=numberrecord;

            c.moveToFirst(); //первая точка

            int longitude = c.getColumnIndex(Coordinates.COLUMN_LONGITUDE);
            int latitude = c.getColumnIndex(Coordinates.COLUMN_LATITUDE);

            long1 = Double.valueOf(c.getString(longitude)); //координаты самой первой точки
            lat1 = Double.valueOf(c.getString(latitude));  //координаты самой первой точки
            arr.add(long1);
            arr.add(lat1);

            c.moveToLast(); //последняя точка

            long2 = Double.valueOf(c.getString(longitude)); //координаты последней точки
            lat2 = Double.valueOf(c.getString(latitude));  //координаты последней точки
            arr.add(long2);
            arr.add(lat2);

            c.close();
        } catch (Exception e) {
        }
        ;
        return arr;
    }

    public ArrayList<ArrayList<String>> getListOfRuns() {

        SQLiteDatabase db = getReadableDatabase();

        ArrayList<String> dayOfWeekList = new ArrayList<>();
        ArrayList<String> dateList = new ArrayList<>();
        ArrayList<String> distanceList = new ArrayList<>();
        ArrayList<String> numberRecordList = new ArrayList<>();
        ArrayList<ArrayList<String>> listOfRuns = new ArrayList<>();


            Cursor c2 = db.query(Coordinates.TABLE_NAME_SEGMENT, null, null, null, null, null, null);
            if (c2.moveToFirst()) {
                // определяем номера столбцов по имени в выборке
                int id = c2.getColumnIndex(Coordinates.COLUMN_ID);
                int dayofweek = c2.getColumnIndex(Coordinates.COLUMN_DAYOFWEEK);
                int date = c2.getColumnIndex(Coordinates.COLUMN_DATE);
                int distance = c2.getColumnIndex(Coordinates.COLUMN_DISTANCE);
                do {
                    numberRecordList.add(c2.getString(id));
                    dayOfWeekList.add(c2.getString(dayofweek));
                    dateList.add(c2.getString(date));
                    distanceList.add(c2.getString(distance));

                } while (c2.moveToNext());
                c2.close();
            }

        db.close();

        listOfRuns.add(dayOfWeekList);
        listOfRuns.add(dateList);
        listOfRuns.add(distanceList);
        listOfRuns.add(numberRecordList);

        return listOfRuns;
    }

}
