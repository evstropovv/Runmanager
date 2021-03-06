package com.vasyaevstropov.runmanager.Services;

import android.app.Service;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vasyaevstropov.runmanager.DB.DBOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SinhrService extends Service {
    private Integer i =0 ;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        new AsyncTask<Void,Void,Void>(){
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected Void doInBackground(Void... params) {
                DBOpenHelper dbOpenHelper = new DBOpenHelper(getBaseContext());

                List<List<String>> listOfRuns = dbOpenHelper.getListOfRuns();

                GsonBuilder gsonBuilder = new GsonBuilder();
                Gson gson = gsonBuilder.create();

                ArrayList<HashMap<String, String>> arrayList = dbOpenHelper.getSpeedTable();

                Log.d("GSON-SPEEDTABLE", gson.toJson(arrayList));

                Log.d("GSON-SPEEDTABLE --","------------------------");

                Log.d("Log.d","list of runs: " + new Gson().toJson(listOfRuns));

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {

                super.onPostExecute(aVoid);
            }
        }.execute();

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
