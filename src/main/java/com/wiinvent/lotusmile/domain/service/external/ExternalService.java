package com.wiinvent.lotusmile.domain.service.external;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wiinvent.lotusmile.app.dto.fpt.*;
import com.wiinvent.lotusmile.app.response.fpt.*;
import com.wiinvent.lotusmile.domain.annotation.LogPerformance;
import com.wiinvent.lotusmile.domain.entity.types.TransactionType;
import com.wiinvent.lotusmile.domain.exception.BadRequestException;
import com.wiinvent.lotusmile.domain.exception.ErrorCode;
import com.wiinvent.lotusmile.domain.exception.ErrorMessage;
import com.wiinvent.lotusmile.domain.exception.InternalServerException;
import com.wiinvent.lotusmile.domain.util.Helper;
import com.wiinvent.lotusmile.domain.util.cache.CacheKey;
import com.wiinvent.lotusmile.domain.util.cache.RemoteCache;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.util.Base64Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static com.wiinvent.lotusmile.domain.exception.InternalServerException.FROM_FPT;

@Log4j2
@Component
public class ExternalService {
  private static final String IP_ADDRESS = "ipAddress";
  private static final String LANG = "lang";
  private static final String ID_DEVICE = "idDevice";
  //  private static final String AUTHORIZATION = "authorization";
  @Value("${fpt.domain}")
  private String domain;
  @Value("${fpt.prefix}")
  private String prefix;
  private String domainPrefix;
  @Value("${fpt.user-name}")
  private String userNamePartner;
  @Value("${fpt.password}")
  private String passwordPartner;
  @Value("${fpt.token.time.expired.before.millisecond}")
  private long tokenTimeExpiredBeforeMillisecond;
  private String encodeAuth;

  @Autowired
  private RemoteCache remoteCache;

  @Autowired
  CacheKey cacheKey;


  @Autowired
  private RestTemplate restTemplate;

  @PostConstruct
  public void init() {
    this.domainPrefix = domain + "/" + prefix;
    String auth = userNamePartner + ":" + passwordPartner;
    this.encodeAuth = Base64Util.encode(auth);
  }

  @LogPerformance
  public FPTConfirmOTPForgotPasswordResponse confirmOTPForgotPassword(String lang,
                                                                      @NonNull String ipAddress,
                                                                      @NonNull Long customerId,
                                                                      @NonNull String otpId,
                                                                      @NonNull String otpValue) {
    HttpHeaders headers = new HttpHeaders();
    headers.set(LANG, lang != null ? lang : "vi");
    headers.set(IP_ADDRESS, Helper.getIpClient(ipAddress));
    log.debug("==============>confirmOTPForgotPassword headers = {}", headers);

    URI uri = UriComponentsBuilder
        .fromHttpUrl(domainPrefix)
        .path("/otp/{customerId}/confirm/{otpId}/{otpValue}")
        .buildAndExpand(customerId, otpId, otpValue)
        .toUri();
    log.debug("==============>confirmOTPForgotPassword uri = {}", uri);

    HttpEntity<String> request = new HttpEntity<>(headers);
    HttpEntity<FPTConfirmOTPForgotPasswordResponse> response = restTemplate.exchange(
        uri,
        HttpMethod.POST,
        request,
        FPTConfirmOTPForgotPasswordResponse.class
    );
    log.debug("==============>confirmOTPForgotPassword response = {}", response);
    checkErrorCode(response);
    return response.getBody();
  }

  public void confirmOTP(String lang,
                         @NonNull String ipAddress,
                         String idDevice,
                         @NonNull Long customerId,
                         @NonNull String otpId,
                         @NonNull String otpValue) {
    HttpHeaders headers = new HttpHeaders();
    headers.set(LANG, lang != null ? lang : "vi");
    headers.set(IP_ADDRESS, Helper.getIpClient(ipAddress));
    if (idDevice != null) {
      headers.set(ID_DEVICE, idDevice);
    }
    log.debug("==============>confirmOTP headers = {}", headers);
    URI uri = UriComponentsBuilder
        .fromHttpUrl(domainPrefix)
        .path("/otp/{customerId}/confirm/{otpId}/{otpValue}")
        .buildAndExpand(customerId, otpId, otpValue)
        .toUri();
    log.debug("==============>confirmOTP uri = {}", uri);
    HttpEntity<Object> request = new HttpEntity<>(headers);
    ResponseEntity<FPTConfirmOTP> response = restTemplate.exchange(
        uri,
        HttpMethod.POST,
        request,
        FPTConfirmOTP.class
    );
    log.debug("==============>confirmOTP response = {}", response);
    checkErrorCode(response);
  }

  @LogPerformance
  public FPTSendForgotPasswordLinkResponse sendForgotPasswordLink(String lang,
                                                                  @NonNull String ipAddress,
                                                                  @NonNull FPTSendForgotPasswordLinkDto FPTSendForgotPasswordLinkDto) {
    HttpHeaders headers = new HttpHeaders();
    headers.set(LANG, lang != null ? lang : "vi");
    headers.set(IP_ADDRESS, Helper.getIpClient(ipAddress));
    log.debug("==============>sendForgotPasswordLink headers = {}", headers);
    URI uri = UriComponentsBuilder
        .fromHttpUrl(domainPrefix)
        .path("/customer/sendForgotPasswordLink")
        .build()
        .toUri();
    log.debug("==============>sendForgotPasswordLink uri = {}", uri);
    HttpEntity<FPTSendForgotPasswordLinkDto> request = new HttpEntity<>(FPTSendForgotPasswordLinkDto, headers);
    HttpEntity<FPTSendForgotPasswordLinkResponse> response = restTemplate.exchange(
        uri,
        HttpMethod.POST,
        request,
        FPTSendForgotPasswordLinkResponse.class
    );
    log.debug("==============>sendForgotPasswordLink response = {}", response);
    checkErrorCode(response);
    return response.getBody();
  }

  @LogPerformance
  public FPTActivityHistoryResponse getActivityHistory(String lang,
                                                       String ipAddress,
                                                       String idDevice,
                                                       @NonNull Long customerId,
                                                       String fromDate,
                                                       String toDate,
                                                       Integer firstResult,
                                                       Integer maxResults) {
    HttpHeaders headers = new HttpHeaders();
    headers.set(LANG, lang != null ? lang : "vi");
    if (ipAddress != null)
      headers.set(IP_ADDRESS, Helper.getIpClient(ipAddress));
    if (idDevice != null)
      headers.set(ID_DEVICE, idDevice);
    headers.set("Authorization", getToken());
    log.debug("==============>getActivityHistory headers = {}", headers);

    UriComponentsBuilder uriBuilder = UriComponentsBuilder
        .fromHttpUrl(domainPrefix)
        .path("/customer/{customerId}/activity-history/v3")
        .queryParamIfPresent("fromDate", Optional.ofNullable(fromDate))
        .queryParamIfPresent("toDate", Optional.ofNullable(toDate))
        .queryParamIfPresent("firstResult", Optional.ofNullable(firstResult))
        .queryParamIfPresent("maxResults", Optional.ofNullable(maxResults));

    URI uri = uriBuilder.buildAndExpand(customerId).encode().toUri();
    log.debug("==============>getActivityHistory uri = {}", uri);
    HttpEntity<String> entity = new HttpEntity<>(headers);

    // Make the HTTP GET request
    HttpEntity<FPTActivityHistoryResponse> response = restTemplate.exchange(
        uri,
        HttpMethod.GET,
        entity,
        FPTActivityHistoryResponse.class
    );
    log.debug("==============>getActivityHistory response = {}", response);
    checkErrorCode(response);
    return response.getBody();

  }

  @LogPerformance
  public FPTResendForgotPassOtpResponse resendForgotPassOtp(String lang,
                                                            @NonNull String ipAddress,
                                                            @NonNull String requestId,
                                                            @NonNull String otpId) {
    HttpHeaders headers = new HttpHeaders();
    headers.set(LANG, lang != null ? lang : "vi");
    headers.set(IP_ADDRESS, Helper.getIpClient(ipAddress));

    URI uri = UriComponentsBuilder
        .fromHttpUrl(domainPrefix)
        .path("/customer/resendForgotPassOtp/{requestId}/{otpId}")
        .buildAndExpand(requestId, otpId)
        .toUri();
    HttpEntity<String> request = new HttpEntity<>(headers);
    HttpEntity<FPTResendForgotPassOtpResponse> response = restTemplate.exchange(
        uri,
        HttpMethod.POST,
        request,
        FPTResendForgotPassOtpResponse.class
    );
    log.debug("==============>resendForgotPassOtp response = {}", response);
    checkErrorCode(response);
    return response.getBody();
  }

  public FPTForgotPasswordTokenResponse getForgotPasswordToken(String lang, @NonNull String ipAddress, FPTForgotPasswordTokenDto fptForgotPasswordTokenDto) {
    HttpHeaders headers = new HttpHeaders();
    headers.set(LANG, lang != null ? lang : "vi");
    headers.set(IP_ADDRESS, Helper.getIpClient(ipAddress));

    log.debug("==============>getForgotPasswordToken headers = {}", headers);

    // TODO: Số điện thoại không Active (Closed) nên không được phép lấy OTP: NOT_ENOUGH_ELIGIBILITY
    URI uri = UriComponentsBuilder
        .fromHttpUrl(domainPrefix)
        .path("/customer/getForgotPasswordToken")
        .build()
        .toUri();
    log.debug("==============>getForgotPasswordToken uri = {}", uri);
    HttpEntity<FPTForgotPasswordTokenDto> request = new HttpEntity<>(fptForgotPasswordTokenDto, headers);
    HttpEntity<FPTForgotPasswordTokenResponse> response = restTemplate.exchange(
        uri,
        HttpMethod.POST,
        request,
        FPTForgotPasswordTokenResponse.class
    );
    log.debug("==============>getForgotPasswordToken response = {}", response);
    checkErrorCode(response);
    return response.getBody();
  }

  public FPTMobileCountryCode getMobileCountryCode(@NonNull String lang,
                                                   String ipAddress,
                                                   String idDevice) {
    HttpHeaders headers = new HttpHeaders();
    headers.set(LANG, lang);
    if (ipAddress != null)
      headers.set(IP_ADDRESS, Helper.getIpClient(ipAddress));
    if (idDevice != null)
      headers.set(ID_DEVICE, idDevice);
    headers.set("Authorization", getToken());
    HttpEntity<Object> entity = new HttpEntity<>(headers);
    log.debug("==============>getMobileCountryCode entity = {}", entity);
    URI uri = UriComponentsBuilder
        .fromHttpUrl(domainPrefix)
        .path("/getDictionary/EXT_MOBILE_COUNTRY_CODE")
        .build()
        .toUri();
    ResponseEntity<FPTMobileCountryCode> response = restTemplate.exchange(
        uri,
        HttpMethod.GET,
        entity,
        FPTMobileCountryCode.class
    );
    log.debug("==============>getMobileCountryCode response = {}", response);
    checkErrorCode(response);
    return response.getBody();
  }

  public FPTCountriesResponse getCountries(@NonNull String lang,
                                           String ipAddress,
                                           String idDevice) {
    HttpHeaders headers = new HttpHeaders();
    headers.set(LANG, lang);
    if (ipAddress != null)
      headers.set(IP_ADDRESS, Helper.getIpClient(ipAddress));
    if (idDevice != null)
      headers.set(ID_DEVICE, idDevice);
    headers.set("Authorization", getToken());
    HttpEntity<Object> entity = new HttpEntity<>(headers);
    log.debug("==============>getCountries entity = {}", entity);
    URI uri = UriComponentsBuilder
        .fromHttpUrl(domainPrefix)
        .path("/getCountries")
        .build()
        .toUri();
    ResponseEntity<FPTCountriesResponse> response = restTemplate.exchange(
        uri,
        HttpMethod.GET,
        entity,
        FPTCountriesResponse.class
    );
    log.debug("==============>getCountries response = {}", response);
    checkErrorCode(response);

    return response.getBody();
  }

  public FPTCountryRegionResponse getCountryRegions(@NonNull String lang,
                                                    String ipAddress,
                                                    String idDevice,
                                                    @NonNull String countryCode) {
    HttpHeaders headers = new HttpHeaders();
    headers.set(LANG, lang);
    if (ipAddress != null)
      headers.set(IP_ADDRESS, Helper.getIpClient(ipAddress));
    if (idDevice != null)
      headers.set(ID_DEVICE, idDevice);
    headers.set("Authorization", getToken());
    HttpEntity<Object> entity = new HttpEntity<>(headers);
    log.debug("==============>getCountryRegions entity = {}", entity);
    URI uri = UriComponentsBuilder
        .fromHttpUrl(domainPrefix)
        .path("/getCountryRegions")
        .queryParam("countryCode", countryCode)
        .build()
        .toUri();
    log.debug("==============>getCountryRegions uri = {}", uri);
    ResponseEntity<FPTCountryRegionResponse> response = restTemplate.exchange(
        uri,
        HttpMethod.GET,
        entity,
        FPTCountryRegionResponse.class
    );
    log.debug("==============>getCountryRegions response = {}", response);
    checkErrorCode(response);
    return response.getBody();
  }

  @LogPerformance
  public FPTChangePasswordResponse changePassword(String lang,
                                                  @NonNull String ipAddress,
                                                  String idDevice,
                                                  @NonNull Long customerId,
                                                  @NonNull String otpId,
                                                  @NonNull FPTChangePasswordDto fptChangePasswordDto) {
    HttpHeaders headers = new HttpHeaders();
    headers.set(LANG, lang != null ? lang : "vi");
    headers.set(IP_ADDRESS, Helper.getIpClient(ipAddress));
    if (idDevice != null) headers.set(ID_DEVICE, idDevice);
    headers.set("Authorization", getToken());
    log.debug("==============>changePassword headers = {}", headers);

    URI uri = UriComponentsBuilder
        .fromHttpUrl(domainPrefix)
        .path("/customer/{customerId}/changePasswordByCustomerId")
        .queryParam("otpId", otpId)
        .buildAndExpand(customerId)
        .toUri();
    log.debug("==============>changePassword uri = {}", uri);

    HttpEntity<FPTChangePasswordDto> request = new HttpEntity<>(fptChangePasswordDto, headers);
    HttpEntity<FPTChangePasswordResponse> response = restTemplate.exchange(
        uri,
        HttpMethod.POST,
        request,
        FPTChangePasswordResponse.class
    );
    log.debug("==============>changePassword response = {}", response);
    checkErrorCode(response);
    return response.getBody();
  }

  @Async
  @LogPerformance
  public CompletableFuture<FPTAccountSummaryResponse> getAccountSummaryAsync(String lang,
                                                                             String ipAddress,
                                                                             String idDevice,
                                                                             @NonNull String mainIdentifier) {
    return CompletableFuture.completedFuture(getAccountSummary(lang, ipAddress, idDevice, mainIdentifier));
  }

  @LogPerformance
  public FPTAccountSummaryResponse getAccountSummary(String lang, String ipAddress, String idDevice, @NonNull String mainIdentifier) {
    HttpHeaders headers = new HttpHeaders();
    headers.set(LANG, lang != null ? lang : "vi");
    if (ipAddress != null) headers.set(IP_ADDRESS, Helper.getIpClient(ipAddress));
    if (idDevice != null) headers.set(ID_DEVICE, idDevice);
    headers.set("Authorization", getToken());
    log.debug("==============>accountSummary headers = {}", headers);
    HttpEntity<Object> entity = new HttpEntity<>(headers);
    String url = domainPrefix + "/customer/no=" + mainIdentifier + "/account-summary";
    log.debug("==============>accountSummary url = {}", url);
    HttpEntity<FPTAccountSummaryResponse> response = restTemplate.exchange(
        url,
        HttpMethod.GET,
        entity,
        FPTAccountSummaryResponse.class);
    log.debug("==============>accountSummary accountSummaryResponse = {}", response);
    checkErrorCode(response);
    return response.getBody();
  }

  @LogPerformance
  public FPTSendMessageResponse sendMessage(String lang,
                                            String ipAddress,
                                            String idDevice,
                                            @NonNull FPTSendMessageDto fptSendMessageDto) {
    HttpHeaders headers = new HttpHeaders();
    headers.set(LANG, lang != null ? lang : "vi");
    if (ipAddress != null) headers.set(IP_ADDRESS, Helper.getIpClient(ipAddress));
    if (idDevice != null) headers.set(ID_DEVICE, idDevice);
    headers.set("Authorization", getToken());
    log.debug("==============>sendMessage headers = {}", headers);
    URI uri = UriComponentsBuilder
        .fromHttpUrl(domainPrefix)
        .path("/sendMessage")
        .build()
        .toUri();
    log.debug("==============>sendMessage uri = {}", uri);
    HttpEntity<FPTSendMessageDto> request = new HttpEntity<>(fptSendMessageDto, headers);
    HttpEntity<FPTSendMessageResponse> response = restTemplate.exchange(
        uri,
        HttpMethod.POST,
        request,
        FPTSendMessageResponse.class
    );

    log.debug("==============>sendMessage response = {}", response);
    checkErrorCode(response);
    return response.getBody();
  }

  @LogPerformance
  public FPTOTPResponse getOTP(String lang, @NonNull String ipAddress, String idDevice, @NonNull Long customerId, @NonNull TransactionType transactionType) {
    HttpHeaders headers = new HttpHeaders();
    headers.set(LANG, lang != null ? lang : "vi");
    headers.set(IP_ADDRESS, Helper.getIpClient(ipAddress));
    if (idDevice != null) headers.set(ID_DEVICE, idDevice);
    headers.set("Authorization", getToken());
    log.debug("==============>getOTP headers = {}", headers);
    String url = domainPrefix + "/otp/" + customerId + "/request/" + transactionType.name();
    log.debug("==============>getOTP url = {}", url);
    HttpEntity<String> request = new HttpEntity<>(null, headers);
    HttpEntity<FPTOTPResponse> response = restTemplate.exchange(
        url,
        HttpMethod.POST,
        request,
        FPTOTPResponse.class);
    log.debug("==============>getOTP otpResponse = {}", response);
    checkErrorCode(response);
    return response.getBody();
  }

  @LogPerformance
  public FPTOTPResponse resendOTP(String lang, @NonNull String ipAddress, String idDevice, @NonNull Long customerId, @NonNull String optId) {
    HttpHeaders headers = new HttpHeaders();
    headers.set(LANG, lang != null ? lang : "vi");
    headers.set(IP_ADDRESS, Helper.getIpClient(ipAddress));
    if (idDevice != null) headers.set(ID_DEVICE, idDevice);
    headers.set("Authorization", getToken());
    log.debug("==============>resendOTP headers = {}", headers);
    URI uri = UriComponentsBuilder
        .fromHttpUrl(domainPrefix)
        .path("/otp/{customerId}/resend/{otpId}")
        .buildAndExpand(customerId, optId)
        .toUri();
    log.debug("==============>resendOTP uri = {}", uri);
    HttpEntity<FPTOTPResponse> request = new HttpEntity<>(headers);

    HttpEntity<FPTOTPResponse> response = restTemplate.exchange(
        uri,
        HttpMethod.POST,
        request,
        FPTOTPResponse.class
    );
    log.debug("==============>resendOTP otpResponse = {}", response);
    checkErrorCode(response);
    return response.getBody();
  }

  @Async
  @LogPerformance
  public CompletableFuture<FPTCustomerProfileResponse> getCustomerProfileAsync(String ipAddress, String idDevice, String lang, @NonNull Long customerId) {
    return CompletableFuture.completedFuture(getCustomerProfile(ipAddress, idDevice, lang, customerId));
  }

  @LogPerformance
  public FPTCustomerProfileResponse getCustomerProfile(String ipAddress, String idDevice, String lang, @NonNull Long customerId) {
    HttpHeaders headers = new HttpHeaders();
    headers.set(LANG, lang != null ? lang : "vi");
    if (ipAddress != null) headers.set(IP_ADDRESS, Helper.getIpClient(ipAddress));
    if (idDevice != null) headers.set(ID_DEVICE, idDevice);
    headers.set("Authorization", getToken());
    log.debug("==============>getCustomerProfile headers = {}", headers);
    String url = domainPrefix + "/customer/" + customerId + "/profile";
    log.debug("==============>getCustomerProfile url = {}", url);
    HttpEntity<String> entity = new HttpEntity<>(headers);
    HttpEntity<FPTCustomerProfileResponse> response = restTemplate.exchange(
        url,
        HttpMethod.GET,
        entity,
        FPTCustomerProfileResponse.class
    );
    log.debug("==============>getCustomerProfile response = {}", response);
    checkErrorCode(response);
    return response.getBody();
  }

  @LogPerformance
  public FPTCustomerProfileResponse updateCustomerProfile(String ipAddress, String idDevice, String lang,
                                                          @NonNull Long customerId, String otpId, @NonNull FPTUpdateProfileDto updateProfileDto) {
    HttpHeaders headers = new HttpHeaders();
    headers.set(LANG, lang != null ? lang : "vi");
    if (ipAddress != null) headers.set(IP_ADDRESS, Helper.getIpClient(ipAddress));
    if (idDevice != null) headers.set(ID_DEVICE, idDevice);
    headers.set("Authorization", getToken());
    log.debug("==============>updateCustomerProfile headers = {}", headers);
    HttpEntity<FPTUpdateProfileDto> entity = new HttpEntity<>(updateProfileDto, headers);

    URI uri = UriComponentsBuilder
        .fromHttpUrl(domainPrefix)
        .path("/customer/{customerId}/profile")
        .queryParam("otpId", otpId)
        .buildAndExpand(customerId)
        .toUri();
    log.debug("==============>updateCustomerProfile uri = {}", uri);
    HttpEntity<FPTCustomerProfileResponse> response = restTemplate.exchange(uri, HttpMethod.PUT, entity, FPTCustomerProfileResponse.class);
    log.debug("==============>updateCustomerProfile response = {}", response);
    checkErrorCode(response);
    return response.getBody();
  }

  @LogPerformance
  public FPTAvatarResponse getAvatar(String lang, @NonNull Long customerId) {
    HttpHeaders headers = new HttpHeaders();
    headers.set(LANG, lang != null ? lang : "vi");
    log.debug("==============>getAvatar headers = {}", headers);

    URI uri = UriComponentsBuilder
        .fromHttpUrl(domainPrefix)
        .path("/customer/{customerId}/avatar")
        .buildAndExpand(customerId)
        .toUri();
    log.debug("==============>getAvatar uri = {}", uri);

    HttpEntity<FPTAvatarResponse> entity = new HttpEntity<>(headers);

    HttpEntity<FPTAvatarResponse> response = restTemplate.exchange(
        uri,
        HttpMethod.GET,
        entity,
        FPTAvatarResponse.class
    );
    log.debug("==============>getAvatar response = {}", response);
    checkErrorCode(response);
    return response.getBody();
  }


  @LogPerformance
  public FPTFullEnrollDataResponse enrollment(@NonNull String ipAddress, String idDevice, String lang, @NonNull FPTFullEnrollDataDto fptFullEnrollDataDto) {
    HttpHeaders headers = new HttpHeaders();
    headers.set(LANG, lang != null ? lang : "vi");
    headers.set(IP_ADDRESS, Helper.getIpClient(ipAddress));
    if (idDevice != null) headers.set(ID_DEVICE, idDevice);
    headers.set("Authorization", getToken());
    log.debug("==============>enrollment headers = {}", headers);
    String url = domainPrefix + "/enrollment";
    log.debug("==============>enrollment url = {}", url);
    HttpEntity<FPTFullEnrollDataDto> request = new HttpEntity<>(fptFullEnrollDataDto, headers);
    HttpEntity<FPTFullEnrollDataResponse> response = restTemplate.exchange(
        url,
        HttpMethod.POST,
        request,
        FPTFullEnrollDataResponse.class
    );
    log.debug("==============>enrollment response = {}", response);
    checkErrorCode(response);
    return response.getBody();
  }

  @LogPerformance
  public FPTLoginResponse login(@NonNull String ipAddress, String idDevice, String lang, @NonNull FPTLoginCustomerDto FPTLoginCustomerDto) {
    HttpHeaders headers = new HttpHeaders();
    headers.set(LANG, lang != null ? lang : "vi");
    headers.set(IP_ADDRESS, Helper.getIpClient(ipAddress));
    if (idDevice != null) headers.set(ID_DEVICE, idDevice);
    headers.set("Authorization", getToken());
    log.debug("==============>login headers = {}", headers);
    String url = domainPrefix + "/customer/login";
    log.debug("==============>login url = {}", url);
    HttpEntity<FPTLoginCustomerDto> request = new HttpEntity<>(FPTLoginCustomerDto, headers);

    HttpEntity<FPTLoginResponse> response = restTemplate.exchange(
        url,
        HttpMethod.POST,
        request,
        FPTLoginResponse.class
    );
    log.debug("==============>login response = {}", response);
    checkErrorCode(response);
    return response.getBody();
  }

  @LogPerformance
  private String getToken() {
    FPTTokenResponse fptToken = remoteCache.get(cacheKey.genFPTTokenKey(), FPTTokenResponse.class);
    if (fptToken == null) {
      FPTTokenResponse tokenAPI = getTokenAPI();
      if (tokenAPI != null) {
        remoteCache.put(cacheKey.genFPTTokenKey(), tokenAPI, TimeUnit.SECONDS.toMillis(tokenAPI.getExpiresIn()) - tokenTimeExpiredBeforeMillisecond);
        return tokenAPI.getTokenType() + " " + tokenAPI.getAccessToken();
      } else {
        throw new RuntimeException("=============================>Failed when get token FPT");
      }
    } else {
      return fptToken.getTokenType() + " " + fptToken.getAccessToken();
    }
  }

  @LogPerformance
  private FPTTokenResponse getTokenAPI() {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "Basic " + encodeAuth);
    log.debug("==============>getToken headers = {}", headers);
    String url = domainPrefix + "/authenticate";
    log.debug("==============>getToken url = {}", url);
    HttpEntity<String> entity = new HttpEntity<>(headers);

    ResponseEntity<FPTTokenResponse> response = restTemplate.exchange(
        url,
        HttpMethod.POST,
        entity,
        ExternalService.FPTTokenResponse.class
    );
    log.debug("==============>getToken response = {}", response);
    if (response.getStatusCode() != HttpStatus.OK) {
      throw new RuntimeException("Get FPT token failed: " + response);
    }
    return response.getBody();
  }

  public void checkErrorCode(HttpEntity<? extends BaseFPTResponse> responseHttpEntity) {
    BaseFPTResponse body = responseHttpEntity.getBody();
    if (body == null) {
      throw new InternalServerException("Body is null");
    }
    if (Objects.equals(body.getErrorCode(), ErrorCode.FPT.MCAB_200)) {
      return;
    }
    switch (body.getErrorCode()) {
      case ErrorCode.FPT.MCAB_400 -> throw new BadRequestException(ErrorMessage.INVALID_DATA);
      case ErrorCode.FPT.MCAB_500 -> throw new InternalServerException(FROM_FPT);
      default -> throw new BadRequestException(body.getErrorMessageConverted());
    }
  }

  @Data
  public static class FPTTokenResponse {
    @JsonProperty("access_token")
    private String accessToken;
    @JsonProperty("token_type")
    private String tokenType;
    // seconds
    @JsonProperty("expires_in")
    private Long expiresIn;
  }

}
