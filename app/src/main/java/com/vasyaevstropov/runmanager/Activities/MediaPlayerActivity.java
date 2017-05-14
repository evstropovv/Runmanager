package com.vasyaevstropov.runmanager.Activities;

import android.app.FragmentManager;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.vasyaevstropov.runmanager.DB.MusicStorage;
import com.vasyaevstropov.runmanager.DB.Preferences;
import com.vasyaevstropov.runmanager.Fragments.MusicFragment;
import com.vasyaevstropov.runmanager.MainActivity;
import com.vasyaevstropov.runmanager.Models.Coordinates;
import com.vasyaevstropov.runmanager.Models.MediaContent;
import com.vasyaevstropov.runmanager.R;
import com.vasyaevstropov.runmanager.Services.MusicService;

import java.util.ArrayList;

public class MediaPlayerActivity extends AppCompatActivity {

    ArrayAdapter<String> adapter;
    ArrayList<MediaContent> arrayMediaContent;
    ArrayList<String> arraySongNames;
    ListView musicListView;

    android.app.FragmentTransaction fm;
    MusicFragment musicFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_player);
        Preferences.init(this);
        setTheme(Preferences.getStyle());
        initToolBar();

        doStuff();
    }

    private void initToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void doStuff() {
        arrayMediaContent = new ArrayList<>();
        arraySongNames = new ArrayList<>();

        fillArrayMedaContent();

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arraySongNames);
        musicListView = (ListView)findViewById(R.id.musicListView);
        musicListView.setAdapter(adapter);
        musicListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(MusicService.PLAYMEDIA);
                intent.setPackage(getApplicationContext().getPackageName());
                intent.putExtra(MediaContent.currentSong, arrayMediaContent.get(position));
                startService(intent);

                Preferences.init(view.getContext());
                Preferences.setLastMusic(position);

            }
        });

        fillMusicFragment();

    }

    private void fillMusicFragment() {
        musicFragment = new MusicFragment();  //активируем фрагмент с музыкой.
        fm = getFragmentManager().beginTransaction();
        fm.add(R.id.musicFrame, musicFragment);
        fm.commit();
    }

    private void fillArrayMedaContent(){

        MusicStorage storage = new MusicStorage(this);
        arrayMediaContent = storage.getMusicList();
        fillArraySongName();

    }
    private void fillArraySongName(){
        for (int i = 0; i < arrayMediaContent.size() ; i++) {
            arraySongNames.add(arrayMediaContent.get(i).getTitle() + "\n" + arrayMediaContent.get(i).getArtist() + "\n" + arrayMediaContent.get(i).getUri() );
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
