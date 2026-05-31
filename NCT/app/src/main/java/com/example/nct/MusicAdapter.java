package com.example.nct;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.util.ArrayList;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MyViewHolder> {

    private Context mContext;
    private ArrayList<MusicFiles> mfiles;
    private String sender;

    public MusicAdapter(Context mContext, ArrayList<MusicFiles> mfiles, String sender) {
        this.mContext = mContext;
        this.mfiles = mfiles;
        this.sender=sender;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView file_name;
        ImageView album_art, menuMore;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            file_name = itemView.findViewById(R.id.music_file_name);
            album_art = itemView.findViewById(R.id.music_img);
            menuMore = itemView.findViewById(R.id.menuMore);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.music_item, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        // 1. Hiển thị dữ liệu bài hát
        holder.file_name.setText(mfiles.get(position).getTitle());
        byte[] image = getAlbumArt(mfiles.get(position).getFileUrl());
        if (image != null) {
            Glide.with(mContext).asBitmap().load(image).into(holder.album_art);
        } else {
            Glide.with(mContext).load(R.drawable.nct_logo).into(holder.album_art);
        }

        // 2. Click để mở trình chơi nhạc
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, PlayerActivity.class);
                intent.putExtra("position", holder.getAdapterPosition());
                intent.putExtra("sender", sender);
                mContext.startActivity(intent);
            }
        });

        // 3. Click menu More (Xóa)
        holder.menuMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(mContext, v);
                popupMenu.getMenuInflater().inflate(R.menu.popup, popupMenu.getMenu());
                popupMenu.show();

                popupMenu.setOnMenuItemClickListener(item -> {
                    if (item.getItemId() == R.id.delete) {
                        int currentPos = holder.getAdapterPosition();
                        Toast.makeText(mContext, "Delete Click!!", Toast.LENGTH_LONG).show();
                        deleteFile(currentPos);
                        return true;
                    }
                    return false;
                });
            }
        });
    }

    private void deleteFile(int position) {

        Uri contentUri = ContentUris.withAppendedId(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                mfiles.get(position).getId()
        );

        if (mContext instanceof MainActivity) {
            ((MainActivity) mContext).deleteSong(contentUri, position);
        }
    }

    @Override
    public int getItemCount() {
        return mfiles.size();
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
    public void updateList(ArrayList<MusicFiles> newList) {
        mfiles = new ArrayList<>();
        mfiles.addAll(newList);
        notifyDataSetChanged();
    }
}