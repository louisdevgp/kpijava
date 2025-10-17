// src/main/java/com/example/paxapi/auth/JwtUtil.java
package com.example.paxapi.auth.utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.SignatureAlgorithm;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

public class JwtUtil {
  private final byte[] key;
  private final long ttlMillis;

  public JwtUtil(String secret, long ttlMillis) {
    this.key = "mySecretKey123456789012345678902".getBytes(StandardCharsets.UTF_8);
    this.ttlMillis = 86400000; // 24h
  }



  public String create(Map<String, Object> claims, String subject) {
    long now = System.currentTimeMillis();
    return Jwts.builder()
        .setClaims(claims)
        .setSubject(subject)
        .setIssuedAt(new Date(now))
        .setExpiration(new Date(now + ttlMillis))
        .signWith(Keys.hmacShaKeyFor(key), Jwts.SIG.HS256)
        .compact();
  }

    void printKeyLength() {
    System.out.println("Key : " + key.toString());
    System.out.println("Key length: " + key.length);
  }

  public Map<String, Object> parse(String token) {
    return Jwts.parser()
        .setSigningKey(Keys.hmacShaKeyFor(key))
        .build()
        .parseClaimsJws(token)
        .getBody();
  }



}
