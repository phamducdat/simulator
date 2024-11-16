package com.wiinvent.lotusmile.app.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RefreshTokenResponse {
  private String accessToken;
}
