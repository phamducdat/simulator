package com.wiinvent.lotusmile.domain.util.cache;

import com.wiinvent.lotusmile.domain.entity.types.TransactionType;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CacheKey {

  @Value("${redis.prefix-key}")
  private String redisPrefixKey;


  public String genFPTTokenKey() {
    return redisPrefixKey + ":fpt:token";
  }

  public String genOTPKeyByTransaction(Long customerId, TransactionType transactionType) {
    return redisPrefixKey + ":simulator:otp:key:" + customerId + ":transaction:" + transactionType.toString();
  }

  public String genOTPIdKeyByTransaction(Long customerId, TransactionType transactionType) {
    return redisPrefixKey + ":simulator:otpId:key:" + customerId + ":transaction:" + transactionType.toString();
  }

  public String genOTPAttemptCountKeyByTransaction(String customerId, TransactionType transactionType) {
    return redisPrefixKey + ":simulator:otpAttemptCount:key:" + customerId + ":transaction:" + transactionType.toString();
  }

  public String genRequestIdKey(String customerId) {
    return redisPrefixKey + ":simulator:requestId:key:" + customerId;
  }

  public String genOTPIdKey(String customerId) {
    return redisPrefixKey + ":simulator:otpId:key:" + customerId;
  }

  public String genOTPForgotPasswordKey(String customerId) {
    return redisPrefixKey + ":simulator:forgotPassword:key:" + customerId;
  }

  public String genOTPForgotPasswordAttemptCountKey(String customerId) {
    return redisPrefixKey + ":simulator:forgotPasswordAttemptCount:key:" + customerId;
  }

  public String genLoginFailed(String username) {
    return redisPrefixKey + ":login:failed:attempt:" + username;
  }

  public String genFPTCountryRegions(@NonNull String lang, @NonNull String countryCode) {
    return redisPrefixKey + ":fpt:country:lang:" + lang + ":region:" + countryCode;
  }

  public String getFPTMobileCountryCode(String lang) {
    return redisPrefixKey + ":fpt:mobile:code:" + lang;
  }

  public String genFPTCountries(@NonNull String lang) {
    return redisPrefixKey + ":fpt:countries:" + lang;
  }

}
