package com.wiinvent.lotusmile.app.dto.fpt;

import com.wiinvent.lotusmile.app.dto.ResetPasswordLinkDto;
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

  public static FPTSendForgotPasswordLinkDto createFrom(ResetPasswordLinkDto resetPasswordLinkDto) {
    return FPTSendForgotPasswordLinkDto.builder()
        .mainIdentifier(resetPasswordLinkDto.getMainIdentifier())
        .customerId(Long.valueOf(resetPasswordLinkDto.getCustomerId()))
        .otpId(resetPasswordLinkDto.getOtpId())
        .requestId(resetPasswordLinkDto.getRequestId())
        .build();
  }
}
