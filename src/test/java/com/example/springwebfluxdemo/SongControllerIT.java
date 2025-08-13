package com.example.springwebfluxdemo;

import java.time.LocalDate;
import org.junit.ClassRule;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.PostgreSQLContainer;

@SpringBootTest
@AutoConfigureWebTestClient
class SongControllerIT {

  @Autowired private WebTestClient webTestClient;

  @Autowired private SongRepository songRepository;

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

  @Test
  public void getSongById_returnsSongDto_whenSongExists() {
    SpotifySong song = addSongWithName("Test Song");

    webTestClient
        .get()
        .uri("/songs/{id}", song.getId())
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.id")
        .isEqualTo(song.getId())
        .jsonPath("$.spotifyId")
        .isEqualTo("test-spotify-id")
        .jsonPath("$.name")
        .isEqualTo("Test Song")
        .jsonPath("$.artists")
        .isEqualTo("Test Artist");
  }

  @Test
  void getSongById_returnsNotFound_whenSongDoesNotExist() {
    webTestClient
        .get()
        .uri("/songs/{id}", 99999)
        .exchange()
        .expectStatus()
        .isNotFound()
        .expectBody(String.class)
        .isEqualTo("not found");
  }

  @Test
  void findByName_returnsSongs_whenSongsExist() {
    addSongWithName("Test Song 1");
    addSongWithName("Test Song 2");

    webTestClient
        .get()
        .uri("/search?search=Test song 1")
        .exchange()
        .expectStatus()
        .isOk()
        .expectBodyList(SongDto.class)
        .hasSize(1)
        .consumeWith(
            response -> {
              SongDto song = response.getResponseBody().getFirst();
              Assertions.assertEquals("Test Song 1", song.getName());
            });
  }

  private SpotifySong addSongWithName(String name) {
    return songRepository
        .save(
            SpotifySong.builder()
                .spotifyId("test-spotify-id")
                .name(name)
                .artists("Test Artist")
                .dailyRank(1)
                .dailyMovement(0)
                .weeklyMovement(0)
                .country("US")
                .snapshotDate(LocalDate.of(2024, 1, 1))
                .popularity(50)
                .isExplicit(false)
                .durationMs(180000)
                .albumName("Test Album")
                .albumReleaseDate(LocalDate.of(2023, 1, 1))
                .build())
        .block();
  }
}
