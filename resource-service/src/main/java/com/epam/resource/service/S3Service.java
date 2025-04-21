package com.epam.resource.service;

import software.amazon.awssdk.services.s3.model.DeleteObjectResponse;

public interface S3Service {

    byte[] getResource(String bucketName, String path, String objectName);

    String putResource(String bucketName, String path, String resourceName, byte[] audioData);

    DeleteObjectResponse deleteResource(String bucketName, String objectName);

}
