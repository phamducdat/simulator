package com.wiinvent.lotusmile.app.response;

import com.wiinvent.lotusmile.app.response.fpt.FPTForgotPasswordTokenResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ForgotPasswordTokenResponse {

  @Schema(description = "Id của request Forgot Password (dùng để truyền các API sau)")
  private String requestId;

  @Schema(description = "Id của OTP dùng để xác thực(dùng để truyền các API sau)")
  private String otpId;

  @Schema(description = "Id của customer đã nhận OTP (dùng để truyền các API sau)")
  private String otpCustomerId;

  @Schema(description = "Số thẻ hội viên BSV")
  private String mainIdentifier;

  @Schema(description = "Tên hội viên")
  private String fistName;

  @Schema(description = "Đệm của họ")
  private String lastName;

//  @Schema(description = "Id của hội viên")
//  private Long customerId;

  public static ForgotPasswordTokenResponse createFrom(FPTForgotPasswordTokenResponse from) {
    return ForgotPasswordTokenResponse.builder()
        .requestId(from.getData().getRequestId())
        .otpId(from.getData().getOtpId())
        .otpCustomerId(String.valueOf(from.getData().getOtpCustomerId()))
        .mainIdentifier(from.getData().getCustomerProfiles().getFirst().getMainIdentifier())
        .lastName(from.getData().getCustomerProfiles().getFirst().getLastName())
        .fistName(from.getData().getCustomerProfiles().getFirst().getFirstName())
        .build();
  }
}
