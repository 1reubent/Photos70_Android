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
   * The set of tags associated with the photo.
   */
  private Set<Tag> tags;

  /**
   * Constructs a Photo object with the specified file path.
   * The date taken is initialized based on the file's last modified timestamp.
   *
   * @param filePath the file path of the photo
   */
  public Photo(String filePath) {
    this.path = filePath;
    this.caption = "";
    this.tags = new HashSet<>();
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

  /**
   * Gets the set of tags associated with the photo.
   *
   * @return the set of tags
   */
  public Set<Tag> getTags() {
    return tags;
  }

  /**
   * Adds a tag to the photo.
   *
   * @param tag the tag to add
   * @throws IllegalStateException if the tag already exists
   */
  public void addTag(Tag tag) {
    if (!tags.add(tag)) {
      throw new IllegalStateException("'" + tag + "' tag already exists.");
    }
  }

  /**
   * Checks if the photo has the specified tag.
   *
   * @param tag the tag to check
   * @return true if the photo has the tag, false otherwise
   */
  public boolean hasTag(Tag tag) {
    return tags.contains(tag);
  }

  /**
   * Removes a tag from the photo.
   *
   * @param tag the tag to remove
   */
  public void removeTag(Tag tag) {
    tags.remove(tag);
  }

  /**
   * Checks if the photo has a tag of the specified type by name.
   *
   * @param name the name of the tag type
   * @return true if the photo has a tag of the specified type, false otherwise
   */
  public boolean hasTagType(String name) {
    for (Tag tag : tags) {
      if (tag.getName().equalsIgnoreCase(name)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Checks if the photo has a tag of the specified type.
   *
   * @param tagType the tag type to check
   * @return true if the photo has a tag of the specified type, false otherwise
   */
  public boolean hasTagType(TagType tagType) {
    for (Tag tag : tags) {
      if (tag.getName().equalsIgnoreCase(tagType.getName())) {
        return true;
      }
    }
    return false;
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
