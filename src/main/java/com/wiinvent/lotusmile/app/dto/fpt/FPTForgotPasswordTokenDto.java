package com.wiinvent.lotusmile.app.dto.fpt;

import com.wiinvent.lotusmile.app.dto.ForgotPasswordTokenDto;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FPTForgotPasswordTokenDto {

  private String identifierNo;

  private String email;

  private String phone;

  public static FPTForgotPasswordTokenDto createFrom(ForgotPasswordTokenDto forgotPasswordTokenDto) {
    FPTForgotPasswordTokenDto fptForgotPasswordTokenDto = new FPTForgotPasswordTokenDto();
    switch (forgotPasswordTokenDto.getType()) {
      case PHONE_NUMBER -> fptForgotPasswordTokenDto.setPhone(forgotPasswordTokenDto.getUserIdentity());
      case MAIN_IDENTIFIER -> fptForgotPasswordTokenDto.setIdentifierNo(forgotPasswordTokenDto.getUserIdentity());
      case EMAIL -> fptForgotPasswordTokenDto.setEmail(forgotPasswordTokenDto.getUserIdentity());
    }
    return fptForgotPasswordTokenDto;
  }
}
