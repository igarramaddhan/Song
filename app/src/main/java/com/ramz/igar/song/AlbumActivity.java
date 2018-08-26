package com.ramz.igar.song;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class AlbumActivity extends AppCompatActivity {

    BottomSheetBehavior bottomSheetBehavior;
    private TextView songTitle;
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
    private ArrayList<Song> songs = GlobalStateClass.getInstance().getPlaylist();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);

        songTitle = findViewById(R.id.song_title);
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        playButton = findViewById(R.id.play_button);
        ImageButton stopButton = findViewById(R.id.stop_button);
        ImageButton nextButton = findViewById(R.id.next_button);
        ImageButton prevButton = findViewById(R.id.prev_button);
        View bottomSheet = findViewById(R.id.bottom_sheet);
        ImageView imageView = findViewById(R.id.expandedImage);
        CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        String albumTitle = getIntent().getStringExtra("albumTitle");
        String albumArt = getIntent().getStringExtra("albumArt");
        collapsingToolbarLayout.setTitle(albumTitle);
        collapsingToolbarLayout.setCollapsedTitleTextColor(ContextCompat.getColor(getApplicationContext(),android.R.color.white));
        collapsingToolbarLayout.setExpandedTitleColor(ContextCompat.getColor(getApplicationContext(), android.R.color.transparent));
        Bitmap coverBitmap = BitmapFactory.decodeFile(albumArt);
        imageView.setImageBitmap(coverBitmap);
        List<Song> playlist = GlobalStateClass.getInstance().getAlbumSongs(albumTitle);
        GlobalStateClass.getInstance().setPlaylist(playlist);
        player = GlobalStateClass.getInstance().getPlayer();
        playButton.setImageResource(R.drawable.ic_pause);

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
        boolean isPlaying = player.isPlaying();
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setState(isPlaying ? BottomSheetBehavior.STATE_COLLAPSED : BottomSheetBehavior.STATE_HIDDEN);
        if (isPlaying) {
            songTitle.setText(player.getCurrentSong().getTitle());
        }

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

    void startPlay(int position) {
        songAdapter.update();
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

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
