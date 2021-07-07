package com.example.appliance.web.controller;

import com.example.appliance.exception.AccessDeniedException;
import com.example.appliance.exception.InvalidApplianceSecretException;
import com.example.appliance.persistence.model.Appliance;
import com.example.appliance.service.ApplianceService;
import com.example.appliance.web.dto.ApplianceDto;
import com.example.appliance.web.dto.ApplianceWithStatusDto;
import com.example.appliance.web.dto.RoleName;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApplianceController {

    private final ApplianceService applianceService;

    @Autowired
    public ApplianceController(ApplianceService applianceService) {
        this.applianceService = applianceService;
    }

    @PostMapping("api/v1/appliance")
    public Appliance createAppliance(
        @RequestBody ApplianceDto applianceDto) {
        return applianceService.createAppliance(applianceDto);
    }

    @PostMapping("api/v1/appliance/register")
    public Appliance registerAppliance(
        @RequestBody ApplianceDto applianceDto) {
        Appliance savedAppliance = applianceService
            .getApplianceByApplianceId(applianceDto.getApplianceId());
        checkApplianceSecret(applianceDto, savedAppliance);
        return applianceService.toApplianceWithStatusDto(
            applianceService.registerAppliance(savedAppliance, getUsernameFromContext()));
    }

    @GetMapping("api/v1/appliance/{applianceId}")
    public ApplianceWithStatusDto getAppliance(
        @PathVariable String applianceId) {
        ApplianceWithStatusDto appliance = applianceService.toApplianceWithStatusDto(
            applianceService.getApplianceByApplianceId(applianceId));
        checkUserAccess(appliance.getUsername());
        return appliance;
    }

    @GetMapping("api/v1/appliance")
    public List<ApplianceWithStatusDto> getAppliances() {
        return applianceService.getAppliances().stream()
            .map(applianceService::toApplianceWithStatusDto)
            .collect(
                Collectors.toList());
    }

    @PostMapping("api/v1/appliance/alive")
    public ResponseEntity applianceAlive(
        @RequestBody ApplianceDto applianceDto) {
        String applianceId = applianceDto.getApplianceId();
        Appliance savedAppliance = applianceService.getApplianceByApplianceId(applianceId);
        checkApplianceSecret(applianceDto, savedAppliance);
        applianceService.applicationAlive(savedAppliance);
        return ResponseEntity.noContent().build();
    }

    private String getUsernameFromContext() {
        return (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    private List<String> getRolesFromContext() {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream().map(
            GrantedAuthority::getAuthority).collect(Collectors.toList());
    }

    private void checkUserAccess(String usernameFromStorage) {
        String username = getUsernameFromContext();
        if (!username.equals(usernameFromStorage) &&
            getRolesFromContext().contains(RoleName.ROLE_ADMIN.name())) {
            throw new AccessDeniedException("Access Denied");
        }
    }

    private void checkApplianceSecret(ApplianceDto applianceFromRequest,
        Appliance applianceFromStorage) {
        if (!applianceFromRequest.getApplianceSecret()
            .equals(applianceFromStorage.getApplianceSecret())) {
            throw new InvalidApplianceSecretException("Invalid appliance secret");
        }
    }
}
