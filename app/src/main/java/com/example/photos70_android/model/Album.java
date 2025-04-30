package com.example.photos70_android.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Represents an album containing photos. Provides functionality to manage photos,
 * retrieve photos by various criteria, and handle album metadata such as name and date range.
 * This class is serializable to allow persistence.
 * @author Reuben Thomas, Ryan Zaken
 */
public class Album implements Serializable {
  private static final long serialVersionUID = 1L;

  /**
   * Indicates whether the stock album is being initialized.
   */
  private static boolean isInitializingStock = false;

  /**
   * The name of the album.
   */
  private String name;
  /**
   * The list of photos in the album.
   */
  private List<Photo> photos;

  /**
   * Constructs an Album with the specified name.
   *
   * @param name the name of the album
   */
  public Album(String name) {
    this.name = name;
    this.photos = new ArrayList<>();
    System.out.println("Album created: " + name);
  }

  /**
   * Sets whether the stock album is being initialized.
   *
   * @param initializing true if initializing stock, false otherwise
   */
  public static void setInitializingStock(boolean initializing) {
    isInitializingStock = initializing;
  }

  /**
   * Gets the name of the album.
   *
   * @return the name of the album
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the name of the album.
   *
   * @param name the new name of the album
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Gets the list of photos in the album.
   *
   * @return the list of photos
   */
  public List<Photo> getPhotos() {
    return photos;
  }

  /**
   * Checks if the album contains the specified photo.
   *
   * @param photo the photo to check
   * @return true if the photo exists in the album, false otherwise
   */
  public boolean hasPhoto(Photo photo) {
    return photos.contains(photo);
  }

  /**
   * Retrieves a photo by its file path.
   *
   * @param path the file path of the photo
   * @return the photo if found, or null if not found
   */
  public Photo getPhoto(String path) {
    for (Photo photo : photos) {
      if (photo.getPath().equals(path)) {
        return photo;
      }
    }
    return null;
  }

//  TODO: get photos by people or location tags

//  /**
//   * Retrieves all photos in the album that have the specified tag.
//   *
//   * @param tag the tag to filter photos by
//   * @return a set of photos with the specified tag
//   */
//  public Set<Photo> getPhotosByTag() {
//    Set<Photo> taggedPhotos = new HashSet<>();
//    for (Photo photo : photos) {
//      if (photo.hasTag(tag)) {
//        taggedPhotos.add(photo);
//      }
//    }
//    return taggedPhotos;
//  }

  /**
   * Retrieves all photos in the album that were taken within the specified date range.
   *
   * @param start the start of the date range
   * @param end the end of the date range
   * @return a set of photos within the specified date range
   */
  public Set<Photo> getPhotosInDateRange(LocalDateTime start, LocalDateTime end) {
    Set<Photo> dateRangePhotos = new HashSet<>();
    for (Photo photo : photos) {
      if (photo.getDateTaken().isAfter(start) && photo.getDateTaken().isBefore(end)) {
        dateRangePhotos.add(photo);
      }
    }
    return dateRangePhotos;
  }

  /**
   * Adds a photo to the album.
   *
   * @param photo the photo to add
   * @throws IllegalStateException if adding photos to the stock album is not allowed
   * @throws IllegalArgumentException if the photo already exists in the album
   */
  public void addPhoto(Photo photo) {
    if (name.equalsIgnoreCase("stock") && !isInitializingStock) {
      throw new IllegalStateException("Cannot add photos to stock album");
    }
    if (photos.contains(photo)) {
      throw new IllegalArgumentException("Photo already exists in the album!");
    }
    photos.add(photo);
  }

  /**
   * Removes a photo from the album.
   *
   * @param photo the photo to remove
   * @throws IllegalStateException if removing photos from the stock album is not allowed
   */
  public void removePhoto(Photo photo) {
    if (name.equalsIgnoreCase("stock") && !isInitializingStock) {
      throw new IllegalStateException("Cannot remove stock photos from stock album");
    }
    photos.remove(photo);
  }

  /**
   * Gets the number of photos in the album.
   *
   * @return the number of photos
   */
  public int getPhotoCount() {
    return photos.size();
  }

  /**
   * Gets the date range of the photos in the album.
   *
   * @return a string representing the date range, or "No photos" if the album is empty
   */
  public String getDateRange() {
    if (photos.isEmpty()) {
      return "No photos";
    }
    return getEarliestDate().toString() + " -- " + getLatestDate().toString();
  }

  /**
   * Gets the earliest date a photo was taken in the album.
   *
   * @return the earliest date, or null if the album is empty
   */
  public LocalDateTime getEarliestDate() {
    return photos.stream().map(Photo::getDateTaken).min(LocalDateTime::compareTo).orElse(null);
  }

  /**
   * Gets the latest date a photo was taken in the album.
   *
   * @return the latest date, or null if the album is empty
   */
  public LocalDateTime getLatestDate() {
    return photos.stream().map(Photo::getDateTaken).max(LocalDateTime::compareTo).orElse(null);
  }

  /**
   * Returns a string representation of the album, including its name, photo count, and date range.
   *
   * @return a string representation of the album
   */
  @Override
  public String toString() {
    return String.format("Name: %s\nPhotos: %d", name, getPhotoCount());
  }

  /**
   * Checks if this album is equal to another object. Two albums are considered equal
   * if they have the same name.
   *
   * @param o the object to compare with
   * @return true if the albums are equal, false otherwise
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Album)) return false;
    Album album = (Album) o;
    return Objects.equals(name, album.name);
  }

  /**
   * Computes the hash code for the album based on its name.
   *
   * @return the hash code of the album
   */
  @Override
  public int hashCode() {
    return Objects.hash(name);
  }

}
