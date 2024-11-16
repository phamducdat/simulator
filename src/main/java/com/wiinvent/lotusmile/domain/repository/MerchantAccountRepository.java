package com.wiinvent.lotusmile.domain.repository;

import com.wiinvent.lotusmile.domain.entity.MerchantAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MerchantAccountRepository extends JpaRepository<MerchantAccount, Integer> {

  MerchantAccount findByMsisdn(String msisdn);

  MerchantAccount findByEmail(String email);

  MerchantAccount findByUsername(String username);

  MerchantAccount findMerchantAccountById(Integer id);
}
