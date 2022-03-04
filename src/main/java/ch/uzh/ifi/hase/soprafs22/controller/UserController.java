package ch.uzh.ifi.hase.soprafs22.controller;

import ch.uzh.ifi.hase.soprafs22.model.User;
import ch.uzh.ifi.hase.soprafs22.model.UserDTO;
import ch.uzh.ifi.hase.soprafs22.service.UserService;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {

  private UserService userService;

  /**
   * Fetch all stored users. Note that the serialized User object does not contain a 'password' field
   * and both ZonedDateTime fields (birthday, creationDate) are formatted to display the date only.
   * @return  list of serialized User objects
   */
  @GetMapping
  public List<User> getAllUsers() {
    return userService.getAllUsers();
  }

  /**
   * Register a new user with their desired credentials (username, password) if the chosen username is available.
   * @param newUser   DTO object containing the desired credentials
   * @return          serialized User object as stored in the database, if created successfully
   * @throws ResponseStatusException    with status 400, when the username or password are empty
   * @throws ResponseStatusException    with status 409, when the chosen username is not available
   */
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public User createUser(@RequestBody UserDTO newUser) {
    if (StringUtils.isAnyEmpty(newUser.getUsername(), newUser.getPassword()))
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Must provide non-empty username and password");
    if (userService.isExistingUsername(newUser.getUsername()))
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Username " + newUser.getUsername() + " is not available");
    return userService.createUser(newUser);
  }

  /**
   * Find an existing User in the database by its unique ID and return it, if exists.
   * @param userId    ID value to search by
   * @return          serialized User object with the given ID, if exists
   * @throws ResponseStatusException    with status 404, when no user was found for the given ID
   */
  @GetMapping("/{userId}")
  public User getUserById(@PathVariable Long userId) {
    return userService.getUserById(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No user found with ID: " + userId));
  }

  /**
   * Update the stored username and/or birthday values of an existing user. Note that empty values are
   * allowed, however an empty username will be ignored while an empty birthday value will be stored.
   * @param userId        ID value of the existing User to update
   * @param updatedUser   DTO with the updated username and/or birthday values
   * @see #getUserById(Long)    for exceptions
   */
  @PutMapping("/{userId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void updateUserData(@PathVariable Long userId, @RequestBody UserDTO updatedUser) {
    User currentUser = getUserById(userId);
    userService.updateUserData(currentUser, updatedUser);
  }

  /**
   * Check if the given credentials (username and password) exist in the database, and if yes mark
   * the stored User object as logged in and return it.
   * @param user    DTO object containing login credentials
   * @return        serialized User object matching the given credentials, if exists
   * @throws ResponseStatusException    with status 401, when no user was found for the given credentials
   */
  @PostMapping("/login")
  public User login(@RequestBody UserDTO user) {
    return userService.getUserByCredentialsAndLogIn(user)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password"));
  }

  /**
   * Alias for {@link #createUser(UserDTO)} that allows simpler client architecture.
   */
  @PostMapping("/register")
  public User register(@RequestBody UserDTO newUser) {
    return createUser(newUser);
  }

  /**
   * Check if a username is already associated with an existing User in the database.
   * @param newUser   DTO containing a username value
   * @return          true if the username is available, false otherwise
   */
  @PostMapping("/validate")
  public boolean isAvailableUsername(@RequestBody UserDTO newUser) {
    return !userService.isExistingUsername(newUser.getUsername());
  }
}
