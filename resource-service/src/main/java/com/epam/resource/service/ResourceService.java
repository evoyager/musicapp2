package com.epam.resource.service;

import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.Bucket;

import java.util.List;

@Service
public interface ResourceService {

    void createBucket(String bucketName);

    void deleteBucket(String bucketName);

    List<Bucket> listBuckets();

    String saveResource(String bucketName, String resourceName, byte[] audioData);

    void deleteResource(String resourceName);

    byte[] getResource(String objectName);

}