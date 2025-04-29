package com.example.photos70_android;

import static com.example.photos70_android.AlbumsManager.addAlbum;
import static com.example.photos70_android.AlbumsManager.loadAlbums;
import static com.example.photos70_android.AlbumsManager.saveAlbums;
import static com.example.photos70_android.AlbumsManager.removeAlbum;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.photos70_android.model.Album;

import java.util.ArrayList;

public class Home extends AppCompatActivity {
    // get listview from main activity (home)
    private ListView listView;

    //get buttons from main activity (home)
    private Button createAlbumButton;
    private Button deleteAlbumButton;
    private Button renameAlbumButton;
    private Button openAlbumButton;

    // albums
    private ArrayList<Album> albums;

    //status label
    private TextView statusLabel;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /* TODO:
        * load the home page layout
        * load the user album/photo data from the previous session
        * populate home page with album names
        * add functionality:
        * - open album (start a new activity, pass an intent)
        * - create album
        * - delete album
        * - rename album
        * - how to save user data when app is closed
        * - show error/warning methods
        * */

        /* BOILERPLATE INITIALIZATION */
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });

//        //DEBUG: save test albums. only need to uncomment this once to save the albums to the device
//        ArrayList<Album> testAlbums = new ArrayList<>();
//        testAlbums.add(new Album("Test Album 1"));
//        testAlbums.add(new Album("Test Album 2"));
//        testAlbums.add(new Album("Test Album 3"));
//        saveAlbums(testAlbums, this);
        checkPermissions();



        /*GET BUTTONS*/
        createAlbumButton = findViewById(R.id.createAlbumButton);
        deleteAlbumButton = findViewById(R.id.deleteAlbumButton);
        renameAlbumButton = findViewById(R.id.renameAlbumButton);
        openAlbumButton = findViewById(R.id.openAlbumButton);
        /*GET STATUS LABEL*/
        statusLabel = findViewById(R.id.statusLabel);
        /*GET & SET TOOLBAR*/
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        myToolbar.setTitle("My Albums");
        setSupportActionBar(myToolbar);

        /*LOAD ALBUMS AND INITIALIZE PERSISTENCE UTILITY*/
        albums = loadAlbums(this);

        /*POPULATE LISTVIEW*/
        populateAlbumList();

        /*Set up the Create Album button logic*/
        createAlbumButton.setOnClickListener(v -> {
            // Show a dialog to get the album name
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Create Album");

            // Input field for album name
            final EditText input = new EditText(this);
            input.setHint("Enter album name");
            builder.setView(input);

            // Set up dialog buttons
            builder.setPositiveButton("Create", (dialog, which) -> {
                String albumName = input.getText().toString().trim();

                // Validate album name
                if (albumName.isEmpty()) {
                    showError("Album name cannot be empty.");
                    return;
                }

                // Create and add the new album
                if (!addAlbum(this, new Album(albumName))) {
                    showError("An album with this name already exists.");
                    return;
                }

                // Save albums and update UI
                populateAlbumList();

                //update status label
                statusLabel.setText("Album created: " + albumName);


            });

            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

            builder.show();
        });

        /*Set up the Delete Album button logic*/
        deleteAlbumButton.setOnClickListener(v -> {
            // Show a dialog to get the album name
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Delete Album");

            // Dropdown (Spinner) for album names
            final Spinner albumDropdown = new Spinner(this);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,
                albums.stream().map(Album::getName).toArray(String[]::new));
//            need to pass String[]::new to convert the stream to an array of strings and not objects
            albumDropdown.setAdapter(adapter);
            builder.setView(albumDropdown);

            // Set up dialog buttons
            builder.setPositiveButton("Delete", (dialog, which) -> {
                String albumName = albumDropdown.getSelectedItem().toString().trim();

                //TODO: ask for confirmation?

                //remove the album and save
                if(!removeAlbum(this, albumName)){
                    showError("Album not found.");
                    return;
                }

                //update status label
                statusLabel.setText("Album deleted: " + albumName);

                // Save albums and update UI
                populateAlbumList();
            });

            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

            builder.show();
        });

        /*TODO: Set up the Rename Album button logic*/

        /*Set up the Open Album button logic*/
        openAlbumButton.setOnClickListener(v -> {
            // Show a dialog to get the album name
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Open Album");

            // Dropdown (Spinner) for album names
            final Spinner albumDropdown = new Spinner(this);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,
                albums.stream().map(Album::getName).toArray(String[]::new));
            albumDropdown.setAdapter(adapter);
            builder.setView(albumDropdown);

            // Set up dialog buttons
            builder.setPositiveButton("Open", (dialog, which) -> {
                String albumName;
                try {
                    albumName = albumDropdown.getSelectedItem().toString().trim();
                } catch (NullPointerException e) {
                    showError("No album selected.");
                    return;
                }
                // Open the selected album (start a new activity)
                openAlbum(albumName);
            });
            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

            builder.show();

        });

        /*TODO: Set up the Search Photos button logic*/

    }

    public static final String ALBUM_NAME = "album_name";
    private void openAlbum(String albumName) {
        // Create an intent to start the AlbumActivity
        Intent intent = new Intent(this, AlbumViewActivity.class);

        // Pass the album as an extra
        intent.putExtra(ALBUM_NAME, albumName);

        // Start the activity
        startActivity(intent);
    }
    public void populateAlbumList() {
        // Save the albums to internal storage
        listView = findViewById(R.id.albumListView);
        ArrayAdapter<Album> albumAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, albums);
        //TODO: change to custom adapter; use new layout for album item
        listView.setAdapter(albumAdapter);
    }
    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload the albums when returning to this activity
        albums = loadAlbums(this);
        System.out.println("Reloaded albums from Home.java: " + albums);
        populateAlbumList();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Save the albums when this activity is destroyed
//        // reload and save
        saveAlbums(this);
    }

    private static final int REQUEST_PERMISSION_READ_MEDIA_IMAGES = 100;

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_MEDIA_IMAGES)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_MEDIA_IMAGES},
                    REQUEST_PERMISSION_READ_MEDIA_IMAGES);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_READ_MEDIA_IMAGES) {
            if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                showError("Permission to access photos is required.");
            }
        }
    }


//
}