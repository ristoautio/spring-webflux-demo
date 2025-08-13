package com.example.springwebfluxdemo;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface SongRepository extends ReactiveCrudRepository<SpotifySong, Long> {

  Mono<SongDto> getById(Long id);

  @Query("SELECT * FROM spotify_song WHERE name ILIKE :name")
  Flux<SongDto> findByName(String name);
}
