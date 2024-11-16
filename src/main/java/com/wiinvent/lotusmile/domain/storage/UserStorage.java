package com.wiinvent.lotusmile.domain.storage;

import com.wiinvent.lotusmile.domain.entity.User;
import com.wiinvent.lotusmile.domain.repository.UserRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class UserStorage extends BaseStorage {

  @Autowired
  UserRepository userRepository;


  @Deprecated(forRemoval = true)
  public User findByCustomerId(String customerId) {
    User user = remoteCache.get(cacheKey.genUserByCustomerId(customerId), User.class);
    if (user == null) {
      user = userRepository.findByCustomerId(customerId);
      if (user != null) {
        remoteCache.put(cacheKey.genUserByCustomerId(customerId), user);
      }
    }
    return user;
  }

  public User findUserByMainIdentity(String mainIdentity) {
    User user = remoteCache.get(cacheKey.genUserByMainIdentifier(mainIdentity), User.class);
    if (user == null) {
      user = userRepository.findByMainIdentifier(mainIdentity);
      if (user != null) {
        remoteCache.put(cacheKey.genUserByMainIdentifier(mainIdentity), user, DAY_CACHE_EXPIRED_TIME);
      }
    }
    return user;
  }

  public User findUserByCustomerId(String customerId) {
    User user = remoteCache.get(cacheKey.genUserByCustomerId(customerId), User.class);
    if (user == null) {
      user = userRepository.findByCustomerId(customerId);
      if (user != null) {
        remoteCache.put(cacheKey.genUserByCustomerId(customerId), user, DAY_CACHE_EXPIRED_TIME);
      }
    }
    return user;
  }

  public User findUserByUserId(Integer userId) {
    User user = remoteCache.get(cacheKey.genUserByUserId(userId), User.class);
    if (user == null) {
      user = userRepository.findUserById(userId);
      if (user != null) {
        remoteCache.put(cacheKey.genUserByUserId(userId), user, DAY_CACHE_EXPIRED_TIME);
      }
    }
    return user;
  }

  public User findUserByUserIdNoCache(Integer userId) {
    return userRepository.findUserById(userId);
  }

  public User save(User user) {
    user = userRepository.save(user);
    remoteCache.del(cacheKey.genUserByMainIdentifier(user.getMainIdentifier()));
    remoteCache.del(cacheKey.genUserByUserId(user.getId()));
    remoteCache.del(cacheKey.genUserProfileById(user.getId()));
    remoteCache.del(cacheKey.genUserByCustomerId(user.getCustomerId()));
    return user;
  }

  public User saveAndFlush(User user) {
    user = userRepository.saveAndFlush(user);
    remoteCache.del(cacheKey.genUserByMainIdentifier(user.getMainIdentifier()));
    remoteCache.del(cacheKey.genUserByUserId(user.getId()));
    remoteCache.del(cacheKey.genUserProfileById(user.getId()));
    remoteCache.del(cacheKey.genUserByCustomerId(user.getCustomerId()));
    return user;
  }
}
