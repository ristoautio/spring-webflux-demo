-- Create the table
CREATE TABLE spotify_song (
                                             id SERIAL PRIMARY KEY,
                                             spotify_id VARCHAR(22) NOT NULL,
                                             name VARCHAR(500) NOT NULL,
                                             artists TEXT NOT NULL,
                                             daily_rank INTEGER,
                                             daily_movement INTEGER,
                                             weekly_movement INTEGER,
                                             country VARCHAR(10),
                                             snapshot_date DATE,
                                             popularity INTEGER CHECK (popularity >= 0 AND popularity <= 100),
                                             is_explicit BOOLEAN NOT NULL,
                                             duration_ms INTEGER NOT NULL,
                                             album_name VARCHAR(500),
                                             album_release_date DATE,
                                             danceability DECIMAL(4,3) CHECK (danceability >= 0 AND danceability <= 1),
                                             energy DECIMAL(4,3) CHECK (energy >= 0 AND energy <= 1),
                                             key INTEGER CHECK (key >= 0 AND key <= 11),
    loudness DECIMAL(7,3),
    mode INTEGER CHECK (mode IN (0, 1)),
    speechiness DECIMAL(6,5) CHECK (speechiness >= 0 AND speechiness <= 1),
    acousticness DECIMAL(6,5) CHECK (acousticness >= 0 AND acousticness <= 1),
    instrumentalness DECIMAL(10,9) CHECK (instrumentalness >= 0 AND instrumentalness <= 1),
    liveness DECIMAL(6,5) CHECK (liveness >= 0 AND liveness <= 1),
    valence DECIMAL(6,5) CHECK (valence >= 0 AND valence <= 1),
    tempo DECIMAL(8,3),
    time_signature INTEGER CHECK (time_signature > 0),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for better query performance
CREATE INDEX idx_spotify_song_spotify_id ON spotify_song(spotify_id);
CREATE INDEX idx_spotify_song_daily_rank ON spotify_song(daily_rank);
CREATE INDEX idx_spotify_song_snapshot_date ON spotify_song(snapshot_date);
CREATE INDEX idx_spotify_song_artists ON spotify_song USING GIN(to_tsvector('english', artists));
CREATE INDEX idx_spotify_song_name ON spotify_song USING GIN(to_tsvector('english', name));

