package com.vasyaevstropov.runmanager.Services;

import android.app.Notification;
import android.app.PendingIntent;
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
import android.os.CountDownTimer;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.vasyaevstropov.runmanager.DB.DBOpenHelper;
import com.vasyaevstropov.runmanager.MainActivity;
import com.vasyaevstropov.runmanager.Models.Coordinates;
import com.vasyaevstropov.runmanager.R;

import java.util.Calendar;

//Сервис используется для:
//1) определения текущих координат.
//2) Сохранения этих координат в БД
//3) Определения расстояния между точками и сохранение этого расстояния в БД.

public class GPSservice extends Service {

    private LocationManager locationManager;
    private LocationListener listener;
    private double lastX = 0, lastY = 0;
    int lastNumberRecord =0;


    private Calendar calendar;
    private int dayOfWeek;
    private String date;
    long seconds = 0;
    private double sumdistance = 0;
    CountDownTimer timer;

    private long time = 0 ; // in milliseconds - 3000 6000 9000 etc.

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        createTimer();
        MainActivity.startGpsService = true;

        //дата и время на момент старта
        calendar = Calendar.getInstance();
        dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        date = calendar.get(Calendar.DATE)+"."+calendar.get(Calendar.MONTH)+"."+calendar.get(Calendar.YEAR) ;

        //подключаем к БД

        final DBOpenHelper dbOpenHelper = new DBOpenHelper(getBaseContext());
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        lastNumberRecord = dbOpenHelper.getLastNumberRecord(db);

        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                double speed = 0;
                if ((lastX==0)&&(lastY==0)) {
                    lastX = location.getLongitude();
                    lastY = location.getLatitude();
                }
                double dist = distance(location.getLongitude(), lastX, location.getLatitude(), lastY);
                speed = dist/3/3600*1000*10000; //скорсоть = dist*1200
                sumdistance = sumdistance + dist;

                lastX = location.getLongitude();
                lastY = location.getLatitude();

                Intent i = new Intent("location_update");
                i.putExtra("coordinates", location.getLongitude() + " " +location.getLatitude());
                i.putExtra("speed", speed);
                i.putExtra("location", location);
                sendBroadcast(i);

                //Записываем в базу координаты...
                long m = dbOpenHelper.insertCoordinates(new Coordinates(String.valueOf(lastNumberRecord), "", String.valueOf(lastX), String.valueOf(lastY), String.valueOf(speed), String.valueOf(time)));
                time = time + 3000; //Обновление каждые 3 секунды
                Toast.makeText(getBaseContext(), m+"", Toast.LENGTH_SHORT).show();
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
        //noinspection MissingPermission
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,3000, 0, listener); //3000 - 3 секунды при обновлении

        setForegroundNotification();



    }

    private void setForegroundNotification() {

        Intent notificationIntent = new Intent(this, MainActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Runmanager")
                .setContentText("Saving GPS coordinates...")
                .setContentIntent(pendingIntent).build();
        startForeground(1337, notification);
    }


    @Override
    public void onDestroy() {

        if (locationManager!=null){
            locationManager.removeUpdates(listener) ;
        }
        writeToSegmentTable();
        timer.onFinish();
        timer.cancel();
        MainActivity.startGpsService = false;

        super.onDestroy();
    }

    private void writeToSegmentTable() {
        final DBOpenHelper dbOpenHelper = new DBOpenHelper(getBaseContext());
        dbOpenHelper.writeToSegmentTable(new Coordinates(String.valueOf(dayOfWeek), String.valueOf(date), String.valueOf(sumdistance) )); //dayOfWeek Date Distnace
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

    private void createTimer() {
        timer = new CountDownTimer(1000000000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                seconds = 1000000 - millisUntilFinished / 1000;
                Intent i = new Intent("timer_update");
                i.putExtra("seconds", seconds);
                sendBroadcast(i);
            }

            @Override
            public void onFinish() {
            }
        }.start();
    }
}
