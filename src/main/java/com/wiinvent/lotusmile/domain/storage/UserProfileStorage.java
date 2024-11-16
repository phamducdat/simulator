package com.wiinvent.lotusmile.domain.storage;

import com.wiinvent.lotusmile.domain.entity.UserProfile;
import com.wiinvent.lotusmile.domain.repository.UserProfileRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class UserProfileStorage extends BaseStorage {

  @Autowired
  UserProfileRepository userProfileRepository;

  public UserProfile findUserProfileById(Integer userId) {
    UserProfile userProfile = remoteCache.get(cacheKey.genUserProfileById(userId), UserProfile.class);
    if (userProfile == null) {
      userProfile = userProfileRepository.findUserProfileById(userId);
      if (userProfile != null) {
        remoteCache.put(cacheKey.genUserProfileById(userId), userProfile);
      }
    }
    return userProfile;
  }

  public UserProfile save(UserProfile userProfile) {
    userProfile = userProfileRepository.save(userProfile);
    remoteCache.del(cacheKey.genUserProfileById(userProfile.getId()));
    return userProfile;
  }

}
