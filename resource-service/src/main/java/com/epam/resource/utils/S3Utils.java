package com.epam.resource.utils;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class S3Utils {

    private static String AWS_SERVICE_ENDPOINT;

    @Value("${aws.serviceEndpoint}")
    public void setAwsServiceEndpoint(String awsServiceEndpoint) {
        S3Utils.AWS_SERVICE_ENDPOINT = awsServiceEndpoint;
    }

    public static String getS3ObjectUrl(String bucketName, String resourceId) {
        return AWS_SERVICE_ENDPOINT + "/" + bucketName + "/" + resourceId;
    }
}
