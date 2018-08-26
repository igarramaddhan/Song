package com.ramz.igar.song;

import java.util.ArrayList;
import java.util.List;

public class Album {
    private String albumTitle;
    private String artist;
    private String albumCover;
    private List<Song> songs;

    Album(String albumTitle, String albumCover, String artist) {
        this.albumTitle = albumTitle;
        this.albumCover = albumCover;
        this.artist = artist;
        this.songs = new ArrayList<>();
    }

    public void addSong(Song song) {
        songs.add(song);
    }

    public List<Song> getSongs() {
        return songs;
    }

    public String getAlbumTitle() {
        return albumTitle;
    }

    public String getAlbumCover() {
        return albumCover;
    }

    public String getArtist() { return artist; }
}
