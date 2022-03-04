package ch.uzh.ifi.hase.soprafs22.controller;

import ch.uzh.ifi.hase.soprafs22.model.User;
import ch.uzh.ifi.hase.soprafs22.model.UserDTO;
import ch.uzh.ifi.hase.soprafs22.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class UserControllerTest {

  ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private UserService userService;

  @Test
  public void givenUsers_whenGetUsers_returnUsersList() throws Exception {
    User user1 = new User();
    user1.setUsername("user-1");
    user1.setLoggedIn(false);
    User user2 = new User();
    user2.setUsername("user-2");
    user2.setLoggedIn(true);
    List<User> allUsers = List.of(user1, user2);
    given(userService.getAllUsers()).willReturn(allUsers);

    MockHttpServletRequestBuilder getRequest = get("/users").contentType(MediaType.APPLICATION_JSON);
    mockMvc.perform(getRequest).andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0].username", is(user1.getUsername())))
        .andExpect(jsonPath("$[0].loggedIn", is(user1.isLoggedIn())))
        .andExpect(jsonPath("$[1].username", is(user2.getUsername())))
        .andExpect(jsonPath("$[1].loggedIn", is(user2.isLoggedIn())));
  }

  @Test
  public void createUser_validInput_returnCreatedUser() throws Exception {
    User user = new User();
    user.setId(1L);
    user.setUsername("user");
    user.setPassword("test");
    user.setLoggedIn(true);

    UserDTO userDTO = new UserDTO();
    userDTO.setUsername("user");
    userDTO.setPassword("test");

    given(userService.isExistingUsername("user")).willReturn(false);
    given(userService.createUser(Mockito.any())).willReturn(user);

    MockHttpServletRequestBuilder postRequest = post("/users")
        .contentType(MediaType.APPLICATION_JSON)
        .content(asJsonString(userDTO));

    mockMvc.perform(postRequest)
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id", is(user.getId().intValue())))
        .andExpect(jsonPath("$.username", is(user.getUsername())))
        .andExpect(jsonPath("$.loggedIn", is(user.isLoggedIn())));
  }

  @Test
  public void createUser_existingUsername_throwException() throws Exception {
    UserDTO userDTO = new UserDTO();
    userDTO.setUsername("user");
    userDTO.setPassword("test");

    given(userService.isExistingUsername("user")).willReturn(true);

    MockHttpServletRequestBuilder postRequest = post("/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(userDTO));

    mockMvc.perform(postRequest).andExpect(status().isConflict());
  }

  @Test
  public void createUser_nullCredentials_throwException() throws Exception {
    UserDTO userDTO = new UserDTO();
    userDTO.setUsername("user");

    given(userService.isExistingUsername("user")).willReturn(false);

    MockHttpServletRequestBuilder postRequest = post("/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(userDTO));

    mockMvc.perform(postRequest).andExpect(status().isBadRequest());
  }

  @Test
  public void getById_userFound_returnUser() throws Exception {
    User user = new User();
    user.setId(1L);
    user.setUsername("user");
    user.setPassword("test");
    user.setLoggedIn(true);

    given(userService.getUserById(1L)).willReturn(Optional.of(user));
    MockHttpServletRequestBuilder getRequest = get("/users/1").contentType(MediaType.APPLICATION_JSON);

    mockMvc.perform(getRequest)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(user.getId().intValue())))
            .andExpect(jsonPath("$.username", is(user.getUsername())))
            .andExpect(jsonPath("$.loggedIn", is(user.isLoggedIn())));
  }

  @Test
  public void getById_userNotFound_throwException() throws Exception {
    given(userService.getUserById(1L)).willReturn(Optional.empty());

    MockHttpServletRequestBuilder getRequest = get("/users/1").contentType(MediaType.APPLICATION_JSON);
    mockMvc.perform(getRequest).andExpect(status().isNotFound());
  }

  @Test
  public void givenUser_updateUserData() throws Exception {
    User user = new User();
    user.setId(1L);
    user.setUsername("user");
    user.setPassword("test");
    user.setLoggedIn(true);

    UserDTO userDTO = new UserDTO();
    userDTO.setId(1L);
    userDTO.setUsername("user");
    userDTO.setBirthday(new Date());
    userDTO.setLoggedIn(true);

    given(userService.getUserById(1L)).willReturn(Optional.of(user));

    MockHttpServletRequestBuilder putRequest = put("/users/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(userDTO));

    mockMvc.perform(putRequest).andExpect(status().isNoContent());
  }

  @Test
  public void updateUserData_userNotFound_throwException() throws Exception {
    UserDTO userDTO = new UserDTO();
    userDTO.setId(1L);
    userDTO.setUsername("user");
    userDTO.setBirthday(new Date());
    userDTO.setLoggedIn(true);

    given(userService.getUserById(1L)).willReturn(Optional.empty());

    MockHttpServletRequestBuilder putRequest = put("/users/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(userDTO));

    mockMvc.perform(putRequest).andExpect(status().isNotFound());
  }

  @Test
  public void givenUser_doLogin_returnUser() throws Exception {
    User user = new User();
    user.setId(1L);
    user.setUsername("user");
    user.setLoggedIn(true);

    UserDTO userDTO = new UserDTO();
    userDTO.setUsername("user");
    userDTO.setPassword("test");

    given(userService.getUserByCredentialsAndLogIn(userDTO)).willReturn(Optional.of(user));

    MockHttpServletRequestBuilder postRequest = post("/users/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(userDTO));

    mockMvc.perform(postRequest)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(user.getId().intValue())))
            .andExpect(jsonPath("$.username", is(user.getUsername())))
            .andExpect(jsonPath("$.loggedIn", is(user.isLoggedIn())));
  }

  @Test
  public void login_userNotFound_throwException() throws Exception {
    UserDTO userDTO = new UserDTO();
    userDTO.setUsername("user");
    userDTO.setPassword("test");

    given(userService.getUserByCredentialsAndLogIn(userDTO)).willReturn(Optional.empty());

    MockHttpServletRequestBuilder postRequest = post("/users/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(userDTO));

    mockMvc.perform(postRequest).andExpect(status().isUnauthorized());
  }

  @Test
  public void register_validInput_returnCreatedUser() throws Exception {
    User user = new User();
    user.setId(1L);
    user.setUsername("user");
    user.setPassword("test");
    user.setLoggedIn(true);

    UserDTO userDTO = new UserDTO();
    userDTO.setUsername("user");
    userDTO.setPassword("test");

    given(userService.isExistingUsername("user")).willReturn(false);
    given(userService.createUser(Mockito.any())).willReturn(user);

    MockHttpServletRequestBuilder postRequest = post("/users/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(userDTO));

    mockMvc.perform(postRequest)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(user.getId().intValue())))
            .andExpect(jsonPath("$.username", is(user.getUsername())))
            .andExpect(jsonPath("$.loggedIn", is(user.isLoggedIn())));
  }

  @Test
  public void register_existingUsername_throwException() throws Exception {
    UserDTO userDTO = new UserDTO();
    userDTO.setUsername("user");
    userDTO.setPassword("test");

    given(userService.isExistingUsername("user")).willReturn(true);

    MockHttpServletRequestBuilder postRequest = post("/users/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(userDTO));

    mockMvc.perform(postRequest).andExpect(status().isConflict());
  }

  @Test
  public void isAvailableUsername_true() throws Exception {
    UserDTO userDTO = new UserDTO();
    userDTO.setUsername("user");
    userDTO.setPassword("test");

    given(userService.isExistingUsername("user")).willReturn(false);

    MockHttpServletRequestBuilder postRequest = post("/users/validate")
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(userDTO));
    mockMvc.perform(postRequest).andExpect(status().isOk()).andExpect(jsonPath("$", is(true)));
  }

  @Test
  public void isAvailableUsername_false() throws Exception {
    UserDTO userDTO = new UserDTO();
    userDTO.setUsername("user");
    userDTO.setPassword("test");

    given(userService.isExistingUsername("user")).willReturn(true);

    MockHttpServletRequestBuilder postRequest = post("/users/validate")
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(userDTO));
    mockMvc.perform(postRequest).andExpect(status().isOk()).andExpect(jsonPath("$", is(false)));
  }

  /**
   * Helper Method for converting a userDTO object into a JSON string.
   * @param userDTO      userDTO with the intended request content
   * @return string      JSON string representation of the given userDTO
   * @throws ResponseStatusException    when the conversion failed
   */
  private String asJsonString(UserDTO userDTO) {
    try {
      return mapper.writeValueAsString(userDTO);
    } catch (JsonProcessingException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
    }
  }
}