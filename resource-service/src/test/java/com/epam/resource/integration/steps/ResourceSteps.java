package com.epam.resource.integration.steps;

import io.cucumber.java.en.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.*;

public class ResourceSteps {

    @Autowired
    private TestRestTemplate restTemplate;

    private ResponseEntity<?> response;

    @Given("I have audio data")
    public void iHaveAudioData() {
        // Mock audio data as bytes
    }

    @When("I send a POST request to {string} with the audio data")
    public void iSendAPOSTRequestToWithTheAudioData(String endpoint) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf("audio/mpeg"));
        HttpEntity<byte[]> request = new HttpEntity<>(new byte[10], headers); // Replace with actual audio data
        response = restTemplate.exchange(endpoint, HttpMethod.POST, request, String.class);
    }

    @Then("the resource URL should be returned")
    public void theResourceURLShouldBeReturned() {
        assertEquals(200, response.getStatusCodeValue());
        // Additional checks on returned URL
    }

    @Given("{string} has been uploaded")
    public void audioFileHasBeenUploaded(String resourceId) {
        // Mock audio data to be uploaded
        byte[] audioData = new byte[]{1, 2, 3, 4, 5}; // Replace with actual audio data in a real-world scenario

        // Configure headers for the POST request
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf("audio/mpeg"));

        HttpEntity<byte[]> request = new HttpEntity<>(audioData, headers);

        // Send POST request to upload the resource
        ResponseEntity<Map> response = restTemplate.postForEntity(
                "/buckets/resources/" + resourceId,
                request,
                Map.class
        );

        // Validate the upload response
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        String uploadedResourceUrl = (String) response.getBody().get("url"); // Capture the resource URL from the response
    }

    @When("I send a GET request to {string} to retrieve mp3")
    public void iSendAGETRequestTo(String endpoint) {
        response = restTemplate.getForEntity(endpoint, byte[].class);
    }

    @Then("I should receive the audio file")
    public void iShouldReceiveTheAudioFile() {
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(MediaType.valueOf("audio/mpeg"), response.getHeaders().getContentType());

        // Ensure the returned audio data is non-empty
        byte[] audioData = (byte[]) response.getBody();
        assertNotNull(audioData);
        assertTrue(audioData.length > 0);
    }

    @Then("the resource {string} should no longer exist")
    public void theResourceShouldNoLongerExist(String resourceId) {
        ResponseEntity<byte[]> response = restTemplate.exchange(
                "/buckets/resources/" + resourceId,
                HttpMethod.GET,
                null,
                byte[].class
        );

        assertNull(response.getBody());
    }
}
