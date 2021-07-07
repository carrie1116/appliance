package com.example.appliance.persistence.dao;

import com.example.appliance.persistence.model.Appliance;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ApplianceRepository extends MongoRepository<Appliance, String> {

    Optional<Appliance> findByApplianceId(String applianceId);

    boolean existsByApplianceId(String applianceId);
}
