package com.epam.resource.controller;

import com.epam.resource.service.ResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import software.amazon.awssdk.services.s3.model.Bucket;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/buckets")
@RequiredArgsConstructor
public class ResourceController {

    private final ResourceService resourceService;

    private final String RESOURCES_BUCKET_NAME = "resources";

    @PostMapping("/{bucketName}")
    public void createBucket(@PathVariable String bucketName) {
        resourceService.createBucket(bucketName);
    }

    @DeleteMapping("/{bucketName}")
    public void deleteBucket(@PathVariable String bucketName) {
        resourceService.deleteBucket(bucketName);
    }

    @GetMapping
    public List<String> listBuckets() {
        return resourceService.listBuckets()
                .stream()
                .map(Bucket::name)
                .collect(toList());
    }

    @PostMapping(value = "/resources/{resourceId}", consumes = "audio/mpeg", produces = "application/json")
    public ResponseEntity<Map<String, String>> uploadResource(@PathVariable String resourceId,
                                                      @RequestBody byte[] audioData) {
        String url = resourceService.saveResource(RESOURCES_BUCKET_NAME, resourceId, audioData);

        return ResponseEntity.ok(Map.of("url", url));
    }

    @GetMapping(value = "/resources/{objectName}", produces = "audio/mpeg")
    public ResponseEntity<byte[]> getResource(@PathVariable String objectName) {
        byte[] data = resourceService.getResource(objectName);
        return ResponseEntity.ok().contentType(MediaType.parseMediaType("audio/mpeg")).body(data);
    }

    @DeleteMapping("/resources/objects/{objectName}")
    public void deleteObject(@PathVariable String objectName) {
        resourceService.deleteResource(objectName);
    }


}
