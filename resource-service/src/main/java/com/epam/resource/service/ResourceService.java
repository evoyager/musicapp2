package com.epam.resource.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResourceService {

    private final S3Client s3Client;

    private final String RESOURCES_BUCKET_NAME = "resources";

    public void createBucket(String bucketName) {
        CreateBucketRequest createBucketRequest = CreateBucketRequest.builder()
                .bucket(bucketName)
                .build();

        try {
            s3Client.createBucket(createBucketRequest);
            log.info("Bucket created: {}", createBucketRequest.bucket());
        } catch (S3Exception e) {
            log.error("Error creating bucket: {}", e.awsErrorDetails().errorMessage());
        }
    }

    public List<Bucket> listBuckets() {
        return s3Client.listBuckets().buckets();
    }

    public void saveResource(String bucketName, String objectName, byte[] audioData) {
        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectName)
                    .contentType("audio/mpeg") // MIME type for MP3
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(audioData));
            log.info("MP3 file uploaded successfully to S3 bucket!");
        }
//        catch (Exception e) {
//            log.error("Error uploading file to S3: {}", e.getMessage());
//            e.printStackTrace();
//        }
        finally {
            s3Client.close();
        }
    }

    public byte[] getResource(String objectName) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(RESOURCES_BUCKET_NAME)
                .key(objectName)
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

    public static byte[] responseInputStreamToByteArray(ResponseInputStream<GetObjectResponse> responseInputStream) throws IOException {
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