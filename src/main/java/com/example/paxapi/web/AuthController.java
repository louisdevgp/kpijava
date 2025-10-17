// src/main/java/com/example/paxapi/web/AuthController.java
package com.example.paxapi.web;

import com.example.paxapi.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService svc;

  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody AuthService.LoginReq req) {
    return ResponseEntity.ok(svc.login(req));
  }
}
