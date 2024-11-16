package com.wiinvent.lotusmile.domain.storage;

import com.wiinvent.lotusmile.domain.entity.DeviceToken;
import com.wiinvent.lotusmile.domain.entity.types.AppType;
import com.wiinvent.lotusmile.domain.repository.DeviceTokenRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Log4j2
public class DeviceTokenStorage extends BaseStorage {

  @Autowired
  private DeviceTokenRepository deviceTokenRepository;

  public DeviceToken findByUserIdAndDeviceTokenAndAppType(Integer userId, String deviceToken, AppType appType) {
    return deviceTokenRepository.findByUserIdAndDeviceTokenAndAppType(userId, deviceToken, appType);
  }

  public void delete(DeviceToken deviceToken) {
    // TODO: cache
    deviceTokenRepository.delete(deviceToken);
  }

  public void deleteByDeviceToken(String deviceToken, AppType appType) {
    deviceTokenRepository.deleteByDeviceTokenAndAppType(deviceToken, appType);
  }

  public void save(DeviceToken deviceToken) {
    deviceTokenRepository.save(deviceToken);
  }

  public DeviceToken findByDeviceTokenAndAppType(String deviceToken, AppType appType) {
    return deviceTokenRepository.findByDeviceTokenAndAppType(deviceToken, appType);
  }
}
