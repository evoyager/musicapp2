package com.epam.song.repository;

import com.epam.song.domain.Song;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SongRepository extends JpaRepository<Song, Long>, CustomSongRepository {
}
