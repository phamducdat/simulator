package com.wiinvent.lotusmile.domain.exception;

import lombok.Getter;

@Getter
public enum ErrorMessage {
  INTERNAL_SERVER("wiinvent.internal.server"),
  INVALID_DATA("wiinvent.invalid.data"),
  USER_NOT_FOUND("wiinvent.user.not.found"),
  INVALID_USERNAME_OR_PASSWORD("wiinvent.invalid.username.or.password"),
  ACCOUNT_BANNED("wiinvent.account.banned"),
  LOGIN_ID_PHONE_NUMBER_INVALID("wiinvent.login.id.phone.number.invalid"),
  LOGIN_ID_EMAIL_INVALID("wiinvent.login.id.email.invalid"),
  LOGIN_ID_MAIN_IDENTIFIER_INVALID("wiinvent.login.id.main.identifier.invalid"),
  UNAUTHORIZED("wiinvent.unauthorized"),
  OLD_PASSWORD_WRONG("wiinvent.password.old.is.wrong"),
  PHONE_CODE_INVALID("wiinvent.phone.code.invalid"),
  COUNTRY_CODE_INVALID("wiinvent.country.code.invalid"),
  COUNTRY_REGION_CODE_INVALID("wiinvent.country.region.code.invalid");

  private final String message;

  ErrorMessage(String message) {
    this.message = message;
  }


}
