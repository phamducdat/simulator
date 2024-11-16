package com.wiinvent.lotusmile.domain.storage;

import com.wiinvent.lotusmile.domain.entity.MerchantAccount;
import com.wiinvent.lotusmile.domain.repository.MerchantAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MerchantAccountStorage extends BaseStorage {

  @Autowired
  MerchantAccountRepository merchantAccountRepository;

  public MerchantAccount findMerchantAccountById(Integer id) {
    MerchantAccount merchantAccount = remoteCache.get(cacheKey.genMerchantById(id), MerchantAccount.class);
    if (merchantAccount == null) {
      merchantAccount = merchantAccountRepository.findMerchantAccountById(id);
      if (merchantAccount != null) {
        remoteCache.put(cacheKey.genMerchantById(id), merchantAccount, DAY_CACHE_EXPIRED_TIME);
      }
    }
    return merchantAccount;
  }

  public MerchantAccount findMerchantByUsername(String username) {
    MerchantAccount merchantAccount = remoteCache.get(cacheKey.genMerchantByUsername(username), MerchantAccount.class);
    if (merchantAccount == null) {
      merchantAccount = merchantAccountRepository.findByUsername(username);
      if (merchantAccount != null) {
        remoteCache.put(cacheKey.genMerchantByUsername(username), merchantAccount, DAY_CACHE_EXPIRED_TIME);
      }
    }
    return merchantAccount;
  }

  @Deprecated
  public MerchantAccount findMerchantAccountByMsisdn(String msisdn) {
    MerchantAccount merchantAccount = remoteCache.get(cacheKey.genMerchantByMsisdnKey(msisdn), MerchantAccount.class);
    if (merchantAccount == null) {
      merchantAccount = merchantAccountRepository.findByMsisdn(msisdn);
      if (merchantAccount != null) {
        remoteCache.put(cacheKey.genMerchantByMsisdnKey(msisdn), merchantAccount, DAY_CACHE_EXPIRED_TIME);
      }
    }
    return merchantAccount;
  }

  @Deprecated
  public MerchantAccount findMerchantAccountByEmail(String email) {
    MerchantAccount merchantAccount = remoteCache.get(cacheKey.genMerchantByEmail(email), MerchantAccount.class);
    if (merchantAccount == null) {
      merchantAccount = merchantAccountRepository.findByEmail(email);
      if (merchantAccount != null) {
        remoteCache.put(cacheKey.genMerchantByEmail(email), merchantAccount, DAY_CACHE_EXPIRED_TIME);
      }
    }
    return merchantAccount;
  }

  public MerchantAccount save(MerchantAccount merchantAccount) {
    merchantAccount = merchantAccountRepository.save(merchantAccount);
    // TODO: delete keys
    return merchantAccount;
  }

}
