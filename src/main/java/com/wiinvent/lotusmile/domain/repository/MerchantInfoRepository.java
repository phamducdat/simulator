package com.wiinvent.lotusmile.domain.repository;

import com.wiinvent.lotusmile.domain.entity.MerchantInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MerchantInfoRepository extends JpaRepository<MerchantInfo, Integer> {

  MerchantInfo findByMsisdnNumber(String msisdn);

  MerchantInfo findByEmail(String email);
}
