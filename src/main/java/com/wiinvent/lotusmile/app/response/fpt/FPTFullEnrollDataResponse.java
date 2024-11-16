package com.wiinvent.lotusmile.app.response.fpt;

import com.wiinvent.lotusmile.domain.exception.ErrorCode;
import com.wiinvent.lotusmile.domain.exception.ErrorMessage;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@ToString(callSuper = true)
public class FPTFullEnrollDataResponse extends BaseFPTResponse {
  private String data;

  @Override
  public String getErrorMessageConverted() {
    return switch (this.getErrorCode()) {
      case ErrorCode.FPT.DUPLICATE_CUSTOMER ->
          "Membership information already exists. Please contact Lotusmiles for assistance.";
      case "INVALID_PHONE_NUMBER" -> "Invalid phone number";
      case "BAD_REQUEST_CODE" -> "Dư liệu không hợp lệ";
      case "PROFILE_PHONE_EXISTED" -> "Số điện thoại đã tồn tại trong hệ thống";
      case "PROFILE_EMAIL_EXISTED" -> "Email đã được sử dụng";
//      case "FULL_ENROLL_MIN_AGE" ->
//      case "FULL_ENROLL_MAX_AGE" ->
//      case "ENROLL_NAME_MAX_LENGTH" ->
//      case "ENROLL_NAME_MIN_LENGTH" ->
//      case "INVALID_EMAIL_ADDRESS" ->
//      case "SABRE_PROFILE_CREATION_ERROR" ->
//      case "INVALID_IDENTIFIER_NO" ->
//      case "MAX_HH_MEMBER_EXCEEDED" ->
      default -> this.getErrorCode();
    };
  }
}
