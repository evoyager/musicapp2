package com.epam.resource.service.impl;

import com.epam.resource.client.StorageServiceClient;
import com.epam.resource.dto.ResourceDto;
import com.epam.resource.dto.StorageDataDto;
import com.epam.resource.mapper.ResourceMapper;
import com.epam.resource.messaging.producer.RabbitMQProducer;
import com.epam.resource.repository.ResourceRepository;
import com.epam.resource.repository.domain.Resource;
import com.epam.resource.service.ResourceService;
import com.epam.resource.service.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;

import static com.epam.resource.dto.StorageType.PERMANENT;
import static com.epam.resource.dto.StorageType.STAGING;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResourceServiceImpl implements ResourceService {

    private final ResourceRepository resourceRepository;
    private final S3Service s3Service;
    private final RabbitMQProducer rabbitMQProducer;
    private final ResourceMapper resourceMapper;
    private final RetryTemplate retryTemplate;
    private final StorageServiceClient storageServiceClient;

    @Override
    public String saveResource(String resourceName, byte[] audioData) {
        StorageDataDto storage = storageServiceClient.getStorageByType(STAGING);

        String resourceId = s3Service.putResource(storage.getBucket(), storage.getPath(), resourceName, audioData);

        log.info("Resource [{}] saved in S3 bucket [{}]. It's location: [{}]", resourceName, storage.getBucket(), resourceId);

        retryTemplate.execute(arg0 -> {
            Resource resource = Resource.builder()
                    .path(storage.getPath())
                    .name(resourceName)
                    .resourceId(resourceId)
                    .state(STAGING.name())
                    .build();

            resource = resourceRepository.save(resource);

            log.info("Resource saved in H2: [{}]", resource);

            ResourceDto resourceDto = resourceMapper.toResourceDto(resource);

            rabbitMQProducer.sendResourceIdMessage(resourceDto);
            log.info("Resource: [{}] was send.", resourceDto);
            return null;
        });

        return resourceId;
    }

    @Override
    public String indicateResourceHasBeenProcessed(ResourceDto resourceDto) {
        String resourceName = resourceDto.getName();
        StorageDataDto stagingStorage = storageServiceClient.getStorageByType(STAGING);
        StorageDataDto permanentStorage = storageServiceClient.getStorageByType(PERMANENT);

        String stagingBucketName = stagingStorage.getBucket();
        String permanentBucketName = permanentStorage.getBucket();
        String stagingPath = stagingStorage.getPath();
        String permanentPath = permanentStorage.getPath();

        byte[] audioData = s3Service.getResource(stagingBucketName, stagingPath, resourceName);
        s3Service.deleteResource(stagingBucketName, stagingPath + "/" + resourceName);
        String resourceId = s3Service.putResource(permanentBucketName, permanentPath, resourceName, audioData);

        log.info("Resource [{}] moved in S3 to new location: [{}]", resourceDto, resourceId);

        Resource resource = Resource.builder()
                .id(resourceDto.getId())
                .path(permanentPath)
                .name(resourceName)
                .resourceId(resourceId)
                .state(PERMANENT.name())
                .build();

        resource = resourceRepository.save(resource);

        log.info("Resource updated in H2: [{}]", resource);

        return resourceId;
    }
}