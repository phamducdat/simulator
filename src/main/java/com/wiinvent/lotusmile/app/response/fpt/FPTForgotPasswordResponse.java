package com.wiinvent.lotusmile.app.response.fpt;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@ToString(callSuper = true)
public class FPTForgotPasswordResponse extends BaseFPTResponse {
  @Override
  public String getErrorMessageConverted() {
    return this.getErrorCode();
  }
}
