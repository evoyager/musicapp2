package com.epam.song.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "songs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Song {
    @Id
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "artist")
    private String artist;

    @Column(name = "album")
    private String album;

    @Column(name = "duration")
    private String duration;

    @Column(name = "year", nullable = true)
    @Pattern(regexp = "^(19|20)\\d{2}$", message = "Year must be in YYYY format and between 1900 and 2099.")
    private String year;
}
