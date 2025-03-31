package com.epam.resource.integration;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        plugin = {"pretty", "json:target/cucumber-report.json"},
        features = "src/test/java/com/epam/resource/features",
        glue = "com.epam.resource.integration"
)
public class RunCucumberIT {}