package com.example.springwebfluxdemo;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface SongRepository extends ReactiveCrudRepository<SpotifySong, Long> {

    Mono<SongDto> getById(Long id);
}
