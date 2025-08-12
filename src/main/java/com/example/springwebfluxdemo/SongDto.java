package com.example.springwebfluxdemo;

import lombok.Data;

@Data
public class SongDto {
    private Long id;
    private String spotifyId;
    private String name;
    private String artists;
}
