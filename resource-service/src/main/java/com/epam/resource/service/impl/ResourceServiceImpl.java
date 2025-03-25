package com.epam.resource.service.impl;

import com.epam.resource.dto.ResourceDto;
import com.epam.resource.mapper.ResourceMapper;
import com.epam.resource.repository.domain.Resource;
import com.epam.resource.messaging.producer.RabbitMQProducer;
import com.epam.resource.repository.ResourceRepository;
import com.epam.resource.service.ResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static com.epam.resource.utils.S3Utils.getS3ObjectUrl;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResourceServiceImpl implements ResourceService {

    private final ResourceRepository resourceRepository;
    private final S3Client s3Client;
    private final RabbitMQProducer rabbitMQProducer;
    private final ResourceMapper resourceMapper;
    private final RetryTemplate retryTemplate;

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

    public void deleteBucket(String bucketName) {
        DeleteBucketRequest deleteBucketRequest = DeleteBucketRequest.builder()
                .bucket(bucketName)
                .build();

        try {
            s3Client.deleteBucket(deleteBucketRequest);
            log.info("Bucket deleted: {}", deleteBucketRequest.bucket());
        } catch (S3Exception e) {
            log.error("Error deleting bucket: {}", e.awsErrorDetails().errorMessage());
        }
    }

    public List<Bucket> listBuckets() {
        return s3Client.listBuckets().buckets();
    }

    public String saveResource(String bucketName, String resourceName, byte[] audioData) {
        String resourceId = saveMp3InCloudStorage(bucketName, resourceName, audioData);

        retryTemplate.execute(arg0 -> {
            Resource resource = Resource.builder()
                    .name(resourceName)
                    .resourceId(resourceId)
                    .build();

            resource = resourceRepository.save(resource);
            ResourceDto resourceDto = resourceMapper.toResourceDto(resource);

            rabbitMQProducer.sendResourceIdMessage(resourceDto);
            log.info("Resource: {} was send.", resourceDto);
            return null;
        });

        return resourceId;
    }

    private String saveMp3InCloudStorage(String bucketName, String resourceName, byte[] audioData) {
        String resourceId = getS3ObjectUrl(RESOURCES_BUCKET_NAME, resourceName);

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(resourceName)
                .contentType("audio/mpeg") // MIME type for MP3
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(audioData));
        log.info("MP3 file uploaded successfully to S3 bucket!");

        return resourceId;
    }

    public void deleteResource(String resourceName) {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(RESOURCES_BUCKET_NAME)
                .key(resourceName)
                .build();

        try {
            s3Client.deleteObject(deleteObjectRequest);
            log.info("Resource deleted: {}", deleteObjectRequest.key());
        } catch (S3Exception e) {
            log.error("Error deleting resource: {}", e.awsErrorDetails().errorMessage());
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