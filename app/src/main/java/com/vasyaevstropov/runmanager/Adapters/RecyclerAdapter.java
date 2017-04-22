package com.vasyaevstropov.runmanager.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vasyaevstropov.runmanager.R;
import com.vasyaevstropov.runmanager.Activities.MapActivity;

import java.util.ArrayList;
import java.util.Locale;


//Адаптер.
//Используется для отображения списка
//сохраненных пробежек

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder>  {

    private ArrayList<String> dayOfWeekList;
    Context context;
    private String[] dayOfWeek;
    private ArrayList<String> dateList;
    private ArrayList<String> distanceList;
    private ArrayList<String> numberRecordList;


    public RecyclerAdapter(Context ctx, ArrayList<String> dayOfWeekList, ArrayList<String> dateList, ArrayList<String> distanceList, ArrayList<String> numberRecordList){
        this.dayOfWeekList = dayOfWeekList;
        this.context = ctx;
        dayOfWeek = ctx.getResources().getStringArray(R.array.day_of_week);
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
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_run, parent, false);
        final ViewHolder vh = new ViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), MapActivity.class);
                intent.putExtra("number", vh.getAdapterPosition()+1);
                v.getContext().startActivity(intent);
            }
        });
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String formatedDistance = String.format(Locale.US,"%.2f", Double.parseDouble(distanceList.get(position))); //уменьшаем дистанцию до 2ух знаков после запятой.

        holder.tvDistance.setText(formatedDistance + context.getResources().getString(R.string.km));
        holder.tvDayOfWeek.setText(dayOfWeek[Integer.parseInt(dayOfWeekList.get(position))-1]);
        holder.tvDate.setText(dateList.get(position));
    }

    @Override
    public int getItemCount() {
        return distanceList.size();
    }
}
