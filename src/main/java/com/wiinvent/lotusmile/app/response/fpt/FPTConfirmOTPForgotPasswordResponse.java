package com.wiinvent.lotusmile.app.response.fpt;

import com.wiinvent.lotusmile.domain.exception.ErrorCode;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@ToString(callSuper = true)
public class FPTConfirmOTPForgotPasswordResponse extends BaseFPTResponse {
  private Object data;

  @Override
  public String getErrorMessageConverted() {
    switch (this.getErrorCode()) {
      case ErrorCode.FPT.BAD_REQUEST_CODE -> {
        return "Thiếu trường trong request body";
      }
      case ErrorCode.FPT.OTHER_OTHER -> {
        return "Lỗi hệ thống";
      }
      case ErrorCode.FPT.RESOURCE_NOT_FOUND_CODE -> {
        return "Thông tin OTP không chính xác";
      }
      case "OTP_INVALID_OTP" -> {
        return "Mã xác thực chưa chính xác";
      }
      case "OTP_EXCESS_INPUT" -> {
        return "Bạn đã nhập quá số lần quy định!";
      }
      default -> {
        return this.getErrorCode();
      }
    }
  }
}
