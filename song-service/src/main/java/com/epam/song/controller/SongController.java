package com.epam.song.controller;

import com.epam.song.domain.Song;
import com.epam.song.domain.SongMetadataDto;
import com.epam.song.service.SongService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static com.epam.song.converter.SongConverter.toDto;
import static com.epam.song.converter.SongConverter.toEntity;

@RestController
@RequestMapping("/songs")
public class SongController {

    @Autowired
    private SongService songService;

    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<Map<String, Long>> createSong(@RequestBody @Valid SongMetadataDto songMetadata) throws Exception {
        Song song = toEntity(songMetadata);
        Song savedSong = songService.saveSong(song);
        return ResponseEntity.ok(Map.of("id", savedSong.getId()));
    }

    @GetMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<SongMetadataDto> getSongById(@PathVariable Long id) throws Exception {
        Song song = songService.getSongById(id);
        return ResponseEntity.ok(toDto(song));
    }

    @DeleteMapping(produces = "application/json")
    public ResponseEntity<Map<String, List<Long>>> deleteSongs(@RequestParam(value = "id") String ids) {
        List<Long> deletedIds;
        deletedIds = songService.deleteSongs(ids);
        return ResponseEntity.ok(Map.of("ids", deletedIds));
    }

    @GetMapping(produces = "application/json")
    public ResponseEntity<List<SongMetadataDto>> getAllSongs() {
        List<SongMetadataDto> songs = songService.getAllSongs();
        return ResponseEntity.ok(songs);
    }
}
