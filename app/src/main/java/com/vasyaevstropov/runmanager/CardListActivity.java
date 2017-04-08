package com.vasyaevstropov.runmanager;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.vasyaevstropov.runmanager.Adapters.RecyclerAdapter;
import com.vasyaevstropov.runmanager.DB.DBOpenHelper;

import java.util.ArrayList;

public class CardListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter recyclerAdapter;
    private RecyclerView.LayoutManager recyclerLayoutManager;

    ArrayList<String> dayOfWeekList;
    ArrayList<String> dateList;
    ArrayList<String> distanceList;
    ArrayList<String> numberRecordList;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_list);

        dayOfWeekList = new ArrayList<>();
        dateList = new ArrayList<>();
        distanceList = new ArrayList<>();
        numberRecordList = new ArrayList<>();

        readDB(this);

        recyclerView = (RecyclerView)findViewById(R.id.my_recycler_view);
        recyclerLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(recyclerLayoutManager);
        recyclerAdapter = new RecyclerAdapter(dayOfWeekList, dateList, distanceList, numberRecordList);
        recyclerView.setAdapter(recyclerAdapter);

    }

    private void readDB(Context context) {
        DBOpenHelper dbOpenHelper = new DBOpenHelper(context);
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();

        Cursor c2 = db.query("segmenttable", null, null, null, null, null, null);

        if (c2.moveToFirst()) {
            // определяем номера столбцов по имени в выборке
            int id = c2.getColumnIndex("id");
            int dayofweek = c2.getColumnIndex("dayofweek");
            int date = c2.getColumnIndex("date");
            int distance = c2.getColumnIndex("distance");
            do {
                numberRecordList.add(c2.getString(id));
                dayOfWeekList.add(c2.getString(dayofweek));
                dateList.add(c2.getString(date));
                distanceList.add(c2.getString(distance));

            } while (c2.moveToNext());
            c2.close();
        }
        db.close();
    }

}
