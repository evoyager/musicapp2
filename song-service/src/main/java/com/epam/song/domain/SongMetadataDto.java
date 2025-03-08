package com.epam.song.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SongMetadataDto {
    private Long id;

    @NotBlank
    @NotNull
    private String name;

    @NotBlank
    @NotNull
    private String artist;

    private String album;

    @Pattern(regexp = "^(0[0-9]|1[0-9]|2[0-9]):[0-5][0-9]$",
            message = "must be in the mm:ss format, where mm is between 00 and 29 and ss is between 00 and 59")
    private String duration;

    @Pattern(regexp = "^(19|20)\\d{2}$", message = "must be in YYYY format and between 1900 and 2099.")
    private String year;
}
