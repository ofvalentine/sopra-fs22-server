package ch.uzh.ifi.hase.soprafs22.service;

import ch.uzh.ifi.hase.soprafs22.model.User;
import ch.uzh.ifi.hase.soprafs22.model.UserDTO;
import ch.uzh.ifi.hase.soprafs22.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Sort;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UserServiceTest {

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private UserService userService;

  @BeforeEach
  public void setup() {
    MockitoAnnotations.openMocks(this);
    doAnswer(invocation -> {
      User createdUser = invocation.getArgument(0);
      if (createdUser.getId() == null)
        createdUser.setId(1L);
      return createdUser;
    }).when(userRepository).save(any());
  }

  @Test
  public void createUser_validInputs_success() {
    UserDTO userDTO = new UserDTO();
    userDTO.setUsername("user");
    userDTO.setPassword("test");
    User createdUser = userService.createUser(userDTO);
    Mockito.verify(userRepository, Mockito.times(1)).save(any());
    assertEquals(userDTO.getUsername(), createdUser.getUsername());
    assertEquals(userDTO.getPassword(), createdUser.getPassword());
    assertTrue(createdUser.isLoggedIn());
  }

  @Test
  public void getById_success() {
    UserDTO userDTO = new UserDTO();
    userDTO.setUsername("user");
    userDTO.setPassword("test");
    User createdUser = userService.createUser(userDTO);
    doReturn(Optional.of(createdUser)).when(userRepository).findById(createdUser.getId());
    Optional<User> foundUser = userService.getUserById(createdUser.getId());
    assertTrue(foundUser.isPresent());
    assertNotNull(foundUser.get().getId());
    assertEquals(userDTO.getUsername(), foundUser.get().getUsername());
    assertTrue(foundUser.get().isLoggedIn());
  }

  @Test
  public void givenUsers_getAllUsers() {
    UserDTO userDTO = new UserDTO();
    userDTO.setUsername("user");
    userDTO.setPassword("test");
    User createdUser = userService.createUser(userDTO);
    doReturn(List.of(createdUser)).when(userRepository).findAll(Sort.by("username"));
    List<User> allUsers = userService.getAllUsers();
    assertEquals(1, allUsers.size());
    assertEquals(createdUser.getId(), allUsers.get(0).getId());
  }

  @Test
  public void givenUser_updateUserData() {
    UserDTO userDTO = new UserDTO();
    userDTO.setUsername("user");
    userDTO.setPassword("test");

    User createdUser = userService.createUser(userDTO);
    assertNotNull(createdUser.getId());
    assertEquals(userDTO.getUsername(), createdUser.getUsername());
    assertNull(createdUser.getBirthday());
    assertTrue(createdUser.isLoggedIn());

    UserDTO updatedUserDTO = new UserDTO();
    updatedUserDTO.setUsername("user-2");
    updatedUserDTO.setBirthday(new Date());
    updatedUserDTO.setLoggedIn(false);

    User updatedUser = userService.updateUserData(createdUser, updatedUserDTO);
    assertEquals(createdUser.getId(), updatedUser.getId());
    assertNotEquals(userDTO.getUsername(), updatedUser.getUsername());
    assertEquals(updatedUserDTO.getUsername(), updatedUser.getUsername());
    assertEquals(updatedUserDTO.getBirthday(), updatedUser.getBirthday());
    assertEquals(updatedUserDTO.isLoggedIn(), updatedUser.isLoggedIn());
  }

  @Test
  public void givenUser_getByCredentialsAndLogIn() {
    UserDTO userDTO = new UserDTO();
    userDTO.setUsername("user");
    userDTO.setPassword("test");
    User createdUser = userService.createUser(userDTO);

    userDTO.setLoggedIn(false);
    User updatedUser = userService.updateUserData(createdUser, userDTO);
    assertFalse(updatedUser.isLoggedIn());

    doAnswer(invocation -> { updatedUser.setLoggedIn(true); return null; }).when(userRepository)
            .findByUsernameAndPasswordAndLogIn(userDTO.getUsername(), userDTO.getPassword());
    doReturn(Optional.of(updatedUser)).when(userRepository)
            .findByUsernameAndPassword(userDTO.getUsername(), userDTO.getPassword());
    Optional<User> foundUser = userService.getUserByCredentialsAndLogIn(userDTO);

    assertTrue(foundUser.isPresent());
    assertEquals(updatedUser.getId(), foundUser.get().getId());
    assertEquals(updatedUser.getUsername(), foundUser.get().getUsername());
    assertTrue(foundUser.get().isLoggedIn());
  }
}
