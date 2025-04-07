package com.epam.resourceprocessor.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class ResourceServiceClient {

    private final RestTemplate restTemplate;

    @Autowired
    public ResourceServiceClient(RestTemplateBuilder restTemplateBuilder,
                             @Value("${resource.service.host}") String resourceServiceHost) {
        this.restTemplate = restTemplateBuilder.build();
    }

    /**
     * Fetches audio data from the specified URL and saves it to a local file.
     *
     * @param resourceId      The resource ID to fetch the audio data by it.
     */
    public byte[] fetchAudioFile(String resourceId) {
        try {
            log.info("Fetching audio file from resource service by resourceId: {}", resourceId);
            ResponseEntity<byte[]> response = restTemplate.getForEntity(resourceId, byte[].class);

            if (response.getStatusCode() == HttpStatus.OK) {
                byte[] body = response.getBody();
                if (body != null) {
                    log.info("Successfully fetched audio file by resourceId: {}", resourceId);
                }
                return body;
            } else {
                System.err.println("Failed to fetch audio: HTTP Status Code " + response.getStatusCode());
            }
        } catch (Exception e) {
            System.err.println("Error occurred while fetching audio data: " + e.getMessage());
        }

        return null;
    }
}
