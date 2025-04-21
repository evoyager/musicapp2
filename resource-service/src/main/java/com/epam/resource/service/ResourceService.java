package com.epam.resource.service;

import com.epam.resource.dto.ResourceDto;
import org.springframework.stereotype.Service;

@Service
public interface ResourceService {

    String saveResource(String resourceName, byte[] audioData);

    String indicateResourceHasBeenProcessed(ResourceDto resource);

}