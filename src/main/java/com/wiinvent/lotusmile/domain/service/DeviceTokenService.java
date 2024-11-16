package com.wiinvent.lotusmile.domain.service;

import com.wiinvent.lotusmile.app.response.UpdateDeviceTokenResponse;
import com.wiinvent.lotusmile.domain.entity.DeviceToken;
import com.wiinvent.lotusmile.domain.entity.types.AppType;
import com.wiinvent.lotusmile.domain.exception.BadRequestException;
import com.wiinvent.lotusmile.domain.storage.DeviceTokenStorage;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@Log4j2
public class DeviceTokenService extends BaseService {

  @Autowired
  DeviceTokenStorage deviceTokenStorage;

  @Transactional(isolation = Isolation.SERIALIZABLE, propagation = Propagation.REQUIRES_NEW)
  public void processSaveDeviceToken(Integer userId, String dvToken, AppType appType) {
    log.debug("======>processSaveDeviceToken dvToken:{} ",dvToken);
    DeviceToken deviceToken = deviceTokenStorage.findByDeviceTokenAndAppType(dvToken, appType);

    if (!Objects.isNull(deviceToken)) {
      deviceTokenStorage.delete(deviceToken);
    }

    DeviceToken deviceTokenNew = new DeviceToken();
    deviceTokenNew.setUserId(userId);
    deviceTokenNew.setDeviceToken(dvToken);
    deviceTokenNew.setAppType(appType);

    deviceTokenStorage.save(deviceTokenNew);
  }

  @Transactional(isolation = Isolation.SERIALIZABLE, propagation = Propagation.REQUIRED)
  public UpdateDeviceTokenResponse processUpdateDeviceToken(Integer userId, UpdateDeviceTokenDto updateDeviceTokenDto, AppType appType) {
    DeviceToken deviceToken = deviceTokenStorage.findByUserIdAndDeviceTokenAndAppType(userId, updateDeviceTokenDto.getDeviceTokenExpired(), appType);
    if (Objects.isNull(deviceToken)) {
      throw new BadRequestException("Fcm token not found");
    }
    deviceToken.setDeviceToken(updateDeviceTokenDto.getNewDeviceToken());
    deviceTokenStorage.save(deviceToken);
    return UpdateDeviceTokenResponse.builder().isSuccess(true).build();
  }
}
