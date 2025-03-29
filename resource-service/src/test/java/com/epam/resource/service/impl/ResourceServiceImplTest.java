package com.epam.resource.service.impl;

import com.epam.resource.dto.ResourceDto;
import com.epam.resource.mapper.ResourceMapper;
import com.epam.resource.messaging.producer.RabbitMQProducer;
import com.epam.resource.repository.ResourceRepository;
import com.epam.resource.repository.domain.Resource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.support.RetryTemplate;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.util.List;

import static com.epam.resource.service.impl.ResourceServiceImpl.RESOURCES_BUCKET_NAME;
import static com.epam.resource.service.impl.constants.TestConstants.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.AssertionErrors.assertEquals;

@ExtendWith(MockitoExtension.class)
class ResourceServiceImplTest {

    @InjectMocks
    private ResourceServiceImpl resourceService;

    @Mock
    private ResourceRepository resourceRepository;

    @Mock
    private ResourceMapper resourceMapper;

    @Mock
    private S3Client s3Client;

    @Mock
    private RabbitMQProducer rabbitMQProducer;

    @Mock
    private RetryTemplate retryTemplate;

    @Test
    void createBucket() {
        CreateBucketRequest createBucketRequest = CreateBucketRequest.builder()
                .bucket(BUCKET_NAME)
                .build();
        var expected = CreateBucketResponse.builder().build();
        when(s3Client.createBucket(createBucketRequest)).thenReturn(expected);

        var actual = resourceService.createBucket(BUCKET_NAME);

        assertEquals("Actual and expected responses aren't equal", expected, actual);
        verify(s3Client, only()).createBucket(createBucketRequest);
    }

    @Test
    void deleteBucket() {
        DeleteBucketRequest deleteBucketRequest = DeleteBucketRequest.builder()
                .bucket(BUCKET_NAME)
                .build();
        var expected = DeleteBucketResponse.builder().build();
        when(s3Client.deleteBucket(deleteBucketRequest)).thenReturn(expected);

        var actual = resourceService.deleteBucket(BUCKET_NAME);

        assertEquals("Actual and expected responses aren't equal", expected, actual);
        verify(s3Client, only()).deleteBucket(deleteBucketRequest);
    }

    @Test
    void listBuckets() {
        var buckets = List.of(Bucket.builder().name(BUCKET_NAME).build());
        ListBucketsResponse listBucketsResponse = ListBucketsResponse.builder()
                .buckets(buckets)
                .build();
        when(s3Client.listBuckets()).thenReturn(listBucketsResponse);

        var actual = resourceService.listBuckets();

        assertEquals("Actual and expected responses aren't equal", buckets, actual);
        verify(s3Client, only()).listBuckets();
    }

    @Test
    void saveResource() {
        byte[] audioData = new byte[]{0, 1};
        Resource resource = Resource.builder()
                .name(RESOURCE_NAME)
                .resourceId(RESOURCE_ID)
                .build();
        ResourceDto resourceDto = ResourceDto.builder().build();
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(BUCKET_NAME)
                .key(RESOURCE_NAME)
                .contentType("audio/mpeg") // MIME type for MP3
                .build();
        PutObjectResponse putObjectResponse = PutObjectResponse.builder().build();
        when(s3Client.putObject(eq(putObjectRequest), any(RequestBody.class))).thenReturn(putObjectResponse);
        when(retryTemplate.execute(any(RetryCallback.class))).thenAnswer(invocation -> {
            RetryCallback retry = invocation.getArgument(0);
            return retry.doWithRetry(null);
        });

        when(resourceRepository.save(resource)).thenReturn(resource);
        when(resourceMapper.toResourceDto(resource)).thenReturn(resourceDto);
        doNothing().when(rabbitMQProducer).sendResourceIdMessage(resourceDto);

        var actual = resourceService.saveResource(BUCKET_NAME, RESOURCE_NAME, audioData);

        assertEquals("Actual and expected responses aren't equal", RESOURCE_ID, actual);
        verify(resourceRepository, only()).save(resource);
        verify(resourceMapper, only()).toResourceDto(resource);
    }

    @Test
    void deleteResource() {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(RESOURCES_BUCKET_NAME)
                .key(RESOURCE_NAME)
                .build();
        var expected = DeleteObjectResponse.builder().build();
        when(s3Client.deleteObject(deleteObjectRequest)).thenReturn(expected);

        var actual = resourceService.deleteResource(RESOURCE_NAME);

        assertEquals("Actual and expected responses aren't equal", expected, actual);
        verify(s3Client, only()).deleteObject(deleteObjectRequest);
    }

    @Test
    void getResource() throws IOException {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(RESOURCES_BUCKET_NAME)
                .key(RESOURCE_NAME)
                .build();

        ResponseInputStream<GetObjectResponse> mockInputStream = mock(ResponseInputStream.class);
        when(s3Client.getObject(getObjectRequest)).thenReturn(mockInputStream);
        when(mockInputStream.read(any())).thenReturn(-1);
        byte[] expected = new byte[]{};

        var actual = resourceService.getResource(RESOURCE_NAME);

        assertEquals("Actual and expected responses aren't equal", expected, actual);
        verify(s3Client, only()).getObject(getObjectRequest);
    }

}
