# Photos70 for Android

A native Android app for organizing personal photos into albums, tagging them by person and location, and searching across your whole library by tag.

## Overview

Photos70 is a local photo management Android app. Photos live in albums that you create yourself — there's no automatic import or cloud sync. From the home screen you can create, rename, and delete albums; inside an album you can add photos from the device's gallery, remove them, or move them to a different album. Opening a photo brings up a full-screen viewer with previous/next navigation and lets you attach **people** and **location** tags. A dedicated search screen lets you find photos across every album by one or two tags at once, combined with AND/OR logic. All album and photo data (including tags) is saved to the app's private internal storage, so your library persists between sessions without needing an account or network connection.

## Features

- **Album management** — create, rename, and delete albums from the home screen
- **Photo management** — add photos from the device gallery into an album, remove them, or move them between albums
- **Photo viewer** — full-screen display with previous/next navigation through an album
- **Tagging** — attach multiple people tags and a single location tag to each photo
- **Search** — find photos by a single tag, or combine two tags with AND/OR logic, with autocomplete on tag values
- **Local persistence** — albums and photos are serialized to the app's private storage, so data survives app restarts

## Tech Stack

- **Language:** Java
- **Platform:** Android (minSdk 34, targetSdk 35, compileSdk 35)
- **Build system:** Gradle (Kotlin DSL) with version catalogs
- **UI:** Android Views (`ListView`, `AlertDialog`, `AutoCompleteTextView`, etc.) with AppCompat & Material components
- **Persistence:** Java object serialization to the app's internal storage (`Context.openFileOutput`/`openFileInput`)

## Project Structure

```
app/src/main/java/com/example/photos70_android/
├── Home.java                  # Home screen: list, create, rename, delete albums
├── AlbumViewActivity.java     # Album screen: add/remove/move photos, open viewer
├── DisplayPhotoActivity.java  # Full-screen photo viewer with tagging and navigation
├── SearchPhotosActivity.java  # Cross-album search by tag(s)
├── DataManager.java           # Loads/saves albums and mediates all data access
├── AlbumAdapter.java          # ListView adapter for albums
├── PhotoAdapter.java          # ListView adapter for photos
└── model/
    ├── Album.java              # Album entity (name, photos, date range)
    └── Photo.java              # Photo entity (path, caption, date, tags)
```

## Getting Started

1. Open the project in Android Studio (Meerkat or newer recommended).
2. Let Gradle sync and download dependencies.
3. Run the `app` module on a device or emulator running Android 14 (API 34) or higher.

## Data Storage

Album and photo metadata is stored as a serialized object graph in `albums.dat` inside the app's private files directory (`/data/data/com.example.photos70_android/files/albums.dat` on device). Photos added from the gallery are copied into this same private directory so they remain accessible to the app under Android's scoped storage rules.

## Notes

This project was built as a coursework assignment. See [GENAI_USAGE.md](GENAI_USAGE.md) for a breakdown of where and how AI assistance was used during development.
