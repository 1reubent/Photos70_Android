package com.example.photos70_android;

import static com.example.photos70_android.DataManager.getCurrentAlbums;

import android.os.Bundle;
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
    private Button searchButton;
    private ListView searchResultsListView;

    private List<Photo> allPhotos; // Assume this is loaded from all albums
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
        searchButton = findViewById(R.id.searchButton);
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
        tagTypeSpinner.setAdapter(tagTypeAdapter);
        tagTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            //update tag suggestions when a tag type is selected
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Set up auto-completion
                String selectedTagType = tagTypeSpinner.getSelectedItem().toString();
                updateFirstTagSuggestions(selectedTagType);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        secondTagTypeSpinner.setAdapter(tagTypeAdapter);
        secondTagTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            //update tag suggestions when a tag type is selected
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Set up auto-completion
                String selectedTagType = secondTagTypeSpinner.getSelectedItem().toString();
                updateSecondTagSuggestions(selectedTagType);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        // Initialize RecyclerView
//        searchResultsListView.setLayoutManager(new LinearLayoutManager(this));

        // Set default selection to single tag search
        searchTypeGroup.check(R.id.singleTagSearch);

        // Handle search type selection
        searchTypeGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.singleTagSearch) {
                secondTagTypeSpinner.setVisibility(View.GONE);
                secondTagValueInput.setVisibility(View.GONE);

            } else {
                secondTagTypeSpinner.setVisibility(View.VISIBLE);
                secondTagValueInput.setVisibility(View.VISIBLE);
            }
        });

        //TODO: Handle search button click
        searchButton.setOnClickListener(v -> performSearch());
    }

    private void updateFirstTagSuggestions(String tagType) {
        // Update the auto-complete suggestions for first tag input based on the selected tag type
        ArrayAdapter<String> autoCompleteAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line,
                allTags.stream()
                        .filter(tag -> tag.first.equalsIgnoreCase(tagType))
                        .map(tag -> tag.second)
                        .distinct()
                        .collect(Collectors.toList()));
        tagValueInput.setAdapter(autoCompleteAdapter);
        tagValueInput.setThreshold(1);
    }
    private void updateSecondTagSuggestions(String tagType) {
        // Update the auto-complete suggestions for second tag input based on the selected tag type
        ArrayAdapter<String> autoCompleteAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line,
                allTags.stream()
                        .filter(tag -> tag.first.equalsIgnoreCase(tagType))
                        .map(tag -> tag.second)
                        .distinct()
                        .collect(Collectors.toList()));
        secondTagValueInput.setAdapter(autoCompleteAdapter);
        secondTagValueInput.setThreshold(1);
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

        if (tagValue1.isEmpty()) {
            Toast.makeText(this, "Please enter a tag value.", Toast.LENGTH_SHORT).show();
            return;
        }

        List<Photo> results = new ArrayList<>();
        int selectedSearchType = searchTypeGroup.getCheckedRadioButtonId();

        if (selectedSearchType == R.id.singleTagSearch) {
            results = searchBySingleTag(tagType, tagValue1);
        } else if (selectedSearchType == R.id.andSearch) {
            if (tagValue2.isEmpty()) {
                Toast.makeText(this, "Please enter the second tag value.", Toast.LENGTH_SHORT).show();
                return;
            }
            results = searchByConjunction(tagType, tagType2 , tagValue1, tagValue2);
        } else if (selectedSearchType == R.id.orSearch) {
            if (tagValue2.isEmpty()) {
                Toast.makeText(this, "Please enter the second tag value.", Toast.LENGTH_SHORT).show();
                return;
            }
            results = searchByDisjunction(tagType, tagType2, tagValue1, tagValue2);
        }

        // Update RecyclerView with results
        PhotoAdapter searchResultsAdapter = new PhotoAdapter(this, results, true);
//        SearchResultsAdapter searchResultsAdapter = new SearchResultsAdapter(results);
        searchResultsListView.setAdapter(searchResultsAdapter);
    }

    private List<Photo> searchBySingleTag(String tagType, String tagValue) {
        return allPhotos.stream()
                .filter(photo -> photo.hasTag(tagType, tagValue))
                .collect(Collectors.toList());
    }

    private List<Photo> searchByConjunction(String tagType, String tagType2, String tagValue1, String tagValue2) {
        return allPhotos.stream()
                .filter(photo -> photo.hasTag(tagType, tagValue1) && photo.hasTag(tagType2, tagValue2))
                .collect(Collectors.toList());
    }

    private List<Photo> searchByDisjunction(String tagType, String tagType2, String tagValue1, String tagValue2) {
        return allPhotos.stream()
                .filter(photo -> photo.hasTag(tagType, tagValue1) || photo.hasTag(tagType2, tagValue2))
                .collect(Collectors.toList());
    }
}