package com.example.photos70_android;


import android.database.Cursor;
import android.provider.MediaStore;

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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.photos70_android.model.Album;
import com.example.photos70_android.model.Photo;

import java.io.File;
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
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*"); // Only show images
            startActivityForResult(intent, REQUEST_IMAGE_GET); // Request code 1
        });
    }

    public void populatePhotoList() {
        // This method should populate the photo list in the UI
        // For example, you can use a RecyclerView or ListView to display the photos
        // You can also update the statusLabel to show the number of photos in the album

        photoListView = findViewById(R.id.photoListView);
//        ArrayAdapter<Photo> photoListAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, this_album.getPhotos());
        PhotoAdapter photoListAdapter = new PhotoAdapter(this, this_album.getPhotos());

        //TODO: change to custom adapter; use new layout for album item
        photoListView.setAdapter(photoListAdapter);
        photoCountLabel.setText("Number of photos: " + this_album.getPhotos().size());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_GET) {
            System.out.println("Result Code: " + resultCode);
            System.out.println("Data: " + data);

            if (resultCode == RESULT_OK && data != null) {
                Uri selectedImageUri = data.getData();
                System.out.println("URI: " + selectedImageUri);
                System.out.println("scheme: " + selectedImageUri.getScheme());
                if (selectedImageUri != null) {
                    String filePath = getRealPathFromURI(selectedImageUri);
                    System.out.println("filePath: " + filePath);
                    if (filePath != null) {
                        Photo newPhoto = new Photo(filePath);
                        this_album.addPhoto(newPhoto);
                        saveAlbumChanges(this, this_album);
                        System.out.println("New updated album list: " + getCurrentAlbums(this));
                        populatePhotoList();
                        statusLabel.setText("Photo added to album: " + this_album.getName());
                    } else {
                        showError("Failed to get the selected photo.");
                    }
                } else {
                    showError("No photo was selected.");
                }
            } else {
                showError("Image selection failed or was canceled.");
            }
        }
    }

    private String getRealPathFromURI(Uri uri) {
        try {
            if ("content".equalsIgnoreCase(uri.getScheme())) {
                try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                    if (cursor != null && cursor.moveToFirst()) {
                        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME);
                        String fileName = cursor.getString(columnIndex);

                        // create the new file (in the app's files directory) to copy the image to
                        File file = createNewAppFile(fileName);

                        // copy the image to the new file
                        try (java.io.InputStream inputStream = getContentResolver().openInputStream(uri);
                             java.io.OutputStream outputStream = new java.io.FileOutputStream(file)) {
                            byte[] buffer = new byte[1024];
                            int length;
                            while ((length = inputStream.read(buffer)) > 0) {
                                outputStream.write(buffer, 0, length);
                            }
                        }
                        //return the path to the copied file
                        return file.getAbsolutePath();
                    }
                }
            } else if ("file".equalsIgnoreCase(uri.getScheme())) {
                return uri.getPath();
            }
        } catch (Exception e) {
            showError("Failed to get the file path from URI: " + e.getMessage());
        }
        return null;
    }

    @NonNull
    private File createNewAppFile(String fileName) {
        String sanitizedFileName = fileName.replaceAll("[^a-zA-Z0-9._()\\- &+@\\[\\]{}~!]", "_").replace(" ", "_");
        if (sanitizedFileName.length() > 255) {
            sanitizedFileName = sanitizedFileName.substring(0, 255);
        }

        //create the file in the app's storage on the device (data/user/0/com.example.photos70_android/files)
        File file = new File(getFilesDir(), sanitizedFileName); // Use internal storage
        return file;
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

//        saveAlbumChanges(this, this_album);
        //update the state of this album in the global list of albums
    }


}