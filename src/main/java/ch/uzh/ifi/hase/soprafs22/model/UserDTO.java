package ch.uzh.ifi.hase.soprafs22.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@NoArgsConstructor
@Data
public class UserDTO {

  private Long id;

  private String username;

  private String password;

  private Date birthday;

  private boolean loggedIn = true;
}
