package com.ramz.igar.song;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    BottomSheetBehavior bottomSheetBehavior;
    private TextView status, songTitle;
    private RecyclerView recyclerView;
    private SongAdapter songAdapter;
    private Player player;
    private ImageButton playButton;
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.play_button: {
                    if (player.isPlaying()) {
                        pause();
                    } else if (!player.isPlaying()) {
                        play();
                    }
                    break;
                }
                case R.id.stop_button: {
                    stop();
                    break;
                }
                case R.id.next_button: {
                    next();
                    break;
                }
                case R.id.prev_button: {
                    prev();
                    break;
                }
            }

        }
    };
    private ArrayList<Song> songs = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        status = findViewById(R.id.status);
        songTitle = findViewById(R.id.song_title);
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        playButton = findViewById(R.id.play_button);
        ImageButton stopButton = findViewById(R.id.stop_button);
        ImageButton nextButton = findViewById(R.id.next_button);
        ImageButton prevButton = findViewById(R.id.prev_button);
        View bottomSheet = findViewById(R.id.bottom_sheet);

        player = new Player(songs);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);

        songAdapter = new SongAdapter(songs);
        recyclerView.setAdapter(songAdapter);

        playButton.setOnClickListener(onClickListener);
        stopButton.setOnClickListener(onClickListener);
        nextButton.setOnClickListener(onClickListener);
        prevButton.setOnClickListener(onClickListener);

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                startPlay(position);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        } else {
            status.setText(getResources().getString(R.string.already_granted));
            getSongList(getApplicationContext());
            status.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }

        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);

//        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
//        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        bottomSheetBehavior.setPeekHeight(bottomSheetBehavior.getPeekHeight());
        bottomSheetBehavior.setHideable(true);
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    stop();
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
    }

    public void getSongList(Context context) {
        ContentResolver musicResolver = context.getContentResolver();
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri, null, MediaStore.Audio.Media.IS_MUSIC + " != 0", null, null);

        if (musicCursor != null && musicCursor.moveToFirst()) {
            //get columns
            int titleColumn = musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);
            int artistColumn = musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media.ARTIST);
            int albumColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
            int durationColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
            int pathColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.DATA);

            //add songs to list
            do {
                long thisId = musicCursor.getLong(idColumn);
                String thisTitle = musicCursor.getString(titleColumn);
                String thisArtist = musicCursor.getString(artistColumn);
                String thisAlbum = musicCursor.getString(albumColumn);
                long thisDuration = musicCursor.getLong(durationColumn);
                String thisPath = musicCursor.getString(pathColumn);
                songs.add(new Song(thisId, thisTitle, thisArtist, thisAlbum, thisDuration, thisPath));
            } while (musicCursor.moveToNext());

            songAdapter.update();
            musicCursor.close();
            //get JSON data
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1: {
                Log.d("PERMISSION", "" + grantResults.length);
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    status.setText(getResources().getString(R.string.granted));
                    getSongList(getApplicationContext());
                    status.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                } else {
                    status.setText(getResources().getString(R.string.denied));
                }
                break;
            }
        }
    }

    void startPlay(int position) {
        player.startPlay(position);
        songTitle.setText(player.getCurrentSong().getTitle());
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        playButton.setImageResource(R.drawable.ic_pause);
    }

    void play() {
        player.play();
        songTitle.setText(player.getCurrentSong().getTitle());
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        playButton.setImageResource(R.drawable.ic_pause);
    }

    void pause() {
        player.pause();
        playButton.setImageResource(R.drawable.ic_play);
    }

    void stop() {
        player.stop();
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        playButton.setImageResource(R.drawable.ic_play);
    }

    void next() {
        player.next();
        songTitle.setText(player.getCurrentSong().getTitle());
        playButton.setImageResource(R.drawable.ic_pause);
    }

    void prev() {
        player.prev();
        songTitle.setText(player.getCurrentSong().getTitle());
        playButton.setImageResource(R.drawable.ic_pause);
    }

}
