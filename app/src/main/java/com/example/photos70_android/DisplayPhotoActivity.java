package com.example.photos70_android;

import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import static com.example.photos70_android.DataManager.getAlbum;
import static com.example.photos70_android.DataManager.getAlbumNamesOfPhoto;
import static com.example.photos70_android.DataManager.saveAlbumChanges;


import com.example.photos70_android.model.Album;
import com.example.photos70_android.model.Photo;

import java.util.ArrayList;
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

    private TextView albumsContainingPhotoTextView;
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
        albumsContainingPhotoTextView = findViewById(R.id.albumsContainingPhotoTextView);

        /*GET ALBUM AND STARTING PHOTO*/
        // Get the album name passed from the previous activity
        String albumName = getIntent().getStringExtra(Home.ALBUM_NAME);
        String photoPath = getIntent().getStringExtra(Home.PHOTO_PATH);
        //Use AlbumManager to get the album and photo
        this_album = getAlbum(this, albumName);
        currentPhoto = this_album.getPhoto(photoPath);
        //print this_album and currentPhoto
        System.out.println("Album: " + this_album.getName());
        System.out.println("Photo: " + currentPhoto.getPath());

        /*INITIALIZE THE TOOLBAR*/
        Toolbar toolbar = findViewById(R.id.my_toolbar);
        toolbar.setTitle("Displaying " + currentPhoto.getName());
        setSupportActionBar(toolbar);
        // Enable the Back button
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);


        /*INITIALIZE PHOTOS FIELDS*/
        initializePhotos(photoPath);

        /*DISPLAY PHOTO*/
        displayPhoto();

        /*BUTTON ACTIONS*/

        //prev photo button:
        prevPhotoButton.setOnClickListener(v -> {
            if (currentPhotoIndex > 0) {
                currentPhotoIndex--;
                displayPhoto();
            } else {
                showMessage("No previous photo");
            }
        });
        //next photo button:
        nextPhotoButton.setOnClickListener(v -> {
            if (currentPhotoIndex < photos.size() - 1) {
                currentPhotoIndex++;
                displayPhoto();
            } else {
                showMessage("No next photo");
            }
        });

        //add tag button:
        addTagButton.setOnClickListener(v -> showAddTagDialog());

        //delete tag button:
        deleteTagButton.setOnClickListener(v -> showDeleteTagDialog());


    }

    private void initializePhotos(String currentPhotoPath) {
        if (this_album == null) {
            this_album = getAlbum(this, getIntent().getStringExtra(Home.ALBUM_NAME));
        }
        //get photos from the album
        photos = this_album.getPhotos();
        if (photos == null || photos.isEmpty()) {
            showMessage("No photos in this album");
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
        photoImageView.setImageURI(Uri.parse(currentPhoto.getPath()));
        //may cause problems
        updatePhotoDetailsDisplay();
    }

    private void updatePhotoDetailsDisplay() {
        //set albums containing photo text using getAlbumNamesOfPhoto of DataManager
        String albumNames = getAlbumNamesOfPhoto(currentPhoto);
        albumsContainingPhotoTextView.setText("In albums: " + albumNames);

        //save updated photo to the album
        saveAlbumChanges(this, this_album);

        // Assuming Photo has a method to get tags as a String
        Map<String, Set<String>> tags = currentPhoto.getAllTags();
        //print tags
        System.out.println("Tags: " + tags);
        StringBuilder tagsString = new StringBuilder();
        for (Map.Entry<String, Set<String>> entry : tags.entrySet()) {
            String tagType = entry.getKey();
            Set<String> tagValues = entry.getValue();
            if (tagValues == null || tagValues.isEmpty()) {
                tagsString.append(tagType).append(": None\n");
            } else {
                tagsString.append(tagType).append(": ").append(tagValues.toString()).append("\n");
            }
        }
        tagsTextView.setText(tagsString.toString());

    }

    private void showAddTagDialog() {
        String[] tagTypes = {"Person", "Location"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Tag Type")
                .setItems(tagTypes, (dialog, which) -> {
                    String selectedTagType = tagTypes[which];
                    showTagInputDialog(selectedTagType);
                })
                .show();
    }

    private void showTagInputDialog(String tagType) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter " + tagType + " Tag");
        final android.widget.EditText input = new android.widget.EditText(this);
        builder.setView(input);
        builder.setPositiveButton("Add", (dialog, which) -> {
            String tagValue = input.getText().toString().trim();
            if (!tagValue.isEmpty()) {
                //add tag based on type
                if (tagType.equalsIgnoreCase("Person")) {
                    currentPhoto.addPersonTag(tagValue); // Assuming Photo has a method to add tags
                } else if (tagType.equalsIgnoreCase("Location")) {
                    currentPhoto.addLocationTag(tagValue);
                }
                updatePhotoDetailsDisplay();
                showMessage("Tag added");
            } else {
                showMessage("Tag cannot be empty");
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void showDeleteTagDialog() {
        Map<String, Set<String>> tags = currentPhoto.getAllTags();
        Set<String> peopleTags = tags.get("people");
        Set<String> locationTags = tags.get("location");

        if ((peopleTags == null || peopleTags.isEmpty()) && (locationTags == null || locationTags.isEmpty())) {
            showMessage("No tags to delete");
            return;
        }
        // Combine all tags into a single list for display
        List<String> allTags = new ArrayList<>();
        if (peopleTags != null) {
            for (String tag : peopleTags) {
                allTags.add("Person: " + tag);
            }
        }
        if (locationTags != null) {
            for (String tag : locationTags) {
                allTags.add("Location: " + tag);
            }
        }

        // Show a simple list dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Tag to Delete");
        builder.setItems(allTags.toArray(new String[0]), (dialog, which) -> {
            String selectedTag = allTags.get(which);
            if (selectedTag.startsWith("Person: ")) {
                String personTag = selectedTag.substring(8); // Remove "Person: " prefix
                currentPhoto.removePersonTag(personTag);
                showMessage("Person tag deleted");
            } else if (selectedTag.startsWith("Location: ")) {
                currentPhoto.clearLocationTag();
                showMessage("Location tag deleted");
            }
            updatePhotoDetailsDisplay();
        });
        builder.show();
    }

    //show message
    private void showMessage(String message) {
        // Show an error message to the user
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}