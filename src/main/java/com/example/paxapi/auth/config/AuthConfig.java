// src/main/java/com/example/paxapi/auth/AuthConfig.java
package com.example.paxapi.auth.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.paxapi.auth.utils.JwtUtil;

@Configuration
public class AuthConfig {
  @Bean
  public JwtUtil jwtUtil(
      @Value("${auth.jwt.secret:mySecretKey123456789012345678902}") String secret,
      @Value("${auth.jwt.ttlMillis:86400000}") long ttlMillis // 24h
  ) {
    return new JwtUtil(secret, ttlMillis);
  }
}
