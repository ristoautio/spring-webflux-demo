package com.example.springwebfluxdemo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class MyClient {

  @Bean
  public WebClient webClient() {
    return WebClient.builder().baseUrl("https://dummyjson.com").build();
  }
}
