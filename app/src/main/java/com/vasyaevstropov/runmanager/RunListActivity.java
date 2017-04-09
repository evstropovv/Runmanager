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




//        googleMap.addMarker(new MarkerOptions().position(new LatLng(37.35, -122.0)).title("Hello!!!")); //обычный маркер
//
//        PolylineOptions rectOptions = new PolylineOptions() //рисуем связные линии
//
//
//                .add(new LatLng(37.35, -122.0))
//                .add(new LatLng(37.45, -122.0))
//                .add(new LatLng(37.45, -122.2))
//                .add(new LatLng(37.35, -122.2))
//                .add(new LatLng(37.55, -122.2))
//                .add(new LatLng(37.35, -122.0))
//                ;
//
//        PolylineOptions polylineOption = new PolylineOptions();
//        for (int i = 0; i <200 ; i++) {
//            polylineOption.add(new LatLng(37.35+i*0.01, -122.0+i*0.045));
//        }
//        polylineOption.color(getResources().getColor(R.color.colorAccent));
//        googleMap.addPolyline(polylineOption);
//
//
//        googleMap.addPolyline(rectOptions);  ///добавляем на карту..
//
//        CameraPosition cameraPosition = new CameraPosition.Builder() //позиция камеры
//                        .target(new LatLng(37.35, -122.0)) //центральная точка
//                        .bearing(45) //ориентация
//                        .tilt(90) //наклон
//                        .zoom(googleMap.getCameraPosition().zoom)
//                        .build();
//        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
//        googleMap.animateCamera(cameraUpdate);
//
//        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(37.35, -122.0), 10.5f), 1000, null); //зум камеры: (точка на карте, x-zoom, длительность анимации, нулл - )


    }

    private void loadPolilyne(Integer number, GoogleMap googleMap) {
        googleMap.addPolyline(readDB(number));
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
            do {

                
                 rectOptions.add(new LatLng(Double.valueOf(c.getString(longitude)), Double.valueOf(c.getString(latitude))));
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
            } while (c.moveToNext());

        }
    return rectOptions;
    }
}
