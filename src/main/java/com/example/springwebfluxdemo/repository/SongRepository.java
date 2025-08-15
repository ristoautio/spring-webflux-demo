package com.example.springwebfluxdemo.repository;

import com.example.springwebfluxdemo.SongDto;
import com.example.springwebfluxdemo.SpotifySong;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface SongRepository
    extends ReactiveCrudRepository<SpotifySong, Long>, ReactiveQueryByExampleExecutor<SpotifySong> {

  Mono<SongDto> getById(Long id);

  @Query("SELECT * FROM spotify_song WHERE name ILIKE :name")
  Flux<SongDto> findByName(String name);
}
