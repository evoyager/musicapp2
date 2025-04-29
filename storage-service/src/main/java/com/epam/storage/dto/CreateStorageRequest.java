package com.epam.storage.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class CreateStorageRequest {

    private String storageType;
    private String bucket;
    private String path;

}
