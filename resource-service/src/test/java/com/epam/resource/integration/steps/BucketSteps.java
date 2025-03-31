package com.epam.resource.integration.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.*;

public class BucketSteps {

    @Autowired
    private TestRestTemplate restTemplate;

    private String bucketName;
    private ResponseEntity<?> response;
    private String uploadedResourceUrl;

    @Given("I send a POST request to {string}")
    public void iSendAPOSTRequestTo(String endpoint) {
        bucketName = endpoint.split("/")[2];
        response = restTemplate.postForEntity(endpoint, null, Void.class);
    }

    @Then("the bucket {string} should be created")
    public void theBucketShouldBeCreated(String name) {
        assertEquals(200, response.getStatusCodeValue());
        // Additional assertions to verify bucket creation
    }

    @Given("there are existing buckets")
    public void thereAreExistingBuckets() {
        // Prepopulate buckets if necessary
    }

//    @Given("\"{string}\" has been uploaded")
//    public void audioFileHasBeenUploaded(String resourceId) {
//        // Mock or load the audio data
//        byte[] audioData = new byte[] {1, 2, 3, 4, 5}; // Replace with actual mock audio data
//
//        // Set headers for audio data
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.valueOf("audio/mpeg"));
//
//        HttpEntity<byte[]> request = new HttpEntity<>(audioData, headers);
//
//        // Upload the resource via POST request
//        ResponseEntity<String> response = restTemplate.postForEntity("/buckets/resources/" + resourceId, request, String.class);
//
//        // Save the resource URL for later validation
//        assertEquals(200, response.getStatusCodeValue());
//        uploadedResourceUrl = response.getBody(); // The response body contains the URL of the uploaded resource
//    }

    @When("I send a GET request to {string}")
    public void iSendAGETRequestTo(String endpoint) {
        response = restTemplate.getForEntity(endpoint, String[].class);
    }

    @Then("I should receive a list of all bucket names")
    public void iShouldReceiveAListOfAllBucketNames() {
        // Validate that the response has a 200 OK status code
        assertEquals(200, response.getStatusCodeValue());

        // Extract the response body as the list of bucket names
        String[] bucketNames = (String[]) response.getBody();

        // Ensure the response body is not null
        assertNotNull(bucketNames);

        // Assert that the bucket names list is not empty
        assertTrue(bucketNames.length > 0);

        // You can perform additional validations here based on expected bucket names. For example:
        System.out.println("Bucket names retrieved: " + Arrays.toString(bucketNames));

        assertTrue(Arrays.asList(bucketNames).contains("test-bucket"));

        // (Optional) Validate against expected bucket names if required:
        // List<String> expectedBuckets = List.of("bucket1", "bucket2");
        // assertTrue(Arrays.asList(bucketNames).containsAll(expectedBuckets));
    }

    @Given("{string} exists")
    public void bucketExists(String name) {
        bucketName = name;
        restTemplate.postForEntity("/buckets/" + bucketName, null, Void.class);
    }

    @When("I send a DELETE request to {string}")
    public void iSendADELETERequestTo(String endpoint) {
        restTemplate.delete(endpoint);
    }

    @Then("the bucket {string} should no longer exist")
    public void theBucketShouldNoLongerExist(String name) {
        // Send a GET request to retrieve the list of buckets
        ResponseEntity<String[]> response = restTemplate.getForEntity("/buckets", String[].class);

        // Ensure the call to list buckets is successful
        assertEquals(200, response.getStatusCodeValue());

        // Extract the list of bucket names from the response
        String[] bucketNames = response.getBody();
        assertNotNull(bucketNames); // Ensure we have valid data

        // Verify that the bucket name is NOT present in the list
        for (String bucketName : bucketNames) {
            assertNotEquals(name, bucketName); // Assert the deleted bucket name is no longer in the list
        }
    }
}
