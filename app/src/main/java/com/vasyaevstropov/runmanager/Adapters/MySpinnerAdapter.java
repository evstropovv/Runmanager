package com.vasyaevstropov.runmanager.Adapters;

        import android.content.Context;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.BaseAdapter;
        import android.widget.TextView;

        import com.vasyaevstropov.runmanager.R;

public class MySpinnerAdapter extends BaseAdapter {
    Context context;
    String[] styles;
    Integer[] colors;
    LayoutInflater inflater;

    public MySpinnerAdapter(Context context, String[] styles, Integer[] colors) {
        this.context = context;
        this.styles = styles;
        this.colors = colors;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return styles.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = inflater.inflate(R.layout.card_style_spinner, null);
        TextView styleName = (TextView)row.findViewById(R.id.tvStyleName);
        View colorView = (View)row.findViewById(R.id.colordView);
        colorView.setBackgroundColor(colors[position]);

        styleName.setText(styles[position]);
        return row;
    }
}
