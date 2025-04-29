package com.example.photos70_android;

import android.content.Context;
import android.widget.Toast;

import com.example.photos70_android.model.Album;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.stream.IntStream;

public class AlbumsManager {
    private static final String ALBUMS_FILE = "albums.dat";

    private static ArrayList<Album> current_albums;

    /**
     * Retrieves the current albums list.
     */
    public static ArrayList<Album> getCurrentAlbums(Context context) {
        if (current_albums == null) {
            current_albums = loadAlbums(context);
        }
        return current_albums;
    }

    //get an album by name
    public static Album getAlbum(Context context, String albumName) {
        if (current_albums == null) {
            current_albums = loadAlbums(context);
        }
        return current_albums.stream().filter((alb -> alb.getName().equals(albumName))).findAny().orElse(null);
    }

    public static void saveAlbumChanges(Context context, Album updatedAlbum) {
        if (current_albums == null) {
            current_albums = loadAlbums(context);
        }
        //put the updated album in the same index as the old one
       int index = IntStream.range(0, current_albums.size())
           .filter(i -> current_albums.get(i).getName().equalsIgnoreCase(updatedAlbum.getName()))
           .findFirst()
           .orElse(-1);
       if (index != -1) {
           current_albums.remove(index);
           current_albums.add(index, updatedAlbum);
       } else {
           current_albums.add(updatedAlbum);
       }
        saveAlbums(context);
    }

    public static boolean addAlbum(Context context, Album album) {
        if (current_albums == null) {
            current_albums = loadAlbums(context);
        }
        if (current_albums.contains(album)) {
            return false; // Album already exists
        }
        current_albums.add(album);
        saveAlbums(context);
        return true;

    }

    public static boolean removeAlbum(Context context, String albumName) {
        if (current_albums == null) {
            current_albums = loadAlbums(context);
        }
        Album album = current_albums.stream().filter((alb -> alb.getName().equals(albumName))).findAny().orElse(null);
        if (album == null) {
            return false;
        }
        current_albums.remove(album);
        saveAlbums(context);
        return true;
    }

    public static boolean removeAlbum(Context context, Album album) {
        if (current_albums == null) {
            current_albums = loadAlbums(context);
        }
        if (!current_albums.contains(album)) {
            return false; // Album does not exist
        }
        current_albums.remove(album);
        saveAlbums(context);
        return true;
    }

    public static void saveAlbums(Context context) {
        if (current_albums == null) {
            current_albums = loadAlbums(context);
        }
        try {
            FileOutputStream fos = context.openFileOutput(ALBUMS_FILE, Context.MODE_PRIVATE);
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
            oos.writeObject(current_albums);
            oos.close();
            fos.close();
        } catch (IOException e) {
            Toast.makeText(context, "Error saving albums: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
        System.out.println("Saving: " + current_albums);
    }

    public static ArrayList<Album> loadAlbums(Context context) {
        //print current_albums
        ArrayList<Album> albums = new ArrayList<>();
        try {
            FileInputStream fis = context.openFileInput(ALBUMS_FILE);
            ObjectInputStream ois = new ObjectInputStream(fis);
            albums = (ArrayList<Album>) ois.readObject();
            ois.close();
            fis.close();
        } catch (IOException | ClassNotFoundException e) {
//            if(e instanceof ClassNotFoundException) {
                // run saveAlbums to create the file
                current_albums = new ArrayList<>();
                saveAlbums(context);
//            } else {
//                // Handle other IO exceptions
//                Toast.makeText(context, "Error loading albums: " + e.getMessage(), Toast.LENGTH_LONG).show();
//            }
        }

        current_albums = albums;
        System.out.println("Loading: " + current_albums);
        return albums;
    }


}
