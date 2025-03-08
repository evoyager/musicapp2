package com.epam.song.service;

import com.epam.song.converter.SongConverter;
import com.epam.song.domain.Song;
import com.epam.song.domain.SongMetadataDto;
import com.epam.song.exceptions.InvalidCsvException;
import com.epam.song.exceptions.ResourceNotFoundException;
import com.epam.song.repository.SongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SongService {

    @Autowired
    private SongRepository songRepository;

    public Song saveSong(Song song) {
        if (song.getId() != null) {
            songRepository.checkSongIdExists(song.getId());
        }
        return songRepository.save(song);
    }

    public Song getSongById(Long id) throws ResourceNotFoundException {
        return songRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Song not found with ID: " + id));
    }

    public List<Long> deleteSongs(String ids) {
        if (ids.length() > 200) {
            throw new InvalidCsvException("CSV string is too long: received " + ids.length()
                    + " characters. Maximum allowed length is 200 characters.");
        }

        List<Long> idList;
        try {
            idList = Arrays.stream(ids.split(","))
                    .map(String::trim)
                    .map(Long::parseLong)
                    .collect(Collectors.toList());
        } catch (NumberFormatException e) {
            throw new InvalidCsvException("Invalid ID format. Could not parse all IDs from the CSV string.");
        }
        return songRepository.deleteAllByIdInReturnIds(idList);
    }

    public List<SongMetadataDto> getAllSongs() {
        List<Song> songs = songRepository.findAll();
        return songs.stream().map(SongConverter::toDto).collect(Collectors.toList());
    }
}