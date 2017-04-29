package com.vasyaevstropov.runmanager.Activities;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.vasyaevstropov.runmanager.DB.MusicStorage;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_player);
        doStuff();
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
                Intent intent = new Intent(view.getContext(), MusicService.class);
                intent.putExtra(MediaContent.currentSong, arrayMediaContent.get(position));
                startService(intent);
            }
        });
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
}
