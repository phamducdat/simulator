package com.wiinvent.lotusmile.domain.storage;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.wiinvent.lotusmile.domain.entity.Config;
import com.wiinvent.lotusmile.domain.exception.ResourceNotFoundException;
import com.wiinvent.lotusmile.domain.repository.ConfigRepository;
import com.wiinvent.lotusmile.domain.util.Constants;
import com.wiinvent.lotusmile.domain.util.JsonParser;
import jakarta.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@Log4j2
public class ConfigStorage extends BaseStorage {

  protected LoadingCache<String, Config> localCache;
  @Autowired
  ConfigRepository configRepository;

  @PostConstruct
  public void init() {
    localCache = Caffeine.newBuilder()
        .maximumSize(100)
        .expireAfterWrite(2, TimeUnit.HOURS)
        .refreshAfterWrite(1, TimeUnit.MINUTES)
        .build(this::findConfigByKeyRemote);
  }

  public List<Config.LanguageCode> getListPreferredLanguage() {
    try {
      Config config = findConfigByKey(Constants.LANGUAGE_COUNTRY_CODE_CONFIG);
      return JsonParser.arrayList(config.getValueConfig(), Config.LanguageCode.class);
    } catch (Exception e) {
      log.error("==============>getListPreferredLanguage exception = ", e);
      return Collections.emptyList();
    }
  }

  public List<Config.RankingLevel> getRankingLevels() {
    Config config = findConfigByKey(Constants.RANKING_LEVEL);
    return JsonParser.arrayList(config.getValueConfig(), Config.RankingLevel.class);
  }

  public Config findConfigByKey(String key) {
    Config config = localCache.getIfPresent(key);
    if (config == null) {
      config = findConfigByKeyRemote(key);
      if (config != null) {
        localCache.put(key, config);
      }
    }
    return config;
  }

  public Config findConfigByKeyRemote(String key) {
    Config config = remoteCache.get(cacheKey.genConfigKey(key), Config.class);
    if (config == null) {
      config = configRepository.findConfigByKeyConfig(key);
      if (config != null) {
        remoteCache.put(cacheKey.genConfigKey(key), config, CACHE_EXPIRED_TIME);
      } else
        throw new ResourceNotFoundException("Config not found key: " + key);
    }
    return config;
  }

  public int getLoginFailedAttempts() {
    return Integer.parseInt(findConfigByKey(Constants.LOGIN_FAILED_ATTEMPT).getValueConfig());
  }

  public int getAccountLockoutDuration() {
    return Integer.parseInt(findConfigByKey(Constants.ACCOUNT_LOCKOUT_DURATION_LOGIN).getValueConfig());
  }
}
