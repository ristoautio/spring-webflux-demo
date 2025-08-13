package com.example.springwebfluxdemo;

import static org.springframework.http.MediaType.APPLICATION_NDJSON_VALUE;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@AllArgsConstructor
@Slf4j
public class SongController {

  private SongService songService;

  @GetMapping("/songs/{id}")
  public Mono<SongDto> getSongById(@PathVariable Long id) {
    return songService.getSongById(id);
  }

  @GetMapping(value = "/search", produces = APPLICATION_NDJSON_VALUE)
  public Flux<SongDto> searchSongs(@RequestParam(value = "search", defaultValue = "") String name) {
    return songService.findByName(name);
  }

  @GetMapping(value = "/test")
  public Mono<String> testClient() {
    log.info("testClient");
    return songService.getFromRemote();
  }

  @GetMapping(value = "/test2")
  public String testClientBlocking() {
    log.info("test blocking client");
    return songService.getFromRemoteBlocking();
  }
}
