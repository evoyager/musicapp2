package com.epam.storage.controller;

import com.epam.storage.dto.CreateStorageRequest;
import com.epam.storage.dto.CreateStorageResponse;
import com.epam.storage.dto.StorageData;
import com.epam.storage.service.S3StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import software.amazon.awssdk.services.s3.model.Bucket;

import java.util.List;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/storages")
@RequiredArgsConstructor
public class StorageController {

    private final S3StorageService s3StorageService;

    @PostMapping("/{bucketName}")
    public void createBucket(@PathVariable String bucketName) {
        var response = s3StorageService.createBucketIfNotExists(bucketName);
    }

    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<CreateStorageResponse> createStorage(@RequestBody CreateStorageRequest request) {

        if (request.getStorageType() == null || request.getBucket() == null) {
            return ResponseEntity.badRequest().build();
        }

        int storageId = s3StorageService.createStorage(request);

        return ResponseEntity.ok(new CreateStorageResponse(storageId));
    }

    @GetMapping(value = "/buckets")
    public List<String> listBuckets() {
        return s3StorageService.listBuckets()
                .stream()
                .map(Bucket::name)
                .collect(toList());
    }

    @GetMapping("/all")
    public ResponseEntity<List<StorageData>> getAllStorages() {
        List<StorageData> storages = s3StorageService.getAllStorages();

        return ResponseEntity.ok(storages);
    }

    @GetMapping()
    public ResponseEntity<StorageData> getStorageByType(@RequestParam(value = "storageType") String storageType) {
        StorageData storage = s3StorageService.getStorageByType(storageType);

        return ResponseEntity.ok(storage);
    }

    @DeleteMapping("/{bucketName}")
    public void deleteBucket(@PathVariable String bucketName) {
        s3StorageService.deleteNonEmptyBucket(bucketName);
    }

    @DeleteMapping()
    public ResponseEntity<Void> deleteBuckets(@RequestParam("id") String idList) {

        s3StorageService.deleteStorageByIds(idList);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{bucketName}/{objectName}")
    public void deleteObject(@PathVariable String bucketName, @PathVariable String objectName) {
        s3StorageService.deleteObject(bucketName, objectName);
    }
}
