package com.example.paxapi.goinsight.repository;

import com.example.paxapi.goinsight.domain.GreenpayRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GreenpayRecordRepo extends JpaRepository<GreenpayRecord, String> {

  Page<GreenpayRecord> findByTerminalSnContainingIgnoreCaseOrMerchantContainingIgnoreCase(
      String sn, String merchant, Pageable pageable
  );
}
