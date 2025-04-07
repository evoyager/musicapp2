Feature: Manage resources

  Scenario: Upload a resource
    Given I have audio data
    When I send a POST request to "/buckets/resources/audio-file" with the audio data
    Then the resource URL should be returned

  Scenario: Retrieve a resource
    Given "audio-file" has been uploaded
    When I send a GET request to "/buckets/resources/audio-file" to retrieve mp3
    Then I should receive the audio file

  Scenario: Delete a resource
    Given "audio-file" has been uploaded
    When I send a DELETE request to "/buckets/resources/objects/audio-file"
    Then the resource "audio-file" should no longer exist