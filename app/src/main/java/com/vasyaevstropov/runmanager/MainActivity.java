package com.vasyaevstropov.runmanager;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import android.widget.TextView;
import android.widget.Toast;

import com.vasyaevstropov.runmanager.DB.DBOpenHelper;

//Главное окно программы
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Button btnStart, btnRead, btnDeleteDB, btnMapTest, btnRecycler;
    TextView tvTime;
    TextView tvCurrentLocation, tvSpeed;
    public static boolean startGpsService = false;
    private BroadcastReceiver broadcastReceiver;
    CountDownTimer timer;


    @Override
    protected void onResume() {
        super.onResume();
        if (broadcastReceiver == null){
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    tvCurrentLocation.append("\n" + intent.getExtras().get("coordinates"));
                    tvSpeed.setText(String.valueOf(intent.getExtras().get("speed")) + " km/h");
                }
            };
        }
        registerReceiver (broadcastReceiver, new IntentFilter("location_update"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (broadcastReceiver !=null){
            unregisterReceiver(broadcastReceiver);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        btnStart = (Button)findViewById(R.id.btnStart);
        btnRead = (Button)findViewById(R.id.btnRead);
        btnRecycler = (Button)findViewById(R.id.btnRecycler);
        btnRecycler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), CardListActivity.class);
                startActivity(intent);
            }
        });
        btnDeleteDB = (Button)findViewById(R.id.btnDeleteDB);
        tvCurrentLocation = (TextView)findViewById(R.id.tvCoordinates);
        tvSpeed = (TextView) findViewById(R.id.tvSpeed);
        tvTime = (TextView) findViewById(R.id.tvTime);
        btnMapTest = (Button)findViewById(R.id.btnTestMap);
        btnMapTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentMapTest = new Intent(v.getContext(), RunListActivity.class);
                startActivity(intentMapTest);
            }
        });

        createTimer();

        if (!runtimePermission()) //запрос на GPS
            enableButtons();
    }

    private void createTimer() {
        timer = new CountDownTimer(1000000000, 1000){
            @Override
            public void onTick(long millisUntilFinished) {
                long seconds = 1000000 - millisUntilFinished/1000;
                tvTime.setText(""+ seconds);
            }

            @Override
            public void onFinish() {
            }
        };
    }

    private void enableButtons() {
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!MainActivity.startGpsService) {
                    Intent intent = new Intent(getApplicationContext(), GPSservice.class);
                    startService(intent);
                    btnStart.setText("STOP");
                    startTimer(MainActivity.startGpsService=true);
                }else {
                    Intent intent = new Intent(getApplicationContext(), GPSservice.class);
                    stopService(intent);
                    btnStart.setText("START");
                    startTimer(MainActivity.startGpsService=false);

                }

            }
        });
        btnRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getBaseContext(), "BUTTON PRESSED",Toast.LENGTH_SHORT).show();
                readDB();

            }
        });
        btnDeleteDB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteDB();
            }
        });
    }

    private void deleteDB() {
        Toast.makeText(this, "deleteFromDB pressed",Toast.LENGTH_SHORT).show();
        DBOpenHelper dbOpenHelper = new DBOpenHelper(getBaseContext());
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        db.rawQuery("DELETE FROM speedtable", null);
        db.close();

    }


    private void readDB() {
        DBOpenHelper dbOpenHelper = new DBOpenHelper(getBaseContext());
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        Cursor c = db.query("speedtable", null, null, null, null, null, null);

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

        Cursor c2 = db.query("segmenttable", null, null, null, null, null, null);

        if (c2.moveToFirst()) {
            // определяем номера столбцов по имени в выборке
            int id = c2.getColumnIndex("id");
            int numberrecord = c2.getColumnIndex("numberrecord");
            int dayofweek = c2.getColumnIndex("dayofweek");
            int date = c2.getColumnIndex("date");
            int distance = c2.getColumnIndex("distance");
            do {
                // получаем значения по номерам столбцов и пишем все в лог
                Log.d("LOG_TAG",
                        "ID = " + c2.getInt(id)  +
                                ", dayofweek = " + c2.getString(dayofweek) +
                                ", date = " + c2.getString(date) +
                                ", distance = " + c2.getString(distance));
                // переход на следующую строку
                // а если следующей нет (текущая - последняя), то false - выходим из цикла
            } while (c2.moveToNext());
        }

        db.close();
    }

    private void startTimer(boolean b) {
        if (b) {
            timer.start();
        }else {
            timer.cancel();
        }

    }

    private boolean runtimePermission() { //запрос у пользователя разрешений на GPS для андроид 6.0 +
        if (Build.VERSION.SDK_INT >=23
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.INTERNET,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    100);
            return true;
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) { //перехват ответа на разрешения
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==100){
            if (grantResults[0]==PackageManager.PERMISSION_GRANTED &&
                    grantResults[1]==PackageManager.PERMISSION_GRANTED &&
                    grantResults[2]==PackageManager.PERMISSION_GRANTED&&
                    grantResults[3]==PackageManager.PERMISSION_GRANTED&&
                    grantResults[4]==PackageManager.PERMISSION_GRANTED){
                enableButtons();
            }else{ //показываем окно, пока не получим разрешения.
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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent settingIntent = new Intent(this, SettingActivity.class);
            startActivityForResult(settingIntent,150);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {
            Intent settingIntent = new Intent(this, SettingActivity.class);
            startActivityForResult(settingIntent,150);

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
