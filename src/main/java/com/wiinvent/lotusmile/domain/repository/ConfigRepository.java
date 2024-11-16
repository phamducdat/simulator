package com.wiinvent.lotusmile.domain.repository;

import com.wiinvent.lotusmile.domain.entity.Config;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConfigRepository extends JpaRepository<Config, Integer> {
  Config findConfigByKeyConfig(String key);
}
