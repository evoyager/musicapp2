package com.epam.storage.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.util.List;

import static com.epam.storage.service.S3StorageService.METADATA_BUCKET_NAME;
import static com.epam.storage.service.constants.TestConstants.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.AssertionErrors.assertEquals;

@ExtendWith(MockitoExtension.class)
class S3StorageServiceTest {

    @InjectMocks
    private S3StorageService s3StorageService;

    @Mock
    private S3Client s3Client;


    @Test
    void createStorage() {
        var createStorageRequest = buildCreateStorageRequest();
        ListObjectsRequest listObjectsRequest = ListObjectsRequest.builder()
                .bucket(METADATA_BUCKET_NAME)
                .build();
        CreateBucketRequest createStagingBucketRequest = CreateBucketRequest.builder().bucket(STAGING_BUCKET_NAME).build();

        when(s3Client.listObjects(listObjectsRequest)).thenReturn(ListObjectsResponse.builder().build());
        when(s3Client.headBucket(any(HeadBucketRequest.class))).thenThrow(NoSuchBucketException.class);
        when(s3Client.createBucket(createStagingBucketRequest)).thenReturn(CreateBucketResponse.builder().build());
        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenReturn(PutObjectResponse.builder().build());

        var actual = s3StorageService.createStorage(createStorageRequest);

        assertEquals("Actual and expected responses aren't equal", 1, actual);
    }

    @Test
    void listBuckets() {
        var buckets = List.of(Bucket.builder().name(STAGING_BUCKET_NAME).build());
        ListBucketsResponse listBucketsResponse = ListBucketsResponse.builder()
                .buckets(buckets)
                .build();
        when(s3Client.listBuckets()).thenReturn(listBucketsResponse);

        var actual = s3StorageService.listBuckets();

        assertEquals("Actual and expected responses aren't equal", buckets, actual);
    }

    @Test
    void getAllStorages() {
    }

    @Test
    void deleteBucket() {
        DeleteBucketRequest deleteBucketRequest = DeleteBucketRequest.builder()
                .bucket(STAGING_BUCKET_NAME)
                .build();
        var expected = DeleteBucketResponse.builder().build();
        when(s3Client.deleteBucket(deleteBucketRequest)).thenReturn(expected);

        var actual = s3StorageService.deleteBucket(STAGING_BUCKET_NAME);

        assertEquals("Actual and expected responses aren't equal", expected, actual);
    }

    @Test
    void deleteStorageByIds() {
    }

    @Test
    void deleteObject() {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(STAGING_BUCKET_NAME)
                .key(OBJECT_NAME)
                .build();
        var expected = DeleteObjectResponse.builder().build();
        when(s3Client.deleteObject(deleteObjectRequest)).thenReturn(expected);

        var actual = s3StorageService.deleteObject(STAGING_BUCKET_NAME, OBJECT_NAME);

        assertEquals("Actual and expected responses aren't equal", expected, actual);
    }

    @Test
    void deleteNonEmptyBucket() {
    }
}