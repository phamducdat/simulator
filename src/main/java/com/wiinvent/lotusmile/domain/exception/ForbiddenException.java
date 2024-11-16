package com.wiinvent.lotusmile.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class ForbiddenException extends BaseException {
  @Serial
  private static final long serialVersionUID = 1L;

  public ForbiddenException(ErrorMessage exception) {
    super(exception.getMessage());
  }
}
