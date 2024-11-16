package com.wiinvent.lotusmile.app.response.base;

import lombok.Data;

@Data
public class SingeResponse<T> {
  private final T data;

  public static <T> SingeResponse<T> createFrom(T data) {
    SingeResponse<T> singeResponse = new SingeResponse(data);
    return singeResponse;
  }
}
