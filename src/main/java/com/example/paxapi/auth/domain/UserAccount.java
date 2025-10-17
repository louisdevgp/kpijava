// src/main/java/com/example/paxapi/auth/UserAccount.java
package com.example.paxapi.auth.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name = "users")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UserAccount {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable=false, unique=true, length=64)
  private String username;  

  @Column(nullable=false, unique=true, length=255)
  private String email;

  @Column(nullable=false, unique=false, length=100)
  private String fullname;

  @Column(name="password_hash", nullable=false, length=100)
  private String passwordHash;

  @Column(nullable=false, length=32)
  private String role; // 'OPS_MANAGER'

  @Column(name="is_active", nullable=false)
  private boolean isActive = true;
}
