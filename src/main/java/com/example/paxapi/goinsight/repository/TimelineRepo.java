package com.example.paxapi.goinsight.repository;

import com.example.paxapi.goinsight.domain.GreenpayTimelineView;
import com.example.paxapi.goinsight.domain.GreenpayTimelineView.Id;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface TimelineRepo extends JpaRepository<GreenpayTimelineView, Id> {

  Page<GreenpayTimelineView> findById_CapturedAtBetweenOrderById_CapturedAtAsc(
      LocalDateTime from, LocalDateTime to, Pageable pageable
  );

  Page<GreenpayTimelineView> findById_TerminalSnAndId_CapturedAtBetweenOrderById_CapturedAtAsc(
      String sn, LocalDateTime from, LocalDateTime to, Pageable pageable
  );
}
