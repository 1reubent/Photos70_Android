package com.example.photos70_android;

import static com.example.photos70_android.DataManager.getCurrentAlbums;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.photos70_android.model.Album;
import com.example.photos70_android.model.Photo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class SearchPhotosActivity extends AppCompatActivity {

    private Spinner tagTypeSpinner, secondTagTypeSpinner;
    private AutoCompleteTextView tagValueInput, secondTagValueInput;
    private RadioGroup searchTypeGroup;
    private ListView searchResultsListView;

    private List<Photo> allPhotos;
    private List<Pair<String,String>> allTags; // For auto-completion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_photos);

        tagTypeSpinner = findViewById(R.id.tagTypeSpinner);
        tagValueInput = findViewById(R.id.tagValueInput);
        secondTagTypeSpinner = findViewById(R.id.secondTagTypeSpinner);
        secondTagValueInput = findViewById(R.id.secondTagValueInput);
        searchTypeGroup = findViewById(R.id.searchTypeGroup);
        searchResultsListView = findViewById(R.id.searchResultsListView);

        /*INITIALIZE THE TOOLBAR*/
        Toolbar toolbar = findViewById(R.id.my_toolbar);
        toolbar.setTitle("Search Photos By Tag");
        setSupportActionBar(toolbar);
        // Enable the Back button
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        // Load all photos from albums
        allPhotos = loadAllPhotos();

        // Load all tags for auto-completion
        allTags = getAllTags();
        System.out.println(allTags);

        // Initialize tag type dropdown
        ArrayAdapter<String> tagTypeAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, new String[]{"Location", "People"});
        tagTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        /*INIT FIRST TAG*/
        tagTypeSpinner.setAdapter(tagTypeAdapter);
        // Add TextWatcher to dynamically update search results
        tagValueInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                performSearch();
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Do nothing
            }
        });

        /*INIT SECOND TAG*/
        secondTagTypeSpinner.setAdapter(tagTypeAdapter);
        // Add TextWatcher to dynamically update search results
        secondTagValueInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                performSearch();
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Do nothing
            }
        });

        // Set default selection to single tag search
        searchTypeGroup.check(R.id.singleTagSearch);

        // Handle search type selection
        searchTypeGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.singleTagSearch) {
                secondTagTypeSpinner.setVisibility(View.GONE);
                secondTagValueInput.setVisibility(View.GONE);
                performSearch();

            } else {
                secondTagTypeSpinner.setVisibility(View.VISIBLE);
                secondTagValueInput.setVisibility(View.VISIBLE);
                performSearch();
            }
        });


    }
    private List<Photo> loadAllPhotos() {
        // Load photos from all albums
        List<Photo> photos = new ArrayList<>();
        for (Album album : getCurrentAlbums(this)) {
            photos.addAll(album.getPhotos());
        }
        return photos;
    }

    private List<Pair<String,String>> getAllTags() {
        // Collect all unique tag values from all photos
        if(allPhotos == null) {
            allPhotos = loadAllPhotos();
        }
        // Pair<String, String> represents (tagType, tagValue)
        List<Pair<String,String>> allTagValues = new ArrayList<>();
        for (Photo photo : allPhotos) {
            if (photo.hasLocationTag()) {
                allTagValues.add(new Pair<>("Location", photo.getLocationTag()));
            }
            allTagValues.addAll(photo.getPeopleTags().stream().map(value -> new Pair<>("People", value)).collect(Collectors.toList()));
        }
        return allTagValues;
    }

    //TODO: make the results display
    private void performSearch() {
        String tagType = tagTypeSpinner.getSelectedItem().toString();
        String tagValue1 = tagValueInput.getText().toString().trim();
        String tagType2 = secondTagTypeSpinner.getSelectedItem().toString();
        String tagValue2 = secondTagValueInput.getText().toString().trim();


        List<Photo> results = new ArrayList<>();
        int selectedSearchType = searchTypeGroup.getCheckedRadioButtonId();

        if (selectedSearchType == R.id.singleTagSearch) {
            results = searchBySingleTag(tagType, tagValue1);
        } else if (selectedSearchType == R.id.andSearch) {
            results = searchByConjunction(tagType, tagType2 , tagValue1, tagValue2);
        } else if (selectedSearchType == R.id.orSearch) {
            results = searchByDisjunction(tagType, tagType2, tagValue1, tagValue2);
        }

        //print results
        System.out.println("Search Results: " + results);
        // Update RecyclerView with results
        PhotoAdapter searchResultsAdapter = new PhotoAdapter(this, results, true);
        searchResultsListView.setAdapter(searchResultsAdapter);
    }

    private List<Photo> searchBySingleTag(String tagType, String tagValue) {
        return allPhotos.stream()
                .filter(photo -> photo.hasTagPrefix(tagType, tagValue))
                .distinct()
                .collect(Collectors.toList());
    }

    private List<Photo> searchByConjunction(String tagType, String tagType2, String tagValue1, String tagValue2) {
        return allPhotos.stream()
                .filter(photo -> photo.hasTagPrefix(tagType, tagValue1) && photo.hasTagPrefix(tagType2, tagValue2))
                .distinct()
                .collect(Collectors.toList());
    }

    private List<Photo> searchByDisjunction(String tagType, String tagType2, String tagValue1, String tagValue2) {
        return allPhotos.stream()
                .filter(photo -> photo.hasTagPrefix(tagType, tagValue1) || photo.hasTagPrefix(tagType2, tagValue2))
                .distinct()
                .collect(Collectors.toList());
    }
}