package com.epam.storage.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StorageData {

    private int id;
    private String storageType;
    private String bucket;
    private String path;

}
