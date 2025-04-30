package com.example.photos70_android;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.photos70_android.model.Album;

import java.util.List;

public class AlbumAdapter extends ArrayAdapter<Album> {

    private final Context context;
    private final List<Album> albums;

    public AlbumAdapter(Context context, List<Album> albums) {
        super(context, R.layout.album_item, albums);
        this.context = context;
        this.albums = albums;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.album_item, parent, false);
        }

        Album album = albums.get(position);

        // Bind data to views
        TextView albumName = convertView.findViewById(R.id.albumName);
        albumName.setText(album.toString());

        return convertView;
    }
}
