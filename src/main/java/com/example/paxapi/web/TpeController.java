package com.example.paxapi.web;

import com.example.paxapi.goinsight.domain.GreenpayRecord;
import com.example.paxapi.goinsight.repository.GreenpayRecordRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tpe")
@RequiredArgsConstructor
public class TpeController {

  private final GreenpayRecordRepo repo;

  @GetMapping
  public Page<GreenpayRecord> list(
      @RequestParam(required = false) String q,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size
  ) {
    if (q == null || q.isBlank()) {
      return repo.findAll(PageRequest.of(page, size));
    }
    return repo.findByTerminalSnContainingIgnoreCaseOrMerchantContainingIgnoreCase(
        q, q, PageRequest.of(page, size)
    );
  }
}
