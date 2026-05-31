package com.example.nct;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class AlbumDetailsAdapter extends RecyclerView.Adapter<AlbumDetailsAdapter.MyHolder> {
    private Context mContext;
    static ArrayList<MusicFiles> albumFiles;
    View view;

    public AlbumDetailsAdapter(Context mContext, ArrayList<MusicFiles> albumFiles) {
        this.mContext = mContext;
        this.albumFiles = albumFiles;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(mContext).inflate(R.layout.music_item, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, @SuppressLint("RecyclerView") int position) {
        MusicFiles songInAlbum = albumFiles.get(position);

        // Hiển thị tên bài hát
        holder.album_name.setText(songInAlbum.getTitle());

        // Tải ảnh bìa (vì trong cùng album nên ảnh thường giống nhau)
        Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
        Uri albumArtUri = android.content.ContentUris.withAppendedId(sArtworkUri, songInAlbum.getAlbumId());

        Glide.with(mContext)
                .load(albumArtUri)
                .placeholder(R.drawable.nct_logo)
                .error(R.drawable.nct_logo)
                .centerCrop()
                .into(holder.album_image);

        // Click để phát nhạc
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, PlayerActivity.class);
            intent.putExtra("sender", "albumDetails");
            intent.putExtra("position", position);
            mContext.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return albumFiles.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        ImageView album_image;
        TextView album_name;
        public MyHolder(@NonNull View itemView) {
            super(itemView);
            album_image = itemView.findViewById(R.id.music_img);
            album_name = itemView.findViewById(R.id.music_file_name);
        }
    }

    private byte[] getAlbumArt(String uri) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(uri);
            byte[] art = retriever.getEmbeddedPicture();
            return art;
        } catch (Exception e) {
            return null;
        } finally {
            try {
                retriever.release();
            } catch (java.io.IOException e) {
                e.printStackTrace();
            }
        }
    }
}
