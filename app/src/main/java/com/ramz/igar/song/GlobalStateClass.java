package com.ramz.igar.song;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GlobalStateClass {
    private static GlobalStateClass instance;

    public static GlobalStateClass getInstance(){
        if(instance==null){
            instance = new GlobalStateClass();
        }
        return instance;
    }

    private Player player;
    private ArrayList<Song> playlist = new ArrayList<>();
    private HashMap<String, Album> albums = new HashMap<>();

    private GlobalStateClass(){
        this.player = new Player(this.playlist);
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlaylist(List<Song> songs) {
        resetPlaylist();
        this.playlist.addAll(songs);
    }

    public void resetPlaylist(){
        this.playlist.clear();
    }

    public HashMap<String, Album> getAlbums() {
        return albums;
    }

    public ArrayList<Song> getPlaylist() {
        return playlist;
    }

    public List<Song> getAlbumSongs(String title){
        return albums.get(title).getSongs();
    }
}
