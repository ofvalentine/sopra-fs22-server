package ch.uzh.ifi.hase.soprafs22.controller;

import ch.uzh.ifi.hase.soprafs22.model.User;
import ch.uzh.ifi.hase.soprafs22.model.UserDTO;
import ch.uzh.ifi.hase.soprafs22.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {

  private UserService userService;

  @GetMapping
  @ResponseBody
  public List<User> getAllUsers() {
    return userService.getAllUsers();
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @ResponseBody
  public User createUser(@RequestBody UserDTO newUser) {
    if (userService.isExistingUsername(newUser.getUsername()))
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Username " + newUser.getUsername() + " is not available");
    return userService.createUser(newUser);
  }

  @GetMapping("/{userId}")
  @ResponseBody
  public User getUserById(@PathVariable Long userId) {
    return userService.getUserById(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No user found with ID: " + userId));
  }

  @PutMapping("/{userId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void updateUserData(@PathVariable Long userId, @RequestBody UserDTO updatedUser) {
    User currentUser = getUserById(userId);
    userService.updateUserData(currentUser, updatedUser);
  }

  @RequestMapping("/login")
  @ResponseBody
  public User login(@RequestBody UserDTO user) {
    return userService.getUserByCredentialsAndLogIn(user)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password"));
  }

  @RequestMapping("/register")
  @ResponseBody
  public User register(@RequestBody UserDTO newUser) {
    return createUser(newUser);
  }

  @GetMapping("/validate/{username}")
  @ResponseBody
  public boolean isAvailableUsername(@PathVariable String username) {
    return !userService.isExistingUsername(username);
  }
}
