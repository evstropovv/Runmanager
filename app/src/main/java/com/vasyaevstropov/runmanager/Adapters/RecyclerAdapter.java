package com.vasyaevstropov.runmanager.Adapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.vasyaevstropov.runmanager.DB.DBOpenHelper;
import com.vasyaevstropov.runmanager.R;
import com.vasyaevstropov.runmanager.RunListActivity;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder>  {

    private ArrayList<String> dayOfWeekList;
    private String[] dayOfWeek = {"Воскресенье","Понедельник","Вторник","Среда","Четверг","Пятница","Суббота"};
    private ArrayList<String> dateList;
    private ArrayList<String> distanceList;
    private ArrayList<String> numberRecordList;


    public RecyclerAdapter(ArrayList<String> dayOfWeekList, ArrayList<String> dateList, ArrayList<String> distanceList, ArrayList<String> numberRecordList){
        this.dayOfWeekList = dayOfWeekList;
        this.dateList = dateList;
        this.distanceList = distanceList;
        this.numberRecordList = numberRecordList;

    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView tvDayOfWeek, tvDistance, tvDate;

        public ViewHolder(final View view) {
            super(view);
            tvDayOfWeek = (TextView)view.findViewById(R.id.tvDayOfWeek);
            tvDistance = (TextView)view.findViewById(R.id.tvDistance);
            tvDate = (TextView)view.findViewById(R.id.tvDate);
        }
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_run, parent, false);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), RunListActivity.class);
                v.getContext().startActivity(intent);
            }
        });

        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.tvDistance.setText(distanceList.get(position));
        holder.tvDayOfWeek.setText(dayOfWeek[Integer.parseInt(dayOfWeekList.get(position))-1]);
        holder.tvDate.setText(dateList.get(position));

    }


    @Override
    public int getItemCount() {
        return distanceList.size();
    }
}
