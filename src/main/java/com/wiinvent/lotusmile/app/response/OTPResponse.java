package com.wiinvent.lotusmile.app.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OTPResponse {
  @Schema(description = "OTPId của request")
  private String data;
}
