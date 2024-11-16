package com.wiinvent.lotusmile.domain.service;

import com.wiinvent.lotusmile.app.response.MerchantAccountResponse;
import com.wiinvent.lotusmile.domain.entity.MerchantAccount;
import com.wiinvent.lotusmile.domain.storage.MerchantAccountStorage;
import com.wiinvent.lotusmile.domain.storage.MerchantInfoStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
public class MerchantService extends BaseService {

  @Autowired
  MerchantInfoStorage merchantInfoStorage;

  @Autowired
  MerchantAccountStorage merchantAccountStorage;

  @Autowired
  @Lazy
  MerchantService self;

  public MerchantAccountResponse getMerchantInfoById(String accountId) {
    MerchantAccount merchantAccount = merchantAccountStorage.findMerchantAccountById(Integer.parseInt(accountId));
    return modelMapper.toMerchantResponse(merchantAccount);
  }
}
