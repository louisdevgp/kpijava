package com.example.paxapi.goinsight.repository;

import com.example.paxapi.goinsight.domain.GreenpayRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GreenpayRecordRepo extends JpaRepository<GreenpayRecord, String> {
}
