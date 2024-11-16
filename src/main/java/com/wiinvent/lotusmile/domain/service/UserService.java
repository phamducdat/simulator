package com.wiinvent.lotusmile.domain.service;

import com.wiinvent.lotusmile.app.dto.fpt.FPTChangePasswordDto;
import com.wiinvent.lotusmile.app.dto.fpt.FPTSendMessageDto;
import com.wiinvent.lotusmile.app.dto.fpt.FPTUpdateProfileDto;
import com.wiinvent.lotusmile.app.response.*;
import com.wiinvent.lotusmile.app.response.fpt.FPTAccountSummaryResponse;
import com.wiinvent.lotusmile.app.response.fpt.FPTActivityHistoryResponse;
import com.wiinvent.lotusmile.app.response.fpt.FPTCustomerProfileResponse;
import com.wiinvent.lotusmile.app.response.fpt.FPTOTPResponse;
import com.wiinvent.lotusmile.domain.annotation.LogMethodInputs;
import com.wiinvent.lotusmile.domain.entity.Config;
import com.wiinvent.lotusmile.domain.entity.User;
import com.wiinvent.lotusmile.domain.entity.UserProfile;
import com.wiinvent.lotusmile.domain.entity.types.TransactionType;
import com.wiinvent.lotusmile.domain.entity.types.fpt.TierCode;
import com.wiinvent.lotusmile.domain.exception.BadRequestException;
import com.wiinvent.lotusmile.domain.storage.UserProfileStorage;
import com.wiinvent.lotusmile.domain.storage.UserStorage;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;


@Service
@Log4j2
public class UserService extends BaseService {

  @Autowired
  private UserStorage userStorage;

  @Autowired
  private UserProfileStorage userProfileStorage;

  @Autowired
  @Lazy
  private UserService self;

  @SneakyThrows
  public UserCardResponse getUserCard(Integer userId, String ipAddress, String idDevice, String lang) {
    User user = userStorage.findUserByUserId(userId);
    CompletableFuture<FPTAccountSummaryResponse> accountSummaryAsync = externalService.getAccountSummaryAsync(lang, ipAddress, idDevice, user.getMainIdentifier());
    CompletableFuture<FPTCustomerProfileResponse> customerProfileAsync = externalService.getCustomerProfileAsync(ipAddress, idDevice, lang, Long.valueOf(user.getCustomerId()));
    CompletableFuture.allOf(accountSummaryAsync, customerProfileAsync).join();
    FPTAccountSummaryResponse accountSummaryResponse = accountSummaryAsync.get();
    FPTCustomerProfileResponse customerProfileResponse = customerProfileAsync.get();

    return UserCardResponse.mapFrom(accountSummaryResponse, customerProfileResponse);

  }

  public SendMessageResponse sendMessage(Integer userId, String ipAddress, String idDevice, String lang, SendMessageDto sendMessageDto) {
    User user = userStorage.findUserByUserId(userId);
    FPTSendMessageDto fptSendMessageDto = modelMapper.mapFromSendMessageDto(sendMessageDto, user.getCustomerId());
    externalService.sendMessage(lang, ipAddress, idDevice, fptSendMessageDto);
    return SendMessageResponse.builder().isSuccess(true).build();
  }

  public CustomerInfoResponse updateUserProfile(Integer userId, String ipAddress, String idDevice, String lang, UpdateCustomerInfoDto updateCustomerInfoDto, String otpId) {
    // validate update profile
    validateUpdateCustomerInfo(lang, ipAddress, idDevice, updateCustomerInfoDto);
    User user = userStorage.findUserByUserId(userId);
    FPTUpdateProfileDto fptUpdateProfileDto = modelMapper.mapFromUpdateProfileDto(updateCustomerInfoDto);
    FPTCustomerProfileResponse fptCustomerProfileResponse = externalService.updateCustomerProfile(ipAddress, idDevice, lang, Long.valueOf(user.getCustomerId()), otpId, fptUpdateProfileDto);
    FPTAccountSummaryResponse fptAccountSummaryResponse = externalService.getAccountSummary(lang, ipAddress, idDevice, user.getMainIdentifier());
    self.updateUser(user.getCustomerId(), fptCustomerProfileResponse, fptAccountSummaryResponse);
    return modelMapper.mapFromCustomerProfileResponseData(fptCustomerProfileResponse.getData());
  }

  public Boolean changePassword(Integer userId, String ipAddress, String idDevice, String lang,
                                String otpId, ChangePasswordDto changePasswordDto) {
    User user = userStorage.findUserByUserId(userId);
    FPTChangePasswordDto fptChangePasswordDto = FPTChangePasswordDto.createFrom(changePasswordDto);
    externalService.changePassword(lang, ipAddress, idDevice, Long.valueOf(user.getCustomerId()), otpId, fptChangePasswordDto);
    return true;
  }

  public Page<ActivityHistoryResponse> getActivityHistory(Integer userId, String ipAddress, String idDevice, String lang,
                                                          String fromDate, String toDate, int page, int size) {
    User user = userStorage.findUserByUserId(userId);
    // TODO: check the converting
    int firstResult = page * size;
    int maxResults = size;
    FPTActivityHistoryResponse response = externalService.getActivityHistory(lang, ipAddress, idDevice, Long.valueOf(user.getCustomerId()), fromDate, toDate, firstResult, maxResults);
    List<ActivityHistoryResponse> list = response.getData().stream().map(ActivityHistoryResponse::mapFrom).toList();
    PageRequest pageable = PageRequest.of(page, size);
    return new PageImpl<>(list, pageable, list.size());
  }

  public OTPResponse getOTP(Integer userId, String ipAddress, String idDevice, String lang, TransactionType transactionType) {
    User user = userStorage.findUserByUserId(userId);
    FPTOTPResponse otp = externalService.getOTP(lang, ipAddress, idDevice, Long.valueOf(user.getCustomerId()), transactionType);
    return OTPResponse.builder()
        .data(otp.getData())
        .build();
  }

  public LevelResponse getLevel(Integer userId, String ipAddress, String idDevice, String lang) {
    User user = userStorage.findUserByUserId(userId);
    FPTAccountSummaryResponse accountSummaryResponse = externalService.getAccountSummary(lang, ipAddress, idDevice, user.getMainIdentifier());
    TierCode currentTierCode = accountSummaryResponse.getData().getCurrentTierCode();
    Boolean millionMiller = accountSummaryResponse.getData().getMillionMiller();
    List<Config.RankingLevel> rankingLevels = configStorage.getRankingLevels();
    Config.RankingLevel currentRank = rankingLevels.stream().filter(rank -> rank.getTierCode() == currentTierCode && Objects.equals(rank.getMillionMiler(), millionMiller))
        .findFirst()
        .orElseThrow(() -> new BadRequestException("Current rank not found"));
    Config.RankingLevel upgradeRank = null;
    if (!currentRank.getMillionMiler()) {
      upgradeRank = rankingLevels.stream().filter(rank -> rank.getPosition() == currentRank.getPosition() + 1)
          .findFirst()
          .orElseThrow(() -> new BadRequestException("Upgrade rank not found"));
    }
    return new LevelResponse(accountSummaryResponse, currentRank, upgradeRank);
  }

  public OTPResponse resendOTP(Integer userId, String ipAddress, String idDevice, String lang, String otpId) {
    User user = userStorage.findUserByUserId(userId);
    FPTOTPResponse otp = externalService.resendOTP(lang, ipAddress, idDevice, Long.valueOf(user.getCustomerId()), otpId);
    return OTPResponse.builder()
        .data(otp.getData())
        .build();
  }

  public ConfirmOTPResponse confirmOTP(Integer userId, String ipAddress, String lang, String idDevice, String otpId, String otpValue) {
    User user = userStorage.findUserByUserId(userId);
    externalService.confirmOTP(lang, ipAddress, idDevice, Long.valueOf(user.getCustomerId()), otpId, otpValue);
    ConfirmOTPResponse confirmOTPResponse = new ConfirmOTPResponse();
    confirmOTPResponse.setIsSuccess(true);
    return confirmOTPResponse;
  }

  // TODO: update db?

  @SneakyThrows
  public UserSummaryResponse getUserSummaryByUserId(Integer userId, String ipAddress, String idDevice, String lang) {
    User user = userStorage.findUserByUserId(userId);
    CompletableFuture<FPTAccountSummaryResponse> accountSummaryAsync = externalService.getAccountSummaryAsync(lang, ipAddress, idDevice, user.getMainIdentifier());
    CompletableFuture<FPTCustomerProfileResponse> customerProfileAsync = externalService.getCustomerProfileAsync(ipAddress, idDevice, lang, Long.valueOf(user.getCustomerId()));
    CompletableFuture.allOf(accountSummaryAsync, customerProfileAsync).join();
    FPTAccountSummaryResponse accountSummaryResponse = accountSummaryAsync.get();
    FPTCustomerProfileResponse customerProfileResponse = customerProfileAsync.get();

    return UserSummaryResponse.createFrom(user.getId(), accountSummaryResponse, customerProfileResponse);
  }

  @Transactional
  public CustomerInfoResponse getUserProfileByUserId(Integer userId, String ipAddress, String idDevice, String lang) {
    User user = userStorage.findUserByUserId(userId);
    FPTCustomerProfileResponse customerProfile = externalService.getCustomerProfile(ipAddress, idDevice, lang, Long.valueOf(user.getCustomerId()));

    UserProfile userProfile = userProfileStorage.findUserProfileById(userId);
    if (userProfile == null) {
      UserProfile userProfileByCustomerProfile = UserProfile.createUserProfileByCustomerProfile(userId, customerProfile.getData());
      userProfileStorage.save(userProfileByCustomerProfile);
    } else {
      userProfile.setUserProfile(customerProfile.getData());
      userProfileStorage.save(userProfile);
    }
    return modelMapper.mapFromCustomerProfileResponseData(customerProfile.getData());
  }


  @Retryable()
  @Transactional(isolation = Isolation.SERIALIZABLE, propagation = Propagation.REQUIRED)
  @LogMethodInputs
  public void createUserFromEnroll(FPTCustomerProfileResponse fptCustomerProfileResponse,
                                   FPTAccountSummaryResponse fptAccountSummaryResponse) {
    User user = User.creatUserFromEnrollment(fptCustomerProfileResponse.getData(), fptAccountSummaryResponse.getData());
    userStorage.saveAndFlush(user);
    UserProfile userProfileByEnrollment = UserProfile.createUserProfileByCustomerProfile(user.getId(), fptCustomerProfileResponse.getData());
    userProfileStorage.save(userProfileByEnrollment);
  }

  @Retryable()
  @Transactional(isolation = Isolation.SERIALIZABLE, propagation = Propagation.REQUIRED)
  @LogMethodInputs
  public User createUserFromLogin(FPTCustomerProfileResponse fptCustomerProfileResponse, FPTAccountSummaryResponse fptAccountSummaryResponse) {
    User user = User.createUserFromLoginResponse(fptCustomerProfileResponse.getData(), fptAccountSummaryResponse.getData());
    // Ensure that data is immediately persisted to the database
    userStorage.saveAndFlush(user);
    UserProfile userProfile = UserProfile.createUserProfileByCustomerProfile(user.getId(), fptCustomerProfileResponse.getData());
    userProfileStorage.save(userProfile);
    return user;
  }

  @Retryable
  @Transactional(isolation = Isolation.SERIALIZABLE, propagation = Propagation.REQUIRED)
  @LogMethodInputs
  public void updateUser(String customerId, FPTCustomerProfileResponse fptCustomerProfileResponse, FPTAccountSummaryResponse fptAccountSummaryResponse) {
    User user = userStorage.findUserByCustomerId(customerId);
    UserProfile userProfile = userProfileStorage.findUserProfileById(user.getId());
    user.updateUser(fptCustomerProfileResponse.getData(), fptAccountSummaryResponse.getData());
    userProfile.updateUserProfile(fptCustomerProfileResponse.getData());
    userStorage.save(user);
    userProfileStorage.save(userProfile);
  }

  @Retryable()
  @Transactional(isolation = Isolation.SERIALIZABLE, propagation = Propagation.REQUIRED)
  @LogMethodInputs
  public User updateLoggedUser(Integer userId) {
    User user = userStorage.findUserByUserIdNoCache(userId);
    user.setIsLogged(true);
    userStorage.save(user);
    return user;
  }

  private void validateUpdateCustomerInfo(@NonNull String lang, String ipAddress, String idDevice, UpdateCustomerInfoDto updateCustomerInfoDto) {
    validateCountryCode(lang, ipAddress, idDevice, updateCustomerInfoDto.getAddress().getCountry());

    validateCountryRegion(lang, ipAddress, idDevice, updateCustomerInfoDto.getAddress().getCountry(), updateCustomerInfoDto.getAddress().getRegion());
  }
}
