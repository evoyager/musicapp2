package com.epam.resource.service.impl.constants;

import com.epam.resource.dto.StorageDataDto;

import static com.epam.resource.dto.StorageType.STAGING;

public class TestConstants {
    public static final String RESOURCE_ID = "null/resources/test.mp3";
    public static final String RESOURCE_NAME = "test.mp3";
    public static final String MUSIC_BUCKET_NAME = "music-bucket";
    public static final String STAGING_PATH = "staging-files";
    public static final String PERMANENT_PATH = "permanent-files";

    public static StorageDataDto buildStagingStorageDataDto() {
        return StorageDataDto.builder()
                .storageType(STAGING.name())
                .bucket(MUSIC_BUCKET_NAME)
                .path(STAGING_PATH)
                .build();
    }
}
