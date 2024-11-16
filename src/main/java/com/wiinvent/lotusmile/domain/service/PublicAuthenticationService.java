package com.wiinvent.lotusmile.domain.service;

import com.wiinvent.lotusmile.app.dto.*;
import com.wiinvent.lotusmile.app.dto.fpt.FPTForgotPasswordTokenDto;
import com.wiinvent.lotusmile.app.dto.fpt.FPTFullEnrollDataDto;
import com.wiinvent.lotusmile.app.dto.fpt.FPTLoginCustomerDto;
import com.wiinvent.lotusmile.app.dto.fpt.FPTSendForgotPasswordLinkDto;
import com.wiinvent.lotusmile.app.response.*;
import com.wiinvent.lotusmile.app.response.fpt.*;
import com.wiinvent.lotusmile.domain.annotation.LogMethodInputs;
import com.wiinvent.lotusmile.domain.entity.User;
import com.wiinvent.lotusmile.domain.entity.types.AppType;
import com.wiinvent.lotusmile.domain.exception.BadRequestException;
import com.wiinvent.lotusmile.domain.exception.ErrorMessage;
import com.wiinvent.lotusmile.domain.exception.UnAuthenticationException;
import com.wiinvent.lotusmile.domain.security.UserTokenInfo;
import com.wiinvent.lotusmile.domain.storage.CountryStorage;
import com.wiinvent.lotusmile.domain.storage.DeviceTokenStorage;
import com.wiinvent.lotusmile.domain.storage.UserStorage;
import com.wiinvent.lotusmile.domain.util.Helper;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static com.wiinvent.lotusmile.domain.exception.ErrorMessage.PHONE_CODE_INVALID;

@Service
@Log4j2
public class PublicAuthenticationService extends BaseService {

  @Autowired
  DeviceTokenStorage deviceTokenStorage;
  @Autowired
  private UserStorage userStorage;
  @Autowired
  private UserService userService;
  @Autowired
  private DeviceTokenService deviceTokenService;
  @Autowired
  CountryStorage countryStorage;

  @Value("${vna.partner-code}")
  private String partnerCode;

  private static void validateTypeForgotPassword(ForgotPasswordTokenDto.Type type, String loginId) {
    switch (type) {
      case MAIN_IDENTIFIER:
        if (loginId.length() != 8 && loginId.length() != 10) {
          throw new BadRequestException(ErrorMessage.LOGIN_ID_MAIN_IDENTIFIER_INVALID);
        }
        break;
      case EMAIL:
        String emailPattern = "^[a-zA-Z0-9]+([._-][a-zA-Z0-9]+)*@[a-zA-Z0-9]+(-[a-zA-Z0-9]+)*\\.[a-zA-Z]{2,}$";
        if (!loginId.matches(emailPattern)) {
          throw new BadRequestException(ErrorMessage.LOGIN_ID_EMAIL_INVALID);
        }
        break;
      case PHONE_NUMBER:
        String phonePattern = "\\d+";
        if (!loginId.matches(phonePattern)) {
          throw new BadRequestException(ErrorMessage.LOGIN_ID_PHONE_NUMBER_INVALID);
        }
        break;
      default:
    }

  }

  public ResendForgotPassOtpResponse resendForgotPassOtpResponse(String lang, String ipAddress, String requestId, String otpId) {
    ResendForgotPassOtpResponse response = new ResendForgotPassOtpResponse();
    FPTResendForgotPassOtpResponse fptResendForgotPassOtpResponse = externalService.resendForgotPassOtp(lang, ipAddress, requestId, otpId);
    response.setNewOtpId(fptResendForgotPassOtpResponse.getData());
    return response;
  }

  public ForgotPasswordTokenResponse forgotPasswordToken(String ipAddress, String lang, ForgotPasswordTokenDto forgotPasswordTokenDto) {
    validateTypeForgotPassword(forgotPasswordTokenDto.getType(), forgotPasswordTokenDto.getUserIdentity());
    FPTForgotPasswordTokenDto fptForgotPasswordTokenDto = FPTForgotPasswordTokenDto.createFrom(forgotPasswordTokenDto);
    FPTForgotPasswordTokenResponse fptForgotPasswordTokenResponse = externalService.getForgotPasswordToken(lang, ipAddress, fptForgotPasswordTokenDto);
    return ForgotPasswordTokenResponse.createFrom(fptForgotPasswordTokenResponse);
  }

  public ConfirmOTPResponse confirmOTPResponse(String lang, String ipAddress, String customerId, String otpId, String otpValue) {
    externalService.confirmOTPForgotPassword(lang, ipAddress, Long.valueOf(customerId), otpId, otpValue);
    ConfirmOTPResponse confirmOTPResponse = new ConfirmOTPResponse();
    confirmOTPResponse.setIsSuccess(true);
    return confirmOTPResponse;
  }

  public ResetPasswordLinkResponse sendForgotPasswordLink(String lang, String ipAddress, ResetPasswordLinkDto resetPasswordLinkDto) {
    FPTSendForgotPasswordLinkDto fptSendForgotPasswordLinkDto = FPTSendForgotPasswordLinkDto.createFrom(resetPasswordLinkDto);
    externalService.sendForgotPasswordLink(lang, ipAddress, fptSendForgotPasswordLinkDto);
    ResetPasswordLinkResponse response = new ResetPasswordLinkResponse();
    response.setIsSuccess(true);
    return response;
  }

  @LogMethodInputs
  public EnrollDataResponse enrollment(String ipAddress, String idDevice, @NonNull String lang, FullEnrollDataDto fullEnrollDataDto) {
    validateEnrollment(lang, ipAddress, idDevice, fullEnrollDataDto);
    FPTFullEnrollDataDto fptFullEnrollDataDto = new FPTFullEnrollDataDto(fullEnrollDataDto, partnerCode);
    FPTFullEnrollDataResponse enrollDataResponse = externalService.enrollment(ipAddress, idDevice, lang, fptFullEnrollDataDto);
    FPTAccountSummaryResponse fptAccountSummaryResponse = externalService.getAccountSummary(lang, ipAddress, idDevice, enrollDataResponse.getData());
    FPTCustomerProfileResponse customerProfile = externalService.getCustomerProfile(ipAddress, idDevice, lang, fptAccountSummaryResponse.getData().getCustomerId());
    userService.createUserFromEnroll(customerProfile, fptAccountSummaryResponse);
    EnrollDataResponse response = new EnrollDataResponse();
    response.setMainIdentifier(enrollDataResponse.getData());
    log.debug("==============>enrollment response = {}", response);
    return response;
  }

  public TokenResponse login(String ipAddress, String idDevice, String lang, LoginCustomerDto loginCustomerDto) {
    log.debug("======>login loginId:{} devToken: {} ", loginCustomerDto.getLoginId(), loginCustomerDto.getDeviceToken());
    if (loginCustomerDto.getType() == LoginCustomerDto.Type.PHONE_NUMBER && !validatePhoneCode(loginCustomerDto.getPhoneCode())) {
      throw new BadRequestException(PHONE_CODE_INVALID);
    }
    FPTLoginCustomerDto fptLoginCustomerDto = new FPTLoginCustomerDto();
    fptLoginCustomerDto.setLoginId(loginCustomerDto.getType() == LoginCustomerDto.Type.PHONE_NUMBER ?
        Helper.convertMobile(loginCustomerDto.getPhoneCode(), loginCustomerDto.getLoginId())
        : loginCustomerDto.getLoginId());
    fptLoginCustomerDto.setPassword(loginCustomerDto.getPassword());
    FPTLoginResponse fptLoginResponse = externalService.login(ipAddress, idDevice, lang, fptLoginCustomerDto);
    User user = getUser(ipAddress, idDevice, lang, fptLoginResponse.getData().getId());

    String accessToken = jwtTokenUtil.createAccessToken(user);
    String refreshToken = "";
    if (Boolean.TRUE.equals(loginCustomerDto.getIsRememberMe())) {
      refreshToken = getRefreshToken(user.getId());
      if (refreshToken == null) {
        refreshToken = jwtTokenUtil.createRefreshToken(user);
        saveRefreshToken(user.getId(), refreshToken, jwtTokenUtil.getExpiredRefreshToken());
      }
    }

    if (loginCustomerDto.getDeviceToken() != null)
      deviceTokenService.processSaveDeviceToken(user.getId(), loginCustomerDto.getDeviceToken(), AppType.APP_CLIENT);
    return new TokenResponse(accessToken, refreshToken);
  }

  @Transactional(isolation = Isolation.SERIALIZABLE, propagation = Propagation.REQUIRED)
  public LogoutResponse logout(LogoutDto logoutDto) {
    log.debug("======> logout dvToken:{}", logoutDto.getDeviceToken());

    Integer userId = jwtTokenUtil.getUserId(logoutDto.getRefreshToken());
    deleteRefreshToken(userId);

    deviceTokenStorage.deleteByDeviceToken(logoutDto.getDeviceToken(), AppType.APP_CLIENT);

    return LogoutResponse.builder().isSuccess(true).build();
  }

  public RefreshTokenResponse refreshToken(RefreshTokenDto refreshTokenDto) {
    Integer userId = jwtTokenUtil.getUserId(refreshTokenDto.getRefreshToken());
    User user = userStorage.findUserByUserIdNoCache(userId);
    if (user == null) {
      throw new UnAuthenticationException(ErrorMessage.UNAUTHORIZED);
    }
    String storedRefreshToken = getRefreshToken(userId);
    if (!refreshTokenDto.getRefreshToken().equals(storedRefreshToken)) {
      throw new UnAuthenticationException(ErrorMessage.UNAUTHORIZED);
    }
    String accessToken = jwtTokenUtil.createAccessToken(user);
    return RefreshTokenResponse.builder().accessToken(accessToken).build();

  }

  @SneakyThrows
  private User getUser(String ipAddress, String idDevice, String lang, @NonNull Long customerId) {

    User user = userStorage.findUserByCustomerId(String.valueOf(customerId));

    if (user == null) {
      FPTCustomerProfileResponse customerProfile = externalService.getCustomerProfile(ipAddress, idDevice, lang, customerId);
      FPTAccountSummaryResponse accountSummary = externalService.getAccountSummary(lang, ipAddress, idDevice, String.valueOf(customerProfile.getData().getMainIdentifier()));
      // TODO: Update user and userProfile?
//      CompletableFuture<FPTAccountSummaryResponse> accountSummaryAsync = externalService.accountSummaryAsync(lang, ipAddress, idDevice, user.getMainIdentifier());
//      CompletableFuture<FPTCustomerProfileResponse> customerProfileAsync = externalService.getCustomerProfileAsync(ipAddress, idDevice, lang, Long.valueOf(user.getCustomerId()));
//      CompletableFuture.allOf(accountSummaryAsync, customerProfileAsync).join();
//      FPTAccountSummaryResponse accountSummaryResponse = accountSummaryAsync.get();
//      FPTCustomerProfileResponse customerProfileResponse = customerProfileAsync.get();

//      if (!Objects.equals(accountSummaryResponse.getErrorCode(), ErrorCode.FPT.MCAB_200)) {
//        throw new BadRequestException(accountSummaryResponse.getErrorMessageConverted());
//      }
//      if (!Objects.equals(customerProfileResponse.getErrorCode(), ErrorCode.FPT.MCAB_200)) {
//        throw new BadRequestException(customerProfileResponse.getErrorMessageConverted());
//      }
      user = userService.createUserFromLogin(customerProfile, accountSummary);

    }
    if (user != null && !user.getIsLogged()) {
      user = userService.updateLoggedUser(user.getId());
    }
    return user;
  }

  public UserTokenInfo validateUserToken(String accessToken) {
    return jwtTokenUtil.validateUserToken(accessToken);
  }


  public void saveRefreshToken(Integer userId, String refreshToken, long durationInMs) {
    remoteCache.setInMs(cacheKey.genRefreshTokenKey(userId), refreshToken, durationInMs);
  }


  public void deleteRefreshToken(Integer userId) {
    remoteCache.del(cacheKey.genRefreshTokenKey(userId));
  }

  private String getRefreshToken(Integer userId) {
    return remoteCache.get(cacheKey.genRefreshTokenKey(userId));
  }

  public boolean validatePhoneCode(String value) {
    // TODO: using validate through FPT
    if (value == null)
      return false;
    return true;
//    return configStorage.getListPhoneCode()
//        .stream().map(Config.PhoneCode::getPhoneCode)
//        .anyMatch(phoneCode -> phoneCode.equals(value));
  }

  public void validateEnrollment(@NonNull String lang, String ipAddress, String idDevice, FullEnrollDataDto fullEnrollDataDto) {
    // validate country
    validateCountryCode(lang, ipAddress, idDevice, fullEnrollDataDto.getAddress().getCountry());

    // validate countryRegion
    validateCountryRegion(lang, ipAddress, idDevice, fullEnrollDataDto.getAddress().getCountry(), fullEnrollDataDto.getAddress().getRegion());

  }
}
