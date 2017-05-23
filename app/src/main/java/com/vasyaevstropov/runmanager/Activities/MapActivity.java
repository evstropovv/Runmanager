package com.vasyaevstropov.runmanager.Activities;

import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.vasyaevstropov.runmanager.DB.DBOpenHelper;
import com.vasyaevstropov.runmanager.DB.Preferences;
import com.vasyaevstropov.runmanager.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//Активити для отображения карты.


public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {
    private Integer number;
    private Double long1, lat1, long2, lat2;
    private DBOpenHelper dbOpenHelper;
    private LineChart lineChart;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Preferences.init(this);
        setTheme(Preferences.getStyle());

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_map);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            this.number = bundle.getInt("number");
        }
        dbOpenHelper = new DBOpenHelper(this);

        getFirstLastPoints();
        lineChart = (LineChart)findViewById(R.id.lineChart);
        initializeChart();
    }

    private void initializeChart() {

        ArrayList<HashMap<String, String>> arrayList = dbOpenHelper.getTimeSpeed(number);

        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < arrayList.size() ; i++) {
            HashMap<String, String> map = arrayList.get(i);

            float speed = Float.parseFloat(map.get("speed"));
            float time = Float.parseFloat(map.get("time"))/1000;

            entries.add(new Entry( time , speed));
        }

        LineDataSet lineData1 = new LineDataSet(entries, "(NAME)LineData1");
        lineData1.setColor(R.color.blue);
        lineData1.setValueTextColor(R.color.night);

        LineData lineData = new LineData(lineData1);

        lineChart.setData(lineData);
        lineChart.setBackgroundColor(Color.WHITE);
        lineChart.invalidate(); //refresh

    }

    private void getFirstLastPoints() {
        ArrayList<Double> firstlastArray = dbOpenHelper.getFirstLastPoint(number);
        //long1 lat1 long2 lat2
        try {
            long1 = firstlastArray.get(0);
            lat1 = firstlastArray.get(1);
            long2 = firstlastArray.get(2);
            lat2 = firstlastArray.get(3);
        }catch (IndexOutOfBoundsException e){e.printStackTrace();}
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        setMapStyle(googleMap);
        try {
            loadPolilyne(number, googleMap);
        } catch (Exception e) {
        }
    }

    private void setMapStyle(GoogleMap googlemap) { //настраиваем отображение карты
        try {
            int mapResource = getResources().getIdentifier(Preferences.getMapName(), "raw", getPackageName());
            boolean success = googlemap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, mapResource));
            if (!success) {
                Log.e("Log.e", "Style parsing failed");
            }
        } catch (Resources.NotFoundException e) {
            Log.e("Log.e", "Can't find style of map", e);
        }

    }

    private void loadPolilyne(Integer number, GoogleMap googleMap) {
        googleMap.addPolyline(dbOpenHelper.readDB(number)); //Добавляем линию движения
        googleMap.addMarker(new MarkerOptions().position(new LatLng(lat1, long1)).title(getResources().getString(R.string.point1))); //маркер 1й точки
        googleMap.addMarker(new MarkerOptions().position(new LatLng(lat2, long2)).title(getResources().getString(R.string.point2))); //маркер 2й точки
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng((lat1 + lat2) / 2, (long1 + long2) / 2), 19.5f), 50, null); //приближение
    }
}

