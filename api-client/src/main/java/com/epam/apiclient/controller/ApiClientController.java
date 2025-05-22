package com.epam.apiclient.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.reactive.function.client.WebClient;

import static org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction.oauth2AuthorizedClient;

@Slf4j
@Controller
public class ApiClientController {

    private final WebClient webClient;
    private final String STORAGE_SERVICE_URL;

    public ApiClientController(WebClient webClient, @Value("${storage.service.host}") String storageServiceHost) {
        this.webClient = webClient;
        STORAGE_SERVICE_URL = "http://" + storageServiceHost + ":8085";
    }

    @GetMapping("/storages")
    public String getBucketForm(@RegisteredOAuth2AuthorizedClient("api-client") OAuth2AuthorizedClient authorizedClient, Model model) {

        String storageServiceUrl = STORAGE_SERVICE_URL + "/storages/create-bucket-form";
        String token = authorizedClient.getAccessToken().getTokenValue();

        log.info("storageServiceUrl: {}", storageServiceUrl);
        log.info("token: {}", token);

        try {
            String formHtml = this.webClient.get()
                    .uri(storageServiceUrl)
                    .headers(headers -> headers.setBearerAuth(token))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            String injectedHtml = formHtml
                    .replace("const token = params.get(\"token\");",
                            "const token = '" + token + "';");

            log.info("formHtml: {}", formHtml);
            model.addAttribute("formHtml", injectedHtml);
        } catch (Exception e) {
            model.addAttribute("formHtml", String.format("<p>Error: Could not fetch form content! Exception: %s</p>", e));
        }

        return "template-view";
    }


    @GetMapping(value = "/storages/buckets")
    public String[] listBuckets(
            @RegisteredOAuth2AuthorizedClient("api-client") OAuth2AuthorizedClient authorizedClient) {
        return this.webClient
                .get()
                .uri(STORAGE_SERVICE_URL + "/storages/buckets")
                .attributes(oauth2AuthorizedClient(authorizedClient))
                .retrieve()
                .bodyToMono(String[].class)
                .block();
    }

}
