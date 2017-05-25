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

import java.io.IOException;


public class MusicService extends Service {

    public static final String PLAYMEDIA = "com.vasyaevstropov.runmanager.TOGGLE_PLAYBACK";
    public static final String PREVMEDIA = "com.vasyaevstropov.runmanager.PREV";
    public static final String NEXTMEDIA = "com.vasyaevstropov.runmanager.NEXT";
    public static final String SELECT_MEDIA = "com.vasyaevstropov.runmanager.SELECT_MEDIA";
    public static boolean ISPLAYING;

    Uri uri;
    MediaPlayer player;
    private final int PREV = -1;
    private final int PLAY = 0;
    private final int NEXT = 1;
    private MediaContent content;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("Service_LOG","onStartCommand");

        if (intent.getAction().contains(PLAYMEDIA) ||
                intent.getAction().contains(PREVMEDIA) ||
                intent.getAction().contains(NEXTMEDIA) ||
                intent.getAction().contains(SELECT_MEDIA)) {

            Log.d("Service_LOG","PLAYMEDIA PREVMEDIA NEXTMEDIA SELECT_MEDIA");

            if (intent.getAction().contains(SELECT_MEDIA)) {
                Log.d("Service_LOG","SELECT_MEDIA");

                content = intent.getExtras().getParcelable(MediaContent.currentSong);

                uri = Uri.parse(content.getUri());

                playNewMedia(uri);

            }


            if (intent.getAction().contains(PLAYMEDIA)) {
                Log.d("Service_LOG","PLAYMEDIA");
                try {
                    uri = Uri.parse(getContent(getBaseContext(), PLAY).getUri());
                    playMedia(uri);
                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(getBaseContext(), "Музыки не обнаружено ! :(", Toast.LENGTH_LONG).show();
                }
            }

            if (intent.getAction().contains(PREVMEDIA)) {
                Log.d("Service_LOG","PREVMEDIA");
                try {
                    uri = Uri.parse(getContent(getBaseContext(), PREV).getUri());
                    playNewMedia(uri);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
            if (intent.getAction().contains(NEXTMEDIA)) {
                Log.d("Service_LOG","NEXTMEDIA");

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
            if (storage.getMusicList().size()!= 0){
                content = storage.getMusicList().get(Preferences.getLastMusic());
            }
        }

        if (storage.getMusicList().size()!= 0) {
            updateCurrentSong(baseContext, content, true);
        }

        return content;
    }

    private void updateCurrentSong(Context baseContext, MediaContent content, boolean b) {
        new PlayerNotification(baseContext, content, b);
        sendBroadc(content.getArtist() + "\n" + content.getTitle(), b);
        ISPLAYING = b;
    }

    private void playMedia(Uri uri) {

        if (player != null) {

            if (player.isPlaying()) {

                player.pause();

                updateCurrentSong(getBaseContext(), content, false);

            } else {
                player.start();
            }

        } else {
            playNewMedia(uri);
        }

    }




    private void playNewMedia(Uri uri) {

        Log.d("VasyaLog", getClass().getName() + " playNewMedia");

        if (player != null) {

            stopMediaPlayer();

        }

        updateCurrentSong(getBaseContext(), content, true);

        player = MediaPlayer.create(getBaseContext(), uri);

        player.start();

        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                try {
                    mp.reset();
                    MediaContent mediaContent = getContent(getBaseContext(),NEXT);

                    mp.setDataSource(mediaContent.getUri());
                    mp.prepare();
                    mp.start();

                    updateCurrentSong(getBaseContext(), content, true);

                }catch (IOException e){
                    e.printStackTrace();
                }
            }

        });


    }

    private void sendBroadc(String curSong, Boolean isPlaing) {

        Intent intent = new Intent("music_update");
        Preferences.init(getBaseContext());
        intent.putExtra("currentSong", curSong);
        intent.putExtra("isPlaying", isPlaing);
        sendBroadcast(intent);
    }

    private void stopMediaPlayer() {

        updateCurrentSong(getBaseContext(), content, false);
        Log.d("VasyaLog", getClass().getName() + " stopMediaPlayer");
        try {
            player.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onCreate() {

        super.onCreate();
        Log.d("Service_LOG","onCreate");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d("Service_LOG","onBind");

        return new MusicBinder();
    }


    @Override
    public void onDestroy() {
        Log.d("Service_LOG","onDestroy");
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
