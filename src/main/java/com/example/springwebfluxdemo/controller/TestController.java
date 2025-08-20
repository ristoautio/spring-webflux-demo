package com.example.springwebfluxdemo.controller;

import com.example.springwebfluxdemo.service.TestService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@Log4j2
public class TestController {

  private final TestService testService;

  @GetMapping("/test-contract")
  public String test() {
    log.info("request received at /test endpoint");
    return testService.test();
  }
}
