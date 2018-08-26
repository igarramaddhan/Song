package com.ramz.igar.song;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AlbumAdapter extends BaseAdapter {
    private List<Album> albums;
    private Context context;

    AlbumAdapter(Context context, HashMap<String, Album> albums) {
        this.context = context;
        this.albums = new ArrayList<>();
        for(String key: albums.keySet()){
            Album album = albums.get(key);
            this.albums.add(album);
        }
    }

    @Override
    public int getCount() {
        return albums.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Album album = albums.get(position);

        if (convertView == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(context);
            convertView = layoutInflater.inflate(R.layout.album_view, parent,false);
        }

        Bitmap coverBitmap = BitmapFactory.decodeFile(album.getAlbumCover());


        final ImageView imageView = convertView.findViewById(R.id.cover_art);
        final TextView titleTextView =convertView.findViewById(R.id.album_title);
        final TextView artistTextView = convertView.findViewById(R.id.album_artist);

        // 4
        imageView.setImageBitmap(coverBitmap);
        titleTextView.setText(album.getAlbumTitle());
        artistTextView.setText(album.getArtist());

        return convertView;
    }

    public void update(HashMap<String, Album> albums) {
        for(String key: albums.keySet()){
            Album album = albums.get(key);
            this.albums.add(album);
        }
        notifyDataSetChanged();
    }

}
