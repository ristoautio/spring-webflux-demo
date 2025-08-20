package com.example.springwebfluxdemo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest
@AutoConfigureWebTestClient
@AutoConfigureStubRunner(
    ids = {"com.example:spring-cloud-contract-demo:0.0.1-SNAPSHOT:stubs:6565"},
    stubsMode = StubRunnerProperties.StubsMode.LOCAL)
public class ContractTest extends ITBase {

  @Autowired private WebTestClient webTestClient;

  @Test
  public void testContract() {

    webTestClient
        .get()
        .uri("/test-contract")
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody(String.class)
        .isEqualTo("Test successful!");
  }
}
