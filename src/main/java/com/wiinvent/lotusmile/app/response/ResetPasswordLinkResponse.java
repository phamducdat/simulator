package com.wiinvent.lotusmile.app.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ResetPasswordLinkResponse {
  @Schema(description = "true: Thành công / false: Thất bại")
  private Boolean isSuccess;
}
