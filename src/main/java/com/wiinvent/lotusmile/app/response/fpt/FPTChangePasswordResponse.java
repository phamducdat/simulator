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
public class FPTChangePasswordResponse extends BaseFPTResponse {
  @Override
  public String getErrorMessageConverted() {
    return switch (this.getErrorCode()) {
      case ErrorCode.FPT.INVALID_OTP_VALUE -> "Mã OTP không hợp lệ";
      default -> "Đã có lỗi xảy ra";
    };
  }
}
