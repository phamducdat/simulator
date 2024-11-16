package com.wiinvent.lotusmile.app.response.fpt;

import jakarta.annotation.Nullable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class FPTMobileCountryCode extends BaseFPTResponse {
  public List<Data> data;

  @Override
  public String getErrorMessageConverted() {
    return this.getErrorCode();
  }

  @lombok.Data
  public static class Data {
    private String code;
    private String value;
    private String order;
    @Nullable
    private String imageId;
  }
}
