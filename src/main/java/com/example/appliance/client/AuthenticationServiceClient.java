package com.example.appliance.client;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class AuthenticationServiceClient {

    @Value("${client.authentication.url}")
    private String url;

    public ValidateTokenResponse validateToken(String token) {

        RestTemplate restTemplate = new RestTemplate();
        String resourceUrl = url + "/api/v1/token/validate";

        Map<String, String> tokenMap = new HashMap();
        tokenMap.put("accessToken", token);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(tokenMap, headers);

        ResponseEntity<ValidateTokenResponse> response
            = restTemplate.postForEntity(resourceUrl, request, ValidateTokenResponse.class);
        return response.getBody();
    }
}
