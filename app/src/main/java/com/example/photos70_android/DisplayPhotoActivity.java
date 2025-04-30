package com.example.photos70_android;

import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import static com.example.photos70_android.AlbumsManager.getAlbum;


import com.example.photos70_android.model.Album;
import com.example.photos70_android.model.Photo;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.IntStream;

public class DisplayPhotoActivity extends AppCompatActivity {
    //buttons and views
    private Button addTagButton;
    private Button deleteTagButton;
    private Button prevPhotoButton;
    private Button nextPhotoButton;
    private TextView tagsTextView;
    private ImageView photoImageView;
    private Album this_album;
    private Photo currentPhoto;
    private List<Photo> photos;
    private int currentPhotoIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_display_photo);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
        /*GET THE BUTTONS AND LIST VIEW*/
        addTagButton = findViewById(R.id.addTagButton);
        deleteTagButton = findViewById(R.id.deleteTagButton);
        prevPhotoButton = findViewById(R.id.prevPhotoButton);
        nextPhotoButton = findViewById(R.id.nextPhotoButton);
        tagsTextView = findViewById(R.id.tagsTextView);
        photoImageView = findViewById(R.id.photoImageView);

        /*INITIALIZE THE TOOLBAR*/
        Toolbar toolbar = findViewById(R.id.my_toolbar);
        toolbar.setTitle("Display Photo");
        setSupportActionBar(toolbar);
        // Enable the Up button
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        /*GET ALBUM AND PHOTO*/
        // Get the album name passed from the previous activity
        String albumName = getIntent().getStringExtra(Home.ALBUM_NAME);
        String photoPath = getIntent().getStringExtra(Home.PHOTO_PATH);
        //Use AlbumManager to get the album and photo
        this_album = getAlbum(this, albumName);
        currentPhoto = this_album.getPhoto(photoPath);
        //print this_album and currentPhoto
        System.out.println("Album: " + this_album.getName());
        System.out.println("Photo: " + currentPhoto.getPath());

        /*INITIALIZE PHOTOS FIELDS*/
        initializePhotos(photoPath);

        /*DISPLAY PHOTO*/
        displayPhoto();

    }

    private void initializePhotos(String currentPhotoPath) {
        if (this_album == null) {
            this_album = getAlbum(this, getIntent().getStringExtra(Home.ALBUM_NAME));
        }
        //get photos from the album
        photos = this_album.getPhotos();
        if (photos == null || photos.isEmpty()) {
            showError("No photos in this album");
            finish(); //close the activity
            return;
        }
        //get current photo index
        currentPhotoIndex = IntStream.range(0, photos.size())
                .filter(i -> photos.get(i).getPath().equals(currentPhotoPath))
                .findFirst()
                .orElse(-1);

    }

    private void displayPhoto() {
        currentPhoto = photos.get(currentPhotoIndex);
        photoImageView.setImageURI(Uri.parse(currentPhoto.getPath())); // Assuming Photo has a getUri() method
        //may cause problems
        updateTagsDisplay();
    }

    private void updateTagsDisplay() {
        // Assuming Photo has a method to get tags as a String
        Map<String, Set<String>> tags = currentPhoto.getAllTags();
        //print tags
        System.out.println("Tags: " + tags);
        StringBuilder tagsString = new StringBuilder();
        for (Map.Entry<String, Set<String>> entry : tags.entrySet()) {
            String tagType = entry.getKey();
            Set<String> tagValues = entry.getValue();
            if(tagValues == null || tagValues.isEmpty()) {
                tagsString.append(tagType).append(": None\n");
            }else {
                tagsString.append(tagType).append(": ").append(tagValues.toString()).append("\n");
            }
        }
        tagsTextView.setText(tagsString.toString());

    }
    //TODO: get photo from its index

    //show error
    private void showError(String message) {
        // Show an error message to the user
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}