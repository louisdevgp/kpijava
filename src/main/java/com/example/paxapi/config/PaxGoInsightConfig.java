package com.example.paxapi.config;

import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.pax.market.api.sdk.java.api.goinsight.GoInsightApi;

@Configuration
public class PaxGoInsightConfig {

  @Bean
  public GoInsightApi goInsightApi(
      @Value("${pax.base-url}") String baseUrl,
      @Value("${pax.key}") String key,
      @Value("${pax.secret}") String secret,
      @Value("${goinsight.time-zone:Etc/GMT-1}") String tzId
  ) {
    return new GoInsightApi(baseUrl, key, secret, TimeZone.getTimeZone(tzId));
  }
}
