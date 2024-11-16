package com.wiinvent.lotusmile.app.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public class ChangePasswordMerchantResponse {
  @Schema(example = "true")
  public Boolean isSuccess;
}
