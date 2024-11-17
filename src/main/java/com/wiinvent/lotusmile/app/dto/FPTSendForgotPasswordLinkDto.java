package com.wiinvent.lotusmile.app.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public class FPTSendForgotPasswordLinkDto {
  @NonNull
  private String mainIdentifier;

  @NonNull
  private String otpId;

  @NonNull
  private String requestId;

  @NonNull
  private Long customerId;
}
