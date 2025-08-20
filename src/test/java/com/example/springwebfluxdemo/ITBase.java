package com.example.springwebfluxdemo;

import org.junit.ClassRule;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

public class ITBase {

  @ClassRule
  public static PostgreSQLContainer postgres =
      new PostgreSQLContainer("postgres:11.1")
          .withDatabaseName("integration-tests-db")
          .withUsername("sa")
          .withPassword("sa");

  @DynamicPropertySource
  static void configureProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.flyway.url", postgres::getJdbcUrl);
    registry.add(
        "spring.r2dbc.url",
        () ->
            "r2dbc:postgresql://"
                + postgres.getHost()
                + ":"
                + postgres.getMappedPort(PostgreSQLContainer.POSTGRESQL_PORT)
                + "/"
                + postgres.getDatabaseName());
    registry.add("spring.r2dbc.username", postgres::getUsername);
    registry.add("spring.r2dbc.password", postgres::getPassword);
  }

  // start container
  @BeforeAll
  static void beforeAll() {
    postgres.start();
  }

  // stop container
  @AfterAll
  static void afterAll() {
    postgres.stop();
  }
}
