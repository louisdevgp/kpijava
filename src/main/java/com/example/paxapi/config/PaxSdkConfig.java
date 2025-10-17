package com.example.paxapi.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.pax.market.api.sdk.java.api.merchant.MerchantApi;
import com.pax.market.api.sdk.java.api.reseller.ResellerApi;
import com.pax.market.api.sdk.java.api.terminal.TerminalApi;


@Configuration
public class PaxSdkConfig {


@Value("${pax.base-url}")
private String baseUrl;
@Value("${pax.key}")
private String key;
@Value("${pax.secret}")
private String secret;


@Bean
public ResellerApi resellerApi() {
return new ResellerApi(baseUrl, key, secret);
}


@Bean
public MerchantApi merchantApi() {
return new MerchantApi(baseUrl, key, secret);
}


@Bean
public TerminalApi terminalApi() {
return new TerminalApi(baseUrl, key, secret);
}
}