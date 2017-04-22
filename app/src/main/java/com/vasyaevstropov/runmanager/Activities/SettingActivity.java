package com.vasyaevstropov.runmanager.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;

import com.vasyaevstropov.runmanager.Adapters.MySpinnerAdapter;
import com.vasyaevstropov.runmanager.DB.Preferences;
import com.vasyaevstropov.runmanager.MainActivity;
import com.vasyaevstropov.runmanager.R;

public class SettingActivity extends AppCompatActivity {

    Spinner spinner;
    MySpinnerAdapter adapter;

    Button btnSave;
    private String[] styles = {"Голубой", "Темный", "Ночной", "Серебрянный"};
    private String[] mapNames = {"map_blue", "map_dark", "map_night", "map_silver"};
    private Integer[] styleNames = {R.style.AppTheme_Blue,R.style.AppTheme_Dark,R.style.AppTheme_Night,R.style.AppTheme_Silver};
    private Integer[] stylesColors = {R.color.blue, R.color.dark, R.color.night, R.color.silver};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Preferences.init(this);
        setTheme(Preferences.getStyle());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        spinner = (Spinner)findViewById(R.id.spinner);
        adapter =  new MySpinnerAdapter(SettingActivity.this, styles, stylesColors);
        spinner.setAdapter(adapter);
        spinner.setSelection(Preferences.getSelectedPosition());
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Preferences.init(view.getContext());
                Preferences.setStyle(stylesColors[position], styles[position], mapNames[position], position, styleNames[position]);

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
