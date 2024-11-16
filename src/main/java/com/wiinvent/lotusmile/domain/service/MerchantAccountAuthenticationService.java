package com.wiinvent.lotusmile.domain.service;

import com.wiinvent.lotusmile.app.response.ChangePasswordMerchantResponse;
import com.wiinvent.lotusmile.app.response.LogoutResponse;
import com.wiinvent.lotusmile.app.response.TokenMerchantResponse;
import com.wiinvent.lotusmile.domain.entity.MerchantAccount;
import com.wiinvent.lotusmile.domain.entity.types.AppType;
import com.wiinvent.lotusmile.domain.entity.types.MerchantAccountState;
import com.wiinvent.lotusmile.domain.exception.BadRequestException;
import com.wiinvent.lotusmile.domain.exception.ErrorMessage;
import com.wiinvent.lotusmile.domain.exception.ForbiddenException;
import com.wiinvent.lotusmile.domain.exception.UnAuthenticationException;
import com.wiinvent.lotusmile.domain.pojo.MerchantTokenInfo;
import com.wiinvent.lotusmile.domain.security.MerchantAccountTokenInfo;
import com.wiinvent.lotusmile.domain.storage.DeviceTokenStorage;
import com.wiinvent.lotusmile.domain.storage.MerchantAccountStorage;
import lombok.extern.log4j.Log4j2;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@Log4j2
public class MerchantAccountAuthenticationService extends BaseService {
  @Value("${jwt.merchant-account-private-key}")
  private String merchantAccountPrivateKey;
  @Autowired
  private MerchantAccountStorage merchantAccountStorage;
  @Autowired
  @Lazy
  private MerchantAccountAuthenticationService self;

  @Autowired
  private DeviceTokenService deviceTokenService;

  @Autowired
  private DeviceTokenStorage deviceTokenStorage;

  public MerchantAccountTokenInfo validateMerchantAccountToken(String accessToken) {
    MerchantTokenInfo merchantTokenInfo = jwtTokenUtil.validateTokenMerchant(accessToken);
    MerchantAccountTokenInfo merchantAccountTokenInfo = new MerchantAccountTokenInfo();
    merchantAccountTokenInfo.setUserId(merchantTokenInfo.getUserId());
    return merchantAccountTokenInfo;
  }

  //login
  public TokenMerchantResponse login(MerchantAccountTokenDto dto) {
    log.debug("======>login username:{} devToken: {} ",dto.getUsername(), dto.getDeviceToken());

    String username = dto.getUsername().trim();
    String password = dto.getPassword().trim();
    if (isBlocked(username)) {
      throw new BadRequestException("wiinvent.account.login.bock");
    }

    try {
      MerchantAccount account = merchantAccountStorage.findMerchantByUsername(username);

      if (account == null) {
        throw new BadRequestException(ErrorMessage.INVALID_USERNAME_OR_PASSWORD);
      }
      if (account.getState() == MerchantAccountState.BANNED) {
        throw new ForbiddenException(ErrorMessage.ACCOUNT_BANNED);
      }
      if (!BCrypt.checkpw(password, account.getPassword())) {
        throw new BadRequestException(ErrorMessage.INVALID_USERNAME_OR_PASSWORD);
      }
      MerchantTokenInfo merchantTokenInfo = MerchantTokenInfo.createFrom(account);

      TokenMerchantResponse response = new TokenMerchantResponse();
      response.setRole(merchantTokenInfo.getRole());
      response.setAccessToken(jwtTokenUtil.generateTokenMerchant(merchantTokenInfo));
      if (dto.getIsRememberMe() != null && dto.getIsRememberMe()) {
        response.setRefreshToken(jwtTokenUtil.createRefreshToken(merchantTokenInfo.getUserName(), merchantTokenInfo.getUserId(), merchantTokenInfo.getMerchantId(), merchantTokenInfo.getRole()));
        saveRefreshToken(merchantTokenInfo.getUserName(), response.getRefreshToken(), jwtTokenUtil.getExpiredRefreshTokenMerchant());
      }

      if (dto.getDeviceToken() != null)
        deviceTokenService.processSaveDeviceToken(merchantTokenInfo.getUserId(), dto.getDeviceToken(), AppType.APP_MERCHANT);
      loginSucceeded(username);
      return response;
    } catch (Exception e) {
      loginFailed(username);
      throw e;
    }
  }

  public void saveRefreshToken(String username, String refreshToken, long durationInMs) {
    remoteCache.setInMs(cacheKey.genRefreshTokenMerchantKey(username), refreshToken, durationInMs);
  }

  //logout
  @Transactional(isolation = Isolation.SERIALIZABLE, propagation = Propagation.REQUIRED)
  public LogoutResponse logout(LogoutDto logoutDto) {
    log.debug("======> logout dvToken:{}", logoutDto.getDeviceToken());

    String username = jwtTokenUtil.getUsernameMerchant(logoutDto.getRefreshToken());
    deleteRefreshToken(username);

    deviceTokenStorage.deleteByDeviceToken(logoutDto.getDeviceToken(), AppType.APP_MERCHANT);
    return LogoutResponse.builder()
        .isSuccess(true)
        .build();
  }

  public void deleteRefreshToken(String username) {
    remoteCache.del(cacheKey.genRefreshTokenMerchantKey(username));
  }

  //refresh token
  public TokenMerchantResponse refreshToken(RefreshTokenDto refreshTokenDto) {
    try {
      String username = jwtTokenUtil.getUsernameMerchant(refreshTokenDto.getRefreshToken());
      MerchantAccount account = merchantAccountStorage.findMerchantByUsername(username);
      if (account == null) {
        throw new UnAuthenticationException(ErrorMessage.UNAUTHORIZED);
      }

      String storedRefreshToken = getRefreshTokenMerchant(username);
      if (!refreshTokenDto.getRefreshToken().equals(storedRefreshToken)) {
        throw new UnAuthenticationException(ErrorMessage.UNAUTHORIZED);
      }

      // Tạo access token mới
      MerchantTokenInfo merchantTokenInfo = MerchantTokenInfo.createFrom(account);

      TokenMerchantResponse response = new TokenMerchantResponse();
      response.setRole(merchantTokenInfo.getRole());
      response.setAccessToken(jwtTokenUtil.generateTokenMerchant(merchantTokenInfo));
      response.setRefreshToken(jwtTokenUtil.createRefreshToken(merchantTokenInfo.getUserName(), merchantTokenInfo.getUserId(), merchantTokenInfo.getMerchantId(), merchantTokenInfo.getRole()));
      saveRefreshToken(merchantTokenInfo.getUserName(), response.getRefreshToken(), jwtTokenUtil.getExpiredRefreshTokenMerchant());
      return response;
    } catch (Exception e) {
      throw new UnAuthenticationException(ErrorMessage.UNAUTHORIZED);
    }
  }

  public String getRefreshTokenMerchant(String username) {
    return remoteCache.get(cacheKey.genRefreshTokenMerchantKey(username));
  }

  //change pass word
  public ChangePasswordMerchantResponse changePassword(Integer merchantAccountId, ChangePasswordDto dto) {
    String username = self.processChangePassword(merchantAccountId, dto);
    //cache
    remoteCache.del(cacheKey.genMerchantById(merchantAccountId));
    remoteCache.del(cacheKey.genMerchantByUsername(username));
    return ChangePasswordMerchantResponse.builder()
        .isSuccess(true)
        .build();
  }

  @Transactional(isolation = Isolation.SERIALIZABLE, propagation = Propagation.REQUIRED)
  public String processChangePassword(Integer merchantAccountId, ChangePasswordDto dto) {
    MerchantAccount account = merchantAccountStorage.findMerchantAccountById(merchantAccountId);
    if (account == null) {
      throw new BadRequestException(ErrorMessage.INVALID_USERNAME_OR_PASSWORD);
    }
    if (account.getState() == MerchantAccountState.BANNED) {
      throw new BadRequestException(ErrorMessage.ACCOUNT_BANNED);
    }
    if (!BCrypt.checkpw(dto.getOldPassword(), account.getPassword())) {
      throw new BadRequestException(ErrorMessage.OLD_PASSWORD_WRONG);
    }

    account.setPassword(BCrypt.hashpw(dto.getNewPassword(), BCrypt.gensalt(12)));
    merchantAccountStorage.save(account);
    return account.getUsername();
  }

  public boolean isBlocked(String username) {
    Integer attempt = remoteCache.get(cacheKey.genLoginFailed(username), Integer.class);
    return attempt != null && attempt >= configStorage.getLoginFailedAttempts();
  }

  public void loginFailed(String username) {
    Integer attempt = remoteCache.get(cacheKey.genLoginFailed(username), Integer.class);
    if (Objects.isNull(attempt)) {
      attempt = 0;
      remoteCache.put(cacheKey.genLoginFailed(username), attempt, configStorage.getAccountLockoutDuration());
    }
    attempt++;
    remoteCache.put(cacheKey.genLoginFailed(username), attempt, configStorage.getAccountLockoutDuration());
  }

  public void loginSucceeded(String username) {
    remoteCache.del(cacheKey.genLoginFailed(username));
  }
}
