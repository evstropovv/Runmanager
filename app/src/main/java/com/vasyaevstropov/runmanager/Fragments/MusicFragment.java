package com.vasyaevstropov.runmanager.Fragments;


import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.vasyaevstropov.runmanager.DB.MusicStorage;
import com.vasyaevstropov.runmanager.DB.Preferences;
import com.vasyaevstropov.runmanager.MainActivity;
import com.vasyaevstropov.runmanager.Models.MediaContent;
import com.vasyaevstropov.runmanager.R;
import com.vasyaevstropov.runmanager.Services.MusicService;

import java.util.ArrayList;


public class MusicFragment extends Fragment {

    ImageButton btnPlayStop, btnPrev, btnNext;

    ArrayAdapter<String> adapter;
    ArrayList<String> arraySongNames;
    ListView musicListView;
    TextView tvSongName;
    BroadcastReceiver broadcastReceiver;


    private ArrayList<MediaContent> arrayMediaContent;

    @Override
    public void onAttach(Context context) {

        Log.d("VasyaLog", getClass().getName() + " onAttach");

        super.onAttach(context);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_music, container, false);
        setPlayerButtons(view);
        doStuff(view);
        setTextViewSongName();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (broadcastReceiver == null) { //Используется для связи с GPSservice;
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    String curSong = intent.getStringExtra("currentSong");
                    Boolean isPlaying = intent.getBooleanExtra("isPlaying", false);

                    if (isPlaying) {
                        btnPlayStop.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_media_pause));
                    }else{
                        btnPlayStop.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_media_play));
                    }
                    tvSongName.setText(curSong);
                }
            };
        }
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("music_update");
        getActivity().registerReceiver(broadcastReceiver, intentFilter); // Регистрация ресивера (для Сервиса)

    }

    private void setPlayerButtons(View view) {

        btnPlayStop = (ImageButton) view.findViewById(R.id.btnPlayStop);
        if (MusicService.ISPLAYING){
            btnPlayStop.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_media_pause));
        }else{
            btnPlayStop.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_media_play));
        }


        btnPrev = (ImageButton)view.findViewById(R.id.btnSongMinus);
        btnNext = (ImageButton)view.findViewById(R.id.btnSongPlus);
        tvSongName = (TextView)view.findViewById(R.id.tvSongName);

        btnPlayStop.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent playIntent = new Intent(MusicService.PLAYMEDIA);
                playIntent.setPackage(v.getContext().getPackageName());
                v.getContext().startService(playIntent);
            }
        });
        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent playIntent = new Intent(MusicService.PREVMEDIA);
                playIntent.setPackage(v.getContext().getPackageName());
                v.getContext().startService(playIntent);
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent playIntent = new Intent(MusicService.NEXTMEDIA);
                playIntent.setPackage(v.getContext().getPackageName());
                v.getContext().startService(playIntent);
            }
        });

    }

    private void doStuff(View view) {
        arrayMediaContent = new ArrayList<>();
        arraySongNames = new ArrayList<>();

        fillArrayMediaContent();

        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, arraySongNames);
        musicListView = (ListView)view.findViewById(R.id.musicLV);
        musicListView.setAdapter(adapter);
        musicListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(MusicService.SELECT_MEDIA);
                intent.setPackage(getActivity().getApplicationContext().getPackageName());

                intent.putExtra(MediaContent.currentSong, arrayMediaContent.get(position));

                getActivity().startService(intent);

                Preferences.init(view.getContext());
                Preferences.setLastMusic(position);

                setTextViewSongName();
            }
        });
    }

    private void fillArrayMediaContent(){

        MusicStorage storage = new MusicStorage(getActivity());

        arrayMediaContent = storage.getMusicList();

        fillArraySongName();
    }

    private void fillArraySongName(){
        for (int i = 0; i < arrayMediaContent.size() ; i++) {
            arraySongNames.add(arrayMediaContent.get(i).getTitle() + "\n" + arrayMediaContent.get(i).getArtist());
        }
    }

    private void setTextViewSongName(){
        Preferences.init(getActivity());
        try {
            tvSongName.setText(arraySongNames.get(Preferences.getLastMusic()));
        }catch (IndexOutOfBoundsException e){
            tvSongName.setText("-");
        }
    }

}
