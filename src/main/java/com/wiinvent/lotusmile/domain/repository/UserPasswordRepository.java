package com.wiinvent.lotusmile.domain.repository;

import com.wiinvent.lotusmile.domain.entity.UserPassword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@Deprecated(forRemoval = true)
public interface UserPasswordRepository extends JpaRepository<UserPassword, Integer> {

  UserPassword findUserPasswordByUserIdOrEmailOrMainIdentifierOrPhoneNumber(Integer userId, String email, String mainIdentifier, String phoneNumber);

  UserPassword findUserPasswordByUserId(Integer userId);

  UserPassword findUserPasswordByCustomerId(String customerId);

}
