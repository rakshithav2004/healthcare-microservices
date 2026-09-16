package com.healthcare.auth.model;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

  @Id private String id;

  @Indexed(unique = true)
  private String email;

  private String password; // stored as BCrypt hash, never plain text

  private Role role;

  private LocalDateTime createdAt;
}
