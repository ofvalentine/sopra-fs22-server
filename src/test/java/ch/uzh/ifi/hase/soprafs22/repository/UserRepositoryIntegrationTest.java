package ch.uzh.ifi.hase.soprafs22.repository;

import ch.uzh.ifi.hase.soprafs22.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class UserRepositoryIntegrationTest {

  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private UserRepository userRepository;

  @Test
  public void findByUsernameAndPassword_success() {
    User user = new User();
    user.setUsername("user");
    user.setPassword("test");

    entityManager.persist(user);
    entityManager.flush();

    Optional<User> found = userRepository.findByUsernameAndPassword(user.getUsername(), user.getPassword());

    assertTrue(found.isPresent());
    assertNotNull(found.get().getId());
    assertEquals(found.get().getUsername(), user.getUsername());
    assertEquals(found.get().getPassword(), user.getPassword());
    assertEquals(found.get().isLoggedIn(), user.isLoggedIn());
  }

  @Test
  public void existsByUsername_success() {
    User user = new User();
    user.setUsername("user");
    user.setPassword("test");

    entityManager.persist(user);
    entityManager.flush();

    assertTrue(userRepository.existsByUsername(user.getUsername()));
  }

  @Test
  public void findByUsernameAndPasswordAndLogIn_success() {
    User user = new User();
    user.setUsername("user");
    user.setPassword("test");
    user.setLoggedIn(false);

    entityManager.persist(user);
    entityManager.flush();

    userRepository.findByUsernameAndPasswordAndLogIn(user.getUsername(), user.getPassword());
    Optional<User> found = userRepository.findById(user.getId());

    assertTrue(found.isPresent());
    assertNotNull(found.get().getId());
    assertEquals(found.get().getUsername(), user.getUsername());
    assertEquals(found.get().getPassword(), user.getPassword());
    assertTrue(found.get().isLoggedIn());
  }
}
