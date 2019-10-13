package com.example.musiced.screen.listsong;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.example.musiced.R;
import com.example.musiced.data.model.Song;
import com.example.musiced.screen.playmusic.PlayMusicFragment;
import com.example.musiced.utils.navigator.IOnClickListeners;
import com.example.musiced.utils.navigator.Navigator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class SongActivity extends AppCompatActivity implements IOnClickListeners<Song> {
    public static final String TAG = "MusicPersonFragment";
    public static final String ACTION = "com.example.musiced.screen.listsong.SongActivity";
    public static final int MY_PERMISSION_REQUEST = 1;
    public List<Song> mSongList;
    public Navigator navigator;
    RecyclerView recyclerView;
    private SongAdapter songAdapter;

    public static Intent getInstance(Context context) {
        Intent intent = new Intent(context, SongActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    public void setSongList(List<Song> songList) {
        this.mSongList = songList;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song);
        init();
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST);

            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST);
            }
        } else {
            getMusic();
        }
    }

    /*
    - Get music from extenal.
    - @Author: NvMinh
    */
    @SuppressLint("ShowToast")
    private void getMusic() {
        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                null,
                null,
                null,
                null
        );
        if (cursor == null) {
            Toast.makeText(this, "Something Went Wrong.", Toast.LENGTH_LONG);
        } else if (!cursor.moveToFirst()) {
            Toast.makeText(this, "No Music Found on SD Card.", Toast.LENGTH_LONG);
        } else {
            int ID = cursor.getColumnIndex(MediaStore.Audio.Media._ID);
            int Title = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int Artist = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int Name = cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME);
            int Data = cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DATA);
            int Duration = cursor.getColumnIndex(MediaStore.Audio.Media.DURATION);

            do {
                int Id = cursor.getInt(ID);
                String songTitle = cursor.getString(Title);
                String songArtist = cursor.getString(Artist);
                String songDisplay = cursor.getString(Name);
                String dataUri = cursor.getString(Data);
                int durations = cursor.getInt(Duration);
                mSongList.add(new Song(Id, songTitle, songArtist, dataUri, durations));
                songAdapter.setListSong(mSongList);
                recyclerView.setAdapter(songAdapter);
            } while (cursor.moveToNext());
        }

    }

    /*
    - KHởi tạo view
    - @Author: NvMinh
    */
    private void init() {
        navigator = new Navigator(SongActivity.this);
        mSongList = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerview_listsong);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        songAdapter = new SongAdapter(mSongList, this);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(Intent.ACTION_MAIN);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }


    @Override
    public void onClick(Song song, int pos) {
        PlayMusicFragment playMusicFragment = new PlayMusicFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putInt("position", pos);
        bundle.putParcelableArrayList("array", (ArrayList<? extends Parcelable>) mSongList);
        bundle.putString("title", song.getSongName());
        bundle.putString("artist", song.getSingerName());
        bundle.putString("path", song.getPathSong());
        bundle.putInt("duration", song.getDurationSong());
        playMusicFragment.setArguments(bundle);
        transaction.add(R.id.content_list_song, playMusicFragment);
        transaction.addToBackStack(null);
        transaction.setCustomAnimations(R.anim.slide_bottom_in, R.anim.slide_bottom_in);
        transaction.commit();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
                        getMusic();
                    }
                } else {
                    Toast.makeText(this, " No Permission granted", Toast.LENGTH_SHORT).show();

                }
                return;
            }
        }
    }


}
