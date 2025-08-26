package com.example.springwebfluxdemo.controller;

import static org.springframework.http.MediaType.APPLICATION_NDJSON_VALUE;

import com.example.springwebfluxdemo.SongDto;
import com.example.springwebfluxdemo.service.SongService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
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
  public Flux<SongDto> searchSongs(
      @RequestParam(value = "search", defaultValue = "") String search) {
    //    log.info("Searching for songs by search: {}", search);
    return songService.findByName(search);
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

  // just to test arch unit
  @PostMapping
  public Mono<Void> createSong(@RequestBody SongDto songDto) {
    return songService.create(songDto);
  }

  // just to test arch unit (frozen allow)
  @PostMapping("/test2")
  public Mono<Void> createSongFrozen(@RequestBody SongDto songDto) {
    Flux<SongDto> song = songService.findByName(songDto.getName());
    return Mono.empty();
  }
}
