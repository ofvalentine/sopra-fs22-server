package ch.uzh.ifi.hase.soprafs22.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

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
  @Temporal(TemporalType.DATE)
  private Date creationDate;

  @Column(nullable = false)
  private boolean loggedIn = true;

  @Temporal(TemporalType.DATE)
  private Date birthday;

}
