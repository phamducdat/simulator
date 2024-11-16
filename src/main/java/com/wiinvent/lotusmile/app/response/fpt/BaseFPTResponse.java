package com.wiinvent.lotusmile.app.response.fpt;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
public abstract class BaseFPTResponse {

  private String errorCode;

  @Getter(AccessLevel.NONE)
  private String errorMessage;

  private String attribute;

  // TODO: not done
  public abstract String getErrorMessageConverted();

}
