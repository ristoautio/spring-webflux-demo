package com.example.springwebfluxdemo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class TestService {

  private final RestTemplate restTemplate = new RestTemplate();

  @Value("${myapp.test-service.url}")
  private String testServiceUrl;

  public String test() {
    return restTemplate.getForObject(testServiceUrl, String.class);
  }
}
