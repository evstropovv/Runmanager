package com.vasyaevstropov.runmanager.Services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.vasyaevstropov.runmanager.DB.MusicStorage;
import com.vasyaevstropov.runmanager.DB.Preferences;
import com.vasyaevstropov.runmanager.Models.MediaContent;
import com.vasyaevstropov.runmanager.Notification.PlayerNotification;

import java.util.ArrayList;


public class MusicService extends Service {

    public static final String PLAYMEDIA = "com.vasyaevstropov.runmanager.TOGGLE_PLAYBACK";
    public static final String PREVMEDIA = "com.vasyaevstropov.runmanager.PREV";
    public static final String NEXTMEDIA = "com.vasyaevstropov.runmanager.NEXT";
    public static final String SELECT_MEDIA = "com.vasyaevstropov.runmanager.SELECT_MEDIA";
    public static boolean ISPLAYING;

    Uri uri;
    MediaPlayer player;
    MediaContent content;
    private final int PREV = -1;
    private final int PLAY = 0;
    private final int NEXT = 1;
    private MediaContent mediaContent;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent.getAction().contains(PLAYMEDIA) ||
                intent.getAction().contains(PREVMEDIA) ||
                intent.getAction().contains(NEXTMEDIA) ||
                intent.getAction().contains(SELECT_MEDIA)) {

            if (intent.getAction().contains(SELECT_MEDIA)) {

                mediaContent = intent.getExtras().getParcelable(MediaContent.currentSong);
                Toast.makeText(getBaseContext(), "INTENT HAS PARCELABLE", Toast.LENGTH_LONG).show();

                uri = Uri.parse(mediaContent.getUri());

                playNewMedia(uri);

            }


            if (intent.getAction().contains(PLAYMEDIA)) {
                try {
                    uri = Uri.parse(getContent(getBaseContext(), PLAY).getUri());
                    playMedia(uri);

                } catch (NullPointerException e) {
                    e.printStackTrace();

                }
            }


            if (intent.getAction().contains(PREVMEDIA)) {
                try {
                    uri = Uri.parse(getContent(getBaseContext(), PREV).getUri());
                    playNewMedia(uri);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
            if (intent.getAction().contains(NEXTMEDIA)) {

                try {

                    uri = Uri.parse(getContent(getBaseContext(), NEXT).getUri());

                    playNewMedia(uri);

                } catch (NullPointerException e) {

                    e.printStackTrace();

                }
            }
        }
        return START_STICKY;
    }


    private MediaContent getContent(Context baseContext, int numb) {

        Preferences.init(baseContext);

        MusicStorage storage = new MusicStorage(getBaseContext());

        try {

            int currentMedia = Preferences.getLastMusic() + numb;

            content = storage.getMusicList().get(currentMedia);

            Preferences.setLastMusic(currentMedia);

        } catch (Exception e) {

            content = storage.getMusicList().get(Preferences.getLastMusic());

        }

        new PlayerNotification(baseContext, content);


        return content;
    }

    private void playMedia(Uri uri) {

        if (player != null) {

            if (player.isPlaying()) {

                player.pause();

            } else {

                player.start();

            }

        } else {

            playNewMedia(uri);

        }

        ISPLAYING = true;
    }


    private void playNewMedia(Uri uri) {

        Log.d("VasyaLog", getClass().getName() + " playNewMedia");

        if (player != null) {

            stopMediaPlayer();

        }

        player = MediaPlayer.create(getBaseContext(), uri);

        player.start();

        ISPLAYING = true;

    }


    private void stopMediaPlayer() {
        Log.d("VasyaLog", getClass().getName() + " stopMediaPlayer");
        try {
            player.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
        ISPLAYING = false;
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

    public class MusicBinder extends Binder {
        public MusicService getMusicService() {
            Log.d("VasyaLog", getClass().getName() + " getMusicService");
            return MusicService.this;
        }
    }

}
