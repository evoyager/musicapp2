package com.epam.resource.service;

import com.epam.resource.service.impl.S3ServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

import java.io.IOException;

import static com.epam.resource.service.impl.constants.TestConstants.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.AssertionErrors.assertEquals;

@ExtendWith(MockitoExtension.class)
class S3ServiceTest {

    @InjectMocks
    private S3ServiceImpl s3Service;

    @Mock
    S3Client s3Client;

    @Test
    void getResource() throws IOException {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(MUSIC_BUCKET_NAME)
                .key(STAGING_PATH + "/" + RESOURCE_NAME)
                .build();
        byte[] expected = new byte[]{};
        ResponseInputStream<GetObjectResponse> mockInputStream = mock(ResponseInputStream.class);

        when(s3Client.getObject(getObjectRequest)).thenReturn(mockInputStream);
        when(mockInputStream.read(any())).thenReturn(-1);

        var actual = s3Service.getResource(MUSIC_BUCKET_NAME, STAGING_PATH, RESOURCE_NAME);

        assertEquals("Actual and expected responses aren't equal", expected, actual);
        verify(s3Client, only()).getObject(getObjectRequest);
    }
}