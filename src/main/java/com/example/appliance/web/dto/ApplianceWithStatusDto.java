package com.example.appliance.web.dto;

import com.example.appliance.persistence.model.Appliance;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ApplianceWithStatusDto extends Appliance {

    private boolean connected;

    public ApplianceWithStatusDto(Appliance appliance, boolean connected) {
        super(appliance);
        this.connected = connected;
    }
}
