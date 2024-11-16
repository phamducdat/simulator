package com.wiinvent.lotusmile.app.response;

import com.wiinvent.lotusmile.domain.entity.types.MerchantAccountRole;
import lombok.Data;

@Data
public class TokenMerchantResponse {
  private String accessToken = "";
  private String refreshToken = "";
  private MerchantAccountRole role;
}
