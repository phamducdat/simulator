package com.wiinvent.lotusmile.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class AuthenticationException extends BaseException {
  @Serial
  private static final long serialVersionUID = 1L;

  public AuthenticationException(String message) {
    super(message);
  }
}
