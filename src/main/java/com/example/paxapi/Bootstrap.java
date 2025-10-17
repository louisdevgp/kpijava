// src/main/java/com/example/paxapi/Bootstrap.java
package com.example.paxapi;

import com.example.paxapi.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Bootstrap implements CommandLineRunner {
  private final AuthService authService;

  @Override public void run(String... args) {
    authService.createIfEmpty(); // seed 'ops' / 'changeMe123'
  }
}
