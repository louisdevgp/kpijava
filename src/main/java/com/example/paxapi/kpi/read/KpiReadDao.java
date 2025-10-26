package com.example.paxapi.kpi.read;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface KpiReadDao {

  Page<DailyRow> daily(LocalDate date, Pageable pageable);

  Page<WeeklyRow> weekly(LocalDate weekStart, Pageable pageable);

  /* === DTOs === */
  public static class DailyRow {
    public LocalDate date;
    public String terminalSn;
    public String merchant;
    public Integer okCount;
    public Integer failCount;
    public Integer checks;
    public Integer decision; // 1=OK,0=KO
    public Integer score;    // 0..100
    public Boolean internetOk;
    public Boolean tpeOnOk;
    public Boolean geofenceOk;
    public Boolean batteryOk;
    public Boolean printerOk;
    public Boolean paperOk;

    public DailyRow(LocalDate date, String terminalSn, String merchant,
                    Integer okCount, Integer failCount, Integer checks,
                    Integer decision, Integer score,
                    Boolean internetOk, Boolean tpeOnOk, Boolean geofenceOk,
                    Boolean batteryOk, Boolean printerOk, Boolean paperOk) {
      this.date = date;
      this.terminalSn = terminalSn;
      this.merchant = merchant;
      this.okCount = okCount;
      this.failCount = failCount;
      this.checks = checks;
      this.decision = decision;
      this.score = score;
      this.internetOk = internetOk;
      this.tpeOnOk = tpeOnOk;
      this.geofenceOk = geofenceOk;
      this.batteryOk = batteryOk;
      this.printerOk = printerOk;
      this.paperOk = paperOk;
    }
  }

  public static class WeeklyRow {
    public LocalDate weekStart;
    public String terminalSn;
    public String merchant;
    public Integer daysOk;
    public Integer daysFail;
    public Integer decision; // 1=OK,0=KO
    public Integer score;    // 0..100

    public WeeklyRow(LocalDate weekStart, String terminalSn, String merchant,
                     Integer daysOk, Integer daysFail, Integer decision, Integer score) {
      this.weekStart = weekStart;
      this.terminalSn = terminalSn;
      this.merchant = merchant;
      this.daysOk = daysOk;
      this.daysFail = daysFail;
      this.decision = decision;
      this.score = score;
    }
  }
}
