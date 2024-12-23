package com.wiinvent.lotusmile.app.response.fpt;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class FPTSendMessageResponse extends BaseFPTResponse {
  @Override
  public String getErrorMessageConverted() {
    return "";
  }
}
