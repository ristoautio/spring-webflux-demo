package com.example.springwebfluxdemo;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
@Slf4j
public class SongService {

  private final SongRepository songRepository;

  public Mono<SongDto> getSongById(Long id) {
    return songRepository
        .getById(id)
        .switchIfEmpty(Mono.error(() -> new SongNotFoundException("not found")));
  }

  public Flux<SongDto> findByName(String name) {
    return songRepository.findByName(name);
  }
}
