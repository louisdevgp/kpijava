package com.example.paxapi.kpi.web;

import com.example.paxapi.kpi.domain.KpiPolicy;
import com.example.paxapi.kpi.service.KpiPolicyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/kpi/policies")
@RequiredArgsConstructor
public class KpiController {

  private final KpiPolicyService svc;

  @GetMapping
  public Page<KpiPolicy> list(Pageable pageable) {
    return svc.list(pageable);
  }

  @GetMapping("/{id}")
  public ResponseEntity<KpiPolicy> get(@PathVariable Long id) {
    return svc.get(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
  }

  @GetMapping("/latest")
  public ResponseEntity<KpiPolicy> latest() {
    return svc.latest().map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
  }

  @PostMapping
  public ResponseEntity<KpiPolicy> create(@RequestBody KpiPolicy body) {
    return ResponseEntity.status(HttpStatus.CREATED).body(svc.create(body));
  }

  @PutMapping("/{id}")
  public ResponseEntity<KpiPolicy> update(@PathVariable Long id, @RequestBody KpiPolicy body) {
    return ResponseEntity.ok(svc.update(id, body));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    svc.delete(id);
    return ResponseEntity.noContent().build();
  }

  // Export JSON (pour bouton "Exporter" côté front)
  @GetMapping("/{id}/export")
  public ResponseEntity<byte[]> export(@PathVariable Long id) {
    var policy = svc.get(id).orElse(null);
    if (policy == null) return ResponseEntity.notFound().build();
    String json = toJson(policy);
    return ResponseEntity.ok()
        .contentType(MediaType.APPLICATION_JSON)
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=policy-" + id + ".json")
        .body(json.getBytes(StandardCharsets.UTF_8));
  }

  private String toJson(KpiPolicy p) {
    // Simple (pas d’ObjectMapper pour éviter les deps ici)
    // Si tu as Jackson, remplace par new ObjectMapper().writeValueAsString(p)
    return "{ \"id\":" + p.getId() + " }";
  }
}
