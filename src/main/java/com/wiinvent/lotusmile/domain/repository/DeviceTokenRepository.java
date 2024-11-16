package com.wiinvent.lotusmile.domain.repository;

import com.wiinvent.lotusmile.domain.entity.DeviceToken;
import com.wiinvent.lotusmile.domain.entity.types.AppType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeviceTokenRepository extends JpaRepository<DeviceToken, Integer> {

  DeviceToken findByUserIdAndDeviceTokenAndAppType(Integer userId, String deviceToken, AppType appType);

  void deleteByDeviceTokenAndAppType(String deviceToken, AppType appType);

  DeviceToken findByDeviceTokenAndAppType(String deviceToken, AppType appType);



}