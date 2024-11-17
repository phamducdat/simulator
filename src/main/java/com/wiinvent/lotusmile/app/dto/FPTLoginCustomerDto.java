package com.wiinvent.lotusmile.app.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
public class FPTLoginCustomerDto {

  @NonNull
  private String loginId;

  @NonNull
  private String password;
}
