package com.epam.resource.client;

import com.epam.resource.dto.StorageDataDto;
import com.epam.resource.dto.StorageType;
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
            }
        } catch (Exception e) {
            log.error("Error occurred while fetching storage data: " + e.getMessage());
        }

        return null;
    }
}
