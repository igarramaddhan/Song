package com.ramz.igar.song;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;
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

    private TextView status, songTitle;
    private RecyclerView recyclerView;
    private SongAdapter songAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private Player player = new Player();
    private View playerView;
    private ImageButton playButton, stopButton;

    private ArrayList<Song> songs = new ArrayList<>();
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.play_button: {
                    if (player.isPlaying()) {
                        pause();
                    } else if (!player.isPlaying()) {
                        Song song = songs.get(0);
                        play(song);
                    }
                    break;
                }
                case R.id.stop_button: {
                    stop();
                    break;
                }
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        status = findViewById(R.id.status);
        songTitle = findViewById(R.id.song_title);
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        playerView = findViewById(R.id.player_view);
        playButton = findViewById(R.id.play_button);
        stopButton = findViewById(R.id.stop_button);

        layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);

        songAdapter = new SongAdapter(songs);
        recyclerView.setAdapter(songAdapter);

        playButton.setOnClickListener(onClickListener);
        stopButton.setOnClickListener(onClickListener);

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Song song = songs.get(position);
                player.setCurrentSong(null);
                play(song);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        } else {
            status.setText("PERMISSION ALREADY GRANTED");
            getSongList(getApplicationContext());
            status.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
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
            //get JSON data
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                Log.d("PERMISSION", "" + grantResults.length);
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    status.setText("PERMISSION GRANTED");
                    getSongList(getApplicationContext());
                    status.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                } else {
                    status.setText("PERMISSION DENIED");
                }
                return;
            }
        }
    }

    void play(Song song) {
        Song currentSong = song;
        if(player.getCurrentSong() != null){
            currentSong = player.getCurrentSong();
        }
        songTitle.setText(currentSong.getTitle());
        Log.d("SONG TITLE", currentSong.getTitle());
        player.play(currentSong);
        playerView.setVisibility(View.VISIBLE);
        playButton.setImageResource(R.drawable.ic_pause);
        stopButton.setVisibility(View.VISIBLE);
    }

    void pause() {
        player.pause();
        playButton.setImageResource(R.drawable.ic_play);
        stopButton.setVisibility(View.GONE);
    }

    void stop() {
        player.stop();
        playerView.setVisibility(View.GONE);
        playButton.setImageResource(R.drawable.ic_play);
        stopButton.setVisibility(View.GONE);
    }

}
