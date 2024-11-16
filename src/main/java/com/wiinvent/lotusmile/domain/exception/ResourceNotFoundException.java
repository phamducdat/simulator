package com.wiinvent.lotusmile.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.Map;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends BaseException {
  private static final long serialVersionUID = 1L;
  private static final Map<String, String> lang = new HashMap<>();

  static {
    lang.put("vi", "Không tồn tại dữ liệu");
    lang.put("en", "Resource not found");
  }

  public ResourceNotFoundException(String exception) {
    super(exception);
  }
}