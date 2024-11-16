package com.wiinvent.lotusmile.app.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SendMessageResponse {
  @Schema(example = "true", description = "Gửi thành công/ thất bại")
  private boolean isSuccess;
}
