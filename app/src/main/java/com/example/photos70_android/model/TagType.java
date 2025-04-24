package com.example.photos70_android.model;

import java.io.Serializable;

/**
 * Represents a type of tag that can be associated with a photo.
 * A tag type has a name and specifies whether it allows multiple values.
 * This class is serializable to allow persistence.
 * @author Reuben Thomas, Ryan Zaken
 */
public class TagType implements Serializable {
  private static final long serialVersionUID = 1L;

  /**
   * The name of the tag type (e.g., "location", "person").
   * This is case-insensitive.
   */
  private String name;

  /**
   * Indicates whether the tag type allows multiple values.
   */
  private boolean allowsMultipleValues;

  /**
   * Constructs a new TagType with the specified name and value allowance.
   *
   * @param name the name of the tag type
   * @param allowsMultipleValues whether the tag type allows multiple values
   */
  public TagType(String name, boolean allowsMultipleValues) {
    this.name = name.toLowerCase(); // Ensure case-insensitivity
    this.allowsMultipleValues = allowsMultipleValues;
  }

  /**
   * Gets the name of the tag type.
   *
   * @return the name of the tag type
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the name of the tag type.
   *
   * @param name the new name of the tag type
   */
  public void setName(String name) {
    this.name = name.toLowerCase(); // Ensure case-insensitivity
  }

  /**
   * Checks if the tag type allows multiple values.
   *
   * @return true if the tag type allows multiple values, false otherwise
   */
  public boolean isMultiValue() {
    return allowsMultipleValues;
  }

  /**
   * Returns a string representation of the tag type, including its name
   * and whether it allows multiple values.
   *
   * @return a string representation of the tag type
   */
  @Override
  public String toString() {
    return name + (allowsMultipleValues ? " (multivalue)" : "");
  }

  /**
   * Checks if this tag type is equal to another object. Two tag types are considered equal
   * if they have the same name.
   *
   * @param obj the object to compare with
   * @return true if the tag types are equal, false otherwise
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null || getClass() != obj.getClass()) return false;
    TagType tagType = (TagType) obj;
    return name.equals(tagType.name);
  }

  /**
   * Computes the hash code for the tag type based on its name.
   *
   * @return the hash code of the tag type
   */
  @Override
  public int hashCode() {
    return name.hashCode();
  }
}