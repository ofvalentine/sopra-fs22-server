package ch.uzh.ifi.hase.soprafs22.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Getter
@Setter
@Entity
public class User {

  @Id
  @GeneratedValue
  private Long id;

  @Column(nullable = false, unique = true)
  private String username;

  @JsonIgnore
  @Column(nullable = false)
  private String password;

  @CreationTimestamp
  @Column(nullable = false)
  @JsonFormat(pattern="dd.MM.yyyy")
  private ZonedDateTime creationDate;

  @Column(nullable = false)
  private boolean loggedIn = true;

  @JsonFormat(pattern="dd.MM.yyyy")
  private ZonedDateTime birthday;

}
