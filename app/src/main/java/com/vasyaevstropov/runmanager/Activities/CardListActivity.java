package com.vasyaevstropov.runmanager.Activities;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.vasyaevstropov.runmanager.Adapters.RecyclerAdapter;
import com.vasyaevstropov.runmanager.DB.DBOpenHelper;
import com.vasyaevstropov.runmanager.DB.Preferences;
import com.vasyaevstropov.runmanager.MainActivity;
import com.vasyaevstropov.runmanager.R;

import java.util.ArrayList;


//Активити используется для
//отображения списка сохраненных пробежек

public class CardListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter recyclerAdapter;
    private RecyclerView.LayoutManager recyclerLayoutManager;

    ArrayList<String> dayOfWeekList;
    ArrayList<String> dateList;
    ArrayList<String> distanceList;
    ArrayList<String> numberRecordList;
    ArrayList<ArrayList<String>> listOfRuns;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Preferences.init(this);
        setTheme(Preferences.getStyle());



        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_list);

        initToolBar();

        dayOfWeekList = new ArrayList<>();
        dateList = new ArrayList<>();
        distanceList = new ArrayList<>();
        numberRecordList = new ArrayList<>();

        setListOfRuns();

        recyclerView = (RecyclerView)findViewById(R.id.my_recycler_view);
        recyclerLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(recyclerLayoutManager);
        recyclerAdapter = new RecyclerAdapter(this, dayOfWeekList, dateList, distanceList, numberRecordList);
        recyclerView.setAdapter(recyclerAdapter);

    }

    private void initToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() !=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

    }

    private void setListOfRuns(){

        DBOpenHelper dbOpenHelper = new DBOpenHelper(this);
        listOfRuns = dbOpenHelper.getListOfRuns();

        dayOfWeekList = listOfRuns.get(0);
        dateList = listOfRuns.get(1);
        distanceList = listOfRuns.get(2);
        numberRecordList = listOfRuns.get(3);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

}
