package com.example.springwebfluxdemo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SongDto {
  private Long id;
  private String spotifyId;
  private String name;
  private String artists;
}
