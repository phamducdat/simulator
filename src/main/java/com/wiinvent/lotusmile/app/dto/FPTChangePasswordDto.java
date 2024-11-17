package com.wiinvent.lotusmile.app.dto;

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

}
