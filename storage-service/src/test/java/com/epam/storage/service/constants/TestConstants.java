package com.epam.storage.service.constants;

import com.epam.storage.dto.CreateStorageRequest;

public class TestConstants {
    public static final String RESOURCE_ID = "null/resources/test.mp3";
    public static final String OBJECT_NAME = "test.mp3";
    public static final String STAGING_BUCKET_NAME = "staging-bucket";
    public static final String STORAGE_TYPE = "STAGING";
    public static final String PATH = "/files";

    public static CreateStorageRequest buildCreateStorageRequest() {
        return CreateStorageRequest.builder()
                .storageType(STORAGE_TYPE)
                .bucket(STAGING_BUCKET_NAME)
                .path(PATH)
                .build();
    }
}
