package com.wiinvent.lotusmile.domain.storage;

import com.wiinvent.lotusmile.domain.entity.MerchantInfo;
import com.wiinvent.lotusmile.domain.repository.MerchantInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MerchantInfoStorage extends BaseStorage {

  @Autowired
  MerchantInfoRepository merchantInfoRepository;

  public MerchantInfo findMerchantInfoByEmail(String email) {
    MerchantInfo merchantInfo = remoteCache.get(cacheKey.genMerchantInfoByEmail(email), MerchantInfo.class);
    if (merchantInfo == null) {
      merchantInfo = merchantInfoRepository.findByEmail(email);
      if (merchantInfo != null) {
        remoteCache.put(cacheKey.genMerchantInfoByEmail(email), merchantInfo, DAY_CACHE_EXPIRED_TIME);
      }
    }
    return merchantInfo;
  }

  public MerchantInfo findMerchantInfoByMsisdn(String msisdn) {
    MerchantInfo merchantInfo = remoteCache.get(cacheKey.genMerchantInfoByMsisdn(msisdn), MerchantInfo.class);
    if (merchantInfo == null) {
      merchantInfo = merchantInfoRepository.findByMsisdnNumber(msisdn);
      if (merchantInfo != null) {
        remoteCache.put(cacheKey.genMerchantInfoByMsisdn(msisdn), merchantInfo, DAY_CACHE_EXPIRED_TIME);
      }
    }
    return merchantInfo;
  }

  public MerchantInfo findMerchantInfoById(Integer id) {
    MerchantInfo merchantInfo = remoteCache.get(cacheKey.genMerchantInfoById(id), MerchantInfo.class);
    if (merchantInfo == null) {
      Optional<MerchantInfo> optionalMerchantInfo = merchantInfoRepository.findById(id);
      optionalMerchantInfo.ifPresent(value -> remoteCache.put(cacheKey.genMerchantInfoById(id), value, DAY_CACHE_EXPIRED_TIME));
    }
    return merchantInfo;
  }

  public MerchantInfo save(MerchantInfo merchantInfo) {
    merchantInfo = merchantInfoRepository.save(merchantInfo);
    // TODO: delete keys
    return merchantInfo;
  }
}
