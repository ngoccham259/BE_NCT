package com.example.nct;

import static com.example.nct.AlbumDetailsAdapter.albumFiles;
import static com.example.nct.MainActivity.musicFiles;
import static com.example.nct.MainActivity.repeatBoolean;
import static com.example.nct.MainActivity.shuffleBoolean;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.palette.graphics.Palette;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Random;

public class PlayerActivity extends AppCompatActivity {
    TextView song_name, artist_name, duration_played, duration_total;
    FloatingActionButton playPauseBtn;
    SeekBar seekBar;
    ImageView cover_art, nextBtn, backBtn, ShufflerBtn, repeatBtn;
    int position= -1;
    static ArrayList<MusicFiles> listSongs = new ArrayList<>();
    static Uri uri;
    static MediaPlayer mediaPlayer;
    private Handler handler = new Handler();
    private  Thread playThread, prevThread, nextThread;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_player);
        initViews();
        getIntentMethod();
       song_name.setText(listSongs.get(position).getTitle());
       artist_name.setText(listSongs.get(position).getArtist());
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(mediaPlayer != null&& fromUser){
                    mediaPlayer.seekTo(progress*1000);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        PlayerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(mediaPlayer!=null){
                    int mCurrentPosition = mediaPlayer.getCurrentPosition()/1000;
                    seekBar.setProgress(mCurrentPosition);
                    duration_played.setText(formattedTime(mCurrentPosition));
                }
                handler.postDelayed(this, 1000);

            }
        });
        ShufflerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(shuffleBoolean){
                    shuffleBoolean= false;
                    ShufflerBtn.setImageResource(R.drawable.ic_shuffle_off);
                }
                else {
                    shuffleBoolean= true;
                    ShufflerBtn.setImageResource(R.drawable.ic_shuffle_on);
                }
            }
        });
        repeatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(repeatBoolean){
                    repeatBoolean= false;
                    repeatBtn.setImageResource(R.drawable.repeat_off);
                }
                else {
                    repeatBoolean= true;
                    repeatBtn.setImageResource(R.drawable.repeat_on);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        playThreadBtn();
        nextThreadBtn();
        prevThreadBtn();
        super.onResume();
    }

    private void playThreadBtn() {
        playThread = new Thread() {
            @Override
            public void run() {
                super.run();
                playPauseBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        playPauseBtnClicked();
                    }
                });
            }
        };
        playThread.start();
    }

    private void playPauseBtnClicked() {
        if (mediaPlayer.isPlaying()) {
            playPauseBtn.setImageResource(R.drawable.ic_play_arrow);
            mediaPlayer.pause();
        } else {
            playPauseBtn.setImageResource(R.drawable.ic_pause);
            mediaPlayer.start();
        }
    }


    private void nextThreadBtn() {
        nextThread = new Thread() {
            @Override
            public void run() {
                super.run();
                nextBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        nextBtnClicked();
                    }
                });
            }
        };
        nextThread.start();
    }

    private void nextBtnClicked() {

        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }

        if (repeatBoolean) {

            // giữ nguyên bài

        } else if (shuffleBoolean) {

            position = getRandom(listSongs.size() - 1);

        } else {

            position = (position + 1) % listSongs.size();
        }

        updatePlayer();
    }

    private int getRandom(int i) {
        Random random = new Random();

        return random.nextInt(i+1);
    }

    private void updatePlayer() {
        uri = Uri.parse(listSongs.get(position).getFileUrl());
        mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);

        song_name.setText(listSongs.get(position).getTitle());
        artist_name.setText(listSongs.get(position).getArtist());
        seekBar.setMax(mediaPlayer.getDuration() / 1000);
        MetaData(uri);

        playPauseBtn.setImageResource(R.drawable.ic_pause);
        mediaPlayer.start();
        mediaPlayer.setOnCompletionListener(mp -> nextBtnClicked());
    }

    private void prevThreadBtn() {
        prevThread = new Thread() {
            @Override
            public void run() {
                super.run();
                backBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        prevBtnClicked();
                    }
                });
            }
        };
        prevThread.start();
    }

    private void prevBtnClicked() {

        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }

        if (repeatBoolean) {

            // giữ nguyên bài

        } else if (shuffleBoolean) {

            position = getRandom(listSongs.size() - 1);

        } else {

            position =
                    ((position - 1) < 0)
                            ? (listSongs.size() - 1)
                            : (position - 1);
        }

        updatePlayer();
    }

    private String formattedTime(int mCurrentPosition) {
        String totalout = "";
        String totalNew = "";
        String seconds = String.valueOf(mCurrentPosition%60);
        String minutes = String.valueOf(mCurrentPosition/60);
        totalout = minutes+ ":"+seconds;
        totalNew = minutes+ ":"+"0"+seconds;
        if (seconds.length()==1){
            return totalNew;
        }else {
            return totalout;
        }
    }

    /*private void getIntentMethod() {
        position = getIntent().getIntExtra("position", -1);
        String sender = getIntent().getStringExtra("sender");
        if (sender!=null&&sender.equals("albumDetails"))
        {
            listSongs =albumFiles;

        }
        else {
            listSongs = musicFiles;
        }

        if(listSongs !=null)
        {
            playPauseBtn.setImageResource(R.drawable.ic_pause);
            uri = Uri.parse(listSongs.get(position).getPath());
        }
        if (mediaPlayer!=null){
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = MediaPlayer.create(getApplicationContext(),uri);
            mediaPlayer.start();
        }else {
            mediaPlayer= MediaPlayer.create(getApplicationContext(), uri);
            mediaPlayer.start();
        }
        seekBar.setMax(mediaPlayer.getDuration()/1000);
        MetaData(uri);
    }*/
    private void getIntentMethod() {
        position = getIntent().getIntExtra("position", -1);
        String sender = getIntent().getStringExtra("sender");

        if (sender != null && sender.equals("albumDetails")) {
            listSongs = albumFiles;
        }
        else if (sender != null && sender.equals("online")) {
            listSongs = MainActivity.onlineMusicFiles;
        }
        else {
            listSongs = musicFiles;
        }

        if (listSongs != null && position != -1 && position < listSongs.size()) {
            String path = listSongs.get(position).getFileUrl();
            if (path == null) {
                Toast.makeText(this, "Đường dẫn nhạc bị trống!", Toast.LENGTH_SHORT).show();
                return;
            }
            uri = Uri.parse(path);

            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
            }

            try {

                mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);

                if (mediaPlayer != null) {
                    mediaPlayer.start();

                    seekBar.setMax(mediaPlayer.getDuration() / 1000);
                } else {

                    Toast.makeText(this, "Không thể tải bài hát. Vui lòng kiểm tra kết nối mạng.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Lỗi khi phát bài hát: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        // 4. Cập nhật giao diện (Tên bài, ca sĩ, ảnh bìa)
        if (uri != null) {
            MetaData(uri);
        }
    }

    private void initViews(){
        song_name = findViewById(R.id.song_name);
        artist_name = findViewById(R.id.song_artist);
        duration_played = findViewById(R.id.durationPlayed);
        duration_total = findViewById(R.id.durationTotal);
        cover_art =  findViewById(R.id.cover_art);
        nextBtn = findViewById(R.id.id_next);
        backBtn = findViewById(R.id.id_prev);
        ShufflerBtn = findViewById(R.id.id_shuffer);
        repeatBtn = findViewById(R.id.id_repeat);
        playPauseBtn = findViewById(R.id.play_pause);
        seekBar = findViewById(R.id.seekBar);


    }

    private void MetaData(Uri uri) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(this, uri);

            byte[] art = retriever.getEmbeddedPicture();
            Bitmap bitmap;

            if (art != null) {
                Glide.with(this).asBitmap().load(art).into(cover_art);
                bitmap = BitmapFactory.decodeByteArray(art, 0, art.length);
                Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                    @Override
                    public void onGenerated(@Nullable Palette palette) {
                        Palette.Swatch swatch = palette.getDominantSwatch();
                        if (swatch != null) {
                            ImageView gredient = findViewById(R.id.imgViewGredient);
                            RelativeLayout mContainer = findViewById(R.id.mContainer);
                            gredient.setBackgroundResource(R.drawable.gredient_bg);
                            mContainer.setBackgroundResource(R.drawable.main_bg);
                            GradientDrawable gradientDrawable = new GradientDrawable(
                                    GradientDrawable.Orientation.BOTTOM_TOP,
                                    new int[]{swatch.getRgb(), 0x00000000});
                            gredient.setBackground(gradientDrawable);
                            GradientDrawable gradientDrawableBg = new GradientDrawable(
                                    GradientDrawable.Orientation.BOTTOM_TOP,
                                    new int[]{swatch.getRgb(), swatch.getRgb()});
                            mContainer.setBackground(gradientDrawableBg);


                            song_name.setTextColor(swatch.getTitleTextColor());
                            artist_name.setTextColor(swatch.getBodyTextColor());
                        }
                    }
                });
            } else {
                Glide.with(this).asBitmap().load(R.drawable.nct_logo).into(cover_art);

                ImageView gredient = findViewById(R.id.imgViewGredient);
                RelativeLayout mContainer = findViewById(R.id.mContainer);

                gredient.setBackgroundResource(R.drawable.gredient_bg);
                mContainer.setBackgroundResource(R.drawable.main_bg);

                song_name.setTextColor(Color.WHITE);
                artist_name.setTextColor(Color.GRAY);
            }
            retriever.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}