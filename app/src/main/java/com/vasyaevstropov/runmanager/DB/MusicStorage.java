package com.vasyaevstropov.runmanager.DB;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.vasyaevstropov.runmanager.Fragments.MusicFragment;
import com.vasyaevstropov.runmanager.Models.MediaContent;

import java.util.ArrayList;

/**
 * Created by Вася on 29.04.2017.
 */

public class MusicStorage {

    private ArrayList<MediaContent> arrayMediaContent;

    private Context context;

    public MusicStorage(Context context){

        this.context = context;

        arrayMediaContent = new ArrayList<>();

    }

    public ArrayList<MediaContent> getMusicList() {

        ContentResolver contentResolver = context.getContentResolver();

        Uri songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        Cursor songCursor = contentResolver.query(songUri, null, null, null, null);

        if (songCursor != null && songCursor.moveToFirst()) {

            int songTitle = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);

            int songArtist = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);

            int songUrl = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA);

            do {
                String currentTitle = songCursor.getString(songTitle);

                String currentArtist = songCursor.getString(songArtist);

                String currentUrl = songCursor.getString(songUrl);

                arrayMediaContent.add(new MediaContent(currentTitle, currentArtist, currentUrl));

            } while (songCursor.moveToNext());
        }

        if (songCursor !=null) songCursor.close();

        return arrayMediaContent;
    }

}
