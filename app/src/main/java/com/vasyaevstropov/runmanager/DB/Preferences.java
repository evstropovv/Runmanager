package com.vasyaevstropov.runmanager.DB;

/**
 * Created by Вася on 22.04.2017.
 */


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.MenuItem;

import com.vasyaevstropov.runmanager.R;

/**
 * Created by вася on 28.01.2017.
 */

public class Preferences { //Класс для сохранения настроек
    public static final String STORAGE_NAME = "applicationTheme";

    private static SharedPreferences preferences = null;
    private static SharedPreferences.Editor editor = null;
    private static Context context = null;

    public static void init(Context cntxt) {
        context = cntxt;
    }

    private static void init() {
        preferences = context.getSharedPreferences(STORAGE_NAME, Context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    public static void setStyle(int item, String name, String mapName, int position, int style) { // Сохраняем выбранный пункт меню
        if (preferences == null) {
            init();
        }
        editor.putInt("styleName", item);
        editor.putString("name", name);
        editor.putString("mapName",mapName);
        editor.putInt("position", position);
        editor.putInt("style", style);
        editor.commit();
    }

    public static int getSelectedPosition(){
        if (preferences ==null){init();}
        return preferences.getInt("position",0);
    }

    public static int getStyle(){
        if (preferences ==null){init();}
        return preferences.getInt("style", R.style.AppTheme_Dark);
    }


    public static String getStyleName(){
        if (preferences ==null){
            init();
        }
        return preferences.getString("name", "Темный");}

    public static String getMapName(){
        if (preferences ==null){
            init();
        }
        return preferences.getString("mapName", "map_dark"); }

    public static Integer getStyleColor(){
        if (preferences ==null){
            init();
        }
        return preferences.getInt("styleName", R.color.dark);
    }


    public static void setLastMusic(int musicPosition){
        if (preferences == null) {
            init();
        }
        editor.putInt("musicPosition", musicPosition);
        editor.commit();
    }

    public static Integer getLastMusic(){
        if (preferences ==null){
            init();
        }
        return preferences.getInt("musicPosition", 0);
    }
}

