package com.wiinvent.lotusmile.domain.repository;

import com.wiinvent.lotusmile.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Integer> {

  @Transactional(readOnly = true)
  User findUserById(Integer id);

  User findUserByEmail(String email);

  User findByCustomerId(String customerId);

  User findByMainIdentifier(String mainIdentifier);


  @Deprecated(forRemoval = true)
  @Transactional(readOnly = true)
  User findByIdentityCardNumberOrEmailOrPhoneNumber(String identityCardNumber, String email, String phoneNumber);

  @Deprecated(forRemoval = true)
  @Query("select u from User u " +
      "where u.mainIdentifier = :mainIdentifier " +
      "or u.email = :email " +
      "or u.phoneNumber like concat('%', :strippedPhoneNumber) ")
  User findByMainIdentifierOrPhoneNumberOrEmail(
      @Param("mainIdentifier") String mainIdentifier,
      @Param("strippedPhoneNumber") String strippedPhoneNumber,
      @Param("email") String email
  );

  @Deprecated(forRemoval = true)
  Boolean existsByMainIdentifier(String mainIdentifier);

  @Deprecated(forRemoval = true)
  User findByCustomerIdAndMainIdentifier(String customerId, String mainIdentifier);

  @Deprecated(forRemoval = true)
  User findByIdentityCardNumber(String identityCardNumber);

  User findByPhoneNumber(String phoneNumber);

  @Query(value = "select u.mainIdentifier from User u")
  List<String> getMainIdentifiers();
}
