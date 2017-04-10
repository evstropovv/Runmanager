package com.vasyaevstropov.runmanager;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.vasyaevstropov.runmanager.DB.DBOpenHelper;

import java.util.ArrayList;

public class RunListActivity extends AppCompatActivity implements OnMapReadyCallback {
    Integer number;
    PolylineOptions rectOptions;
    Double long1, lat1, long2, lat2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_map);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        Bundle bundle = getIntent().getExtras();
        if (bundle!=null){
            this.number = bundle.getInt("number");
        }
        readDB(number);

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        loadPolilyne(number, googleMap);
    }

    private void loadPolilyne(Integer number, GoogleMap googleMap) {

        googleMap.addPolyline(readDB(number)); //Добавляем линию движения
        googleMap.addMarker(new MarkerOptions().position(new LatLng(lat1, long1)).title("Начальная точка")); //маркер 1й точки
        googleMap.addMarker(new MarkerOptions().position(new LatLng(lat2, long2)).title("Конечная точка")); //маркер 2й точки
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng((lat1+lat2)/2, (long1+long2)/2), 11.5f), 5000, null); //приближение

    }

    private PolylineOptions readDB(Integer numb) {
        rectOptions = new PolylineOptions();
        DBOpenHelper dbOpenHelper = new DBOpenHelper(getBaseContext());
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        Cursor c = db.query("speedtable", null, "numberrecord="+numb, null, null, null, null); //делаем выборку элементов со значением numberrecord (номер записи). В таблице segmenttable=numberrecord;

        if (c.moveToFirst()) {
            // определяем номера столбцов по имени в выборке
            int id = c.getColumnIndex("id");
            int numberrecord = c.getColumnIndex("numberrecord");
            int namerecord = c.getColumnIndex("namerecord");
            int longitude = c.getColumnIndex("longitude");
            int latitude = c.getColumnIndex("latitude");
            int speed = c.getColumnIndex("speed");
            int time = c.getColumnIndex("time");
            long1 = Double.valueOf(c.getString(longitude)); //координаты самой первой точки
            lat1 = Double.valueOf(c.getString(latitude));  //координаты самой первой точки

            do {


                 rectOptions.add(new LatLng(Double.valueOf(c.getString(latitude)),Double.valueOf(c.getString(longitude))));
                // получаем значения по номерам столбцов и пишем все в лог
                Log.d("LOG_TAG",
                        "ID = " + c.getInt(id) +
                                ", numberrecord = " + c.getString(numberrecord) +
                                ", namerecord = " + c.getString(namerecord) +
                                ", longitude = " + c.getString(longitude) +
                                ", latitude = " + c.getString(latitude) +
                                ", speed = " + c.getString(speed) +
                                ", time = " + c.getString(time));

                // переход на следующую строку
                // а если следующей нет (текущая - последняя), то false - выходим из цикла
                if (c.isLast()) {
                    long2 = Double.valueOf(c.getString(longitude)); //координаты последней точки
                    lat2 = Double.valueOf(c.getString(latitude));  //координаты последней точки
                }
            } while (c.moveToNext());

        }
    return rectOptions;
    }
}
