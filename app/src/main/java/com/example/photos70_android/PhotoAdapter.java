package com.example.photos70_android;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import static com.example.photos70_android.DataManager.getAlbumNamesOfPhoto;

import androidx.annotation.NonNull;

import com.example.photos70_android.model.Photo;

import java.util.List;

public class PhotoAdapter extends ArrayAdapter<Photo> {

    private final Context context;
    private final List<Photo> photos;

    private final boolean forSearch;
    private static int layout;

    public PhotoAdapter(Context context, List<Photo> photos, boolean forSearch) {
        super(context, forSearch ? R.layout.search_photo_item : R.layout.photo_item, photos);
        //use the search_photo_item layout if forSearch is true.
        // this means it's being used in the search photos activity
        layout = forSearch ? R.layout.search_photo_item : R.layout.photo_item;
        this.context = context;
        this.photos = photos;
        this.forSearch = forSearch;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(layout, parent, false);
        }

        Photo photo = photos.get(position);

        // Set thumbnail
        ImageView thumbnail = convertView.findViewById(R.id.photoThumbnail);
        thumbnail.setImageURI(Uri.parse(photo.getPath()));

        // Set caption
        TextView caption = convertView.findViewById(R.id.photoCaption);
        if(forSearch){
            caption.setText(photo.getName() + " (Albums: " + getAlbumNamesOfPhoto(photo) + ")");
        }else{
            caption.setText(photo.getName());
        }

        return convertView;
    }
}