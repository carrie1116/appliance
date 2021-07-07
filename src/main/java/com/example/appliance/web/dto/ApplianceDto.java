package com.example.appliance.web.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
public class ApplianceDto {

    @NonNull
    private String applianceId;
    @NonNull
    private String applianceSecret;
    private String factoryNumber;
}
