package com.vasyaevstropov.runmanager.Fragments;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.vasyaevstropov.runmanager.DB.MusicStorage;
import com.vasyaevstropov.runmanager.DB.Preferences;
import com.vasyaevstropov.runmanager.Models.MediaContent;
import com.vasyaevstropov.runmanager.Notification.PlayerNotification;
import com.vasyaevstropov.runmanager.R;
import com.vasyaevstropov.runmanager.Services.MusicService;

import java.util.ArrayList;


public class MusicFragment extends Fragment {

    private boolean musicBound = false;

    private boolean paused = false, playbackPaused = false;
    MusicService musicService;
    Intent playIntent;
    Button btnPlay;
    private ArrayList<MediaContent> arrayMediaContent;
    ServiceConnection musicConnection;

    @Override
    public void onAttach(Context context) {
        Log.d("VasyaLog", getClass().getName() + " onAttach");
        super.onAttach(context);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d("VasyaLog", getClass().getName() + " onCreateView");

        View view = inflater.inflate(R.layout.fragment_music, container, false);

        view.findViewById(R.id.btnPlayStop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MusicStorage storage = new MusicStorage(v.getContext());
                arrayMediaContent = storage.getMusicList();

                //  musicBinder.playTestMusic();
                Toast.makeText(v.getContext(), "TOAST ", Toast.LENGTH_SHORT).show();

                if (musicConnection == null) {
                    musicConnection = new ServiceConnection() {

                        @Override
                        public void onServiceConnected(ComponentName name, IBinder service) {

                            Log.d("VasyaLog", getClass().getName() + " onServiceConnected");

                            musicService = ((MusicService.MusicBinder) service).getMusicService();

                            Preferences.init(getActivity().getApplicationContext());

                            musicService.playTestMusic(Uri.parse(arrayMediaContent.get(Preferences.getLastMusic()).getUri()));

                            musicBound = true;

                        }

                        @Override
                        public void onServiceDisconnected(ComponentName name) {

                            Log.d("VasyaLog", getClass().getName() + " onServiceDisconnected");

                            musicBound = false;
                        }
                    };

                    playIntent = new Intent(getActivity(), MusicService.class);

                    playIntent.putExtra(MediaContent.currentSong, arrayMediaContent.get(0));

                    getActivity().bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);

                } else {

                    musicService.playTestMusic(Uri.parse(arrayMediaContent.get(Preferences.getLastMusic()).getUri()));

                }

                new PlayerNotification(v.getContext());

            }
        });


        return view;
    }

}
