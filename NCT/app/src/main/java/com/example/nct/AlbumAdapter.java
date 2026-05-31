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

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.MyHolder> {
    private Context mContext;
    private ArrayList<MusicFiles> albumFiles;
    View view;

    public AlbumAdapter(Context mContext, ArrayList<MusicFiles> albumFiles) {
        this.mContext = mContext;
        this.albumFiles = albumFiles;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(mContext).inflate(R.layout.album_item, parent, false);
        return new MyHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        MusicFiles albumFile = albumFiles.get(position);
        holder.album_name.setText(albumFile.getAlbum());

        if (albumFile.getAlbum().equals("Yêu thích")) {
            Glide.with(mContext)
                    .load(R.drawable.favorite)
                    .into(holder.album_image);
        } else {

            Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
            Uri albumArtUri = android.content.ContentUris.withAppendedId(sArtworkUri, albumFile.getAlbumId());

            Glide.with(mContext)
                    .load(albumArtUri)
                    .placeholder(R.drawable.nct_logo)
                    .into(holder.album_image);
        }

        // Sự kiện click giữ nguyên để mở sang AlbumDetails
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, AlbumDetails.class);
            intent.putExtra("albumName", albumFile.getAlbum());
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
            album_image = itemView.findViewById(R.id.album_img);
            album_name = itemView.findViewById(R.id.album_name);
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
