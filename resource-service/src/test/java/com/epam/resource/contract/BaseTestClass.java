package com.epam.resource.contract;

import com.epam.resource.controller.ResourceController;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.verifier.messaging.boot.AutoConfigureMessageVerifier;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.nio.file.Path;
import java.nio.file.Paths;

import static com.epam.resource.service.impl.constants.TestConstants.MUSIC_BUCKET_NAME;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@DirtiesContext
@AutoConfigureMessageVerifier
public class BaseTestClass {
    @Autowired
    private ResourceController resourceController;

    @Autowired
    S3Client s3Client;

    @BeforeEach
    public void setup() {
        StandaloneMockMvcBuilder standaloneMockMvcBuilder
                = MockMvcBuilders.standaloneSetup(resourceController);
        RestAssuredMockMvc.standaloneSetup(standaloneMockMvcBuilder);

        prepareS3Data();
    }

    private void prepareS3Data() {
        // Clear bucket
        s3Client.listObjectsV2(b -> b.bucket(MUSIC_BUCKET_NAME)).contents().forEach(file -> {
            s3Client.deleteObject(b -> b.bucket(MUSIC_BUCKET_NAME).key(file.key()));
        });

        // Prepare a test MP3 file for required test cases
        Path testAudioPath = Paths.get("src/test/resources/contracts/resources/test.mp3");
        s3Client.putObject(PutObjectRequest.builder()
                        .bucket(MUSIC_BUCKET_NAME)
                        .key("audio123")
                        .contentType("audio/mpeg")
                        .build(),
                software.amazon.awssdk.core.sync.RequestBody.fromFile(testAudioPath));
    }
}
