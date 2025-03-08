package com.epam.resource.client;

import com.epam.resource.domain.SongMetadataDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class SongServiceClient {

    private final RestTemplate restTemplate;

    private final String SONG_SERVICE_URL;

    @Autowired
    public SongServiceClient(RestTemplateBuilder restTemplateBuilder,
                             @Value("${song.service.host}") String songServiceHost) {
        this.restTemplate = restTemplateBuilder.build();
        SONG_SERVICE_URL = "http://" + songServiceHost + ":8081/songs";
    }

    public void createSongMetadata(Map<String, String> metadata, Long id) {
        String rawDuration = metadata.get("xmpDM:duration");
        String formattedDuration = formatDuration(rawDuration);
        SongMetadataDto song = SongMetadataDto.builder()
                .id(id)
                .name(metadata.get("title"))
                .artist(metadata.get("Author"))
                .album(metadata.get("xmpDM:album"))
                .duration(formattedDuration)
                .year(metadata.get("xmpDM:releaseDate"))
                .build();

        restTemplate.postForObject(SONG_SERVICE_URL, song, SongMetadataDto.class);
    }

    public void deleteSongs(String ids) {
        restTemplate.delete(SONG_SERVICE_URL + "?id=" + ids);
    }

    private String formatDuration(String rawDuration) {
        if (rawDuration == null) return "00:00";

        try {
            // assuming the raw duration is in seconds with possible decimal points, e.g., 179.38
            long seconds = Long.parseLong(rawDuration.split("\\.")[0]);
            long minutes = seconds / 60;
            seconds = seconds % 60;

            return String.format("%02d:%02d", minutes, seconds);
        } catch (NumberFormatException e) {
            return "00:00";
        }
    }
}
