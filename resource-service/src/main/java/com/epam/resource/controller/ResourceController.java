package com.epam.resource.controller;

import com.epam.resource.service.ResourceService;
import com.epam.resource.service.S3Service;
import io.micrometer.tracing.Tracer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/resources")
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class ResourceController {

    private final ResourceService resourceService;
    private final S3Service s3Service;
    private final Tracer tracer;

    @PostMapping(value = "/{resourceId}", consumes = "audio/mpeg", produces = "application/json")
    public ResponseEntity<Map<String, String>> uploadResource(@PathVariable String resourceId,
                                                      @RequestBody byte[] audioData) {
        String traceId = tracer.currentSpan().context().traceId();
        log.info("Trace ID: {}", traceId);

        String url = resourceService.saveResource(resourceId, audioData);

        return ResponseEntity.ok(Map.of("url", url));
    }

    @GetMapping(value = "/{bucketName}/{path}/{objectName}", produces = "audio/mpeg")
    public ResponseEntity<byte[]> getResource(@PathVariable String bucketName, @PathVariable String path, @PathVariable String objectName) {
        byte[] data = s3Service.getResource(bucketName, path, objectName);
        return ResponseEntity.ok().contentType(MediaType.parseMediaType("audio/mpeg")).body(data);
    }

}
