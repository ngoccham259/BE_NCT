package com.example.nct;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import android.widget.Toast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;


import com.google.android.material.tabs.TabLayout;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    public static  final int REQUESt_CODE=1;
    static ArrayList<MusicFiles> musicFiles;
    static  boolean shuffleBoolean = false, repeatBoolean = false;
    private Uri deleteUri;
    private int deletePosition;
    static MusicAdapter musicAdapter;
    static ArrayList<MusicFiles> albums=new ArrayList<>();
    public static ArrayList<MusicFiles> onlineMusicFiles = new ArrayList<>();
    private String MY_SORT_PREF = "SortOrder";
    public static User currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        permission();
        loadOnlineSongs();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    private void permission() {

        String permission;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permission = Manifest.permission.READ_MEDIA_AUDIO;
        } else {
            permission = Manifest.permission.READ_EXTERNAL_STORAGE;
        }

        if (ContextCompat.checkSelfPermission(this, permission)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{permission},
                    REQUESt_CODE
            );

        } else {

            musicFiles = getAllAudio(this);
            initViewPager();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUESt_CODE){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                musicFiles = getAllAudio(this);
                initViewPager();
            }else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUESt_CODE);
            }
        }
    }

    private void initViewPager(){
        ViewPager viewPager = findViewById(R.id.viewpape);
        TabLayout tabLayout  = findViewById((R.id.tab_layout));
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragments(new OnlineSongsFragment(), "Online");
        viewPagerAdapter.addFragments(new SongsFragment(), "Offline");
        viewPagerAdapter.addFragments(new AlbumFragment(), "Albums");
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

    }


    public static class ViewPagerAdapter extends FragmentPagerAdapter {

        private ArrayList <Fragment> fragments;
        private ArrayList <String> titles;

        public ViewPagerAdapter(@NonNull FragmentManager fragmentActivity) {
            super(fragmentActivity);
            this.fragments = new ArrayList<>();
            this.titles = new ArrayList<>();
        }
        void  addFragments(Fragment fragment,String tittle){
            this.fragments.add(fragment);
            this.titles.add(tittle);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }
    public ArrayList<MusicFiles> getAllAudio(Context context) {

        SharedPreferences preferences =
                getSharedPreferences(MY_SORT_PREF, MODE_PRIVATE);

        String sortOrder =
                preferences.getString("sorting", "sortByName");

        ArrayList<String> duplicate = new ArrayList<>();
        albums.clear();

        ArrayList<MusicFiles> tempAudioList = new ArrayList<>();

        String order = null;

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        if (sortOrder.equals("sortByName")) {
            order = MediaStore.MediaColumns.DISPLAY_NAME + " ASC";
        }
        else if (sortOrder.equals("sortByDate")) {
            order = MediaStore.MediaColumns.DATE_ADDED + " DESC";
        }
        else if (sortOrder.equals("sortBySize")) {
            order = MediaStore.MediaColumns.SIZE + " DESC";
        }

        String[] projection = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.ALBUM_ID
        };

        Cursor cursor = context.getContentResolver().query(
                uri,
                projection,
                null,
                null,
                order
        );

        if (cursor != null) {

            while (cursor.moveToNext()) {

                Long id = cursor.getLong(0);

                String title = cursor.getString(1);

                String artist = cursor.getString(2);

                String album = cursor.getString(3);

                String path = cursor.getString(4);

                Long albumId = cursor.getLong(5);

                MusicFiles music = new MusicFiles();

                music.setId(id);

                music.setTitle(title);

                music.setArtist(artist);

                music.setAlbum(album);

                // OFFLINE dùng PATH
                music.setPath(path);

                music.setAlbumId(albumId);

                tempAudioList.add(music);

                if (!duplicate.contains(album)) {

                    albums.add(music);

                    duplicate.add(album);
                }

                Log.e("SONG", title + " - " + artist);
            }

            cursor.close();
            MusicFiles favoriteAlbum = new MusicFiles();

            favoriteAlbum.setAlbum("Yêu thích");

            favoriteAlbum.setTitle("Yêu thích");

            favoriteAlbum.setArtist("Của bạn");

            favoriteAlbum.setPath("firebase");

            boolean isExist = false;
            for (MusicFiles f : albums) {
                if ("Yêu thích".equals(f.getAlbum())) {
                    isExist = true;
                    break;
                }
            }
            if (!isExist) {
                albums.add(favoriteAlbum);
            }
        }

        return tempAudioList;
    }
//    private final ActivityResultLauncher<IntentSenderRequest> deleteLauncher =
//            registerForActivityResult(
//                    new StartIntentSenderForResult(),
//                    result -> {
//                        if (result.getResultCode() == RESULT_OK) {
//
//                            if (musicFiles != null && musicAdapter != null) {
//                                musicFiles.remove(deletePosition);
//                                musicAdapter.notifyItemRemoved(deletePosition);
//                                musicAdapter.notifyItemRangeChanged(deletePosition, musicFiles.size());
//                                Toast.makeText(this, "Đã xóa bài hát", Toast.LENGTH_SHORT).show();
//                            }
//                        } else {
//                            Toast.makeText(this, "Đã hủy xóa", Toast.LENGTH_SHORT).show();
//                        }
//                    });

    public void deleteSong(Uri uri, int position) {
        deleteUri = uri;
        deletePosition = position;
        try {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {

                PendingIntent pendingIntent =
                        MediaStore.createDeleteRequest(
                                getContentResolver(),
                                java.util.Collections.singletonList(uri)
                        );
                startIntentSenderForResult(
                        pendingIntent.getIntentSender(), 123, null, 0, 0, 0, null
                );
            } else {

                getContentResolver().delete(uri, null, null);

                if (musicAdapter != null) {
                    musicFiles.remove(position);
                    musicAdapter.notifyItemRemoved(position);
                    musicAdapter.notifyItemRangeChanged(
                            position,
                            musicFiles.size()
                    );
                }

                Toast.makeText(this,
                        "Đã xóa bài hát",
                        Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this,
                    "Lỗi: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 123) {
            if (resultCode == RESULT_OK) {
                if (musicFiles != null
                        && musicAdapter != null) {
                    musicFiles.remove(deletePosition);
                    musicAdapter.notifyItemRemoved(deletePosition);
                    musicAdapter.notifyItemRangeChanged(deletePosition, musicFiles.size()
                    );
                    Toast.makeText(this,
                            "Đã xóa bài hát",
                            Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this,
                        "Đã hủy xóa",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search, menu);
        MenuItem menuItem = menu.findItem(R.id.search_option);

        androidx.appcompat.widget.SearchView searchView = (androidx.appcompat.widget.SearchView) menuItem.getActionView();

        searchView.setOnQueryTextListener(this);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        String useInput=newText.toLowerCase();
        ArrayList<MusicFiles>myFiles = new ArrayList<>();
        if (musicFiles != null) {
            for (MusicFiles song : musicFiles) {
                if (song.getTitle().toLowerCase().contains(useInput)) {
                    myFiles.add(song);
                }
            }
        }
        if (musicAdapter != null) {
            musicAdapter.updateList(myFiles);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        SharedPreferences.Editor editor = getSharedPreferences(MY_SORT_PREF,MODE_PRIVATE).edit();
        int id = item.getItemId();

        if (id == R.id.admin_panel) {
            Intent intent = new Intent(MainActivity.this, DangNhapActivity.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.by_name) {
            editor.putString("sorting", "sortByName");
            editor.apply();
            this.recreate();
        } else if (id == R.id.by_date) {
            editor.putString("sorting", "sortByDate");
            editor.apply();
            this.recreate();
        } else if (id == R.id.by_size) {
            editor.putString("sorting", "sortBySize");
            editor.apply();
            this.recreate();
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadOnlineSongs() {

        Log.d("FIREBASE", "loadOnlineSongs called");

        DatabaseReference ref =
                FirebaseDatabase.getInstance()
                        .getReference("songs");

        Log.d("FIREBASE", "ref = " + ref);

        ref.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Log.d("FIREBASE", "onDataChange: " + snapshot.getChildrenCount());

                onlineMusicFiles.clear();

                for (DataSnapshot data : snapshot.getChildren()) {

                    MusicFiles music =
                            data.getValue(MusicFiles.class);

                    if (music != null) {

                        Log.d("FIREBASE",
                                "Song: " + music.getTitle());

                        onlineMusicFiles.add(music);
                    }
                }

                Log.d("FIREBASE", "Total = " + onlineMusicFiles.size());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Log.e("FIREBASE",
                        error.getMessage());
            }
        });
    }
}