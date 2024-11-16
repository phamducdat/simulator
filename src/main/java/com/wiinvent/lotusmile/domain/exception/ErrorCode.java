package com.wiinvent.lotusmile.domain.exception;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ErrorCode {

  @UtilityClass
  public static class FPT {
    public static final String MCAB_200 = "MCAB_200";
    public static final String MCAB_400 = "MCAB_400";
    public static final String MCAB_500 = "MCAB_500";
    public static final String DUPLICATE_CUSTOMER = "DUPLICATE_CUSTOMER";
    public static final String INVALID_PHONE_NUMBER = "INVALID_PHONE_NUMBER";
    public static final String INVALID_EMAIL_ADDRESS = "INVALID_EMAIL_ADDRESS";
    public static final String OTHER = "OTHER";
    public static final String INTERNAL_SERVER_ERROR_CODE = "INTERNAL_SERVER_ERROR";
    public static final String OTHER_OTHER = "OTHER_OTHER";
//    public static final String OTHER_ERROR = "OTHER_ERROR";
    public static final String RESOURCE_NOT_FOUND_CODE = "RESOURCE_NOT_FOUND_CODE";
    public static final String FORGOT_PASS_INCORRECT_INFO = "FORGOT_PASS_INCORRECT_INFO";
    public static final String BAD_REQUEST_CODE = "BAD_REQUEST_CODE";
    public static final String FORGOT_PHONE_NEEDED = "FORGOT_PHONE_NEEDED";
    public static final String FORGOT_EMAIL_NEEDED = "FORGOT_EMAIL_NEEDED";
    public static final String NOT_FOUND = "NOT_FOUND";
    public static final String LOGIN_ACCOUNT_INACTIVE = "LOGIN_ACCOUNT_INACTIVE";
    public static final String INVALID_OTP_VALUE = "INVALID_OTP_VALUE";
//    public static final String OTP_EXCESS_INPUT = "OTP_EXCESS_INPUT";
//    public static final String OTP_EXPIRED_OTP = "OTP_EXPIRED_OTP";
//    public static final String OTP_INVALID_OTP = "OTP_INVALID_OTP";
//    @Deprecated
//    public static final String DUPLICATE_EMAIL = "DUPLICATE_EMAIL";
//    @Deprecated
//    public static final String DUPLICATE_PHONE_NUMBER = "DUPLICATE_PHONE_NUMBER";
//    @Deprecated
//    public static final String DUPLICATE_IDENTIFY_NUMBER = "DUPLICATE_IDENTIFY_NUMBER";
//    @Deprecated
//    public static final String OTP_EXCEEDED = "OTP_EXCEEDED";
  }
}
