package com.example.springwebfluxdemo;

import static org.instancio.Select.field;

import com.example.springwebfluxdemo.repository.SongRepository;
import java.math.BigDecimal;
import org.instancio.Instancio;
import org.instancio.Model;
import org.instancio.Select;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest
@AutoConfigureWebTestClient
class SongControllerIT extends ITBase {

  @Autowired private WebTestClient webTestClient;

  @Autowired private SongRepository songRepository;

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
        .isEqualTo(song.getSpotifyId())
        .jsonPath("$.name")
        .isEqualTo(song.getName())
        .jsonPath("$.artists")
        .isEqualTo(song.getArtists());
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
    // Create a model that respects all DB constraints
    Model<SpotifySong> songModel =
        Instancio.of(SpotifySong.class)
            // --- Basic Setup ---
            .ignore(field(SpotifySong::getId))
            .ignore(field(SpotifySong::getCreatedAt))
            .ignore(field(SpotifySong::getUpdatedAt))
            .set(field(SpotifySong::getName), name)

            // --- NOT NULL Constraints ---
            // spotify_id VARCHAR(22) NOT NULL
            .generate(field(SpotifySong::getSpotifyId), gen -> gen.string().length(22))
            // artists TEXT NOT NULL
            .generate(field(SpotifySong::getArtists), gen -> gen.string().length(10, 50))
            // is_explicit BOOLEAN NOT NULL
            .set(field(SpotifySong::getIsExplicit), true)
            // duration_ms INTEGER NOT NULL
            .generate(field(SpotifySong::getDurationMs), gen -> gen.ints().range(60000, 300000))

            // --- CHECK Constraints ---
            // popularity INTEGER CHECK (popularity >= 0 AND popularity <= 100)
            .generate(field(SpotifySong::getPopularity), gen -> gen.ints().range(0, 100))
            // danceability DECIMAL(4,3) CHECK (>= 0 AND <= 1)
            .generate(
                field(SpotifySong::getDanceability),
                gen -> gen.math().bigDecimal().scale(3).min(BigDecimal.ZERO).max(BigDecimal.ONE))
            // energy DECIMAL(4,3) CHECK (>= 0 AND <= 1)
            .generate(
                field(SpotifySong::getEnergy),
                gen -> gen.math().bigDecimal().scale(3).min(BigDecimal.ZERO).max(BigDecimal.ONE))
            // key INTEGER CHECK (>= 0 AND <= 11)
            .generate(field(SpotifySong::getKey), gen -> gen.ints().range(0, 11))
            // mode INTEGER CHECK (mode IN (0, 1))
            .generate(field(SpotifySong::getMode), gen -> gen.ints().range(0, 1))
            // speechiness, acousticness, liveness, valence DECIMAL(6,5) CHECK (>= 0 AND <= 1)
            .generate(
                Select.fields().matching("speechiness|acousticness|liveness|valence"),
                gen -> gen.math().bigDecimal().scale(5).min(BigDecimal.ZERO).max(BigDecimal.ONE))
            // instrumentalness DECIMAL(10,9) CHECK (>= 0 AND <= 1)
            .generate(
                field(SpotifySong::getInstrumentalness),
                gen -> gen.math().bigDecimal().scale(9).min(BigDecimal.ZERO).max(BigDecimal.ONE))
            // time_signature INTEGER CHECK (time_signature > 0)
            .generate(field(SpotifySong::getTimeSignature), gen -> gen.ints().range(1, 8))

            // --- Sensible Defaults for Other Fields ---
            .generate(field(SpotifySong::getSnapshotDate), gen -> gen.temporal().localDate().past())
            .generate(
                field(SpotifySong::getAlbumReleaseDate), gen -> gen.temporal().localDate().past())
            .generate(
                field(SpotifySong::getTempo),
                gen -> gen.doubles().range(80d, 160d).as(BigDecimal::valueOf))
            .withSeed(22L)
            .toModel();

    // Create a new instance from the model and save it
    SpotifySong song = Instancio.create(songModel);
    return songRepository.save(song).block(); // Assuming reactive repository
  }
}
