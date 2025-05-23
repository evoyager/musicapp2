package com.epam.storage.service;

import com.epam.storage.dto.CreateStorageRequest;
import com.epam.storage.dto.StorageData;
import com.epam.storage.exceptions.InvalidCsvException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.tracing.Tracer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class S3StorageService {

    public static final String METADATA_BUCKET_NAME = "storage-metadata";

    private final S3Client s3Client;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Tracer tracer;

    public S3StorageService(S3Client s3Client, Tracer tracer) {
        this.s3Client = s3Client;
        createBucketIfNotExists(METADATA_BUCKET_NAME);
        this.tracer = tracer;
    }

    public CreateBucketResponse createBucketIfNotExists(String bucketName) {
//        String traceId = tracer.currentSpan().context().traceId();
//        String spanId = tracer.currentSpan().context().spanId();
//
//        log.info("Trace ID in S3StorageService: [{}], Span ID: [{}]", traceId , spanId);

        try {
            s3Client.headBucket(HeadBucketRequest.builder().bucket(bucketName).build());
        } catch (NoSuchBucketException e) {
            try {
                return s3Client.createBucket(CreateBucketRequest.builder().bucket(bucketName).build());
            } catch (Exception ex) {
                throw new RuntimeException("Failed to create bucket: " + bucketName, ex);
            }
        }
        return null;
    }

    public int createStorage(CreateStorageRequest request) {
        int id = generateUniqueId();
        String bucketName = request.getBucket();

        createBucketIfNotExists(bucketName);

        StorageData storageData = new StorageData(id, request.getStorageType(), bucketName, request.getPath());
        saveMetadataToS3(id, storageData);

        log.info("Storage created in S3: [{}]", storageData);

        return id;
    }

    public List<Bucket> listBuckets() {
        List<Bucket> result = s3Client.listBuckets().buckets();

        log.info("Buckets retrieved from S3: [{}]", result);

        return result;
    }

    public List<StorageData> getAllStorages() {
        List<StorageData> storages = new ArrayList<>();
        try {
            ListObjectsResponse listObjectsResponse = s3Client.listObjects(ListObjectsRequest.builder()
                    .bucket(METADATA_BUCKET_NAME)
                    .build());

            for (S3Object object : listObjectsResponse.contents()) {
                String jsonString = getObjectContent(METADATA_BUCKET_NAME, object.key());
                try {
                    StorageData storageData = objectMapper.readValue(jsonString, StorageData.class);
                    storages.add(storageData);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve storages from S3", e);
        }

        log.info("Storages retrieved from S3: [{}]", storages);

        return storages;
    }

    public StorageData getStorageByType(String storageType) {
        List<StorageData> storages = getAllStorages();
        var result = storages.stream().filter(storageData -> storageType.equals(storageData.getStorageType())).findFirst().get();

        log.info("Storage fetched by type [{}] from S3: [{}]", storageType, result);

        return result;
    }

    public DeleteBucketResponse deleteBucket(String bucketName) {
        DeleteBucketRequest deleteBucketRequest = DeleteBucketRequest.builder()
                .bucket(bucketName)
                .build();

        try {
            var response = s3Client.deleteBucket(deleteBucketRequest);
            log.info("Bucket deleted: [{}]", deleteBucketRequest.bucket());
            return response;
        } catch (S3Exception e) {
            log.error("Error deleting bucket: [{}]", e.awsErrorDetails().errorMessage());
        }
        return null;
    }

    public void deleteStorageByIds(String idList) {
        if (idList.length() > 200) {
            throw new InvalidCsvException("CSV string is too long: received " + idList.length()
                    + " characters. Maximum allowed length is 200 characters.");
        }

        String[] ids = idList.split(",");

        for (String idStr : ids) {
            deleteObject(METADATA_BUCKET_NAME, idStr);
        }
    }

    public DeleteObjectResponse deleteObject(String bucketName, String resourceName) {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(resourceName)
                .build();

        try {
            var response = s3Client.deleteObject(deleteObjectRequest);
            log.info("Object deleted: [{}]", deleteObjectRequest.key());
            return response;
        } catch (Exception e) {
            log.error(String.format("Error deleting object with key: [%s], from bucket: [%s]. Reason: ", bucketName, bucketName), e.getMessage());
        }
        return null;
    }

    private void saveMetadataToS3(int id, StorageData storageData) {
        try {
            String jsonString = objectMapper.writeValueAsString(storageData);

            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(METADATA_BUCKET_NAME)
                            .key(String.valueOf(id)) // Use the ID as the object key
                            .contentType("application/json")
                            .build(),
                    RequestBody.fromBytes(jsonString.getBytes(StandardCharsets.UTF_8))
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to save metadata to S3", e);
        }
    }

    private String getObjectContent(String bucketName, String key) {
        try {
            ResponseInputStream<GetObjectResponse> responseInputStream = s3Client.getObject(GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build());

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(responseInputStream, StandardCharsets.UTF_8))) {
                return reader.lines().collect(Collectors.joining("\n"));
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to get content of object with key: " + key + " in bucket: " + bucketName, e);
        }
    }

    private int generateUniqueId() {
        try {
            ListObjectsResponse listObjectsResponse = s3Client.listObjects(ListObjectsRequest.builder()
                    .bucket(METADATA_BUCKET_NAME)
                    .build());

            return listObjectsResponse.contents().size() + 1;
        } catch (NoSuchBucketException e) {
            return 1;
        }
    }

    private void deleteEmptyBucket(String bucketName) {
        try {
            List<S3Object> bucketObjects = listBucketObjects(bucketName);
            if (bucketObjects.isEmpty()) {
                s3Client.deleteBucket(DeleteBucketRequest.builder().bucket(bucketName).build());
                log.info("Deleted bucket: " + bucketName);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete bucket: " + bucketName, e);
        }
    }

    public void deleteNonEmptyBucket(String bucketName) {
        try {
            List<S3Object> objectsToDelete = listBucketObjects(bucketName);

            deleteObjects(bucketName, objectsToDelete);

            s3Client.deleteBucket(DeleteBucketRequest.builder().bucket(bucketName).build());
            log.info("Deleted bucket: " + bucketName);
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete bucket: " + bucketName, e);
        }
    }

    private List<S3Object> listBucketObjects(String bucketName) {
        try {
            ListObjectsV2Request listObjectsRequest = ListObjectsV2Request.builder()
                    .bucket(bucketName)
                    .build();

            ListObjectsV2Response listObjectsResponse = s3Client.listObjectsV2(listObjectsRequest);
            return listObjectsResponse.contents();
        } catch (Exception e) {
            throw new RuntimeException("Failed to list objects in bucket: " + bucketName, e);
        }
    }

    private void deleteObjects(String bucketName, List<S3Object> objectsToDelete) {
        try {
            int batchSize = 1000;
            for (int i = 0; i < objectsToDelete.size(); i += batchSize) {
                List<S3Object> batch = objectsToDelete.subList(i,
                        Math.min(i + batchSize, objectsToDelete.size()));

                List<ObjectIdentifier> keysToDelete = batch.stream()
                        .map(obj -> ObjectIdentifier.builder().key(obj.key()).build())
                        .collect(Collectors.toList());

                DeleteObjectsRequest deleteObjectsRequest = DeleteObjectsRequest.builder()
                        .bucket(bucketName)
                        .delete(Delete.builder().objects(keysToDelete).build())
                        .build();

                s3Client.deleteObjects(deleteObjectsRequest);
                log.info("Deleted objects in batch for bucket: " + bucketName);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete objects from bucket: " + bucketName, e);
        }
    }

}
