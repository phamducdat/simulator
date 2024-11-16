package com.wiinvent.lotusmile.app.response;

import lombok.Data;

@Data
public class TokenResponse {
  private String accessToken;
  private String type = "Bearer ";
  private String refreshToken;

  public TokenResponse(String accessToken, String refreshToken) {
    this.accessToken = accessToken;
    this.refreshToken = refreshToken;
  }
}
