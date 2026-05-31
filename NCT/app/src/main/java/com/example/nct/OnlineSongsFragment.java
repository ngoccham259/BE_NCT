package com.example.nct;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class OnlineSongsFragment extends Fragment {

    RecyclerView recyclerView;
    MusicAdapter musicAdapter;

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {

        View view =
                inflater.inflate(
                        R.layout.fragment_songs,
                        container,
                        false);

        recyclerView =
                view.findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(
                new LinearLayoutManager(getContext()));

        musicAdapter =
                new MusicAdapter(getContext(), MainActivity.onlineMusicFiles, "online");

        recyclerView.setAdapter(musicAdapter);

        return view;
    }
}