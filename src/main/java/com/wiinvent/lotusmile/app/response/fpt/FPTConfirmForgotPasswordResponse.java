package com.wiinvent.lotusmile.app.response.fpt;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@ToString(callSuper = true)
public class FPTConfirmForgotPasswordResponse extends BaseFPTResponse {
  private Object data;

  @Override
  public String getErrorMessageConverted() {
    return this.getErrorCode();
  }

}
