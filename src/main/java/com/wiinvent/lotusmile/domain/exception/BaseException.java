package com.wiinvent.lotusmile.domain.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class BaseException extends RuntimeException {

  public BaseException(String message) {
    super(message);
  }
}
