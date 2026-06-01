package com.example.nct;

import static com.example.nct.MainActivity.musicFiles;

import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AlbumDetails extends AppCompatActivity {

    RecyclerView recyclerView;
    ImageView albumPhoto;

    String albumName;

    ArrayList<MusicFiles> albumSongs = new ArrayList<>();

    AlbumDetailsAdapter albumDetailsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_details);
        recyclerView = findViewById(R.id.recyclerView);
        albumPhoto = findViewById(R.id.album_photo);
        albumName = getIntent().getStringExtra("albumName");
        recyclerView.setLayoutManager(
                new LinearLayoutManager(
                        this,
                        RecyclerView.VERTICAL,
                        false
                )
        );

        if ("Yêu thích".equals(albumName)) {
            Glide.with(this)
                    .load(R.drawable.favorite)
                    .into(albumPhoto);
            loadFavoritesFromFirebase();
        } else {
            loadLocalAlbumSongs();
        }
    }


    private void loadFavoritesFromFirebase() {
        if (MainActivity.currentUser == null) {
            Toast.makeText(this, "Hãy đăng nhập để xem yêu thích", Toast.LENGTH_SHORT).show();
            return;
        }
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("favorites").child(String.valueOf(MainActivity.currentUser.getId()));
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                albumSongs.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    MusicFiles song =
                            ds.getValue(MusicFiles.class);
                    if (song != null) {
                        albumSongs.add(song);
                    }
                }
                albumDetailsAdapter =
                        new AlbumDetailsAdapter(
                                AlbumDetails.this,
                                albumSongs
                        );
                recyclerView.setAdapter(albumDetailsAdapter);
                if (albumSongs.size() == 0) {
                    Toast.makeText(
                            AlbumDetails.this,
                            "Chưa có bài hát yêu thích",
                            Toast.LENGTH_SHORT
                    ).show();
                }
            }
            @Override
            public void onCancelled(
                    @NonNull DatabaseError error
            ) {

                Toast.makeText(
                        AlbumDetails.this,
                        "Lỗi tải dữ liệu",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }
    private void loadLocalAlbumSongs() {
        albumSongs.clear();
        for (int i = 0; i < musicFiles.size(); i++) {
            if (albumName.equals(
                    musicFiles.get(i).getAlbum()
            )) {
                albumSongs.add(
                        musicFiles.get(i)
                );
            }
        }

        if (albumSongs.size() > 0) {
            byte[] image =
                    getAlbumArt(
                            albumSongs.get(0).getPath()
                    );
            if (image != null) {
                Glide.with(this)
                        .load(image)
                        .into(albumPhoto);
            } else {
                Glide.with(this)
                        .load(R.drawable.nct_logo)
                        .into(albumPhoto);
            }
        }
        albumDetailsAdapter =
                new AlbumDetailsAdapter(
                        this,
                        albumSongs
                );
        recyclerView.setAdapter(albumDetailsAdapter);
    }

    private byte[] getAlbumArt(String uri) {
        MediaMetadataRetriever retriever =
                new MediaMetadataRetriever();
        try {
            retriever.setDataSource(uri);
            byte[] art =
                    retriever.getEmbeddedPicture();
            retriever.release();
            return art;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}