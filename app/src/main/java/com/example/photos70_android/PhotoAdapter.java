package com.example.photos70_android;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.photos70_android.model.Photo;

import java.util.List;

public class PhotoAdapter extends ArrayAdapter<Photo> {

    private final Context context;
    private final List<Photo> photos;

    public PhotoAdapter(Context context, List<Photo> photos) {
        super(context, R.layout.photo_item, photos);
        this.context = context;
        this.photos = photos;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.photo_item, parent, false);
        }

        Photo photo = photos.get(position);

        // Set thumbnail
        ImageView thumbnail = convertView.findViewById(R.id.photoThumbnail);
        thumbnail.setImageURI(Uri.parse(photo.getPath()));

        // Set caption
        TextView caption = convertView.findViewById(R.id.photoCaption);
        caption.setText(photo.getCaption().isEmpty() ? "No Caption" : photo.getCaption());

        return convertView;
    }
}