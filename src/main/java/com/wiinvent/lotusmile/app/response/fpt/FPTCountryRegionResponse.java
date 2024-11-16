package com.wiinvent.lotusmile.app.response.fpt;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class FPTCountryRegionResponse extends BaseFPTResponse {

  private List<Data> data;

  @lombok.Data
  @AllArgsConstructor
  public static class Data {
    private String code;
    private String name;
    private String countryCode;
  }

  @Override
  public String getErrorMessageConverted() {
    return "";
  }
}
