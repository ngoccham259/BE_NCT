package com.example.nct;

import static com.example.nct.MainActivity.musicFiles;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class SongsFragment extends Fragment {

   RecyclerView recyclerView;
   static MusicAdapter musicAdapter;

    public SongsFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_songs, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);

        if(musicFiles != null && musicFiles.size() >= 1){
            // Lấy instance của MainActivity
            MainActivity mainActivity = (MainActivity) getActivity();
            if (mainActivity != null) {
                // Khởi tạo adapter và gán vào biến của MainActivity
                mainActivity.musicAdapter = new MusicAdapter(getContext(), musicFiles,"default");
                recyclerView.setAdapter(mainActivity.musicAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
            }
        }
        return view;
    }
    public void filter(String text) {

        ArrayList<MusicFiles> filtered = new ArrayList<>();

        for (MusicFiles song : MainActivity.musicFiles) {

            if (song.getTitle() != null &&
                    song.getTitle().toLowerCase().contains(text)) {

                filtered.add(song);
            }
        }

        musicAdapter.updateList(filtered);
    }
}