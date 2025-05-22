package com.epam.storage.controller;

import com.epam.storage.dto.CreateStorageRequest;
import com.epam.storage.dto.CreateStorageResponse;
import com.epam.storage.dto.StorageData;
import com.epam.storage.service.S3StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import software.amazon.awssdk.services.s3.model.Bucket;

import java.nio.file.Files;
import java.util.List;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/storages")
@CrossOrigin(origins = "http://127.0.0.1:8091")
@RequiredArgsConstructor
public class StorageController {

    private final S3StorageService s3StorageService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/create-bucket-form")
    public ResponseEntity<String> getCreateBucketForm() {
        try {
            ClassPathResource resource = new ClassPathResource("static/index.html");
            String content = Files.readString(resource.getFile().toPath());
            return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_HTML)
                    .body(content);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error serving form content!");
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{bucketName}")
    public void createBucket(@PathVariable String bucketName) {
        var response = s3StorageService.createBucketIfNotExists(bucketName);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<CreateStorageResponse> createStorage(@RequestBody CreateStorageRequest request) {

        if (request.getStorageType() == null || request.getBucket() == null) {
            return ResponseEntity.badRequest().build();
        }

        int storageId = s3StorageService.createStorage(request);

        return ResponseEntity.ok(new CreateStorageResponse(storageId));
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping(value = "/buckets")
    public List<String> listBuckets() {
        return s3StorageService.listBuckets()
                .stream()
                .map(Bucket::name)
                .collect(toList());
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<List<StorageData>> getAllStorages() {
        List<StorageData> storages = s3StorageService.getAllStorages();

        return ResponseEntity.ok(storages);
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping()
    public ResponseEntity<StorageData> getStorageByType(@RequestParam(value = "storageType") String storageType) {
        StorageData storage = s3StorageService.getStorageByType(storageType);

        return ResponseEntity.ok(storage);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{bucketName}")
    public void deleteBucket(@PathVariable String bucketName) {
        s3StorageService.deleteNonEmptyBucket(bucketName);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping()
    public ResponseEntity<Void> deleteBuckets(@RequestParam("id") String idList) {

        s3StorageService.deleteStorageByIds(idList);

        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{bucketName}/{objectName}")
    public void deleteObject(@PathVariable String bucketName, @PathVariable String objectName) {
        s3StorageService.deleteObject(bucketName, objectName);
    }
}
