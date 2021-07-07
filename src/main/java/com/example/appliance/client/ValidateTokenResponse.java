package com.example.appliance.client;

import java.util.List;
import lombok.Data;

@Data
public class ValidateTokenResponse {

    private boolean isValid;
    private String username;
    private List<String> roles;
}
