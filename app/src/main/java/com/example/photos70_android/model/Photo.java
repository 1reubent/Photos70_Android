package com.example.photos70_android.model;

import java.io.File;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

/**
 * Represents a photo with a file path, caption, date taken, and associated tags.
 * This class is serializable to allow persistence.
 * @author Reuben Thomas, Ryan Zaken
 */
public class Photo implements Serializable {

  private static final long serialVersionUID = 1L;

  /**
   * The file path of the photo.
   */
  private String path;

  /**
   * The caption of the photo.
   */
  private String caption;

  /**
   * The date and time the photo was taken.
   */
  private LocalDateTime dateTaken;

  /**
   * The set of people tags associated with the photo.
   */
  // Simplified fields for tags
  private Set<String> peopleTags;
  /**
   * The location tag associated with the photo.
   */
  private String locationTag;

  /**
   * Constructs a Photo object with the specified file path.
   * The date taken is initialized based on the file's last modified timestamp.
   *
   * @param filePath the file path of the photo
   */
  public Photo(String filePath) {
    this.path = filePath;
    this.caption = "";
    this.peopleTags = new HashSet<>();
    this.locationTag = null;
    File file = new File(filePath);
    this.dateTaken = LocalDateTime.ofInstant(
            new Date(file.lastModified()).toInstant(), ZoneId.systemDefault()
    );
  }

  /**
   * Gets the name of the photo file.
   *
   * @return the name of the photo file
   */
  public String getName() {
    return new File(path).getName();
  }

  /**
   * Gets the file path of the photo.
   *
   * @return the file path of the photo
   */
  public String getPath() {
    return path;
  }

  /**
   * Gets the caption of the photo.
   *
   * @return the caption of the photo
   */
  public String getCaption() {
    return caption;
  }

  /**
   * Sets the caption of the photo.
   *
   * @param caption the new caption of the photo
   */
  public void setCaption(String caption) {
    this.caption = caption;
  }

  /**
   * Gets the date and time the photo was taken.
   *
   * @return the date and time the photo was taken
   */
  public LocalDateTime getDateTaken() {
    return dateTaken;
  }

  // Methods for managing people tags
  public Set<String> getPeopleTags() {
    return peopleTags;
  }

  public void addPersonTag(String person) {
    peopleTags.add(person.toLowerCase());
  }

  public void removePersonTag(String person) {
    peopleTags.remove(person.toLowerCase());
  }
  //clear all person tags
    public void clearPeopleTags() {
        peopleTags.clear();
    }
  //has person tag
    public boolean hasPersonTag(String person) {
        return peopleTags.contains(person.toLowerCase());
    }

  // Methods for managing location tag
  public String getLocationTag() {
    return locationTag;
  }

  public void setLocationTag(String location) {
    this.locationTag = location.toLowerCase();
  }

  public void clearLocationTag() {
    this.locationTag = null;
  }
  //has location tag
  public boolean hasLocationTag() {
    return locationTag != null;
  }

  //get all tags; return a pair of people and location tags
  public Map<String, Set<String>> getAllTags() {
      Map<String, Set<String>> allTags = new HashMap<>();
      allTags.put("people", peopleTags);
      allTags.put("location", locationTag != null ? Collections.singleton(locationTag) : Collections.emptySet());
      return allTags;
  }


  /**
   * Checks if this photo is equal to another object.
   * Two photos are considered equal if they have the same file path.
   *
   * @param o the object to compare with
   * @return true if the photos are equal, false otherwise
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Photo photo = (Photo) o;
    return Objects.equals(path, photo.path);
  }

  /**
   * Computes the hash code for the photo based on its file path.
   *
   * @return the hash code of the photo
   */
  @Override
  public int hashCode() {
    return Objects.hash(path);
  }

  /**
   * Returns a string representation of the photo, including its name and caption.
   *
   * @return a string representation of the photo
   */
  @Override
  public String toString() {
    return String.format("%s (Caption: %s)", getName(), !caption.isEmpty() ? caption : "No caption");
  }
}
