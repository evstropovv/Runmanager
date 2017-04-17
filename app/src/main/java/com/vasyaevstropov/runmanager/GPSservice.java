package com.vasyaevstropov.runmanager;

import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.util.Log;

import com.vasyaevstropov.runmanager.DB.DBOpenHelper;

import java.util.Calendar;

//Сервис используется для:
//1) определения текущих координат.
//2) Сохранения этих координат в БД
//3) Определения расстояния между точками и сохранение этого расстояния в БД.




public class GPSservice extends Service {


    private LocationManager locationManager;
    private LocationListener listener;
    private double lastX = 0, lastY = 0;
    int lastNumberRecord;

    SQLiteDatabase db;
    Calendar calendar;
    int dayOfWeek;
    String date;
    double sumdistance = 0;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        //дата и время на момент старта
        calendar = Calendar.getInstance();
        dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        date = calendar.get(Calendar.DATE)+"."+calendar.get(Calendar.MONTH)+"."+calendar.get(Calendar.YEAR) ;


        //подключаем к БД
        final DBOpenHelper dbOpenHelper = new DBOpenHelper(getBaseContext());

        lastNumberRecord = getLastNumberRecord(db,dbOpenHelper);

        listener = new LocationListener() {
            SQLiteDatabase db;
            ContentValues cv = new ContentValues();
            @Override
            public void onLocationChanged(Location location) {
                double speed = 0;

                Intent i = new Intent("location_update");
                if ((lastX==0)&&(lastY==0)) {
                    lastX = location.getLongitude();
                    lastY = location.getLatitude();
                }
                double dist =distance(location.getLongitude(), lastX, location.getLatitude(), lastY);
                speed = dist*1200; //скорсоть
                sumdistance = sumdistance + dist;

                lastX = location.getLongitude();
                lastY = location.getLatitude();

                i.putExtra("coordinates", location.getLongitude() + " " +location.getLatitude());
                i.putExtra("speed", speed);
                sendBroadcast(i);

                cv.put("numberrecord",lastNumberRecord); //номер записи
                cv.put("namerecord",""); //название записи
                cv.put("longitude",lastX); //долгота
                cv.put("latitude", lastY); //широта
                cv.put("speed", speed); //скорость
                cv.put("time",12);//время

                db = dbOpenHelper.getWritableDatabase();
                db.insert("speedtable",null,cv);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        };
        locationManager = (LocationManager)getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,3000, 0, listener);

        dbOpenHelper.close();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (locationManager!=null){
            locationManager.removeUpdates(listener) ;
        }
        writeToSegmentTable();
    }

    private void writeToSegmentTable() {
        final DBOpenHelper dbOpenHelper = new DBOpenHelper(getBaseContext());
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        ContentValues cv = new ContentValues();
       // cv.put("numberrecord", getLastNumberRecord(db,dbOpenHelper)); // не нужен!!! надо будет вытягивать последний ID и к нему +1   ;
        cv.put("dayofweek",dayOfWeek ); //Воскресенье показівает как первый день.
        cv.put("date",date ); //текущий день месяца показывает
        cv.put("distance", sumdistance);

        db.close();

        db = dbOpenHelper.getWritableDatabase();
        db.insert("segmenttable", null, cv);
        db.close();
    }

    public static double distance(double lat1, double lat2, double lon1,
                                  double lon2) {

        final int R = 6371; // Radius of the earth

        Double latDistance = Math.toRadians(lat2 - lat1);
        Double lonDistance = Math.toRadians(lon2 - lon1);
        Double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c ; // *1000 to convert to meters
        distance = Math.pow(distance, 2);

        return Math.sqrt(distance);
    }


    public int getLastNumberRecord(SQLiteDatabase db, DBOpenHelper dbOpenHelper) { //получаем последний ИД записанный в Segmenttable
        int lastNumberRecord =1;

        db = dbOpenHelper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM segmenttable",null);

        if (c.moveToLast()) {
            int columnID = c.getColumnIndex("id");
            lastNumberRecord = Integer.parseInt(c.getString(columnID));
            ++lastNumberRecord;
        }

        db.close();
        return lastNumberRecord;
    }
}
