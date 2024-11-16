package com.wiinvent.lotusmile.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

@ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
public class ToManyRequestsException extends BaseException {

  @Serial
  private static final long serialVersionUID = 1L;

  public ToManyRequestsException(ErrorMessage errorMessage) {
    super(errorMessage.getMessage());
  }

  public ToManyRequestsException(String message) {
    super(message);
  }
}
