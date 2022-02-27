package ch.uzh.ifi.hase.soprafs22.service;

import ch.uzh.ifi.hase.soprafs22.model.User;
import ch.uzh.ifi.hase.soprafs22.model.UserDTO;
import ch.uzh.ifi.hase.soprafs22.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@AllArgsConstructor
@Service
public class UserService {

  private UserRepository userRepository;

  public Optional<User> getUserById(Long userId) {
    return userRepository.findById(userId);
  }

  public User updateUserData(User currentUser, UserDTO updatedUser) {
    currentUser.setUsername(updatedUser.getUsername());
    currentUser.setBirthday(updatedUser.getBirthday());
    currentUser.setLoggedIn(updatedUser.isLoggedIn());
    return userRepository.save(currentUser);
  }

  public List<User> getAllUsers() {
    return userRepository.findAll(Sort.by("username"));
  }

  public User createUser(UserDTO newUserDTO) {
    User newUser = new User();
    newUser.setUsername(newUserDTO.getUsername());
    newUser.setPassword(newUserDTO.getPassword());
    return userRepository.save(newUser);
  }

  public Optional<User> getUserByCredentialsAndLogIn(UserDTO UserDTO) {
    userRepository.findByUsernameAndPasswordAndLogIn(UserDTO.getUsername(), UserDTO.getPassword());
    return userRepository.findByUsernameAndPassword(UserDTO.getUsername(), UserDTO.getPassword());
  }

  public boolean isExistingUsername(String username) {
    return userRepository.existsByUsername(username);
  }
}
