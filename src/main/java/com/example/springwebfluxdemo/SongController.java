package com.example.springwebfluxdemo;

import static org.springframework.http.MediaType.APPLICATION_NDJSON_VALUE;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@AllArgsConstructor
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
}
