package com.epam.storage.dto;

import lombok.Data;

@Data
public class CreateStorageResponse {

    private int id;

    public CreateStorageResponse(int id) {
        this.id = id;
    }
}
