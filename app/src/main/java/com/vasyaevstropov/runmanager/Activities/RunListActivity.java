package com.vasyaevstropov.runmanager.Activities;

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
import com.vasyaevstropov.runmanager.R;

import java.util.ArrayList;

//Активити для отображения карты.


public class RunListActivity extends AppCompatActivity implements OnMapReadyCallback {
    private Integer number;
    private Double long1, lat1, long2, lat2;
    private DBOpenHelper dbOpenHelper;

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
        dbOpenHelper = new DBOpenHelper(this);

        getFirstLastPoints();
    }

    private void getFirstLastPoints() {
        ArrayList<Double> firstlastArray = dbOpenHelper.getFirstLastPoint(number);
        //long1 lat1 long2 lat2
        long1 = firstlastArray.get(0);
        lat1 = firstlastArray.get(1);
        long2 = firstlastArray.get(2);
        lat2 = firstlastArray.get(3);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        loadPolilyne(number, googleMap);
    }

    private void loadPolilyne(Integer number, GoogleMap googleMap) {
        googleMap.addPolyline(dbOpenHelper.readDB(number)); //Добавляем линию движения
        googleMap.addMarker(new MarkerOptions().position(new LatLng(lat1, long1)).title(getResources().getString(R.string.point1))); //маркер 1й точки
        googleMap.addMarker(new MarkerOptions().position(new LatLng(lat2, long2)).title(getResources().getString(R.string.point2))); //маркер 2й точки
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng((lat1+lat2)/2, (long1+long2)/2), 8.5f), 5000, null); //приближение
    }
}

