package com.epam.resource.service.impl;

import com.epam.resource.client.StorageServiceClient;
import com.epam.resource.dto.ResourceDto;
import com.epam.resource.mapper.ResourceMapper;
import com.epam.resource.messaging.producer.RabbitMQProducer;
import com.epam.resource.repository.ResourceRepository;
import com.epam.resource.repository.domain.Resource;
import com.epam.resource.service.S3Service;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.support.RetryTemplate;

import static com.epam.resource.dto.StorageType.STAGING;
import static com.epam.resource.service.impl.constants.TestConstants.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.AssertionErrors.assertEquals;

@ExtendWith(MockitoExtension.class)
class ResourceServiceImplTest {

    @InjectMocks
    private ResourceServiceImpl resourceService;

    @Mock
    StorageServiceClient storageServiceClient;

    @Mock
    private ResourceRepository resourceRepository;

    @Mock
    private ResourceMapper resourceMapper;

    @Mock
    private S3Service s3Service;

    @Mock
    private RabbitMQProducer rabbitMQProducer;

    @Mock
    private RetryTemplate retryTemplate;

    @Test
    void saveResource() {
        byte[] audioData = new byte[]{0, 1};
        Resource resource = Resource.builder()
                .path(STAGING_PATH)
                .name(RESOURCE_NAME)
                .resourceId(RESOURCE_ID)
                .state(STAGING.name())
                .build();
        ResourceDto resourceDto = ResourceDto.builder().build();

        when(storageServiceClient.getStorageByType(STAGING)).thenReturn(buildStagingStorageDataDto());
        when(s3Service.putResource(MUSIC_BUCKET_NAME, STAGING_PATH, RESOURCE_NAME, audioData)).thenReturn(RESOURCE_ID);
        when(retryTemplate.execute(any(RetryCallback.class))).thenAnswer(invocation -> {
            RetryCallback retry = invocation.getArgument(0);
            return retry.doWithRetry(null);
        });

        when(resourceRepository.save(resource)).thenReturn(resource);
        when(resourceMapper.toResourceDto(resource)).thenReturn(resourceDto);
        doNothing().when(rabbitMQProducer).sendResourceIdMessage(resourceDto);

        var actual = resourceService.saveResource(RESOURCE_NAME, audioData);

        assertEquals("Actual and expected responses aren't equal", RESOURCE_ID, actual);
        verify(resourceRepository, only()).save(resource);
        verify(resourceMapper, only()).toResourceDto(resource);
    }
}
