package com.wiinvent.lotusmile.domain.util.cache;

import com.sun.nio.sctp.IllegalReceiveException;
import com.wiinvent.lotusmile.domain.util.JsonParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class RemoteCache {

  public static final int CACHE_DURATION_DEFAULT = 3600; // 1 tieng
  public static final int CACHE_6H_DURATION = 3600 * 6; // 6 tieng
  public static final int CACHE_5MIN_DURATION = 60 * 5; // 5 min
  public static final int CACHE_1MIN_DURATION = 60; // 1 min
  public static final int CACHE_1W_DURATION = 3600 * 24 * 7; // 1 week
  public static final int CACHE_3M_DURATION = 3600 * 24 * 90; // 3 month
  public static final int CACHE_1DAY_DURATION = 3600 * 24; // 1 day


  @Autowired
  private RedisTemplate<String, String> redisTemplate;

  public void put(String key, String value, int expireTime) {
    redisTemplate.opsForValue().set(key, value, expireTime, TimeUnit.SECONDS);
  }

  public void put(String key, String value, long expireTime) {
    redisTemplate.opsForValue().set(key, value, expireTime, TimeUnit.MILLISECONDS);
  }

  public void put(String key, Object object, long expireTime) {
    //log.debug("=======save cache " + key + ": ", JsonParser.toJson(object));
    try {
      put(key, JsonParser.toJson(object), expireTime);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void put(String key, Object object, int expireTime) {
    //log.debug("=======save cache " + key + ": ", JsonParser.toJson(object));
    try {
      put(key, JsonParser.toJson(object), expireTime);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void put(String key, Object object) {
    //log.debug("=======save cache " + key + ": ", object);
    try {
      put(key, JsonParser.toJson(object), CACHE_DURATION_DEFAULT);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void put(String key, String value) {
    redisTemplate.opsForValue().set(key, value);
  }

  public String get(String key) {
    return redisTemplate.opsForValue().get(key);
  }

  public <T> ArrayList<T> getList(String key, Class<T> tClass) {
    try {
      String value = redisTemplate.opsForValue().get(key);
      return JsonParser.arrayList(value, tClass);
    } catch (Exception e) {
      return null;
    }
  }

  public <T> Page<T> getPage(String key, Class<T> tClass) {
    try {
      String value = redisTemplate.opsForValue().get(key);
      return JsonParser.toPage(value, tClass);
    } catch (Exception e) {
      return null;
    }
  }

  public Long getExpireTime(String key) {
    Long ttl = redisTemplate.getExpire(key, TimeUnit.SECONDS);
    if (ttl == 2L) throw new IllegalReceiveException("Key not found");
    return ttl;
  }

  public <T> T get(String key, Class<T> tClass) {
    try {
      String value = redisTemplate.opsForValue().get(key);
      return JsonParser.entity(value, tClass);
    } catch (Exception e) {
      return null;
    }
  }

  //  public Object getToken(String accessToken) {
  //    try {
  //      String sessionKey = CacheKey.genThirdPartySessionKey(accessToken, Partner.VIETTEL);
  //      return JsonParser.entity(get(sessionKey), Object.class);
  //    } catch (Exception e) {
  //      log.error(e.getMessage());
  //      return null;
  //    }
  //  }

  public Boolean exists(String key) {
    return redisTemplate.hasKey(key);
  }

  public void del(String key) {
    redisTemplate.delete(key);
  }

  public void del(List<String> keys) {
    redisTemplate.delete(keys);
  }

  public Set<String> keys(String pattern) {
    return redisTemplate.keys(pattern);
  }

  //  public void saveConfig(String key, String value) {
  //    String configKey = CacheKey.getConfigKey(key);
  //    put(configKey, value, Integer.MAX_VALUE);
  //  }

  public Set<ZSetOperations.TypedTuple<String>> reverseRangeWithScoreCursor(String key, Integer to, long topUser) {
    return redisTemplate.boundZSetOps(key).reverseRangeWithScores(to, to + topUser - 1);
  }

  // set cache
  public boolean zAdd(String key, String value, Long score) {
    return Boolean.TRUE.equals(redisTemplate.opsForZSet().add(key, value, score));
  }

  public boolean zAdds(String key, Set<ZSetOperations.TypedTuple<String>> typedTuples) {
    return Boolean.TRUE.equals(redisTemplate.opsForZSet().add(key, typedTuples));
  }

  public void zdel(String key, String value) {
    redisTemplate.opsForZSet().remove(key, value);
  }

  //  @PostConstruct
  //  public void init() {
  //    zAdd("test", "test01", 1L);
  //    zAdd("test", "test02", 2L);
  //    zAdd("test", "test03", 3L);
  //    zAdd("test", "test04", 4L);
  //
  //    log.info("============= " + reverseRangeWithScores("test", 5L));
  //    zdel("test", "test03");
  //    log.info("============= " + reverseRangeWithScores("test", 5L));
  //  }
  //  public boolean zAdd(String key, String value, Double score) {
  //    return Boolean.TRUE.equals(redisTemplate.opsForZSet().add(key, value, score));
  //  }

  // get top
  public Set<ZSetOperations.TypedTuple<String>> reverseRangeWithScores(String key, Long topUser) {

    return redisTemplate.boundZSetOps(key).reverseRangeWithScores(0, topUser);
  }

  public Set<ZSetOperations.TypedTuple<String>> rangeWithScores(String key, Long from, Long topUser) {

    return redisTemplate.boundZSetOps(key).rangeWithScores(from, topUser);
  }

  public Set<String> rangeByScore(String key, Double topUser) {

    return redisTemplate.boundZSetOps(key).rangeByScore(topUser, Double.MAX_VALUE);
  }


  public Long zSize(String key) {
    return redisTemplate.boundZSetOps(key).size();
  }

  // getRank
  public Long zRank(String key, String value) {
    return redisTemplate.boundZSetOps(key).reverseRank(value);
  }

  public Double zUpdatePoint(String key, String value, double score) {
    return redisTemplate.opsForZSet().incrementScore(key, value, score);
  }

  public Double score(String key, String value) {
    return redisTemplate.opsForZSet().score(key, value);
  }

  /**
   * for Millisecond
   *
   * @param key
   * @param value
   * @param expireTime
   */
  public void setInMs(String key, String value, long expireTime) {
    redisTemplate.opsForValue().set(key, value, expireTime, TimeUnit.MILLISECONDS);
  }

  public Set<ZSetOperations.TypedTuple<String>> reverseRangeWithScoresCursor(String key, Integer to, long topUser) {

    return redisTemplate.boundZSetOps(key).reverseRangeWithScores(to, topUser);
  }
}
