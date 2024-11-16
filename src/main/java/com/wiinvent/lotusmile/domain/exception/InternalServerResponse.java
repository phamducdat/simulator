package com.wiinvent.lotusmile.domain.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InternalServerResponse {
  @Schema(defaultValue = "Internal Server")
  @NotNull
  private String message;

}
