package com.wiinvent.lotusmile.app.response.fpt;

import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@Getter
@ToString(callSuper = true)
public class FPTResendForgotPassOtpResponse extends BaseFPTResponse {
  private String data;

  @Override
  public String getErrorMessageConverted() {
    return this.getErrorCode();
  }
}
