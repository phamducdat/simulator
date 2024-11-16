package com.wiinvent.lotusmile.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class InternalServerException extends BaseException {
  public static final String FROM_FPT = "From FPT API";
  @Serial
  private static final long serialVersionUID = 1L;

  public InternalServerException(String message) {
    super(message);
  }
}
