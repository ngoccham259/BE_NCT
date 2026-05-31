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
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SongAdminFragment extends Fragment implements AdminMusicAdapter.OnSongClickListener {

    private RecyclerView recyclerView;
    private AdminMusicAdapter adapter;
    private ArrayList<MusicFiles> songList = new ArrayList<>();
    private FloatingActionButton fabAdd;
    private ApiService apiService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_song_admin, container, false);

        recyclerView = view.findViewById(R.id.rv_admin_songs);
        fabAdd = view.findViewById(R.id.fab_add_song);
        apiService = RetrofitClient.getApiService();

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Gọi dữ liệu từ Server
        refreshData();

        fabAdd.setOnClickListener(v -> showAddEditDialog(-1));

        return view;
    }

    private void refreshData() {
        apiService.getOnlineSongs().enqueue(new Callback<List<MusicFiles>>() {
            @Override
            public void onResponse(Call<List<MusicFiles>> call, Response<List<MusicFiles>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    songList = new ArrayList<>(response.body());
                    adapter = new AdminMusicAdapter(getContext(), songList, SongAdminFragment.this);
                    recyclerView.setAdapter(adapter);
                }
            }
            @Override
            public void onFailure(Call<List<MusicFiles>> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi tải nhạc từ Server", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showAddEditDialog(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_edit_song, null);
        builder.setView(dialogView);

        EditText etTitle = dialogView.findViewById(R.id.et_song_title);
        EditText etArtist = dialogView.findViewById(R.id.et_song_artist);
        EditText etAlbum = dialogView.findViewById(R.id.et_song_album);
        EditText etPath = dialogView.findViewById(R.id.et_song_path);
        TextView tvHeader = dialogView.findViewById(R.id.tv_dialog_title);

        if (position != -1) {
            tvHeader.setText("Sửa bài hát Online");
            MusicFiles song = songList.get(position);
            etTitle.setText(song.getTitle());
            etArtist.setText(song.getArtist());
            etAlbum.setText(song.getAlbum());
            etPath.setText(song.getFileUrl());
        }

        AlertDialog dialog = builder.create();

        dialogView.findViewById(R.id.btn_save).setOnClickListener(v -> {
            String title = etTitle.getText().toString().trim();
            String artist = etArtist.getText().toString().trim();
            String album = etAlbum.getText().toString().trim();
            String url = etPath.getText().toString().trim(); // Lấy từ ô nhập link

            if (title.isEmpty() || url.isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng nhập Tên và Link nhạc", Toast.LENGTH_SHORT).show();
                return;
            }

            if (position == -1) {
                // Gửi lên Spring Boot tạo bài mới
                MusicFiles newSong = new MusicFiles(url, title, artist, album, "0", "0", true);
                apiService.addSong(newSong).enqueue(new Callback<MusicFiles>() {
                    @Override
                    public void onResponse(Call<MusicFiles> call, Response<MusicFiles> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(getContext(), "Đã lưu vào MySQL", Toast.LENGTH_SHORT).show();
                            refreshData(); // Hàm gọi lại getAllSongs() để cập nhật danh sách
                        }
                    }
                    @Override public void onFailure(Call<MusicFiles> call, Throwable t) {}
                });
            } else {
                // Cập nhật bài cũ
                MusicFiles song = songList.get(position);
                song.setTitle(title);
                song.setArtist(artist);
                song.setAlbum(album);
                song.setFileUrl(url);

                apiService.updateSong(song.getId(), song).enqueue(new Callback<MusicFiles>() {
                    @Override
                    public void onResponse(Call<MusicFiles> call, Response<MusicFiles> response) {
                        if (response.isSuccessful()) {
                            adapter.notifyItemChanged(position);
                            Toast.makeText(getContext(), "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override public void onFailure(Call<MusicFiles> call, Throwable t) {}
                });
            }
            dialog.dismiss();
        });
        dialog.show();
    }

    @Override
    public void onDeleteClick(int position) {
        MusicFiles song = songList.get(position);
        apiService.deleteSong(song.getId()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                songList.remove(position);
                adapter.notifyItemRemoved(position);
                Toast.makeText(getContext(), "Đã xóa khỏi Cloud", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {}
        });
    }

    @Override public void onEditClick(int position) { showAddEditDialog(position); }
}