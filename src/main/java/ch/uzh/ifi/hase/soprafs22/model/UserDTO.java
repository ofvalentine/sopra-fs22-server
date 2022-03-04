package ch.uzh.ifi.hase.soprafs22.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@NoArgsConstructor
@Data
public class UserDTO {

  private Long id;

  private String username;

  private String password;

  private ZonedDateTime birthday;

  private boolean loggedIn = true;
}
