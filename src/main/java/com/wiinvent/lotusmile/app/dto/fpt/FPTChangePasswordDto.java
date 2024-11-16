package com.wiinvent.lotusmile.app.dto.fpt;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public class FPTChangePasswordDto {

  @NonNull
  private String oldPassword;

  @NonNull
  private String newPassword;


  public static FPTChangePasswordDto createFrom(ChangePasswordDto changePasswordDto) {
    return FPTChangePasswordDto.builder()
        .oldPassword(changePasswordDto.getOldPassword())
        .newPassword(changePasswordDto.getNewPassword())
        .build();
  }
}
