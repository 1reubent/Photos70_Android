package com.example.photos70_android;

import android.content.Context;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.photos70_android.model.Album;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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

        /*LOAD ALBUMS*/
        albums = loadAlbums(this);

        /*POPULATE LISTVIEW*/
        saveAndUpdateAlbumListView();

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
                if (!addAlbum(new Album(albumName))) {
                    showError("An album with this name already exists.");
                    return;
                }

                // Save albums and update UI
                saveAndUpdateAlbumListView();

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
                if(!removeAlbum(albumName)){
                    showError("Album not found.");
                    return;
                }

                //update status label
                statusLabel.setText("Album deleted: " + albumName);

                // Save albums and update UI
                saveAndUpdateAlbumListView();
            });

            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

            builder.show();
        });


    }

    public Album getAlbum(String name) {
        for (Album album : albums) {
            if (album.getName().equalsIgnoreCase(name)) {
                return album;
            }
        }
        return null; // Album not found
    }

    public boolean addAlbum(Album album) {
        if (albums.contains(album)) {
            return false; // Album already exists
        }
        albums.add(album);
        return true;
    }
    public boolean removeAlbum(Album album) {
        if (!albums.contains(album)) {
            return false; // Album does not exist
        }
        albums.remove(album);
        return true;
    }
    public boolean removeAlbum(String albumName){
        Album album = albums.stream().filter((alb -> alb.getName().equals(albumName))).findAny().orElse(null);
        if(album == null){
            return false;
        }
        albums.remove(album);
        return true;
    }

    public void saveAndUpdateAlbumListView() {
        // Save the albums to internal storage
        saveAlbums(albums, this);
        listView = findViewById(R.id.albumListView);
        ArrayAdapter<Album> albumAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, albums);
        //TODO: change to custom adapter; use new layout for album item
        listView.setAdapter(albumAdapter);
    }
    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
    public void saveAlbums(ArrayList<Album> albums, Context context) {
        try {
            FileOutputStream fos = context.openFileOutput("albums.dat", Context.MODE_PRIVATE);
            /*
            * The file `albums.dat` is stored in the app's internal storage directory,
            * which is private to the app. Specifically, it is located in the directory
            * returned by `Context.getFilesDir()` on the device. This directory is typically
            * something like:
            * `/data/data/com.example.photos70_android/files/albums.dat`
            * You can access it programmatically using `context.getFileStreamPath("albums.dat")`.
               * in Android Studio, you can view this file by navigating to the Device File Explorer
            * */
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(albums);
            oos.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
//            TODO: change to dialog
        }
    }
    public ArrayList<Album> loadAlbums(Context context) {
        ArrayList<Album> albums = new ArrayList<>();
        try {
            FileInputStream fis = context.openFileInput("albums.dat");
            ObjectInputStream ois = new ObjectInputStream(fis);
            albums = (ArrayList<Album>) ois.readObject();
            ois.close();
            fis.close();
        } catch (IOException | ClassNotFoundException e) {
            // No saved data yet or error loading
            e.printStackTrace();
//            TODO: change to dialog
//            // create file if it doesn't exist
//            try {
//                FileOutputStream fos = context.openFileOutput("albums.dat", Context.MODE_PRIVATE);
//                fos.close(); // Create the file if it doesn't exist
//            } catch (IOException ioException) {
//                ioException.printStackTrace();
//            }
//            e.printStackTrace();
        }
        return albums;
    }

//
}