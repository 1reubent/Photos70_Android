package com.example.photos70_android.model;


import java.io.*;
import java.util.*;

/**
 * Represents a list of users, providing functionality to manage users
 * such as adding, deleting, and retrieving users.
 * This class is serializable.
 * @author Reuben Thomas, Ryan Zaken
 */
public class UserList implements Serializable {
  private static final long serialVersionUID = 1L;

  /**
   * A map of usernames to their corresponding User objects.
   */
  private Map<String, User> users;

  /**
   * Constructs an empty UserList.
   */
  public UserList() {
    users = new HashMap<>();
  }

  /**
   * Adds a new user to the list by username.
   *
   * @param username the username of the new user
   * @return the newly created User object
   * @throws IllegalArgumentException if a user with the given username already exists
   */
  public User addUser(String username) {
    if (hasUser(username)) throw new IllegalArgumentException("User already exists: " + username);
    User new_user = new User(username);
    users.put(username, new_user);
    return new_user;
  }

  /**
   * Adds a new user to the list using a User object.
   *
   * @param user the User object to add
   * @return the added User object, or null if a user with the same username already exists
   */
  public User addUser(User user) {
    if (hasUser(user.getUsername())) return null;
    users.put(user.getUsername(), user);
    return user;
  }

  /**
   * Deletes a user from the list by username.
   *
   * @param username the username of the user to delete
   * @return true if the user was deleted, false if the user was not found
   */
  public boolean deleteUser(String username) {
    return users.remove(username) != null;
  }

  /**
   * Retrieves a user by their username.
   *
   * @param username the username of the user to retrieve
   * @return the User object, or null if no user with the given username exists
   */
  public User getUser(String username) {
    return users.get(username);
  }

  /**
   * Retrieves all users in the list.
   *
   * @return a collection of all User objects
   */
  public Collection<User> getAllUsers() {
    return users.values();
  }

  /**
   * Retrieves all usernames in the list.
   *
   * @return a set of all usernames
   */
  public Set<String> getAllUsernames() {
    return users.keySet();
  }

  /**
   * Checks if a user with the given username exists in the list.
   *
   * @param username the username to check
   * @return true if the user exists, false otherwise
   */
  public boolean hasUser(String username) {
    return users.containsKey(username);
  }
}