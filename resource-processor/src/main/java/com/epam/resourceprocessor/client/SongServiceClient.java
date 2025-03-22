package com.epam.resourceprocessor.client;

import com.epam.resourceprocessor.dto.ResourceDto;
import com.epam.resourceprocessor.dto.SongMetadataDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
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

    public void createSongMetadata(Map<String, String> metadata, ResourceDto resource) {
        String rawDuration = metadata.get("xmpDM:duration");
        String formattedDuration = formatDuration(rawDuration);
        SongMetadataDto song = SongMetadataDto.builder()
                .id(resource.getId())
                .name(metadata.get("title"))
                .artist(metadata.get("Author"))
                .album(metadata.get("xmpDM:album"))
                .duration(formattedDuration)
                .year(metadata.get("xmpDM:releaseDate"))
                .build();

        try {
            log.info("Sending song metadata to song service: {}", song);
            restTemplate.postForObject(SONG_SERVICE_URL, song, SongMetadataDto.class);
            log.info("Successfully send song metadata to song service.");
        } catch (Exception e) {
            System.err.println("Error occurred while sending audio data to song service: " + e.getMessage());
        }
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
