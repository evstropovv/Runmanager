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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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

    Button btnPlayStop, btnPrev, btnNext;

    ArrayAdapter<String> adapter;
    ArrayList<String> arraySongNames;
    ListView musicListView;
    TextView tvSongName;


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



    private void setPlayerButtons(View view) {

        btnPlayStop = (Button) view.findViewById(R.id.btnPlayStop);
        btnPrev = (Button)view.findViewById(R.id.btnSongMinus);
        btnNext = (Button)view.findViewById(R.id.btnSongPlus);
        tvSongName = (TextView)view.findViewById(R.id.tvSongName);

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

                Intent intent = new Intent(MusicService.PLAYMEDIA);
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
