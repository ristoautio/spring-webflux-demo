package com.example.springwebfluxdemo;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@AllArgsConstructor
public class SongController {

  private SongService songService;

  @GetMapping("/songs/{id}")
  public Mono<SongDto> getSongById(@PathVariable Long id) {
    return songService.getSongById(id);
  }
}
