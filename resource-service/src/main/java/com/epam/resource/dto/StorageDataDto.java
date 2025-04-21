package com.epam.resource.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StorageDataDto {

    private int id;
    private String storageType;
    private String bucket;
    private String path;

}
