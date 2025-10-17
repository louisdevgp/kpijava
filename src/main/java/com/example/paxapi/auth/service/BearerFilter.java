// src/main/java/com/example/paxapi/auth/BearerFilter.java
package com.example.paxapi.auth.service;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.example.paxapi.auth.utils.JwtUtil;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Component
@Order(1)
public class BearerFilter implements Filter {

  private final JwtUtil jwt;
  private final List<String> open = List.of("/api/auth/login", "/actuator/health");

  public BearerFilter(JwtUtil jwt) { this.jwt = jwt; }

  @Value("${auth.enabled:false}") // par défaut OFF pour la démo
  private boolean authEnabled;

  @Override public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
      throws IOException, ServletException {
    HttpServletRequest http = (HttpServletRequest) req;
    HttpServletResponse out = (HttpServletResponse) res;

    if (!authEnabled || open.stream().anyMatch(p -> http.getRequestURI().startsWith(p))) {
      chain.doFilter(req, res); return;
    }

    String h = http.getHeader("Authorization");
    if (h == null || !h.startsWith("Bearer ")) { out.setStatus(401); return; }

    String token = h.substring(7);
    try {
      Map<String,Object> claims = jwt.parse(token);
      // tu peux mettre le role en attribute si besoin
      http.setAttribute("role", claims.get("role"));
      chain.doFilter(req, res);
    } catch (Exception e) {
      out.setStatus(401);
    }
  }
}
