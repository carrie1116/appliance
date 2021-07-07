package com.example.appliance.persistence.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
public class Appliance {

    @Id
    private String _id;
    @Indexed(unique = true, direction = IndexDirection.ASCENDING)
    private String applianceId;
    private String factoryNumber;
    @JsonIgnore
    private String applianceSecret;
    private String username;
    private Long lastActiveTime;


    public Appliance(Appliance a) {
        this._id = a.get_id();
        this.applianceId = a.getApplianceId();
        this.factoryNumber = a.getFactoryNumber();
        this.applianceSecret = a.getApplianceSecret();
        this.username = a.getUsername();
        this.lastActiveTime = a.getLastActiveTime();
    }

    public Appliance() {
    }
}
