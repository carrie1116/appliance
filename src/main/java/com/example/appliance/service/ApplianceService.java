package com.example.appliance.service;

import com.example.appliance.exception.ApplianceAlreadyExistException;
import com.example.appliance.exception.ApplianceNotFoundException;
import com.example.appliance.persistence.dao.ApplianceRepository;
import com.example.appliance.persistence.model.Appliance;
import com.example.appliance.web.dto.ApplianceDto;
import com.example.appliance.web.dto.ApplianceWithStatusDto;
import java.util.List;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.stereotype.Service;

@Service
public class ApplianceService {

    private static final int requestIntervalInMinutes = 1;
    private static final int delayThresholdInSeconds = 5;
    private final ApplianceRepository applianceRepository;

    public ApplianceService(
        ApplianceRepository applianceRepository) {
        this.applianceRepository = applianceRepository;
    }

    public Appliance createAppliance(ApplianceDto applianceDto) {
        if (applianceRepository.existsByApplianceId(applianceDto.getApplianceId())) {
            throw new ApplianceAlreadyExistException(
                "There is an existed appliance with applianceId: " + applianceDto.getApplianceId());
        }
        Appliance appliance = new Appliance();
        appliance.setApplianceId(applianceDto.getApplianceId());
        appliance.setApplianceSecret(applianceDto.getApplianceSecret());
        appliance.setFactoryNumber(applianceDto.getFactoryNumber());
        return applianceRepository.save(appliance);
    }

    public Appliance registerAppliance(Appliance savedAppliance, String username) {
        savedAppliance.setUsername(username);
        return applianceRepository.save(savedAppliance);
    }

    public Appliance getApplianceByApplianceId(String applianceId) {
        return applianceRepository.findByApplianceId(applianceId)
            .orElseThrow(() -> new ApplianceNotFoundException(
                "There is no appliance with applianceId: " + applianceId));
    }

    public List<Appliance> getAppliances() {
        return applianceRepository.findAll();
    }

    public ApplianceWithStatusDto toApplianceWithStatusDto(Appliance appliance) {
        Long activeTime = appliance.getLastActiveTime();
        if (activeTime != null) {
            DateTime lastActiveDateTime = new DateTime(Long.valueOf(activeTime), DateTimeZone.UTC);
            DateTime now = new DateTime(DateTimeZone.UTC);
            if (lastActiveDateTime.plusMinutes(requestIntervalInMinutes).plusSeconds(
                delayThresholdInSeconds).isAfter(now)) {
                return new ApplianceWithStatusDto(appliance, true);
            }
        }
        return new ApplianceWithStatusDto(appliance, false);

    }

    public void applicationAlive(Appliance savedAppliance) {
        Long now = new DateTime(DateTimeZone.UTC).getMillis();
        savedAppliance.setLastActiveTime(now);
        applianceRepository.save(savedAppliance);
    }

}
