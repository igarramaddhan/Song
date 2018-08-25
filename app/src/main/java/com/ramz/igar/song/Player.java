package com.ramz.igar.song;

import android.media.MediaPlayer;
import android.util.Log;

import java.io.IOException;

public class Player  {

    private boolean isPlaying = false;
    private Song currentSong = null;
    private MediaPlayer player = new MediaPlayer();

    public void play(Song file){
        try {
            if(currentSong == null) {
                stop();
                currentSong = file;
                player.setDataSource(file.getPath());
                player.prepare();
                player.start();
            }else{
                player.start();
            }
        }catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        isPlaying = true;
        Log.d("PLAYER", "PLAY");
    }

    public void pause(){
        player.pause();
        isPlaying = false;
        Log.d("PLAYER", "PAUSE");
    }

    public void stop(){
        player.stop();
        player.reset();
        currentSong = null;
        isPlaying = false;
    }

    public void setCurrentSong(Song song){
        this.currentSong = song;
    }

    public Song getCurrentSong() {
        return currentSong;
    }

    public boolean isPlaying(){
        return isPlaying;
    }

}
