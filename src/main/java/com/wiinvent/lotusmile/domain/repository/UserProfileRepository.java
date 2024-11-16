package com.wiinvent.lotusmile.domain.repository;

import com.wiinvent.lotusmile.domain.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserProfileRepository extends JpaRepository<UserProfile, Integer> {
  @Deprecated(forRemoval = true)
  UserProfile findCustomerProfileById(Integer id);

  UserProfile findUserProfileById(Integer id);

}
