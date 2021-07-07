package com.example.appliance.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.example.appliance.exception.ApplianceAlreadyExistException;
import com.example.appliance.exception.ApplianceNotFoundException;
import com.example.appliance.persistence.dao.ApplianceRepository;
import com.example.appliance.persistence.model.Appliance;
import com.example.appliance.web.dto.ApplianceDto;
import com.example.appliance.web.dto.ApplianceWithStatusDto;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@DataMongoTest
@ExtendWith(SpringExtension.class)
public class ApplianceServiceTest {
  private ApplianceService applianceService;
  @Autowired private ApplianceRepository applianceRepository;
  private final String applianceId1 = "app1";
  private final String applianceSecret1 = "secret1";
  private final String factoryNumber1 = "factory1";
  private final String username1 = "name1";
  private ApplianceDto applianceDto1;
  private Appliance appliance1;

  @BeforeEach
  public void setUp() {
    applianceRepository.deleteAll();
    applianceService = new ApplianceService(applianceRepository);
    applianceDto1 = new ApplianceDto();
    applianceDto1.setApplianceId(applianceId1);
    applianceDto1.setApplianceSecret(applianceSecret1);
    applianceDto1.setFactoryNumber(factoryNumber1);
    appliance1 = new Appliance();
    appliance1.setApplianceId(applianceId1);
    appliance1.setApplianceSecret(applianceSecret1);
    appliance1.setFactoryNumber(factoryNumber1);
  }

  @Test
  public void testCreateAppliance(){
    Appliance appliance = applianceService.createAppliance(applianceDto1);
    assertEquals(applianceId1, appliance.getApplianceId());
    assertEquals(applianceSecret1, appliance.getApplianceSecret());
    assertEquals(factoryNumber1, appliance.getFactoryNumber());
  }

  @Test
  public void testCreateApplianceThrowException(){
    applianceService.createAppliance(applianceDto1);
    ApplianceAlreadyExistException exception = assertThrows(ApplianceAlreadyExistException.class,
        () -> applianceService.createAppliance(applianceDto1));
    String expectedExceptionMsg = "There is an existed appliance with applianceId: " + applianceId1;
    assertEquals(expectedExceptionMsg, exception.getMessage());
  }

  @Test
  public void testRegisterAppliance(){
    Appliance appliance = applianceService.registerAppliance(appliance1, username1);
    assertEquals(username1, appliance.getUsername());
  }

  @Test
  public void testGetApplianceByApplianceId(){
    applianceService.createAppliance(applianceDto1);
    Appliance appliance = applianceService.getApplianceByApplianceId(applianceId1);
    assertEquals(applianceId1, appliance.getApplianceId());
    assertEquals(applianceSecret1, appliance.getApplianceSecret());
    assertEquals(factoryNumber1, appliance.getFactoryNumber());
  }

  @Test
  public void testGetApplianceByApplianceIdThrowException(){
    ApplianceNotFoundException exception = assertThrows(ApplianceNotFoundException.class,
        () ->applianceService.getApplianceByApplianceId(applianceId1));
    String expectedExceptionMsg = "There is no appliance with applianceId: " + applianceId1;
    assertEquals(expectedExceptionMsg, exception.getMessage());
  }

  @Test
  public void testGetAppliances(){
    String applianceId2 = "app2";
    String applianceSecret2 = "secret2";
    String factoryNumber2 = "factory2";
    ApplianceDto applianceDto2 = new ApplianceDto();
    applianceDto2.setApplianceId(applianceId2);
    applianceDto2.setApplianceSecret(applianceSecret2);
    applianceDto2.setFactoryNumber(factoryNumber2);
    applianceService.createAppliance(applianceDto1);
    applianceService.createAppliance(applianceDto2);
    List<Appliance> applianceList = applianceService.getAppliances();
    assertEquals(2, applianceList.size());
  }

  @Test
  public void testApplicationAlive(){
    applianceService.applicationAlive(appliance1);
    Appliance appliance = applianceService.getApplianceByApplianceId(applianceId1);
    assertNotNull(appliance.getLastActiveTime());
  }

  @Test
  public void testToApplianceWithStatusDto(){
    applianceService.applicationAlive(appliance1);
    ApplianceWithStatusDto applianceWithStatusDto = applianceService.toApplianceWithStatusDto(appliance1);
    assertEquals(true, applianceWithStatusDto.isConnected());
  }
}
