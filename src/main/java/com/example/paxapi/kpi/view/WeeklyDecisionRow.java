// WeeklyDecisionRow.java
package com.example.paxapi.kpi.view;

import java.time.LocalDate;

public interface WeeklyDecisionRow {
  String getTerminalSn();
  LocalDate getWeekStart();
  Integer getChecksWeek();
  Integer getFailWeek();
  Long getPolicyId();
  Integer getWeeklyFailN();
  Integer getWeeklyX();
  Integer getWeekAvailable(); // 1 dispo, 0 indispo
}
