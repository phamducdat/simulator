package com.wiinvent.lotusmile.domain.util.cache;

import com.wiinvent.lotusmile.domain.entity.types.TransactionType;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CacheKey {

  @Value("${redis.prefix-key}")
  private String redisPrefixKey;

  public String genCountOptTimeByExternalUserKey(String externalUserId) {
    return redisPrefixKey + ":otp:count:user:" + externalUserId;
  }

  public String genCountOtpTimeByIp(String ip) {
    return redisPrefixKey + ":opt:count:ip:" + ip;
  }

  public String genConfigKey(String key) {
    return redisPrefixKey + ":cf:" + key;
  }

  public String genUserByMsisdnKey(String msisdn) {
    return redisPrefixKey + ":u:msisdn:" + msisdn;
  }

  public String genUserByUserId(Integer userId) {
    return redisPrefixKey + ":u:id:" + userId.toString();
  }

  public String genUserByMainIdentifier(String mainIdentifier) {
    return redisPrefixKey + ":u:mi:" + mainIdentifier;
  }

  public String genUserByCustomerId(String customerId) {
    return redisPrefixKey + ":u:cid:" + customerId;
  }

  public String genUserProfileById(Integer id) {
    return redisPrefixKey + ":u:profile:id:" + id.toString();
  }

  public String genUserByEmail(String email) {
    return redisPrefixKey + ":u:email:" + email;
  }

  public String genMerchantByMsisdnKey(String msisdn) {
    return redisPrefixKey + ":merchant:msisdn:" + msisdn;
  }

  public String genMerchantById(Integer id) {
    return redisPrefixKey + ":merchant:id:" + id.toString();
  }

  public String genMerchantByUsername(String username) {
    return redisPrefixKey + ":merchant:username:" + username;
  }

  public String genMerchantProfileById(Integer id) {
    return redisPrefixKey + ":merchant:profile:id:" + id.toString();
  }

  public String genMerchantByEmail(String email) {
    return redisPrefixKey + ":merchant:email:" + email;
  }

  public String genMerchantInfoById(Integer id) {
    return redisPrefixKey + ":merchant:info:id:" + id.toString();
  }

  public String genMerchantInfoByMsisdn(String msisdn) {
    return redisPrefixKey + ":merchant:info:msisdn:" + msisdn;
  }

  public String genMerchantInfoByEmail(String email) {
    return redisPrefixKey + ":merchant:info:email:" + email;
  }

  public String genRefreshTokenKey(Integer userId) {
    return redisPrefixKey + ":refresh:token:" + userId;
  }

  public String genRefreshTokenMerchantKey(String username) {
    return redisPrefixKey + ":refresh:token:merchant:" + username;
  }

  public String genFPTTokenKey() {
    return redisPrefixKey + ":fpt:token";
  }

  @Deprecated(forRemoval = true)
  public String genOTPKeyByTransaction(Long customerId, TransactionType transactionType) {
    return redisPrefixKey + ":simulator:otp:key:" + customerId + ":transaction:" + transactionType.toString();
  }

  @Deprecated(forRemoval = true)
  public String genOTPIdKeyByTransaction(Long customerId, TransactionType transactionType) {
    return redisPrefixKey + ":simulator:otpId:key:" + customerId + ":transaction:" + transactionType.toString();
  }

  @Deprecated(forRemoval = true)
  public String genOTPAttemptCountKeyByTransaction(String customerId, TransactionType transactionType) {
    return redisPrefixKey + ":simulator:otpAttemptCount:key:" + customerId + ":transaction:" + transactionType.toString();
  }

  @Deprecated(forRemoval = true)
  public String genRequestIdKey(String customerId) {
    return redisPrefixKey + ":simulator:requestId:key:" + customerId;
  }

  @Deprecated(forRemoval = true)
  public String genOTPIdKey(String customerId) {
    return redisPrefixKey + ":simulator:otpId:key:" + customerId;
  }

  @Deprecated(forRemoval = true)
  public String genOTPForgotPasswordKey(String customerId) {
    return redisPrefixKey + ":simulator:forgotPassword:key:" + customerId;
  }

  @Deprecated(forRemoval = true)
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
