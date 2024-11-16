package com.wiinvent.lotusmile.app.response.fpt;

import com.wiinvent.lotusmile.domain.entity.User;
import lombok.*;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@Getter
@Setter
@ToString(callSuper = true)
public class FPTForgotPasswordTokenResponse extends BaseFPTResponse {
  private Data data;

  @Override
  public String getErrorMessageConverted() {
    return this.getErrorCode();
  }

  @Deprecated
  public String getMessageError(ForgotPasswordTokenDto forgotPasswordTokenDto) {
    String loginIdStr = switch (forgotPasswordTokenDto.getType()) {
      case EMAIL -> "email";
      case PHONE_NUMBER -> "Số điện thoại";
      case MAIN_IDENTIFIER -> "Số thẻ hội viên";
    };
    return loginIdStr + " không tồn tại trong hệ thống. Vui lòng kiểm tra lại";
  }

  @lombok.Data
  @NoArgsConstructor
  public static class Data {

    private String requestId;

    private String otpId;

    private Long otpCustomerId;

    private Boolean multipleAccounts;

    private List<CustomerProfile> customerProfiles;

    @lombok.Data
    @Builder
    public static class CustomerProfile {
      private String mainIdentifier;

      private String firstName;

      private String lastName;

      private Long customerId;

      @Deprecated(forRemoval = true)
      public static CustomerProfile createFrom(User user) {
        return CustomerProfile.builder()
            .mainIdentifier(user.getMainIdentifier())
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .customerId(Long.valueOf(user.getCustomerId()))
            .build();
      }
    }
  }


}
