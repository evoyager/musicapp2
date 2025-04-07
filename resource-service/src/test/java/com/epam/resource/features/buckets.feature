Feature: Manage buckets

  Scenario: Create a new bucket
    Given I send a POST request to "/buckets/test-bucket"
    Then the bucket "test-bucket" should be created

  Scenario: List all buckets
    Given there are existing buckets
    When I send a GET request to "/buckets"
    Then I should receive a list of all bucket names

  Scenario: Delete a bucket
    Given "test-bucket" exists
    When I send a DELETE request to "/buckets/test-bucket"
    Then the bucket "test-bucket" should no longer exist