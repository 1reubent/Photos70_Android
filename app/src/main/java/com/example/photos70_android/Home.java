package com.example.photos70_android;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.photos70_android.model.Album;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class Home extends AppCompatActivity {

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

        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.home_page);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });

        /*LOAD ALBUM DATA*/
        ArrayList<Album> userAlbums = loadAlbums(this);



    }

    public void saveAlbums(ArrayList<Album> albums, Context context) {
        try {
            FileOutputStream fos = context.openFileOutput("albums.dat", Context.MODE_PRIVATE);
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