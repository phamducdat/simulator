package com.wiinvent.lotusmile.domain.pojo;

import com.wiinvent.lotusmile.domain.entity.MerchantAccount;
import com.wiinvent.lotusmile.domain.entity.types.MerchantAccountRole;
import com.wiinvent.lotusmile.domain.entity.types.MerchantAccountState;
import lombok.Data;

@Data
public class MerchantTokenInfo {
  private Integer userId;

  private String userName;

  private MerchantAccountRole role;

  private MerchantAccountState state;

  private Integer merchantId;

  public static MerchantTokenInfo createFrom(MerchantAccount account) {
    MerchantTokenInfo merchantTokenInfo = new MerchantTokenInfo();
    merchantTokenInfo.setUserId(account.getId());
    merchantTokenInfo.setUserName(account.getUsername());
    merchantTokenInfo.setRole(account.getRole());
    merchantTokenInfo.setState(account.getState());
    merchantTokenInfo.setMerchantId(account.getMerchantId());
    return merchantTokenInfo;
  }
}
