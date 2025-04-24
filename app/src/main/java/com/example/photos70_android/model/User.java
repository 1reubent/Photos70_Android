package com.example.photos70_android.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Represents a user in the application. A user has a username, a collection of albums,
 * and a list of user-specific tag types.
 * This class is serializable to allow persistence.
 * @author Reuben Thomas, Ryan Zaken
 */
public class User implements Serializable {
  private static final long serialVersionUID = 1L;

  /**
   * The username of the user.
   */
  private String username;

  /**
   * A map of album names to their corresponding Album objects.
   */
  private Map<String, Album> albums;

  /**
   * A list of tag types specific to the user.
   */
  private List<TagType> myTagTypes;

  /**
   * Constructs a new User with the specified username.
   * Initializes the albums and tag types, and adds default tag types.
   *
   * @param username the username of the user
   */
  public User(String username) {
    this.username = username;
    this.albums = new HashMap<>();
    this.myTagTypes = new ArrayList<>();
    // Initialize default tag types
    addTagType("location", false);
    addTagType("people", true);
  }

  /**
   * Retrieves all photos that have a specific tag.
   *
   * @param tag the tag to search for
   * @return a list of photos with the specified tag
   */
  public List<Photo> getPhotosWithSingleTag(Tag tag) {
    Set<Photo> taggedPhotos = new HashSet<>();
    for (Album album : albums.values()) {
      taggedPhotos.addAll(album.getPhotosByTag(tag));
    }
    return new ArrayList<>(taggedPhotos);
  }

  /**
   * Retrieves all photos that have both of the specified tags.
   *
   * @param tag1 the first tag
   * @param tag2 the second tag
   * @return a list of photos with both tags
   */
  public List<Photo> getPhotosWithBothTags(Tag tag1, Tag tag2) {
    Set<Photo> photosWithTag1 = new HashSet<>();
    Set<Photo> photosWithTag2 = new HashSet<>();

    for (Album album : albums.values()) {
      photosWithTag1.addAll(album.getPhotosByTag(tag1));
      photosWithTag2.addAll(album.getPhotosByTag(tag2));
    }

    photosWithTag1.retainAll(photosWithTag2); // Retain only photos present in both sets
    return new ArrayList<>(photosWithTag1); // Convert the result to a list
  }

  /**
   * Retrieves all photos that have either of the specified tags.
   *
   * @param tag1 the first tag
   * @param tag2 the second tag
   * @return a list of photos with either tag
   */
  public List<Photo> getPhotosWithEitherTag(Tag tag1, Tag tag2) {
    Set<Photo> photosWithTags = new HashSet<>();

    for (Album album : albums.values()) {
      photosWithTags.addAll(album.getPhotosByTag(tag1));
      photosWithTags.addAll(album.getPhotosByTag(tag2));
    }

    return new ArrayList<>(photosWithTags);
  }

  /**
   * Retrieves all photos within a specified date range.
   *
   * @param start the start of the date range
   * @param end   the end of the date range
   * @return a list of photos within the date range
   */
  public List<Photo> getPhotosInDateRange(LocalDateTime start, LocalDateTime end) {
    Set<Photo> dateRangePhotos = new HashSet<>();
    for (Album album : albums.values()) {
      dateRangePhotos.addAll(album.getPhotosInDateRange(start, end));
    }
    return new ArrayList<>(dateRangePhotos);
  }

  /**
   * Retrieves all albums that contain a specific photo.
   *
   * @param photo the photo to search for
   * @return a list of albums containing the photo
   */
  public List<Album> getAlbumsContainingPhoto(Photo photo) {
    List<Album> containingAlbums = new ArrayList<>();
    for (Album album : albums.values()) {
      if (album.hasPhoto(photo)) {
        containingAlbums.add(album);
      }
    }
    return containingAlbums;
  }

  /**
   * Gets the username of the user.
   *
   * @return the username
   */
  public String getUsername() {
    return username;
  }

  /**
   * Gets all albums of the user.
   *
   * @return a collection of albums
   */
  public Collection<Album> getAlbums() {
    return albums.values();
  }

  /**
   * Gets the names of all albums of the user.
   *
   * @return a set of album names
   */
  public Set<String> getAlbumNames() {
    return albums.keySet();
  }

  /**
   * Retrieves an album by its name.
   *
   * @param name the name of the album
   * @return the album with the specified name, or null if not found
   */
  public Album getAlbum(String name) {
    return albums.get(name);
  }

  /**
   * Gets all tag types defined by the user.
   *
   * @return a list of tag types
   */
  public List<TagType> getAllTagTypes() {
    return myTagTypes;
  }

  /**
   * Retrieves a tag type by its name.
   *
   * @param name the name of the tag type
   * @return the tag type with the specified name, or null if not found
   */
  public TagType getTagType(String name) {
    return myTagTypes.stream()
            .filter(tagType -> tagType.getName().equalsIgnoreCase(name))
            .findFirst()
            .orElse(null);
  }

  /**
   * Adds a new tag type with the specified name and multi-value allowance.
   *
   * @param name         the name of the tag type
   * @param isMultiValue whether the tag type allows multiple values
   * @return true if the tag type was added, false if it already exists
   */
  public boolean addTagType(String name, boolean isMultiValue) {
    if (isTagTypeDefined(name)) return false;
    TagType newTagType = new TagType(name, isMultiValue);
    myTagTypes.add(newTagType);
    return true;
  }

  /**
   * Adds a new tag type from a TagType object.
   *
   * @param tagType the tag type to add
   * @return true if the tag type was added, false if it already exists
   */
  public boolean addTagType(TagType tagType) {
    if (!isTagTypeDefined(tagType)) return false;
    myTagTypes.add(tagType);
    return true;
  }

  /**
   * Removes a tag type by its name.
   *
   * @param name the name of the tag type to remove
   * @return true if the tag type was removed, false if it was not found
   */
  public boolean removeTagType(String name) {
    TagType tagType = getTagType(name);
    if (tagType == null) return false;
    return myTagTypes.remove(tagType);
  }

  /**
   * Checks if a tag type is defined by its name.
   *
   * @param name the name of the tag type
   * @return true if the tag type is defined, false otherwise
   */
  public boolean isTagTypeDefined(String name) {
    return getTagType(name) != null;
  }

  /**
   * Checks if a tag type is defined by a TagType object.
   *
   * @param tagType the tag type to check
   * @return true if the tag type is defined, false otherwise
   */
  public boolean isTagTypeDefined(TagType tagType) {
    return getTagType(tagType.getName()) != null;
  }

  /**
   * Adds a new album with the specified name.
   *
   * @param name the name of the album
   * @return the newly created album, or null if an album with the same name already exists
   */
  public Album addAlbum(String name) {
    if (albums.containsKey(name)) return null;
    Album newAlbum = new Album(name);
    albums.put(name, newAlbum);
    return newAlbum;
  }

  /**
   * Deletes an album by its name.
   *
   * @param name the name of the album to delete
   * @return true if the album was deleted, false if it was not found
   */
  public boolean deleteAlbum(String name) {
    return albums.remove(name) != null;
  }

  /**
   * Renames an album.
   *
   * @param oldName the current name of the album
   * @param newName the new name for the album
   * @return true if the album was renamed, false otherwise
   */
  public boolean renameAlbum(String oldName, String newName) {
    if (!albums.containsKey(oldName) || albums.containsKey(newName)) return false;
    Album album = albums.remove(oldName);
    album.setName(newName);
    albums.put(newName, album);
    return true;
  }

  /**
   * Returns a string representation of the user, including the username,
   * albums, and tag types.
   *
   * @return a string representation of the user
   */
  @Override
  public String toString() {
    return "User{" +
            "username='" + username + '\'' +
            ", albums=" + albums +
            ", myTagTypes=" + myTagTypes +
            '}';
  }
}