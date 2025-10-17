package com.example.paxapi.kpi.read;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class KpiReadDao {

  @PersistenceContext
  private final EntityManager em;

  public Page<Object[]> daily(LocalDate date, Pageable pageable) {
    // Requête data
    String base = "SELECT * FROM daily_decision_view";
    String where = (date != null) ? " WHERE date = :date" : "";
    String sql = base + where + " ORDER BY 1"; // adapte l’ORDER BY sur la colonne voulue

    Query q = em.createNativeQuery(sql);
    if (date != null) q.setParameter("date", date);
    q.setFirstResult((int) pageable.getOffset());
    q.setMaxResults(pageable.getPageSize());
    @SuppressWarnings("unchecked")
    List<Object[]> rows = q.getResultList();

    // Requête count
    String countSql = "SELECT COUNT(1) FROM daily_decision_view" + where;
    Query countQ = em.createNativeQuery(countSql);
    if (date != null) countQ.setParameter("date", date);
    Number total = (Number) countQ.getSingleResult();

    return new PageImpl<>(rows, pageable, total.longValue());
  }

  public Page<Object[]> weekly(LocalDate weekStart, Pageable pageable) {
    // Requête data
    String base = "SELECT * FROM weekly_decision_view";
    String where = (weekStart != null) ? " WHERE week_start = :ws" : "";
    String sql = base + where + " ORDER BY 1"; // adapte l’ORDER BY sur la colonne voulue

    Query q = em.createNativeQuery(sql);
    if (weekStart != null) q.setParameter("ws", weekStart);
    q.setFirstResult((int) pageable.getOffset());
    q.setMaxResults(pageable.getPageSize());
    @SuppressWarnings("unchecked")
    List<Object[]> rows = q.getResultList();

    // Requête count
    String countSql = "SELECT COUNT(1) FROM weekly_decision_view" + where;
    Query countQ = em.createNativeQuery(countSql);
    if (weekStart != null) countQ.setParameter("ws", weekStart);
    Number total = (Number) countQ.getSingleResult();

    return new PageImpl<>(rows, pageable, total.longValue());
  }
}
