// src/main/java/com/example/paxapi/auth/UserAccountRepo.java
package com.example.paxapi.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.paxapi.auth.domain.UserAccount;

import java.util.Optional;

public interface UserAccountRepo extends JpaRepository<UserAccount, Long> {
  Optional<UserAccount> findByEmail(String email);
}
