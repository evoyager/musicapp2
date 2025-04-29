package com.epam.resource.service.impl;

import com.epam.resource.service.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3ServiceImpl implements S3Service {

    private static String AWS_SERVICE_ENDPOINT;

    @Value("${aws.serviceEndpoint}")
    public void setAwsServiceEndpoint(String awsServiceEndpoint) {
        AWS_SERVICE_ENDPOINT = awsServiceEndpoint;
    }

    private final S3Client s3Client;


    @Override
    public byte[] getResource(String bucketName, String path, String objectName) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(path + "/" + objectName)
                .build();

        try {
            ResponseInputStream<GetObjectResponse> responseInputStream = s3Client.getObject(getObjectRequest);
            return responseInputStreamToByteArray(responseInputStream);
        } catch (S3Exception e) {
            log.error(e.awsErrorDetails().errorMessage());
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return new byte[0];
    }

    @Override
    public String putResource(String bucketName, String path, String resourceName, byte[] audioData) {
        String resourceId = getS3ObjectUrl(bucketName, path, resourceName);

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(path + "/" + resourceName)
                .contentType("audio/mpeg") // MIME type for MP3
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(audioData));
        log.info("MP3 file uploaded successfully to S3 bucket!");

        return resourceId;
    }

    @Override
    public DeleteObjectResponse deleteResource(String bucketName, String objectName) {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(objectName)
                .build();

        return s3Client.deleteObject(deleteObjectRequest);
    }

    public static String getS3ObjectUrl(String bucketName, String path, String resourceName) {
        return AWS_SERVICE_ENDPOINT + "/" + bucketName + "/" + path + "/" + resourceName;
    }

    private static byte[] responseInputStreamToByteArray(ResponseInputStream<GetObjectResponse> responseInputStream) throws IOException {
        try (InputStream inputStream = responseInputStream;
             ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {

            byte[] buffer = new byte[4096]; // 4KB buffer size
            int bytesRead;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
            }

            return byteArrayOutputStream.toByteArray();
        }
    }
}
