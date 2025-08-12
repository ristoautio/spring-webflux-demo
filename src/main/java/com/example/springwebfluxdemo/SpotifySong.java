package com.example.springwebfluxdemo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.*;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Table
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpotifySong {

    @Id
    private Long id;

    @Column("spotify_id")
    private String spotifyId;

    @Column("name")
    private String name;

    @Column("artists")
    private String artists;

    @Column("daily_rank")
    private Integer dailyRank;

    @Column("daily_movement")
    private Integer dailyMovement;

    @Column("weekly_movement")
    private Integer weeklyMovement;

    @Column("country")
    private String country;

    @Column("snapshot_date")
    private LocalDate snapshotDate;

    @Column("popularity")
    private Integer popularity;

    @Column("is_explicit")
    private Boolean isExplicit;

    @Column("duration_ms")
    private Integer durationMs;

    @Column("album_name")
    private String albumName;

    @Column("album_release_date")
    private LocalDate albumReleaseDate;

    @Column("danceability")
    private BigDecimal danceability;

    @Column("energy")
    private BigDecimal energy;

    @Column("key")
    private Integer key;

    @Column("loudness")
    private BigDecimal loudness;

    @Column("mode")
    private Integer mode;

    @Column("speechiness")
    private BigDecimal speechiness;

    @Column("acousticness")
    private BigDecimal acousticness;

    @Column("instrumentalness")
    private BigDecimal instrumentalness;

    @Column("liveness")
    private BigDecimal liveness;

    @Column("valence")
    private BigDecimal valence;

    @Column("tempo")
    private BigDecimal tempo;

    @Column("time_signature")
    private Integer timeSignature;

    @CreatedDate
    @Column("created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column("updated_at")
    private LocalDateTime updatedAt;
}
