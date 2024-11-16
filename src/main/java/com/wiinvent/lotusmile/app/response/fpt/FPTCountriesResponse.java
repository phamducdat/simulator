package com.wiinvent.lotusmile.app.response.fpt;

import jakarta.annotation.Nullable;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class FPTCountriesResponse extends BaseFPTResponse {

  public List<Data> data;

  @Override
  public String getErrorMessageConverted() {
    return this.getErrorCode();
  }

  @lombok.Data
  public static class Data {
    private String code;
    private String name;
    @Nullable
    private String language;
  }
}
