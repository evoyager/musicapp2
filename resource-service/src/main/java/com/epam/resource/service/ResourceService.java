package com.epam.resource.service;

import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.Bucket;
import software.amazon.awssdk.services.s3.model.CreateBucketResponse;
import software.amazon.awssdk.services.s3.model.DeleteBucketResponse;
import software.amazon.awssdk.services.s3.model.DeleteObjectResponse;

import java.util.List;

@Service
public interface ResourceService {

    CreateBucketResponse createBucket(String bucketName);

    DeleteBucketResponse deleteBucket(String bucketName);

    List<Bucket> listBuckets();

    String saveResource(String bucketName, String resourceName, byte[] audioData);

    DeleteObjectResponse deleteResource(String resourceName);

    byte[] getResource(String objectName);

}