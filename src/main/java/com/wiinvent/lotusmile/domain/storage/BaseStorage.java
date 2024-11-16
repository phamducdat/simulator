package com.wiinvent.lotusmile.domain.storage;

import com.wiinvent.lotusmile.domain.util.cache.CacheKey;
import com.wiinvent.lotusmile.domain.util.cache.RemoteCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public abstract class BaseStorage {
  public static final int CACHE_EXPIRED_TIME = 30 * 24 * 3600; //1 thang
  public static final int CACHE_5MIN_EXPIRED_TIME = 60 * 10; // 10 phút
  public static final int DAY_CACHE_EXPIRED_TIME = 24 * 3600; //1 day

  @Autowired
  protected RemoteCache remoteCache;
  @Autowired
  CacheKey cacheKey;
}
