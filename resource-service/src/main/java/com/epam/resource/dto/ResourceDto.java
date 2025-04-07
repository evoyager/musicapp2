package com.epam.resource.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class ResourceDto implements Serializable {
    private Long id;

    private String name;

    private String resourceId;
}
