package com.vasyaevstropov.runmanager;

import android.Manifest;

import android.animation.ValueAnimator;
import android.app.ListFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;

import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.vasyaevstropov.runmanager.Activities.CardListActivity;
import com.vasyaevstropov.runmanager.Activities.SettingActivity;
import com.vasyaevstropov.runmanager.DB.Preferences;
import com.vasyaevstropov.runmanager.Fragments.MusicFragment;
import com.vasyaevstropov.runmanager.Interfaces.OnFragmentListener;
import com.vasyaevstropov.runmanager.Services.GPSservice;
import com.vasyaevstropov.runmanager.Services.SinhrService;

//Главное окно программы
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, OnFragmentListener {

    com.cuboid.cuboidcirclebutton.CuboidButton btnStart;
    RelativeLayout relativeMap;
    TextView tvTime;
    TextView tvSpeed;
    SupportMapFragment mapFragment;
    double lat1 = 0;
    double long1 = 0;
    Toolbar toolbar;

    public static boolean startGpsService = false;
    private BroadcastReceiver broadcastReceiver;

    MusicFragment musicFragment;
    android.app.FragmentTransaction fragmTrans;

    @Override
    protected void onResume() {
        super.onResume();


//        if (broadcastReceiver == null) { //Используется для связи с GPSservice;
//            broadcastReceiver = new BroadcastReceiver() {
//                @Override
//                public void onReceive(Context context, Intent intent) {
//                    btnStart.setText(getResources().getString(R.string.stop));
//                    if (intent.getExtras().get("coordinates") != null) {
//                        String speed = String.format("%.0f", intent.getExtras().get("speed"));
//
//                        tvSpeed.setText(speed + getResources().getString(R.string.kmh));
//
//                        Location location = (Location) intent.getExtras().getParcelable("location");
//
//                        updateMapPosition(location);
//                    }
//                    if (intent.getExtras().get("seconds") != null) {
//
//                        long seconds = intent.getExtras().getLong("seconds");
//
//                        String sec = String.format("%02d:%02d:%02d", seconds / 3600, seconds / 60, seconds % 60);
//
//                        tvTime.setText(sec);
//                    }
//                }
//            };
//        }
//        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction("location_update");
//        intentFilter.addAction("timer_update");
//        intentFilter.addAction("music_update");
//        registerReceiver(broadcastReceiver, intentFilter); // Регистрация ресивера (для Сервиса)

    }

    @Override
    protected void onDestroy() {
        Log.d("Log.d", "onDestroy");
        super.onDestroy();
        if (broadcastReceiver != null) {
            unregisterReceiver(broadcastReceiver); //удаляем броадкаст
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("Log.d", "onCreate");
        Preferences.init(this);
        setTheme(Preferences.getStyle());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initToolbar();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        if (!runtimePermission()) //запрос на GPS
        {

        }

    }

    private void initializeBtnTV() {
        btnStart = (com.cuboid.cuboidcirclebutton.CuboidButton) findViewById(R.id.btnStart);
        tvSpeed = (TextView) findViewById(R.id.tvSpeed);
        tvTime = (TextView) findViewById(R.id.tvTime);
    }

    private void initializeMapFragment() {
        mapFragment =
                (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.map_main);
        relativeMap = (RelativeLayout) findViewById(R.id.relativeMap);
        relativeMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottonSheetBehaviorListener(false);
            }
        });

    }

    private void initilalizeMusicFragment() {
        musicFragment = new MusicFragment();  //активируем фрагмент с музыкой.
        fragmTrans = getFragmentManager().beginTransaction();

        fragmTrans.add(R.id.musicFrame, musicFragment);
        fragmTrans.commit();
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }


    private void enableButtons() {
        initializeBtnTV();
        initializeMapFragment();
        initilalizeMusicFragment();

        mapFragment.getMapAsync(this);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!MainActivity.startGpsService) {
                    Intent intent = new Intent(getApplicationContext(), GPSservice.class);
                    startService(intent);
                    btnStart.setText(getResources().getString(R.string.stop));
                } else {
                    Intent intent = new Intent(getApplicationContext(), GPSservice.class);
                    stopService(intent);
                    btnStart.setText(getResources().getString(R.string.start));
                }
            }
        });
    }

    private boolean runtimePermission() { //запрос у пользователя разрешений на GPS для андроид 6.0 +
        if (Build.VERSION.SDK_INT >= 23
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.INTERNET,
                            Manifest.permission.ACCESS_NETWORK_STATE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE},
                    100);
            return true;
        } else {
            enableButtons();
        }


        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) { //перехват ответа на разрешения
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[2] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[3] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[4] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[5] == PackageManager.PERMISSION_GRANTED) {
                enableButtons();
            } else { //показываем окно, пока не получим разрешения.
                runtimePermission();
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent settingIntent = new Intent(this, SettingActivity.class);
            startActivity(settingIntent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.history) {
            // открыть история пробежек

            Intent intent = new Intent(MainActivity.this, CardListActivity.class);
            startActivity(intent);

            Intent intent1 = new Intent(MainActivity.this, SinhrService.class);
            startService(intent1);

        } else if (id == R.id.settings) {
            //настройки
            Intent settingIntent = new Intent(this, SettingActivity.class);
            startActivity(settingIntent);
            finish();

        } else if (id == R.id.mus_player) {
            bottonSheetBehaviorListener(true);
        } else if (id == R.id.chat) {

            Intent chatIntent = new Intent(this, ChatActivity.class);

            startActivity(chatIntent);

            finish();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        setMapStyle(googleMap);
        setLastLocation();
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat1, long1), 14.5f), 10, null); //приближение

    }

    private void setLastLocation() {

        LocationManager locationManager = (LocationManager) getSystemService
                (Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location getLastLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        if (getLastLocation == null) {
            try {
                getLastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        try {
            lat1 = getLastLocation.getLatitude();
            long1 = getLastLocation.getLongitude();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        Toast.makeText(this, lat1 + " " + long1, Toast.LENGTH_SHORT).show();
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

    public void updateMapPosition(final Location location) {


    }


    @Override
    public void bottonSheetBehaviorListener(Boolean isOpen) {
        MusicFragment musicFragment = (MusicFragment) getFragmentManager().findFragmentById(R.id.musicFrame);
        musicFragment.setState(isOpen);
    }

    @Override
    public void updateLocation(final Location location) {
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                googleMap.clear();
                LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                final Marker currentLocationMarker = googleMap.addMarker(new MarkerOptions().position(currentLocation));
                currentLocationMarker.setVisible(false);

                final Bitmap bitmap = BitmapFactory.decodeResource(getResources(), android.R.drawable.ic_menu_compass);
                final Bitmap target = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
                final Canvas canvas = new Canvas(target);
                ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
                animator.setDuration(200);
                animator.setStartDelay(500);
                final Rect originalRect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
                final RectF scaledRect = new RectF();
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        float scale = (float) animation.getAnimatedValue();
                        scaledRect.set(0, 0, originalRect.right * scale, originalRect.bottom * scale);
                        canvas.drawBitmap(bitmap, originalRect, scaledRect, null);
                        currentLocationMarker.setIcon(BitmapDescriptorFactory.fromBitmap(target));
                        currentLocationMarker.setVisible(true);
                    }
                });
                animator.start();

                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
            }
        });
    }
}
