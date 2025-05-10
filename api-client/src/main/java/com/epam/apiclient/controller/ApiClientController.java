package com.epam.apiclient.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.reactive.function.client.WebClient;

import static org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction.oauth2AuthorizedClient;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ApiClientController {

    private final WebClient webClient;

    @GetMapping("/storages")
    public String getBucketForm(@RegisteredOAuth2AuthorizedClient("api-client") OAuth2AuthorizedClient authorizedClient, Model model) {
        String storageServiceUrl = "http://127.0.0.1:8085/storages/create-bucket-form";

        try {
            String formHtml = this.webClient.get()
                    .uri(storageServiceUrl)
                    .headers(headers -> headers.setBearerAuth(authorizedClient.getAccessToken().getTokenValue()))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            String token = authorizedClient.getAccessToken().getTokenValue();
            String injectedHtml = formHtml
                    .replace("const token = params.get(\"token\");",
                            "const token = '" + token + "';");

            log.info("formHtml: {}", formHtml);
            model.addAttribute("formHtml", injectedHtml);
        } catch (Exception e) {
            model.addAttribute("formHtml", "<p>Error: Could not fetch form content!</p>");
        }

        return "template-view";
    }


    @GetMapping(value = "/storages/buckets")
    public String[] listBuckets(
            @RegisteredOAuth2AuthorizedClient("api-client") OAuth2AuthorizedClient authorizedClient) {
        return this.webClient
                .get()
                .uri("http://127.0.0.1:8085/storages/buckets")
                .attributes(oauth2AuthorizedClient(authorizedClient))
                .retrieve()
                .bodyToMono(String[].class)
                .block();
    }

}
