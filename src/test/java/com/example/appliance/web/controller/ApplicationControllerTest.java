package com.example.appliance.web.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.appliance.client.AuthenticationServiceClient;
import com.example.appliance.client.ValidateTokenResponse;
import com.example.appliance.persistence.dao.ApplianceRepository;
import com.example.appliance.persistence.model.Appliance;
import com.example.appliance.web.dto.ApplianceDto;
import com.example.appliance.web.dto.ApplianceWithStatusDto;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class ApplicationControllerTest {

    @LocalServerPort
    private int port;
    @Autowired
    private TestRestTemplate restTemplate;
    @MockBean
    private ApplianceRepository applianceRepository;
    @MockBean
    private AuthenticationServiceClient authenticationServiceClient;
    private final String applianceId1 = "app1";
    private final String applianceSecret1 = "secret1";
    private final String factoryNumber1 = "factory1";
    private final String username1 = "name1";
    private final String token = "testToken";
    private ApplianceDto applianceDto1;
    private Appliance appliance1;
    private ValidateTokenResponse tokenResponseAdmin;
    private ValidateTokenResponse tokenResponseUser;

    @BeforeEach
    public void setUp() {
        applianceDto1 = new ApplianceDto();
        applianceDto1.setApplianceId(applianceId1);
        applianceDto1.setApplianceSecret(applianceSecret1);
        applianceDto1.setFactoryNumber(factoryNumber1);
        appliance1 = new Appliance();
        appliance1.setApplianceId(applianceId1);
        appliance1.setApplianceSecret(applianceSecret1);
        appliance1.setFactoryNumber(factoryNumber1);
        tokenResponseAdmin = new ValidateTokenResponse();
        tokenResponseAdmin.setValid(true);
        tokenResponseAdmin.setUsername(username1);
        tokenResponseAdmin.setRoles(Arrays.asList("ROLE_ADMIN"));
        tokenResponseUser = new ValidateTokenResponse();
        tokenResponseUser.setValid(true);
        tokenResponseUser.setUsername(username1);
        tokenResponseUser.setRoles(Arrays.asList("ROLE_USER"));
    }

    @Test
    public void testCreateAppliance() {
        when(authenticationServiceClient.validateToken(token)).thenReturn(tokenResponseAdmin);
        when(applianceRepository.existsByApplianceId(applianceId1)).thenReturn(false);
        when(applianceRepository.save(any())).thenReturn(appliance1);

        String resourceUrl = "http://localhost:" + port + "/api/v1/appliance";
        Map<String, String> entity = new HashMap();
        entity.put("applianceId", applianceId1);
        entity.put("applianceSecret", applianceSecret1);
        entity.put("factoryNumber", factoryNumber1);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, String>> request = new HttpEntity<>(entity, headers);
        ResponseEntity<Appliance> responseEntity = restTemplate
            .postForEntity(resourceUrl, request, Appliance.class);
        Appliance appliance = responseEntity.getBody();

        ArgumentCaptor<Appliance> argumentCaptor =
            ArgumentCaptor.forClass(Appliance.class);
        verify(applianceRepository, times(1)).save(argumentCaptor.capture());
        Appliance savedAppliance = argumentCaptor.getValue();

        assertEquals(applianceId1, appliance.getApplianceId());
        assertEquals(null, appliance.getApplianceSecret());
        assertEquals(factoryNumber1, appliance.getFactoryNumber());
        assertEquals(null, appliance.getUsername());
        assertEquals(null, appliance.getLastActiveTime());

        assertEquals(applianceId1, savedAppliance.getApplianceId());
        assertEquals(applianceSecret1, savedAppliance.getApplianceSecret());
        assertEquals(factoryNumber1, savedAppliance.getFactoryNumber());
        assertEquals(null, savedAppliance.getUsername());
        assertEquals(null, savedAppliance.getLastActiveTime());
    }

    @Test
    public void testCreateApplianceForbidden() {
        when(authenticationServiceClient.validateToken(token)).thenReturn(tokenResponseUser);
        String resourceUrl = "http://localhost:" + port + "/api/v1/appliance";
        Map<String, String> entity = new HashMap();
        entity.put("applianceId", applianceId1);
        entity.put("applianceSecret", applianceSecret1);
        entity.put("factoryNumber", factoryNumber1);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, String>> request = new HttpEntity<>(entity, headers);
        ResponseEntity<Appliance> responseEntity = restTemplate
            .postForEntity(resourceUrl, request, Appliance.class);
        assertEquals(403, responseEntity.getStatusCodeValue());
    }

    @Test
    public void testRegisterAppliance() {
        when(authenticationServiceClient.validateToken(token)).thenReturn(tokenResponseUser);
        when(applianceRepository.findByApplianceId(applianceId1))
            .thenReturn(Optional.of(appliance1));
        appliance1.setUsername(username1);
        when(applianceRepository.save(any())).thenReturn(appliance1);

        String resourceUrl = "http://localhost:" + port + "/api/v1/appliance/register";
        Map<String, String> entity = new HashMap();
        entity.put("applianceId", applianceId1);
        entity.put("applianceSecret", applianceSecret1);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, String>> request = new HttpEntity<>(entity, headers);
        ResponseEntity<Appliance> responseEntity = restTemplate
            .postForEntity(resourceUrl, request, Appliance.class);
        Appliance appliance = responseEntity.getBody();

        ArgumentCaptor<Appliance> argumentCaptor =
            ArgumentCaptor.forClass(Appliance.class);
        verify(applianceRepository, times(1)).save(argumentCaptor.capture());
        Appliance savedAppliance = argumentCaptor.getValue();

        assertEquals(applianceId1, appliance.getApplianceId());
        assertEquals(null, appliance.getApplianceSecret());
        assertEquals(factoryNumber1, appliance.getFactoryNumber());
        assertEquals(username1, appliance.getUsername());
        assertEquals(null, appliance.getLastActiveTime());

        assertEquals(applianceId1, savedAppliance.getApplianceId());
        assertEquals(applianceSecret1, savedAppliance.getApplianceSecret());
        assertEquals(factoryNumber1, savedAppliance.getFactoryNumber());
        assertEquals(username1, savedAppliance.getUsername());
        assertEquals(null, savedAppliance.getLastActiveTime());
    }

    @Test
    public void testGetAppliance() {
        when(authenticationServiceClient.validateToken(token)).thenReturn(tokenResponseUser);
        when(applianceRepository.findByApplianceId(applianceId1))
            .thenReturn(Optional.of(appliance1));

        String resourceUrl = "http://localhost:" + port + "/api/v1/appliance/" + applianceId1;
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity entity = new HttpEntity(headers);
        ResponseEntity<ApplianceWithStatusDto> responseEntity = restTemplate.exchange(
            resourceUrl, HttpMethod.GET, entity, ApplianceWithStatusDto.class);

        ApplianceWithStatusDto applianceWithStatusDto = responseEntity.getBody();
        assertEquals(applianceId1, applianceWithStatusDto.getApplianceId());
        assertEquals(null, applianceWithStatusDto.getApplianceSecret());
        assertEquals(factoryNumber1, applianceWithStatusDto.getFactoryNumber());
        assertEquals(null, applianceWithStatusDto.getUsername());
        assertEquals(null, applianceWithStatusDto.getLastActiveTime());
    }

    @Test
    public void testGetApplianceNotFound() {
        when(authenticationServiceClient.validateToken(token)).thenReturn(tokenResponseUser);
        when(applianceRepository.findByApplianceId(applianceId1)).thenReturn(Optional.empty());

        String resourceUrl = "http://localhost:" + port + "/api/v1/appliance/" + applianceId1;
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity entity = new HttpEntity(headers);
        ResponseEntity<ApplianceWithStatusDto> responseEntity = restTemplate.exchange(
            resourceUrl, HttpMethod.GET, entity, ApplianceWithStatusDto.class);
        assertEquals(404, responseEntity.getStatusCodeValue());
    }

    @Test
    public void testGetAppliances() {
        Appliance appliance2 = new Appliance();
        appliance2.setApplianceId("app2");
        appliance2.setApplianceSecret("secret2");
        appliance2.setFactoryNumber("factory2");

        when(authenticationServiceClient.validateToken(token)).thenReturn(tokenResponseAdmin);
        when(applianceRepository.findAll())
            .thenReturn(Arrays.asList(appliance1, appliance2));

        String resourceUrl = "http://localhost:" + port + "/api/v1/appliance";
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity entity = new HttpEntity(headers);
        ResponseEntity<List> responseEntity = restTemplate.exchange(
            resourceUrl, HttpMethod.GET, entity, List.class);

        List<ApplianceWithStatusDto> applianceList = responseEntity.getBody();
        assertEquals(2, applianceList.size());
    }

    @Test
    public void testApplianceAlive() {
        when(applianceRepository.findByApplianceId(applianceId1))
            .thenReturn(Optional.of(appliance1));
        when(applianceRepository.save(any())).thenReturn(appliance1);

        String resourceUrl = "http://localhost:" + port + "/api/v1/appliance/alive";
        Map<String, String> entity = new HashMap();
        entity.put("applianceId", applianceId1);
        entity.put("applianceSecret", applianceSecret1);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, String>> request = new HttpEntity<>(entity, headers);
        ResponseEntity<String> responseEntity = restTemplate
            .postForEntity(resourceUrl, request, String.class);

        ArgumentCaptor<Appliance> argumentCaptor =
            ArgumentCaptor.forClass(Appliance.class);
        verify(applianceRepository, times(1)).save(argumentCaptor.capture());
        Appliance savedAppliance = argumentCaptor.getValue();

        assertEquals(204, responseEntity.getStatusCodeValue());
        assertNotNull(savedAppliance.getLastActiveTime());
    }
}
