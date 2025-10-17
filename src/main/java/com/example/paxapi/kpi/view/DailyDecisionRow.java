// DailyDecisionRow.java
package com.example.paxapi.kpi.view;

import java.time.LocalDate;

public interface DailyDecisionRow {
  String getTerminalSn();
  LocalDate getDay();
  Integer getChecks();
  Integer getFailCount();
  Long getPolicyId();
  Integer getDailyFailN();
  Integer getDailyX();
  Integer getDayAvailable(); // 1 dispo, 0 indispo
}
