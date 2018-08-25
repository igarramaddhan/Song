package com.ramz.igar.song;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.support.v7.widget.RecyclerView;

import java.util.List;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder> {
    private List<Song> songs;

    public static class SongViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView content;
        public SongViewHolder(View v){
            super(v);
            title = v.findViewById(R.id.card_title);
            content = v.findViewById(R.id.card_artist);
        }
    }

    public SongAdapter (List<Song> songs){
        this.songs= songs;
    }

    public void update() {
        notifyDataSetChanged();
    }

    @Override
    public SongAdapter.SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_view, parent,false);
        return new SongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        Song song = songs.get(position);
        holder.title.setText(song.getTitle());
        holder.content.setText(song.getArtist());
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }
}
