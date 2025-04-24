package com.example.photos70_android.model;

import java.io.Serializable;
import java.util.*;

/**
 * Represents a tag associated with a photo. A tag consists of a type and a set of values.
 * This class is serializable to allow persistence.
 * @author Reuben Thomas, Ryan Zaken
 */
public class Tag implements Serializable {
  private static final long serialVersionUID = 1L;

  /**
   * The type of the tag (e.g., location, person, etc.).
   */
  private TagType type;

  /**
   * The set of values associated with the tag.
   * For example, a "location" tag might have values like "Paris" or "New York".
   */
  private Set<String> values;

  /**
   * Constructs a Tag object with the specified type and initial value.
   *
   * @param type  the type of the tag
   * @param value the initial value of the tag
   */
  public Tag(TagType type, String value) {
    this.type = type;
    this.values = new HashSet<>();
    this.values.add(value.toLowerCase());
  }

  /**
   * Gets the name of the tag type.
   *
   * @return the name of the tag type
   */
  public String getName() {
    return type.getName();
  }

  /**
   * Gets the set of values associated with the tag.
   *
   * @return the set of values
   */
  public Set<String> getValues() {
    return values;
  }

  /**
   * Checks if this tag is equal to another object. Two tags are considered equal
   * if they have the same type and the same set of values.
   *
   * @param obj the object to compare with
   * @return true if the tags are equal, false otherwise
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (!(obj instanceof Tag)) return false;
    Tag other = (Tag) obj;
    return type.equals(other.type) && values.equals(other.values);
  }

  /**
   * Computes the hash code for the tag based on its type and values.
   *
   * @return the hash code of the tag
   */
  @Override
  public int hashCode() {
    return Objects.hash(type, values);
  }

  /**
   * Returns a string representation of the tag, including its type and values.
   *
   * @return a string representation of the tag
   */
  @Override
  public String toString() {
    return type.getName() + "=" + String.join(", ", values);
  }
}