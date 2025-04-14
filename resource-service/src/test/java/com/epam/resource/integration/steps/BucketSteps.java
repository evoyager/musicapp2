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

    @When("I send a GET request to {string}")
    public void iSendAGETRequestTo(String endpoint) {
        response = restTemplate.getForEntity(endpoint, String[].class);
    }

    @Then("I should receive a list of all bucket names")
    public void iShouldReceiveAListOfAllBucketNames() {
        assertEquals(200, response.getStatusCodeValue());

        String[] bucketNames = (String[]) response.getBody();

        assertNotNull(bucketNames);

        assertTrue(bucketNames.length > 0);

        System.out.println("Bucket names retrieved: " + Arrays.toString(bucketNames));

        assertTrue(Arrays.asList(bucketNames).contains("test-bucket"));

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
        ResponseEntity<String[]> response = restTemplate.getForEntity("/buckets", String[].class);

        assertEquals(200, response.getStatusCodeValue());

        String[] bucketNames = response.getBody();
        assertNotNull(bucketNames); // Ensure we have valid data

        for (String bucketName : bucketNames) {
            assertNotEquals(name, bucketName); // Assert the deleted bucket name is no longer in the list
        }
    }
}
