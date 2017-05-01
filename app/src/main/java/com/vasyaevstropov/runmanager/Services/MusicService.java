package com.vasyaevstropov.runmanager.Services;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.vasyaevstropov.runmanager.Models.MediaContent;


public class MusicService extends Service {
    Uri uri;
    MediaPlayer player;
    MediaContent content;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d("VasyaLog", getClass().getName() + " onStartCommand");
        content = intent.getExtras().getParcelable(MediaContent.currentSong);

        try {
            uri = Uri.parse(content.getUri());
            playMedia(uri);
        } catch (NullPointerException e){
            e.printStackTrace();
        }

        return START_STICKY;
    }

    public void playTestMusic(Uri uri){

        Log.d("VasyaLog", getClass().getName() + " playTestMusic()");

        if (player.isPlaying()){
            pauseMedia();
        }else {
            playMedia(uri);
        }
    }

    private void playMedia(Uri uri) {

        Log.d("VasyaLog", getClass().getName() + " playMedia");

        if (player!=null){
            stopMediaPlayer();
        }
        player = MediaPlayer.create(getBaseContext(), uri);

        player.start();

    }

    private void pauseMedia(){
        Log.d("VasyaLog", getClass().getName() + " pauseMedia");
        player.pause();
    }

    private void stopMediaPlayer() {
        Log.d("VasyaLog", getClass().getName() + " stopMediaPlayer");
        try {
            player.stop();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate() {
        Log.d("VasyaLog", getClass().getName() + " onCreate");
        super.onCreate();

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d("VasyaLog", getClass().getName() + " onBind");

        return new MusicBinder();
    }


    @Override
    public void onDestroy() {
        Log.d("VasyaLog", getClass().getName() + " onDestroy");
        super.onDestroy();
        player.stop();
    }

    public class MusicBinder extends Binder{
        public MusicService getMusicService(){
            Log.d("VasyaLog", getClass().getName() + " getMusicService");
            return MusicService.this;
        }
    }

}
