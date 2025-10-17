package com.example.paxapi.web;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.paxapi.PaxResellerService;
import com.pax.market.api.sdk.java.api.reseller.ResellerApi.ResellerSearchOrderBy;
import com.pax.market.api.sdk.java.api.reseller.ResellerApi.ResellerStatus;
import com.pax.market.api.sdk.java.api.reseller.dto.ResellerPageDTO;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;

@Validated
@RestController
@RequestMapping("/api/resellers")
@RequiredArgsConstructor
public class ResellerController {

  private final PaxResellerService svc;

  @GetMapping
  public ResponseEntity<ResellerPageDTO> list(
      @RequestParam(name = "pageNo",   defaultValue = "1")  @Min(1)           int pageNo,
      @RequestParam(name = "pageSize", defaultValue = "20") @Min(1) @Max(100) int pageSize,
      @RequestParam(name = "orderBy",  required = false)    ResellerSearchOrderBy orderBy,
      @RequestParam(name = "name",     required = false)    @Size(max = 64)   String name,
      // compat ascendante : si 'name' est absent, on prendra 'keyword'
      @RequestParam(name = "keyword",  required = false)    @Size(max = 64)   String keyword,
      @RequestParam(name = "status",   required = false)    ResellerStatus status
  ) {
    ResellerSearchOrderBy ob = (orderBy != null) ? orderBy : ResellerSearchOrderBy.Name;
    String searchName = (name != null && !name.isBlank()) ? name : keyword;
    ResellerPageDTO data = svc.search(pageNo, pageSize, ob, searchName, status);
    return ResponseEntity.ok(data);
  }
}
