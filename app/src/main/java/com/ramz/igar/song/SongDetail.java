package com.ramz.igar.song;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

public class SongDetail extends AppCompatActivity {

    private Player player;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_detail);

        player = (Player) getIntent().getSerializableExtra("player");
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getIntent().getExtras() != null)
            toolbar.setTitle(player.getCurrentSong().getTitle());
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
