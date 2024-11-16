package com.wiinvent.lotusmile.app.response.fpt;

import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@ToString(callSuper = true)
public class SendForgotPasswordLinkResponse extends BaseFPTResponse {
  private Object data;

  @Override
  public String getErrorMessageConverted() {
    return this.getErrorCode();
  }
}
