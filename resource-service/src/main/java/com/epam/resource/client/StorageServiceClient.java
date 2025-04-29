package com.epam.resource.client;

import com.epam.resource.dto.StorageDataDto;
import com.epam.resource.dto.StorageType;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

import static com.epam.resource.dto.StorageType.PERMANENT;
import static com.epam.resource.dto.StorageType.STAGING;

@Slf4j
@Service
public class StorageServiceClient {

    private final RestTemplate restTemplate;
    private final String STORAGE_SERVICE_URL;

    @Autowired
    public StorageServiceClient(RestTemplateBuilder restTemplateBuilder,
                                @Value("${cloud.gateway.host}") String cloudGatewayHost) {
        this.restTemplate = restTemplateBuilder.build();
        STORAGE_SERVICE_URL = "http://" + cloudGatewayHost + ":8080/storages";
    }

    @CircuitBreaker(name = "storageServiceCircuitBreaker", fallbackMethod = "getStorageByTypeFallback")
    public StorageDataDto getStorageByType(StorageType storageType) {
        try {
            UriComponents uriComponents = UriComponentsBuilder
                    .fromUriString(STORAGE_SERVICE_URL)
                    .queryParam("storageType", "{storageType}")
                    .encode()
                    .build();
            URI uri = uriComponents.expand(storageType).toUri();
            ResponseEntity<StorageDataDto> response = restTemplate.getForEntity(uri, StorageDataDto.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                StorageDataDto storage = response.getBody();

                if (storage != null) {
                    log.info("Successfully fetched S3 storage [{}] by storageType: [{}]", storage, storageType);
                }

                return storage;
            } else {
                throw new RuntimeException("Failed to fetch storage data: HTTP Status Code: " + response.getStatusCode());
            }
        } catch (Exception e) {
            log.error("Error occurred while fetching storage data: " + e.getMessage());
            throw e;
        }
    }

    public StorageDataDto getStorageByTypeFallback(StorageType storageType, Throwable throwable) {
        log.error("Fallback triggered for getStorageByType with storageType [{}] due to: {}", storageType, throwable.getMessage());
        return switch (storageType) {
            case STAGING -> new StorageDataDto(
                    1,
                    STAGING.name(),
                    "music-bucket",
                    "staging-files");
            case PERMANENT -> new StorageDataDto(
                    2,
                    PERMANENT.name(),
                    "music-bucket",
                    "permanent-files");

        };
    }
}
