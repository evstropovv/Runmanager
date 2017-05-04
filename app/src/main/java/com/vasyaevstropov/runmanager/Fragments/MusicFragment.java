package com.vasyaevstropov.runmanager.Fragments;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.vasyaevstropov.runmanager.DB.MusicStorage;
import com.vasyaevstropov.runmanager.DB.Preferences;
import com.vasyaevstropov.runmanager.Models.MediaContent;
import com.vasyaevstropov.runmanager.R;
import com.vasyaevstropov.runmanager.Services.MusicService;

import java.util.ArrayList;


public class MusicFragment extends Fragment {

    private boolean musicBound = false;

    private boolean paused = false, playbackPaused = false;
    MusicService musicService;
    Button btnPlayStop, btnPrev, btnNext;

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

        btnPlayStop = (Button) view.findViewById(R.id.btnPlayStop);
        btnPrev = (Button)view.findViewById(R.id.btnSongMinus);
        btnNext = (Button)view.findViewById(R.id.btnSongPlus);

        btnPlayStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Preferences.init(getActivity().getApplicationContext());

                Intent playIntent = new Intent(MusicService.PLAYMEDIA);
                playIntent.setPackage(v.getContext().getPackageName());

                playIntent.putExtra(MediaContent.currentSong, Preferences.getLastMusic());

                v.getContext().startService(playIntent);

            }
        });
        btnPrev.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Preferences.init(getActivity().getApplicationContext());

                Intent playIntent = new Intent(MusicService.PREVMEDIA);

                playIntent.setPackage(v.getContext().getPackageName());

                playIntent.putExtra(MediaContent.currentSong, Preferences.getLastMusic());

                v.getContext().startService(playIntent);

            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Preferences.init(getActivity().getApplicationContext());

                Intent playIntent = new Intent(MusicService.NEXTMEDIA);

                playIntent.setPackage(v.getContext().getPackageName());

                playIntent.putExtra(MediaContent.currentSong, Preferences.getLastMusic());

                v.getContext().startService(playIntent);

            }
        });

        return view;
    }

}
