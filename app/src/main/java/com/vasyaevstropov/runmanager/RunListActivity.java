package com.vasyaevstropov.runmanager;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

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

public class RunListActivity extends AppCompatActivity implements OnMapReadyCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_map);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.addMarker(new MarkerOptions().position(new LatLng(37.35, -122.0)).title("Hello!!!")); //обычный маркер

        PolylineOptions rectOptions = new PolylineOptions() //рисуем связные линии
                .add(new LatLng(37.35, -122.0))
                .add(new LatLng(37.45, -122.0))
                .add(new LatLng(37.45, -122.2))
                .add(new LatLng(37.35, -122.2))
                .add(new LatLng(37.55, -122.2))
                .add(new LatLng(37.35, -122.0))
                ;

        googleMap.addPolyline(rectOptions);  ///добавляем на карту..

        CameraPosition cameraPosition = new CameraPosition.Builder() //позиция камеры
                        .target(new LatLng(37.35, -122.0)) //центральная точка
                        .bearing(45) //ориентация
                        .tilt(90) //наклон
                        .zoom(googleMap.getCameraPosition().zoom)
                        .build();
        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
        googleMap.animateCamera(cameraUpdate);

        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(37.35, -122.0), 10.5f), 1000, null); //зум камеры: (точка на карте, x-zoom, длительность анимации, нулл - )
    }
}
