package ch.uzh.ifi.hase.soprafs22;

import ch.uzh.ifi.hase.soprafs22.controller.UserController;
import ch.uzh.ifi.hase.soprafs22.model.User;
import ch.uzh.ifi.hase.soprafs22.model.UserDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class IntegrationTest {

  @Autowired
  private UserController userController;

  @Test
  public void register_validInput_thenRegister_existingUsername_throwException() {
    UserDTO testUser1 = new UserDTO();
    testUser1.setUsername("user");
    testUser1.setPassword("test");

    assertTrue(userController.isAvailableUsername(testUser1));

    UserDTO testUser2 = new UserDTO();
    testUser2.setUsername("user");
    testUser2.setPassword("test-2");

    User createdUser1 = userController.register(testUser1);
    assertNotNull(createdUser1.getId());
    assertEquals(testUser1.getUsername(), createdUser1.getUsername());
    assertThrows(ResponseStatusException.class, () -> userController.register(testUser2));
  }
}
