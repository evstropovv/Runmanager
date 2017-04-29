package com.vasyaevstropov.runmanager.Services;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.vasyaevstropov.runmanager.Models.MediaContent;


public class MusicService extends Service {

    MediaPlayer player;
    @Override
    public int onStartCommand(Intent intent,int flags, int startId) {
        MediaContent content = intent.getExtras().getParcelable(MediaContent.currentSong);
        try {
            Uri uri = Uri.parse(content.getUri());
            playMedia(uri);
        } catch (NullPointerException e){
            e.printStackTrace();
        }

        return START_STICKY;
    }

    private void playMedia(Uri uri) {

        if (player!=null){
            stopMediaPlayer();
        }
        player = MediaPlayer.create(getBaseContext(), uri);

        player.start();

    }

    private void stopMediaPlayer() {
        try {
            player.stop();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate() {

        super.onCreate();

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        player.stop();
    }
}
