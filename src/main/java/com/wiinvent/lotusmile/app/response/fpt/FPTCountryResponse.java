package com.wiinvent.lotusmile.app.response.fpt;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Deprecated(forRemoval = true)
@Data
@ToString(callSuper = true)
public class FPTCountryResponse extends BaseFPTResponse {

  private List<Data> data;

  @lombok.Data
  @AllArgsConstructor
  public static class Data {
    private String code;
    private String name;
    private String language;
  }

  @Override
  public String getErrorMessageConverted() {
    return "";
  }
}
