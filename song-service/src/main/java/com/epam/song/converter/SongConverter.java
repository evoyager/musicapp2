package com.epam.song.converter;

import com.epam.song.domain.Song;
import com.epam.song.domain.SongMetadataDto;

public class SongConverter {

    /**
     * Converts a SongMetadataDto to a Song entity.
     * @param dto the SongMetadataDto to convert
     * @return a Song entity
     */
    public static Song toEntity(SongMetadataDto dto) {
        if (dto == null) {
            return null;
        }

        return Song.builder()
                .id(dto.getId())
                .name(dto.getName())
                .artist(dto.getArtist())
                .album(dto.getAlbum())
                .duration(dto.getDuration())
                .year(dto.getYear())
                .build();
    }

    /**
     * Converts a Song entity to a SongMetadataDto.
     * @param entity the Song entity to convert
     * @return a SongMetadataDto
     */
    public static SongMetadataDto toDto(Song entity) {
        if (entity == null) {
            return null;
        }

        return new SongMetadataDto(
                entity.getId(),
                entity.getName(),
                entity.getArtist(),
                entity.getAlbum(),
                entity.getDuration(),
                entity.getYear()
        );
    }
}