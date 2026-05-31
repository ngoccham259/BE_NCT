package com.example.nct;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

public class SongAdminFragment extends Fragment implements AdminMusicAdapter.OnSongClickListener {

    private RecyclerView recyclerView;
    private AdminMusicAdapter adapter;
    private ArrayList<MusicFiles> songList = new ArrayList<>();
    private FloatingActionButton fabAdd;
    private DatabaseReference mDatabase;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_song_admin, container, false);

        recyclerView = view.findViewById(R.id.rv_admin_songs);
        fabAdd = view.findViewById(R.id.fab_add_song);

        // Kết nối tới node "songs" trên Firebase
        mDatabase = FirebaseDatabase.getInstance().getReference("songs");

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Lắng nghe dữ liệu từ Firebase
        refreshData();

        fabAdd.setOnClickListener(v -> showAddEditDialog(null));

        return view;
    }

    private void refreshData() {
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                songList.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    MusicFiles song = data.getValue(MusicFiles.class);
                    if (song != null) {
                        // Lưu key của Firebase vào ID để sau này dễ xóa/sửa
                        song.setId(Long.parseLong(data.getKey()));
                        songList.add(song);
                    }
                }
                adapter = new AdminMusicAdapter(getContext(), songList, SongAdminFragment.this);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Lỗi Firebase: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showAddEditDialog(MusicFiles selectedSong) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_edit_song, null);
        builder.setView(dialogView);

        EditText etTitle = dialogView.findViewById(R.id.et_song_title);
        EditText etArtist = dialogView.findViewById(R.id.et_song_artist);
        EditText etAlbum = dialogView.findViewById(R.id.et_song_album);
        EditText etPath = dialogView.findViewById(R.id.et_song_path);
        TextView tvHeader = dialogView.findViewById(R.id.tv_dialog_title);

        if (selectedSong != null) {
            tvHeader.setText("Sửa bài hát Firebase");
            etTitle.setText(selectedSong.getTitle());
            etArtist.setText(selectedSong.getArtist());
            etAlbum.setText(selectedSong.getAlbum());
            etPath.setText(selectedSong.getFileUrl());
        }

        AlertDialog dialog = builder.create();

        dialogView.findViewById(R.id.btn_save).setOnClickListener(v -> {
            String title = etTitle.getText().toString().trim();
            String artist = etArtist.getText().toString().trim();
            String album = etAlbum.getText().toString().trim();
            String url = etPath.getText().toString().trim();


            if (title.isEmpty() || url.isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng nhập Tên và Link nhạc", Toast.LENGTH_SHORT).show();
                return;
            }

            if (selectedSong == null) {
                // THÊM MỚI: Tạo một key tự động (ID)
                String idStr = String.valueOf(System.currentTimeMillis());
                String id = String.valueOf(System.currentTimeMillis());
                MusicFiles newSong = new MusicFiles(Long.parseLong(id), title, artist, album, url, 0L);
                mDatabase.child(id).setValue(newSong);
                Toast.makeText(getContext(), "Đã thêm vào Firebase", Toast.LENGTH_SHORT).show();
            } else {
                // CẬP NHẬT
                selectedSong.setTitle(title);
                selectedSong.setArtist(artist);
                selectedSong.setAlbum(album);
                selectedSong.setFileUrl(url);
                mDatabase.child(String.valueOf(selectedSong.getId())).setValue(selectedSong);
                Toast.makeText(getContext(), "Đã cập nhật Firebase", Toast.LENGTH_SHORT).show();
            }
            dialog.dismiss();
        });
        dialog.show();
    }

    @Override
    public void onDeleteClick(int position) {
        MusicFiles song = songList.get(position);
        // Xóa khỏi Firebase theo ID
        mDatabase.child(String.valueOf(song.getId())).removeValue()
                .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Đã xóa khỏi Firebase", Toast.LENGTH_SHORT).show());
    }

    @Override public void onEditClick(int position) {
        showAddEditDialog(songList.get(position));
    }
}