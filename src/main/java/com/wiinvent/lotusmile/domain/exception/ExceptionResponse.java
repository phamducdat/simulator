package com.wiinvent.lotusmile.domain.exception;

import brave.internal.Nullable;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Data
@Log4j2
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExceptionResponse {

  @Schema(defaultValue = "Định dạng không hợp lệ")
  @JsonInclude(JsonInclude.Include.NON_NULL)
  @Nullable
  private String fieldName;

  @Schema(defaultValue = "dateOfBirth")
  @NotNull
  private String message;

  public static ExceptionResponse createFrom(BaseException e) {
    ExceptionResponse response = new ExceptionResponse();
    response.setMessage(e.getMessage());
    return response;
  }

  public static ExceptionResponse createFrom(Exception e) {
    ExceptionResponse response = new ExceptionResponse();
    response.setMessage(e.getMessage());
    return response;
  }
}