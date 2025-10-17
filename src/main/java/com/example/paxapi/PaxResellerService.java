package com.example.paxapi;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.pax.market.api.sdk.java.api.base.dto.Result;
import com.pax.market.api.sdk.java.api.reseller.ResellerApi;
import com.pax.market.api.sdk.java.api.reseller.ResellerApi.ResellerSearchOrderBy;
import com.pax.market.api.sdk.java.api.reseller.ResellerApi.ResellerStatus;
import com.pax.market.api.sdk.java.api.reseller.dto.ResellerPageDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaxResellerService {

  private final ResellerApi resellerApi;

  public ResellerPageDTO search(int pageNo, int pageSize, ResellerSearchOrderBy orderBy, String name, ResellerStatus status) {
    // La doc indique que 'name' est nullable → on passe null si vide
    String searchName = (name == null || name.isBlank()) ? null : name;

    Result<ResellerPageDTO> res = resellerApi.searchReseller(
        pageNo, pageSize, orderBy, searchName, status
    );
    if (res == null) throw new IllegalStateException("PAX result is null");

    Integer code = res.getBusinessCode();   // 0 => succès
    String msg   = Optional.ofNullable(res.getMessage()).orElse("no message");
    if (code != null && code != 0) {
      log.warn("PAX error: code={}, message={}", code, msg);
      throw new RuntimeException("PAX API non-success (" + code + "): " + msg);
    }

    ResellerPageDTO data = res.getData();
    if (data == null) {
      log.warn("PAX success (code=0) but data is null");
      return null;
    }
    return data;
  }
}
