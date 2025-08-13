package com.example.springwebfluxdemo;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
@Slf4j
public class SongService {

  private final SongRepository songRepository;
  private final WebClient webClient;
  private final RestTemplate restTemplate = new RestTemplate();

  public Mono<SongDto> getSongById(Long id) {
    return songRepository
        .getById(id)
        .switchIfEmpty(Mono.error(() -> new SongNotFoundException("not found")));
  }

  public Flux<SongDto> findByName(String name) {
    return songRepository.findByName(name);
  }

  public Mono<String> getFromRemote() {
    return webClient
        .get()
        .uri("/c/3029-d29f-4014-9fb4?delay=5000")
        //                .uri("/ip")
        .retrieve()
        .bodyToMono(String.class)
        .doOnError(error -> log.error("Error fetching song from external service", error));
  }

  public String getFromRemoteBlocking() {
    return restTemplate.getForObject(
        "https://dummyjson.com/c/3029-d29f-4014-9fb4?delay=5000", String.class);
  }
}
