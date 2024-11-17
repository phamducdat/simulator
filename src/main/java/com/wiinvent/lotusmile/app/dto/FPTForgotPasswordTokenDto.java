package com.wiinvent.lotusmile.app.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FPTForgotPasswordTokenDto {

  private String identifierNo;

  private String email;

  private String phone;
}
