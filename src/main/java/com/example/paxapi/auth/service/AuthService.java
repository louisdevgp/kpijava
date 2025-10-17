// src/main/java/com/example/paxapi/auth/AuthService.java
package com.example.paxapi.auth.service;

import lombok.Builder;
import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.paxapi.auth.repository.UserAccountRepo;

import java.util.Map;

import com.example.paxapi.auth.domain.UserAccount;
import com.example.paxapi.auth.utils.JwtUtil;

@Service
@RequiredArgsConstructor
@Builder
public class AuthService {

    private final UserAccountRepo repo;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    private final JwtUtil jwt;


    public record LoginReq(String email, String password) {

    }

    public record LoginResp(String token, String email, String role) {

    }

    public LoginResp login(LoginReq req) {
        var user = repo.findByEmail(req.email())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));
        if (!user.isActive() || !encoder.matches(req.password(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid credentials");
        }

        String token = jwt.create(Map.of("role", user.getRole()), user.getEmail());
        return new LoginResp(token, user.getEmail(), user.getRole());
    }

    // utilitaire pour créer un user (pour seed)
    public void createIfEmpty() {
        if (repo.count() == 0) {
            var u = UserAccount.builder()
                    .email("ops@example.com")
                    .fullname("Operations Manager")
                    .username("ops")
                    .passwordHash(encoder.encode("changeMe123"))
                    .role("OPS_MANAGER")
                    .isActive(true)
                    .build();
            repo.save(u);
        }
    }
}
