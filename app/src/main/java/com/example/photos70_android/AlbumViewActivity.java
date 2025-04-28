package com.example.photos70_android;

import static com.example.photos70_android.AlbumsManager.getAlbum;
import static com.example.photos70_android.AlbumsManager.getCurrentAlbums;
import static com.example.photos70_android.AlbumsManager.saveAlbumChanges;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.photos70_android.model.Album;
import com.example.photos70_android.model.Photo;

import java.util.Objects;

public class AlbumViewActivity extends AppCompatActivity {

    private Button addPhotoButton;
    private Button removePhotoButton;
    private Button captionPhotoButton;
    private Button displayPhotoButton;
    private Button addTagButton;
    private Button removeTagButton;
    private Button movePhotoButton;

    private TextView photoCountLabel;
    private ListView photoListView;

    private static String album_name;
    private static Album this_album;

    private TextView statusLabel;


    static final int REQUEST_IMAGE_GET = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_album_view);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });

        /*GET ALBUM NAME FROM BUNDLE*/
        Bundle bundle = getIntent().getExtras();
        album_name = bundle.getString(Home.ALBUM_NAME);
        this_album = getAlbum(this, album_name);
        //for debugging, print the album
//        System.out.println("Album: " + this_album);


        /*INITIALIZE TOOLBAR*/
        Toolbar myToolbar = findViewById(R.id.my_toolbar2);
        myToolbar.setTitle("Photos in " + album_name);
        setSupportActionBar(myToolbar);
        // Enable the Up button
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);


        /*GET BUTTONS*/
        addPhotoButton = findViewById(R.id.addPhotoButton);
        removePhotoButton = findViewById(R.id.removePhotoButton);
        captionPhotoButton = findViewById(R.id.captionPhotoButton);
        displayPhotoButton = findViewById(R.id.displayPhotoButton);
        addTagButton = findViewById(R.id.addTagButton);
        removeTagButton = findViewById(R.id.removeTagButton);
        movePhotoButton = findViewById(R.id.movePhotoButton);
        statusLabel = findViewById(R.id.statusLabel);
        photoCountLabel = findViewById(R.id.photoCountLabel);

        /*POPULATE PHOTO LIST*/
        populatePhotoList();


        /*TODO: add listeners to all the buttons:
        *  - display photo activity should implement the slideshow feature
        *  - make it so that you select a photo FIRST and then click the button*/

        addPhotoButton.setOnClickListener(view -> {
            // Create an intent to open the gallery
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*"); // Only show images
            startActivityForResult(intent, REQUEST_IMAGE_GET); // Request code 1
        });
    }

    public void populatePhotoList() {
        // This method should populate the photo list in the UI
        // For example, you can use a RecyclerView or ListView to display the photos
        // You can also update the statusLabel to show the number of photos in the album

        photoListView = findViewById(R.id.photoListView);
        ArrayAdapter<Photo> photoListAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, this_album.getPhotos());
        //TODO: change to custom adapter; use new layout for album item
        photoListView.setAdapter(photoListAdapter);
        photoCountLabel.setText("Number of photos: " + this_album.getPhotos().size());
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //"Add Photo" result:
        if (requestCode == REQUEST_IMAGE_GET && resultCode == RESULT_OK && data != null) {
            // Get the selected photo's URI
            Uri selectedImageUri = data.getData();

            if (selectedImageUri != null) {
                // Add the photo to the album
                Photo newPhoto = new Photo(selectedImageUri.toString()); // Assuming Photo has a constructor that accepts a URI string
                this_album.addPhoto(newPhoto);

                //save the album changes and update the UI
                saveAlbumChanges(this, this_album);
                populatePhotoList();
                statusLabel.setText("Photo added to album: " + this_album.getName());
            } else {
                showError("Failed to get the selected photo.");
            }
        }
    }
    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload the albums when returning to this activity
        this_album = getAlbum(this, album_name);
        populatePhotoList();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Save the albums when this activity is destroyed

        saveAlbumChanges(this, this_album);
        System.out.println("New updated album list: " + getCurrentAlbums(this));
            //update the state of this album in the global list of albums
    }
}