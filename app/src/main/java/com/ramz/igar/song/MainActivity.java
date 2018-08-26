package com.ramz.igar.song;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    BottomSheetBehavior bottomSheetBehavior;
    private TextView status, songTitle;
    private GridView gridView;
    private AlbumAdapter albumAdapter;
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
    private ArrayList<Song> playlist = GlobalStateClass.getInstance().getPlaylist();
    private HashMap<String, Album> albums = GlobalStateClass.getInstance().getAlbums();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        status = findViewById(R.id.status);
        songTitle = findViewById(R.id.song_title);
        gridView = findViewById(R.id.grid_view);
        playButton = findViewById(R.id.play_button);
        ImageButton stopButton = findViewById(R.id.stop_button);
        ImageButton nextButton = findViewById(R.id.next_button);
        ImageButton prevButton = findViewById(R.id.prev_button);
        View bottomSheet = findViewById(R.id.bottom_sheet);

        player = GlobalStateClass.getInstance().getPlayer();

        albumAdapter = new AlbumAdapter(getApplicationContext(), albums);
        gridView.setAdapter(albumAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), AlbumActivity.class);
                ArrayList<String> tempTitle = new ArrayList<>(albums.keySet());
                ArrayList<Album> tempAlbums = new ArrayList<>(albums.values());
                intent.putExtra("albumTitle", tempTitle.get(position));
                intent.putExtra("albumArt", tempAlbums.get(position).getAlbumCover());
                Log.d("TITLE", tempTitle.get(position));
                startActivityForResult(intent, 1);
            }
        });


        playButton.setOnClickListener(onClickListener);
        stopButton.setOnClickListener(onClickListener);
        nextButton.setOnClickListener(onClickListener);
        prevButton.setOnClickListener(onClickListener);

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        } else {
            status.setText(getResources().getString(R.string.already_granted));
            if (GlobalStateClass.getInstance().getAlbums().isEmpty())
                getSongList(getApplicationContext());
            status.setVisibility(View.GONE);
            gridView.setVisibility(View.VISIBLE);
        }

        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);

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

            ArrayList<Song> songs = new ArrayList<>();
            //add songs to list
            do {
                long thisId = musicCursor.getLong(idColumn);
                String thisTitle = musicCursor.getString(titleColumn);
                String thisArtist = musicCursor.getString(artistColumn);
                String thisAlbum = musicCursor.getString(albumColumn);
                long thisDuration = musicCursor.getLong(durationColumn);
                String thisPath = musicCursor.getString(pathColumn);

                String albumArt = "";
                Cursor cursor = getContentResolver().query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                        new String[]{MediaStore.Audio.Albums._ID, MediaStore.Audio.Albums.ALBUM_ART},
                        MediaStore.Audio.Albums._ID + "=?",
                        new String[]{String.valueOf(thisId)},
                        null);

                if (cursor.moveToFirst()) {
                    albumArt = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART));
                }

                songs.add(new Song(thisId, thisTitle, thisArtist, thisAlbum, thisDuration, thisPath, albumArt));
                cursor.close();
            } while (musicCursor.moveToNext());

            for (Song song : songs) {
                String key = song.getAlbum();
                String albumCover = song.getAlbumArt();
                String artist = song.getArtist();
                if (!albums.containsKey(key)) {
                    albums.put(key, new Album(key, albumCover, artist));
                    Album album = albums.get(key);
                    album.addSong(song);
                } else {
                    Album album = albums.get(key);
                    album.addSong(song);
                }
            }


            albumAdapter.update(albums);
            musicCursor.close();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1: {
                Log.d("PERMISSION", "" + grantResults.length);
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    status.setText(getResources().getString(R.string.granted));
                    if (GlobalStateClass.getInstance().getAlbums().isEmpty())
                        getSongList(getApplicationContext());
                    status.setVisibility(View.GONE);
                    gridView.setVisibility(View.VISIBLE);
                } else {
                    status.setText(getResources().getString(R.string.denied));
                }
                break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        boolean isPlaying = player.isPlaying();
        bottomSheetBehavior.setState(isPlaying ? BottomSheetBehavior.STATE_COLLAPSED : BottomSheetBehavior.STATE_HIDDEN);
        if (isPlaying) {
            playButton.setImageResource(R.drawable.ic_pause);
            songTitle.setText(player.getCurrentSong().getTitle());
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
