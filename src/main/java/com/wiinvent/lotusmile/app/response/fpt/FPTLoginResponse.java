package com.wiinvent.lotusmile.app.response.fpt;

import com.wiinvent.lotusmile.domain.entity.types.UserState;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@ToString(callSuper = true)
public class FPTLoginResponse extends BaseFPTResponse {
  // CustomerId
  private Data data;

  @Override
  public String getErrorMessageConverted() {
    return "Thông tin đăng nhập chưa đúng. Quý khách vui lòng kiểm tra lại.";

  }


  @lombok.Data
  public static class Data {
    // CustomerId
    private Long id;

    private UserState status;

    private String firstName;

    private String lastName;

    private String language;

    private Address address;

    @lombok.Data
    public static class Address {
      private String email;
    }
  }
}
