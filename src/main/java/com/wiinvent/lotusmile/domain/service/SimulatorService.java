package com.wiinvent.lotusmile.domain.service;

import com.wiinvent.lotusmile.app.dto.*;
import com.wiinvent.lotusmile.app.response.fpt.*;
import com.wiinvent.lotusmile.domain.entity.UserPassword;
import com.wiinvent.lotusmile.domain.entity.types.TransactionType;
import com.wiinvent.lotusmile.domain.entity.types.UserState;
import com.wiinvent.lotusmile.domain.exception.BadRequestException;
import com.wiinvent.lotusmile.domain.exception.ErrorCode;
import com.wiinvent.lotusmile.domain.repository.UserPasswordRepository;
import com.wiinvent.lotusmile.domain.util.GeneratorUtil;
import com.wiinvent.lotusmile.domain.util.Helper;
import com.wiinvent.lotusmile.domain.util.JsonParser;
import com.wiinvent.lotusmile.domain.util.cache.CacheKey;
import com.wiinvent.lotusmile.domain.util.cache.RemoteCache;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Log4j2
public class SimulatorService {

  private final Random random = new Random();
  @Autowired
  RemoteCache remoteCache;
  @Autowired
  CacheKey cacheKey;
  @Value("${jwt.user-private-key}")
  private String userPrivateKey;
  @Autowired
  private RedisTemplate<String, String> redisTemplate;
  @Value("${redis.prefix-key}")
  private String redisPrefixKey;
  @Autowired
  private UserPasswordRepository userPasswordRepository;
  @Autowired
  private TelegramService telegramService;

  public FPTAccountSummaryResponse getAccountSummaryResponse(String identifierNo) {
    User user = userStorage.findUserByMainIdentity(identifierNo);
    UserProfile userProfile = userProfileStorage.findUserProfileById(user.getId());
    return FPTAccountSummaryResponse.simulatorByFaker(identifierNo, user, userProfile);
  }

  public FPTPromotionCodeResponse getPromotionCode(String identifierNo) {
    return FPTPromotionCodeResponse.createFakeData(identifierNo);
  }


  public FPTSendMessageResponse sendMessage(FPTSendMessageDto fptSendMessageDto) {
    FPTSendMessageResponse fptSendMessageResponse = new FPTSendMessageResponse();
    fptSendMessageResponse.setErrorCode(ErrorCode.FPT.MCAB_200);
    fptSendMessageResponse.setErrorMessage("Fake data");
    return fptSendMessageResponse;
  }

  @Transactional(isolation = Isolation.SERIALIZABLE, propagation = Propagation.REQUIRED)
  public FPTChangePasswordResponse changePassword(Long customerId, String otpId, FPTChangePasswordDto FPTChangePasswordDto) {
    FPTChangePasswordResponse response = new FPTChangePasswordResponse();
    String otpAttemptCountKey = cacheKey.genOTPAttemptCountKeyByTransaction(String.valueOf(customerId), TransactionType.CHANGE_PASS);
    int otpAttemptCount = remoteCache.get(otpAttemptCountKey) != null
        ? Integer.parseInt(remoteCache.get(otpAttemptCountKey))
        : 0;
    if (otpAttemptCount >= 4) {
      response.setErrorCode("OTP_EXPIRED_OTP");
//      response.setErrorCode("Bạn đã nhập quá số lần quy định");
      return response;
    }
//    String otp = remoteCache.get(cacheKey.genOTPKeyByTransaction(customerId, TransactionType.CHANGE_PASS));
    String otpIdRedis = remoteCache.get(cacheKey.genOTPIdKeyByTransaction(customerId, TransactionType.CHANGE_PASS));
    if (!otpId.equals(otpIdRedis)) {
      remoteCache.put(otpAttemptCountKey, String.valueOf(otpAttemptCount + 1));
      response.setErrorCode(ErrorCode.FPT.INVALID_OTP_VALUE);
      response.setErrorMessage("Mã OTP không chính xác");
      return response;
    }
    remoteCache.del(cacheKey.genOTPKeyByTransaction(customerId, TransactionType.CHANGE_PASS));
    UserPassword userPassword = userPasswordRepository.findUserPasswordByCustomerId(String.valueOf(customerId));
    if (userPassword == null) {
      User user = userRepository.findByCustomerId(String.valueOf(customerId));
      userPassword = UserPassword.createFrom(user);
      userPassword = userPasswordRepository.save(userPassword);
    }
    if (!userPassword.getPassword().equals(FPTChangePasswordDto.getOldPassword())) {
      response.setErrorCode(ErrorCode.FPT.OTHER_OTHER);
      response.setErrorMessage("Invalid Old Password");
      return response;
    }
    userPassword.setPassword(FPTChangePasswordDto.getNewPassword());
    userPasswordRepository.save(userPassword);
    response.setErrorCode(ErrorCode.FPT.MCAB_200);
    response.setErrorMessage("Xử lý thành công");
    remoteCache.put(otpAttemptCountKey, "0");
    remoteCache.del(cacheKey.genOTPKeyByTransaction(customerId, TransactionType.CHANGE_PASS));
    return response;
  }

  public FPTOTPResponse resetOTP(Long customerId, String otpId) {
    User user = userRepository.findByCustomerId(String.valueOf(customerId));
    String transactionTypeByOTP = findTransactionTypeByOTP(customerId, otpId);
    if (transactionTypeByOTP == null) {
      throw new BadRequestException("Transaction type not found");
    }
    TransactionType transactionType = TransactionType.valueOf(transactionTypeByOTP);
    remoteCache.del(cacheKey.genOTPKeyByTransaction(customerId, transactionType));
    FPTOTPResponse response = new FPTOTPResponse();

    int otp = 100000 + random.nextInt(900000);
    String newOtpId = UUID.randomUUID().toString();
    boolean isSendMail = telegramService.sendOTP(user.getEmail(), user.getMainIdentifier(), user.getPhoneNumber(), String.valueOf(otp));
    if (isSendMail) {
      remoteCache.put(cacheKey.genOTPKeyByTransaction(customerId, transactionType), otp, 60);
      response.setData(newOtpId);
      response.setErrorMessage("Xử lý thành công");
      response.setErrorCode(ErrorCode.FPT.MCAB_200);
      String otpAttemptCountKey = cacheKey.genOTPAttemptCountKeyByTransaction(String.valueOf(customerId), transactionType);
      remoteCache.put(otpAttemptCountKey, "0");
      return response;
    } else {
      response.setErrorCode(ErrorCode.FPT.OTHER_OTHER);
    }
    return response;
  }

  public FPTActivityHistoryResponse getActivityHistory(Long customerId, String fromDate, String toDate, Integer firstResult, Integer maxResults) {
    return FPTActivityHistoryResponse.fakeData(fromDate, toDate, firstResult, maxResults);
  }

  public FPTOTPResponse getOTP(Long customerId, TransactionType transactionType) {
    User user = userRepository.findByCustomerId(String.valueOf(customerId));
    FPTOTPResponse response = new FPTOTPResponse();

    int otp = 100000 + random.nextInt(900000);
    String otpId = UUID.randomUUID().toString();

    remoteCache.put(cacheKey.genOTPIdKeyByTransaction(customerId, transactionType), otpId, 600);
    remoteCache.put(cacheKey.genOTPKeyByTransaction(customerId, transactionType), otp, 60);

    boolean isSendMail = telegramService.sendOTP(user.getEmail(), user.getMainIdentifier(), user.getPhoneNumber(), String.valueOf(otp));
    if (isSendMail) {
      response.setData(otpId);
      response.setErrorMessage("Xử lý thành công");
      response.setErrorCode(ErrorCode.FPT.MCAB_200);
      String otpAttemptCountKey = cacheKey.genOTPAttemptCountKeyByTransaction(String.valueOf(customerId), transactionType);
      remoteCache.put(otpAttemptCountKey, "0");
    } else {
      response.setErrorCode(ErrorCode.FPT.OTHER_OTHER);
    }

    return response;
  }

  public FPTForgotPasswordTokenResponse getForgotPasswordToken(FPTForgotPasswordTokenDto forgotPasswordTokenDto) {
    FPTForgotPasswordTokenResponse response = new FPTForgotPasswordTokenResponse();
    FPTForgotPasswordTokenResponse.Data data = new FPTForgotPasswordTokenResponse.Data();
    String phoneNumber = forgotPasswordTokenDto.getPhone();
    if (phoneNumber != null) {
      phoneNumber = phoneNumber.replaceFirst("^\\+?84", "");
    }
    User user = userRepository.findByMainIdentifierOrPhoneNumberOrEmail(
        forgotPasswordTokenDto.getIdentifierNo(),
        phoneNumber,
        forgotPasswordTokenDto.getEmail()
    );
    if (user == null) {
      response.setErrorCode(ErrorCode.FPT.FORGOT_PASS_INCORRECT_INFO);
      return response;
    }
    String requestId = UUID.randomUUID().toString();
    String otpId = UUID.randomUUID().toString();
    int otp = 100000 + random.nextInt(900000);

    remoteCache.put(cacheKey.genRequestIdKey(user.getCustomerId()), requestId, 900);
    remoteCache.put(cacheKey.genOTPIdKey(user.getCustomerId()), otpId, 900);
    remoteCache.put(cacheKey.genOTPForgotPasswordKey(user.getCustomerId()), otp, 60);

    boolean isSendMail = telegramService.sendOTP(user.getEmail(), user.getMainIdentifier(), user.getPhoneNumber(), String.valueOf(otp));
    if (isSendMail) {
      FPTForgotPasswordTokenResponse.Data.CustomerProfile customerProfile = FPTForgotPasswordTokenResponse.Data.CustomerProfile.createFrom(user);
      data.setRequestId(requestId);
      data.setOtpId(otpId);
      data.setOtpCustomerId(Long.valueOf(user.getCustomerId()));
      data.setMultipleAccounts(false);
      data.setCustomerProfiles(List.of(customerProfile));
      response.setErrorCode(ErrorCode.FPT.MCAB_200);
      response.setData(data);
      response.setErrorMessage("To complete the transaction, please enter the verification code sent to email " + user.getEmail());
      String otpForgotPasswordAttemptCountKey = cacheKey.genOTPForgotPasswordAttemptCountKey(String.valueOf(user.getCustomerId()));
      remoteCache.put(otpForgotPasswordAttemptCountKey, "0");
    } else {
      response.setErrorCode(ErrorCode.FPT.OTHER_OTHER);
    }

    return response;
  }

  @Transactional
  public FPTResendForgotPassOtpResponse resendForgotPassOtpResponse(@NonNull String requestId, @NonNull String otpId) {
    FPTResendForgotPassOtpResponse response = new FPTResendForgotPassOtpResponse();
    String customerIdByRequestId = findCustomerIdByRequestId(requestId);
    String customerIdByOtpId = findCustomerIdByOtpId(otpId);
    if (customerIdByRequestId == null ||
        customerIdByOtpId == null ||
        !Objects.equals(customerIdByRequestId, customerIdByOtpId)) {
      response.setErrorCode("OTP_INVALID_OTP");
      return response;
    }
    User user = userRepository.findByCustomerId(customerIdByOtpId);
    assert user != null;

    String newOTPId = UUID.randomUUID().toString();
    int newOTP = 100000 + random.nextInt(900000);

    remoteCache.put(cacheKey.genOTPIdKey(customerIdByOtpId), newOTPId, 900);
    remoteCache.put(cacheKey.genOTPForgotPasswordKey(customerIdByOtpId), newOTP, 60);
    if (telegramService.sendOTP(user.getEmail(), user.getMainIdentifier(), user.getPhoneNumber(), String.valueOf(newOTP))) {
      response.setErrorCode(ErrorCode.FPT.MCAB_200);
      response.setData(newOTPId);
      String otpForgotPasswordAttemptCountKey = cacheKey.genOTPForgotPasswordAttemptCountKey(String.valueOf(user.getCustomerId()));
      remoteCache.put(otpForgotPasswordAttemptCountKey, "0");
      return response;
    } else {
      response.setErrorCode(ErrorCode.FPT.OTHER_OTHER);
    }
    return response;
  }

  @Transactional
  public FPTSendForgotPasswordLinkResponse sendForgotPasswordLink(FPTSendForgotPasswordLinkDto forgotPasswordLinkDto) {
    FPTSendForgotPasswordLinkResponse response = new FPTSendForgotPasswordLinkResponse();

    User user = userRepository.findByCustomerIdAndMainIdentifier(String.valueOf(forgotPasswordLinkDto.getCustomerId()), forgotPasswordLinkDto.getMainIdentifier());
    if (user == null) {
      response.setErrorCode(ErrorCode.FPT.NOT_FOUND);
      return response;
    }
    if (user.getState() != UserState.A) {
      response.setErrorCode(ErrorCode.FPT.LOGIN_ACCOUNT_INACTIVE);
    }
    String otpIdRedis = remoteCache.get(cacheKey.genOTPIdKey(user.getCustomerId()));
    String requestIdRedis = remoteCache.get(cacheKey.genRequestIdKey(user.getCustomerId()));
    if (!Objects.equals(otpIdRedis, forgotPasswordLinkDto.getOtpId()) || !Objects.equals(requestIdRedis, forgotPasswordLinkDto.getRequestId())) {
      response.setErrorCode(ErrorCode.FPT.INVALID_OTP_VALUE);
      return response;
    }
    if (telegramService.sendResetPassword(user.getEmail(), user.getMainIdentifier(), user.getPhoneNumber())) {
      UserPassword userPassword = userPasswordRepository.findUserPasswordByCustomerId(user.getCustomerId());
      if (userPassword == null) {
        userPassword = UserPassword.createFrom(user);
        userPasswordRepository.save(userPassword);
      } else {
        userPassword.setPassword("123456");
        userPasswordRepository.save(userPassword);
      }
      response.setErrorCode(ErrorCode.FPT.MCAB_200);
    } else {
      response.setErrorCode(ErrorCode.FPT.OTHER_OTHER);
    }
    return response;
  }


  public FPTConfirmOTPForgotPasswordResponse confirmOTPForgotPasswordResponse(
      @NonNull Long customerId,
      @NonNull String otpId,
      @NonNull String otpValue
  ) {
    FPTConfirmOTPForgotPasswordResponse response = new FPTConfirmOTPForgotPasswordResponse();
    User user = userRepository.findByCustomerId(String.valueOf(customerId));
    if (user == null) {
      response.setErrorCode(ErrorCode.FPT.OTHER_OTHER);
      return response;
    }

    String otpForgotPasswordAttemptCountKey = cacheKey.genOTPForgotPasswordAttemptCountKey(String.valueOf(customerId));
    int otpForgotPasswordAttemptCount = remoteCache.get(otpForgotPasswordAttemptCountKey) != null
        ? Integer.parseInt(remoteCache.get(otpForgotPasswordAttemptCountKey))
        : 0;
    if (otpForgotPasswordAttemptCount >= 4) {
      response.setErrorCode("OTP_EXCESS_INPUT");
      response.setErrorMessage("Bạn đã nhập quá số lần quy định!");
      return response;
    }
    String otpIdRedis = remoteCache.get(cacheKey.genOTPIdKey(String.valueOf(customerId)));
    String otpValueRedis = remoteCache.get(cacheKey.genOTPForgotPasswordKey(String.valueOf(customerId)));
    if (!Objects.equals(otpIdRedis, otpId) || !Objects.equals(otpValueRedis, otpValue)) {
      remoteCache.put(otpForgotPasswordAttemptCountKey, String.valueOf(otpForgotPasswordAttemptCount + 1));
      response.setErrorCode("OTP_INVALID_OTP");
      return response;
    }
    remoteCache.del(cacheKey.genOTPIdKey(String.valueOf(customerId)));
    remoteCache.del(cacheKey.genOTPForgotPasswordKey(String.valueOf(customerId)));
    response.setErrorCode(ErrorCode.FPT.MCAB_200);
    response.setErrorMessage("Successfully processed");
    remoteCache.put(otpForgotPasswordAttemptCountKey, "0");
    return response;
  }

  public FPTCustomerProfileResponse getCustomerProfile(String customerId) {
    User user = userStorage.findByCustomerId(customerId);
    if (user != null) {
      UserProfile userProfile = userProfileRepository.findCustomerProfileById(user.getId());
      if (userProfile != null)
        return FPTCustomerProfileResponse.simulatorByUserProfile(user, userProfile);
      else
        return FPTCustomerProfileResponse.simulatorByFaker(customerId);
    }
    return FPTCustomerProfileResponse.simulatorByFaker(customerId);
  }

  public FPTCustomerProfileResponse updateCustomerProfile(String customerId, FPTUpdateProfileDto updateProfile) {
    User user = userRepository.findByCustomerId(customerId);
    if (user != null) {
      FPTCustomerProfileResponse response = new FPTCustomerProfileResponse();
      User userIdentityCardNumber = userRepository.findByIdentityCardNumber(updateProfile.getIdentityCardNumber());
      if (!Objects.equals(userIdentityCardNumber.getId(), user.getId())) {
        response.setErrorCode("DUPLICATE_IDENTIFY_NUMBER");
        return response;
      }
      User userPhoneNumber = userRepository.findByPhoneNumber(Helper.convertMobile(updateProfile.getAddress().getPhoneCountryCode(), updateProfile.getAddress().getPhoneNumber()));
      if (!Objects.equals(userPhoneNumber.getId(), user.getId())) {
        response.setErrorCode("DUPLICATE_PHONE_NUMBER");
        return response;
      }
      User userByEmail = userRepository.findUserByEmail(updateProfile.getAddress().getEmail());
      if (!Objects.equals(userByEmail.getId(), user.getId())) {
        response.setErrorCode("DUPLICATE_EMAIL");
        return response;
      }
      UserProfile userProfile = userProfileRepository.findCustomerProfileById(user.getId());
      String title = userProfile.getTitle();
      String dateOfBirth = userProfile.getDateOfBirth();
      String localFullName = userProfile.getLocalFullName();
      userProfile = modelMapper.mapFromFPTUpdateProfileDto(updateProfile);
      userProfile.setId(user.getId());
      userProfile.setTitle(title);
      userProfile.setDateOfBirth(dateOfBirth);
      userProfile.setLocalFullName(localFullName);
//      userProfile = userProfileStorage.save(userProfile);

      user.updateUserProfileSimulator(updateProfile);
//      userStorage.save(user);
      response = FPTCustomerProfileResponse.simulatorByUserProfile(user, userProfile);
      return response;
    }
    return null;
  }

  public FPTFullEnrollDataResponse enrollment(FPTFullEnrollDataDto fullEnrollDataDto) {
    FPTFullEnrollDataResponse response = new FPTFullEnrollDataResponse();

    if (fullEnrollDataDto.getAddress().getPhoneNumber() == null
        || fullEnrollDataDto.getAddress().getPhoneNumber().length() > 20) {
      response.setErrorCode(ErrorCode.FPT.INVALID_PHONE_NUMBER);
      response.setErrorMessage("Invalid phone number");
      return response;
    }

    User user;
//    if (fullEnrollDataDto.getIdentityCardNumber() != null) {
//      user = userRepository.findByIdentityCardNumber(fullEnrollDataDto.getIdentityCardNumber());
//      if (user != null) {
//        response.setErrorCode(ErrorCode.FPT.DUPLICATE_CUSTOMER);
//        response.setErrorMessage("Membership information (identity card number) already exists. Please contact Lotusmiles for assistance.");
//        response.setData(user.getMainIdentifier());
//        return response;
//      }
//    }

    user = userRepository.findByPhoneNumber(fullEnrollDataDto.getAddress().getPhoneNumber());
    if (user != null) {
      response.setErrorCode(ErrorCode.FPT.DUPLICATE_CUSTOMER);
      response.setErrorMessage("Membership information (phone number) already exists. Please contact Lotusmiles for assistance.");
      response.setData(user.getMainIdentifier());
      return response;
    }

    user = userRepository.findUserByEmail(fullEnrollDataDto.getAddress().getEmail());
    if (user != null) {
      response.setErrorCode(ErrorCode.FPT.DUPLICATE_CUSTOMER);
      response.setErrorMessage("Membership information (email) already exists. Please contact Lotusmiles for assistance.");
      response.setData(user.getMainIdentifier());
      return response;
    }

    List<String> mainIdentifiers = userRepository.getMainIdentifiers();
    String newMainIdentifier;
    do {
      newMainIdentifier = String.valueOf(GeneratorUtil.genLongId());
    } while (mainIdentifiers.contains(newMainIdentifier));
    if (telegramService.sendEnrollment(fullEnrollDataDto.getAddress().getEmail(), fullEnrollDataDto.getAddress().getPhoneNumber())) {
      response.setErrorCode(ErrorCode.FPT.MCAB_200);
      response.setData(newMainIdentifier);
      response.setErrorMessage("");
    } else {
      response.setErrorCode(ErrorCode.FPT.OTHER_OTHER);
    }

    return response;
  }


  @Transactional(isolation = Isolation.SERIALIZABLE, propagation = Propagation.REQUIRED)
  public FPTLoginResponse login(FPTLoginCustomerDto fptLoginCustomerDto) {
    FPTLoginResponse response = new FPTLoginResponse();

    if (ErrorCode.FPT.INTERNAL_SERVER_ERROR_CODE.equals(fptLoginCustomerDto.getPassword())) {
      response.setErrorCode(ErrorCode.FPT.INTERNAL_SERVER_ERROR_CODE);
      return response;
    }
    if (ErrorCode.FPT.OTHER_OTHER.equals(fptLoginCustomerDto.getPassword())) {
      response.setErrorCode(ErrorCode.FPT.OTHER_OTHER);
      return response;
    }
    User user = userRepository.findByMainIdentifierOrPhoneNumberOrEmail(
        fptLoginCustomerDto.getLoginId(),
        fptLoginCustomerDto.getLoginId().startsWith("0") ? fptLoginCustomerDto.getLoginId().substring(1) : fptLoginCustomerDto.getLoginId(),
        fptLoginCustomerDto.getLoginId());
    response.setErrorCode(ErrorCode.FPT.MCAB_200);
    if (user != null) {
      response.setErrorMessage("User found");

      UserPassword userPassword = userPasswordRepository.findUserPasswordByUserId(user.getId());
      if (userPassword == null) {
        userPassword = UserPassword.createFrom(user);
        userPassword = userPasswordRepository.save(userPassword);
        boolean equals = fptLoginCustomerDto.getPassword().equals(userPassword.getPassword());
        if (!equals) {
          response.setErrorCode(ErrorCode.FPT.OTHER_OTHER);
          response.setErrorMessage("Invalid password");
          return response;
        }
      } else {
        boolean equals = fptLoginCustomerDto.getPassword().equals(userPassword.getPassword());
        if (!equals) {
          response.setErrorCode(ErrorCode.FPT.OTHER_OTHER);
          response.setErrorMessage("Invalid password");
          return response;
        }
      }

      FPTLoginResponse.Data data = new FPTLoginResponse.Data();
      data.setId(Long.valueOf(user.getCustomerId()));
      data.setStatus(user.getState());
      data.setFirstName(user.getFirstName());
      data.setLastName(user.getLastName());

      FPTLoginResponse.Data.Address address = new FPTLoginResponse.Data.Address();
      address.setEmail(user.getEmail());

      data.setAddress(address);
      response.setData(data);
    } else {
      response.setErrorCode(ErrorCode.FPT.OTHER_OTHER);
      response.setAttribute("Not found user");
      response.setErrorMessage("Not found user");
      return response;
    }
    return response;
  }

  public FPTCountryResponse getCountry() {
    FPTCountryResponse response = new FPTCountryResponse();
    List<FPTCountryResponse.Data> datList = Arrays.asList(
        new FPTCountryResponse.Data("US", "United States", null),
        new FPTCountryResponse.Data("FR", "France", null),
        new FPTCountryResponse.Data("JP", "Japan", null),
        new FPTCountryResponse.Data("VN", "Vietnam", null),
        new FPTCountryResponse.Data("CN", "China", null),
        new FPTCountryResponse.Data("IN", "India", null),
        new FPTCountryResponse.Data("BR", "Brazil", null),
        new FPTCountryResponse.Data("RU", "Russia", null),
        new FPTCountryResponse.Data("ZA", "South Africa", null),
        new FPTCountryResponse.Data("AU", "Australia", null),
        new FPTCountryResponse.Data("CA", "Canada", null),
        new FPTCountryResponse.Data("DE", "Germany", null)
    );
    response.setData(datList);
    response.setErrorCode(ErrorCode.FPT.MCAB_200);
    response.setErrorMessage("Simulator");
    return response;
  }

  public FPTCountryRegionResponse getCountryRegion(String countryCode) {
    FPTCountryRegionResponse response = new FPTCountryRegionResponse();
    List<FPTCountryRegionResponse.Data> dataList = new ArrayList<>();
    dataList.add(new FPTCountryRegionResponse.Data("AG", "An Giang", "VN"));
    dataList.add(new FPTCountryRegionResponse.Data("BV", "Ba Ria Vung Tau", "VN"));
    dataList.add(new FPTCountryRegionResponse.Data("BK", "Bac Kan", "VN"));
    dataList.add(new FPTCountryRegionResponse.Data("BG", "Bac Giang", "VN"));
    dataList.add(new FPTCountryRegionResponse.Data("BL", "Bac Lieu", "VN"));
    dataList.add(new FPTCountryRegionResponse.Data("BN", "Bac Ninh", "VN"));
    dataList.add(new FPTCountryRegionResponse.Data("BR", "Ben Tre", "VN"));
    dataList.add(new FPTCountryRegionResponse.Data("BD", "Binh Dinh", "VN"));
    dataList.add(new FPTCountryRegionResponse.Data("BI", "Binh Duong", "VN"));
    dataList.add(new FPTCountryRegionResponse.Data("BP", "Binh Phuoc", "VN"));
    dataList.add(new FPTCountryRegionResponse.Data("BU", "Binh Thuan", "VN"));
    dataList.add(new FPTCountryRegionResponse.Data("CM", "Ca Mau", "VN"));
    dataList.add(new FPTCountryRegionResponse.Data("CN", "Can Tho", "VN"));
    dataList.add(new FPTCountryRegionResponse.Data("CB", "Cao Bang", "VN"));
    dataList.add(new FPTCountryRegionResponse.Data("DA", "Da Nang", "VN"));
    dataList.add(new FPTCountryRegionResponse.Data("DC", "Dak Lak", "VN"));
    dataList.add(new FPTCountryRegionResponse.Data("DO", "Dak Nong", "VN"));
    dataList.add(new FPTCountryRegionResponse.Data("DB", "Dien Bien", "VN"));
    dataList.add(new FPTCountryRegionResponse.Data("DN", "Dong Nai", "VN"));
    dataList.add(new FPTCountryRegionResponse.Data("DT", "Dong Thap", "VN"));
    dataList.add(new FPTCountryRegionResponse.Data("GL", "Gia Lai", "VN"));
    dataList.add(new FPTCountryRegionResponse.Data("HG", "Ha Giang", "VN"));
    dataList.add(new FPTCountryRegionResponse.Data("HM", "Ha Nam", "VN"));
    dataList.add(new FPTCountryRegionResponse.Data("HN", "Ha Noi", "VN"));
    dataList.add(new FPTCountryRegionResponse.Data("HT", "Ha Tinh", "VN"));
    dataList.add(new FPTCountryRegionResponse.Data("HD", "Hai Duong", "VN"));
    dataList.add(new FPTCountryRegionResponse.Data("HP", "Hai Phong", "VN"));
    dataList.add(new FPTCountryRegionResponse.Data("HU", "Hau Giang", "VN"));
    dataList.add(new FPTCountryRegionResponse.Data("HC", "Ho Chi Minh", "VN"));
    dataList.add(new FPTCountryRegionResponse.Data("HO", "Hoa Binh", "VN"));
    dataList.add(new FPTCountryRegionResponse.Data("HY", "Hung Yen", "VN"));
    dataList.add(new FPTCountryRegionResponse.Data("KH", "Khanh Hoa", "VN"));
    dataList.add(new FPTCountryRegionResponse.Data("KG", "Kien Giang", "VN"));
    dataList.add(new FPTCountryRegionResponse.Data("KT", "Kon Tum", "VN"));
    dataList.add(new FPTCountryRegionResponse.Data("LI", "Lai Chau", "VN"));
    dataList.add(new FPTCountryRegionResponse.Data("LD", "Lam Dong", "VN"));
    dataList.add(new FPTCountryRegionResponse.Data("LS", "Lang Son", "VN"));
    dataList.add(new FPTCountryRegionResponse.Data("LO", "Lao Cai", "VN"));
    dataList.add(new FPTCountryRegionResponse.Data("LA", "Long An", "VN"));
    dataList.add(new FPTCountryRegionResponse.Data("ND", "Nam Dinh", "VN"));
    dataList.add(new FPTCountryRegionResponse.Data("NA", "Nghe An", "VN"));
    dataList.add(new FPTCountryRegionResponse.Data("NB", "Ninh Binh", "VN"));
    dataList.add(new FPTCountryRegionResponse.Data("NT", "Ninh Thuan", "VN"));
    dataList.add(new FPTCountryRegionResponse.Data("PT", "Phu Tho", "VN"));
    dataList.add(new FPTCountryRegionResponse.Data("PY", "Phu Yen", "VN"));
    dataList.add(new FPTCountryRegionResponse.Data("QB", "Quang Binh", "VN"));
    dataList.add(new FPTCountryRegionResponse.Data("QM", "Quang Nam", "VN"));
    dataList.add(new FPTCountryRegionResponse.Data("QG", "Quang Ngai", "VN"));
    dataList.add(new FPTCountryRegionResponse.Data("QN", "Quang Ninh", "VN"));
    dataList.add(new FPTCountryRegionResponse.Data("QT", "Quang Tri", "VN"));
    dataList.add(new FPTCountryRegionResponse.Data("QH", "Quy Nhon", "VN"));
    dataList.add(new FPTCountryRegionResponse.Data("ST", "Soc Trang", "VN"));
    dataList.add(new FPTCountryRegionResponse.Data("SL", "Son La", "VN"));
    dataList.add(new FPTCountryRegionResponse.Data("TN", "Tay Ninh", "VN"));
    dataList.add(new FPTCountryRegionResponse.Data("TB", "Thai Binh", "VN"));
    dataList.add(new FPTCountryRegionResponse.Data("TY", "Thai Nguyen", "VN"));
    dataList.add(new FPTCountryRegionResponse.Data("TH", "Thanh Hoa", "VN"));
    dataList.add(new FPTCountryRegionResponse.Data("TT", "Thua Thien Hue", "VN"));
    dataList.add(new FPTCountryRegionResponse.Data("TG", "Tien Giang", "VN"));
    dataList.add(new FPTCountryRegionResponse.Data("TV", "Tra Vinh", "VN"));
    dataList.add(new FPTCountryRegionResponse.Data("TQ", "Tuyen Quang", "VN"));
    dataList.add(new FPTCountryRegionResponse.Data("VL", "Vinh Long", "VN"));
    dataList.add(new FPTCountryRegionResponse.Data("VC", "Vinh Phuc", "VN"));
    dataList.add(new FPTCountryRegionResponse.Data("YB", "Yen Bai", "VN"));
    response.setData(dataList);
    response.setErrorCode(ErrorCode.FPT.MCAB_200);
    response.setErrorMessage("Simulator");
    return response;
  }


  public FPTMobileCountryCode getMobileCountryCode() {
    String phoneCodeSimulator = "[{\"phoneCode\":93,\"name\":\"AFGHANISTAN [93]\"},{\"phoneCode\":35818,\"name\":\"ALAND ISLANDS [35818]\"},{\"phoneCode\":355,\"name\":\"ALBANIA [355]\"},{\"phoneCode\":213,\"name\":\"ALGERIA [213]\"},{\"phoneCode\":1684,\"name\":\"AMERICAN SAMOA [1684]\"},{\"phoneCode\":376,\"name\":\"ANDORRA [376]\"},{\"phoneCode\":244,\"name\":\"ANGOLA [244]\"},{\"phoneCode\":1264,\"name\":\"ANGUILLA [1264]\"},{\"phoneCode\":1268,\"name\":\"ANTIGUA AND BARBUDA [1268]\"},{\"phoneCode\":54,\"name\":\"ARGENTINA [54]\"},{\"phoneCode\":374,\"name\":\"ARMENIA [374]\"},{\"phoneCode\":297,\"name\":\"ARUBA [297]\"},{\"phoneCode\":61,\"name\":\"AUSTRALIA & CHRISTMAS AND COCOS (KEELING) ISLANDS [61]\"},{\"phoneCode\":43,\"name\":\"AUSTRIA [43]\"},{\"phoneCode\":994,\"name\":\"AZERBAIJAN [994]\"},{\"phoneCode\":1242,\"name\":\"BAHAMAS [1242]\"},{\"phoneCode\":973,\"name\":\"BAHRAIN [973]\"},{\"phoneCode\":880,\"name\":\"BANGLADESH [880]\"},{\"phoneCode\":1246,\"name\":\"BARBADOS [1246]\"},{\"phoneCode\":375,\"name\":\"BELARUS [375]\"},{\"phoneCode\":32,\"name\":\"BELGIUM [32]\"},{\"phoneCode\":501,\"name\":\"BELIZE [501]\"},{\"phoneCode\":229,\"name\":\"BENIN [229]\"},{\"phoneCode\":1441,\"name\":\"BERMUDA [1441]\"},{\"phoneCode\":975,\"name\":\"BHUTAN [975]\"},{\"phoneCode\":591,\"name\":\"BOLIVIA [591]\"},{\"phoneCode\":387,\"name\":\"BOSNIA AND HERZEGOVINA [387]\"},{\"phoneCode\":267,\"name\":\"BOTSWANA [267]\"},{\"phoneCode\":55,\"name\":\"BRAZIL [55]\"},{\"phoneCode\":246,\"name\":\"BRITISH INDIAN OCEAN TERRITORY [246]\"},{\"phoneCode\":673,\"name\":\"BRUNEI DARUSSALAM [673]\"},{\"phoneCode\":359,\"name\":\"BULGARIA [359]\"},{\"phoneCode\":226,\"name\":\"BURKINA FASO [226]\"},{\"phoneCode\":257,\"name\":\"BURUNDI [257]\"},{\"phoneCode\":855,\"name\":\"CAMBODIA [855]\"},{\"phoneCode\":237,\"name\":\"CAMEROON [237]\"},{\"phoneCode\":1,\"name\":\"CANADA & USA & US MINOR OUTLYING ISLANDS [1]\"},{\"phoneCode\":238,\"name\":\"CAPE VERDE [238]\"},{\"phoneCode\":1345,\"name\":\"CAYMAN ISLANDS [1345]\"},{\"phoneCode\":236,\"name\":\"CENTRAL AFRICAN REPUBLIC [236]\"},{\"phoneCode\":235,\"name\":\"CHAD [235]\"},{\"phoneCode\":56,\"name\":\"CHILE [56]\"},{\"phoneCode\":86,\"name\":\"CHINA [86]\"},{\"phoneCode\":57,\"name\":\"COLOMBIA [57]\"},{\"phoneCode\":269,\"name\":\"COMOROS [269]\"},{\"phoneCode\":242,\"name\":\"CONGO [242]\"},{\"phoneCode\":682,\"name\":\"COOK ISLANDS [682]\"},{\"phoneCode\":506,\"name\":\"COSTA RICA [506]\"},{\"phoneCode\":385,\"name\":\"CROATIA [385]\"},{\"phoneCode\":53,\"name\":\"CUBA [53]\"},{\"phoneCode\":599,\"name\":\"CURACAO & NETHERLANDS ANTILLES & BONAIRE ST EUSTATIUS & SABA [599]\"},{\"phoneCode\":357,\"name\":\"CYPRUS [357]\"},{\"phoneCode\":420,\"name\":\"CZECH REPUBLIC [420]\"},{\"phoneCode\":850,\"name\":\"DEMOCRATIC PEOPLES REPUBLIC OF KOREA [850]\"},{\"phoneCode\":243,\"name\":\"DEMOCRATIC REPUBLIC OF THE CONGO [243]\"},{\"phoneCode\":45,\"name\":\"DENMARK [45]\"},{\"phoneCode\":253,\"name\":\"DJIBOUTI [253]\"},{\"phoneCode\":1767,\"name\":\"DOMINICA [1767]\"},{\"phoneCode\":1809,\"name\":\"DOMINICAN REPUBLIC [1809]\"},{\"phoneCode\":1829,\"name\":\"DOMINICAN REPUBLIC [1829]\"},{\"phoneCode\":1849,\"name\":\"DOMINICAN REPUBLIC [1849]\"},{\"phoneCode\":593,\"name\":\"ECUADOR [593]\"},{\"phoneCode\":20,\"name\":\"EGYPT [20]\"},{\"phoneCode\":503,\"name\":\"EL SALVADOR [503]\"},{\"phoneCode\":240,\"name\":\"EQUATORIAL GUINEA [240]\"},{\"phoneCode\":291,\"name\":\"ERITREA [291]\"},{\"phoneCode\":372,\"name\":\"ESTONIA [372]\"},{\"phoneCode\":251,\"name\":\"ETHIOPIA [251]\"},{\"phoneCode\":500,\"name\":\"FALKLAND ISLANDS (MALVINAS) & SOUTH GEORGIA AND SOUTH SANDWICH IS. [500]\"},{\"phoneCode\":298,\"name\":\"FAROE ISLANDS [298]\"},{\"phoneCode\":679,\"name\":\"FIJI [679]\"},{\"phoneCode\":358,\"name\":\"FINLAND [358]\"},{\"phoneCode\":33,\"name\":\"FRANCE [33]\"},{\"phoneCode\":594,\"name\":\"FRENCH GUIANA [594]\"},{\"phoneCode\":689,\"name\":\"FRENCH POLYNESIA [689]\"},{\"phoneCode\":241,\"name\":\"GABON [241]\"},{\"phoneCode\":220,\"name\":\"GAMBIA [220]\"},{\"phoneCode\":995,\"name\":\"GEORGIA [995]\"},{\"phoneCode\":49,\"name\":\"GERMANY [49]\"},{\"phoneCode\":233,\"name\":\"GHANA [233]\"},{\"phoneCode\":350,\"name\":\"GIBRALTAR [350]\"},{\"phoneCode\":30,\"name\":\"GREECE [30]\"},{\"phoneCode\":299,\"name\":\"GREENLAND [299]\"},{\"phoneCode\":1473,\"name\":\"GRENADA [1473]\"},{\"phoneCode\":590,\"name\":\"GUADELOUPE & ST MARTIN (FRENCH PART) & ST BARTHELEMY [590]\"},{\"phoneCode\":1671,\"name\":\"GUAM [1671]\"},{\"phoneCode\":502,\"name\":\"GUATEMALA [502]\"},{\"phoneCode\":224,\"name\":\"GUINEA [224]\"},{\"phoneCode\":245,\"name\":\"GUINEA-BISSAU [245]\"},{\"phoneCode\":592,\"name\":\"GUYANA [592]\"},{\"phoneCode\":509,\"name\":\"HAITI [509]\"},{\"phoneCode\":504,\"name\":\"HONDURAS [504]\"},{\"phoneCode\":852,\"name\":\"HONG KONG SPECIAL ADMINISTRATIVE REGION OF CHINA [852]\"},{\"phoneCode\":36,\"name\":\"HUNGARY [36]\"},{\"phoneCode\":354,\"name\":\"ICELAND [354]\"},{\"phoneCode\":91,\"name\":\"INDIA [91]\"},{\"phoneCode\":62,\"name\":\"INDONESIA [62]\"},{\"phoneCode\":98,\"name\":\"IRAN (ISLAMIC REPUBLIC OF) [98]\"},{\"phoneCode\":964,\"name\":\"IRAQ [964]\"},{\"phoneCode\":353,\"name\":\"IRELAND [353]\"},{\"phoneCode\":972,\"name\":\"ISRAEL [972]\"},{\"phoneCode\":39,\"name\":\"ITALY [39]\"},{\"phoneCode\":225,\"name\":\"IVORY COAST [225]\"},{\"phoneCode\":1876,\"name\":\"JAMAICA [1876]\"},{\"phoneCode\":81,\"name\":\"JAPAN [81]\"},{\"phoneCode\":962,\"name\":\"JORDAN [962]\"},{\"phoneCode\":7,\"name\":\"KAZAKHSTAN & RUSSIAN FEDERATION [7]\"},{\"phoneCode\":254,\"name\":\"KENYA [254]\"},{\"phoneCode\":686,\"name\":\"KIRIBATI [686]\"},{\"phoneCode\":965,\"name\":\"KUWAIT [965]\"},{\"phoneCode\":996,\"name\":\"KYRGYZSTAN [996]\"},{\"phoneCode\":856,\"name\":\"LAO PEOPLES DEMOCRATIC REPUBLIC [856]\"},{\"phoneCode\":371,\"name\":\"LATVIA [371]\"},{\"phoneCode\":961,\"name\":\"LEBANON [961]\"},{\"phoneCode\":266,\"name\":\"LESOTHO [266]\"},{\"phoneCode\":231,\"name\":\"LIBERIA [231]\"},{\"phoneCode\":218,\"name\":\"LIBYAN ARAB JAMAHIRIYA [218]\"},{\"phoneCode\":423,\"name\":\"LIECHTENSTEIN [423]\"},{\"phoneCode\":370,\"name\":\"LITHUANIA [370]\"},{\"phoneCode\":352,\"name\":\"LUXEMBOURG [352]\"},{\"phoneCode\":853,\"name\":\"MACAO SPECIAL ADMINISTRATIVE REGION OF CHINA [853]\"},{\"phoneCode\":261,\"name\":\"MADAGASCAR [261]\"},{\"phoneCode\":265,\"name\":\"MALAWI [265]\"},{\"phoneCode\":60,\"name\":\"MALAYSIA [60]\"},{\"phoneCode\":960,\"name\":\"MALDIVES [960]\"},{\"phoneCode\":223,\"name\":\"MALI [223]\"},{\"phoneCode\":356,\"name\":\"MALTA [356]\"},{\"phoneCode\":692,\"name\":\"MARSHALL ISLANDS [692]\"},{\"phoneCode\":596,\"name\":\"MARTINIQUE [596]\"},{\"phoneCode\":222,\"name\":\"MAURITANIA [222]\"},{\"phoneCode\":230,\"name\":\"MAURITIUS [230]\"},{\"phoneCode\":52,\"name\":\"MEXICO [52]\"},{\"phoneCode\":691,\"name\":\"MICRONESIA (FEDERATED STATES OF) [691]\"},{\"phoneCode\":373,\"name\":\"MOLDOVA [373]\"},{\"phoneCode\":377,\"name\":\"MONACO [377]\"},{\"phoneCode\":976,\"name\":\"MONGOLIA [976]\"},{\"phoneCode\":382,\"name\":\"MONTENEGRO [382]\"},{\"phoneCode\":1664,\"name\":\"MONTSERRAT [1664]\"},{\"phoneCode\":212,\"name\":\"MOROCCO & WESTERN SAHARA [212]\"},{\"phoneCode\":258,\"name\":\"MOZAMBIQUE [258]\"},{\"phoneCode\":95,\"name\":\"MYANMAR [95]\"},{\"phoneCode\":264,\"name\":\"NAMIBIA [264]\"},{\"phoneCode\":674,\"name\":\"NAURU [674]\"},{\"phoneCode\":977,\"name\":\"NEPAL [977]\"},{\"phoneCode\":31,\"name\":\"NETHERLANDS [31]\"},{\"phoneCode\":687,\"name\":\"NEW CALEDONIA [687]\"},{\"phoneCode\":64,\"name\":\"NEW ZEALAND & PITCAIRN [64]\"},{\"phoneCode\":505,\"name\":\"NICARAGUA [505]\"},{\"phoneCode\":227,\"name\":\"NIGER [227]\"},{\"phoneCode\":234,\"name\":\"NIGERIA [234]\"},{\"phoneCode\":683,\"name\":\"NIUE [683]\"},{\"phoneCode\":672,\"name\":\"NORFOLK ISLAND & ANTARCTICA [672]\"},{\"phoneCode\":1670,\"name\":\"NORTHERN MARIANA ISLANDS [1670]\"},{\"phoneCode\":47,\"name\":\"NORWAY & SVALBARD AND JAN MAYEN ISLANDS [47]\"},{\"phoneCode\":970,\"name\":\"OCCUPIED PALESTINIAN TERRITORY [970]\"},{\"phoneCode\":968,\"name\":\"OMAN [968]\"},{\"phoneCode\":92,\"name\":\"PAKISTAN [92]\"},{\"phoneCode\":680,\"name\":\"PALAU [680]\"},{\"phoneCode\":507,\"name\":\"PANAMA [507]\"},{\"phoneCode\":675,\"name\":\"PAPUA NEW GUINEA [675]\"},{\"phoneCode\":595,\"name\":\"PARAGUAY [595]\"},{\"phoneCode\":51,\"name\":\"PERU [51]\"},{\"phoneCode\":63,\"name\":\"PHILIPPINES [63]\"},{\"phoneCode\":48,\"name\":\"POLAND [48]\"},{\"phoneCode\":351,\"name\":\"PORTUGAL [351]\"},{\"phoneCode\":1787,\"name\":\"PUERTO RICO [1787]\"},{\"phoneCode\":1939,\"name\":\"PUERTO RICO [1939]\"},{\"phoneCode\":974,\"name\":\"QATAR [974]\"},{\"phoneCode\":82,\"name\":\"REPUBLIC OF KOREA [82]\"},{\"phoneCode\":262,\"name\":\"REUNION & MAYOTTE & FRENCH SOUTHERN TERRITORIES - TF [262]\"},{\"phoneCode\":40,\"name\":\"ROMANIA [40]\"},{\"phoneCode\":250,\"name\":\"RWANDA [250]\"},{\"phoneCode\":290,\"name\":\"SAINT HELENA [290]\"},{\"phoneCode\":1869,\"name\":\"SAINT KITTS AND NEVIS [1869]\"},{\"phoneCode\":1758,\"name\":\"SAINT LUCIA [1758]\"},{\"phoneCode\":508,\"name\":\"SAINT PIERRE AND MIQUELON [508]\"},{\"phoneCode\":1784,\"name\":\"SAINT VINCENT AND THE GRENADINES [1784]\"},{\"phoneCode\":685,\"name\":\"SAMOA [685]\"},{\"phoneCode\":378,\"name\":\"SAN MARINO [378]\"},{\"phoneCode\":239,\"name\":\"SAO TOME AND PRINCIPE [239]\"},{\"phoneCode\":966,\"name\":\"SAUDI ARABIA [966]\"},{\"phoneCode\":221,\"name\":\"SENEGAL [221]\"},{\"phoneCode\":381,\"name\":\"SERBIA [381]\"},{\"phoneCode\":248,\"name\":\"SEYCHELLES [248]\"},{\"phoneCode\":232,\"name\":\"SIERRA LEONE [232]\"},{\"phoneCode\":65,\"name\":\"SINGAPORE [65]\"},{\"phoneCode\":1721,\"name\":\"SINT MAARTEN (DUTCH PART) [1721]\"},{\"phoneCode\":421,\"name\":\"SLOVAKIA [421]\"},{\"phoneCode\":386,\"name\":\"SLOVENIA [386]\"},{\"phoneCode\":677,\"name\":\"SOLOMON ISLANDS [677]\"},{\"phoneCode\":252,\"name\":\"SOMALIA [252]\"},{\"phoneCode\":27,\"name\":\"SOUTH AFRICA [27]\"},{\"phoneCode\":211,\"name\":\"SOUTH SUDAN [211]\"},{\"phoneCode\":34,\"name\":\"SPAIN [34]\"},{\"phoneCode\":94,\"name\":\"SRI LANKA [94]\"},{\"phoneCode\":249,\"name\":\"SUDAN [249]\"},{\"phoneCode\":597,\"name\":\"SURIname [597]\"},{\"phoneCode\":268,\"name\":\"SWAZILAND [268]\"},{\"phoneCode\":46,\"name\":\"SWEDEN [46]\"},{\"phoneCode\":41,\"name\":\"SWITZERLAND [41]\"},{\"phoneCode\":963,\"name\":\"SYRIAN ARAB REPUBLIC [963]\"},{\"phoneCode\":886,\"name\":\"TAIWAN (PROVINCE OF CHINA) [886]\"},{\"phoneCode\":992,\"name\":\"TAJIKISTAN [992]\"},{\"phoneCode\":66,\"name\":\"THAILAND [66]\"},{\"phoneCode\":389,\"name\":\"THE FORMER YUGOSLAV REPUBLIC OF MACEDONIA [389]\"},{\"phoneCode\":670,\"name\":\"TIMOR-LESTE [670]\"},{\"phoneCode\":228,\"name\":\"TOGO [228]\"},{\"phoneCode\":690,\"name\":\"TOKELAU [690]\"},{\"phoneCode\":676,\"name\":\"TONGA [676]\"},{\"phoneCode\":1868,\"name\":\"TRINIDAD AND TOBAGO [1868]\"},{\"phoneCode\":216,\"name\":\"TUNISIA [216]\"},{\"phoneCode\":90,\"name\":\"TURKEY [90]\"},{\"phoneCode\":993,\"name\":\"TURKMENISTAN [993]\"},{\"phoneCode\":1649,\"name\":\"TURKS AND CAICOS ISLANDS [1649]\"},{\"phoneCode\":688,\"name\":\"TUVALU [688]\"},{\"phoneCode\":256,\"name\":\"UGANDA [256]\"},{\"phoneCode\":380,\"name\":\"UKRAINE [380]\"},{\"phoneCode\":971,\"name\":\"UNITED ARAB EMIRATES [971]\"},{\"phoneCode\":44,\"name\":\"UNITED KINGDOM OF GREAT BRITAIN & CHANNEL ISLANDS [44]\"},{\"phoneCode\":255,\"name\":\"UNITED REPUBLIC OF TANZANIA [255]\"},{\"phoneCode\":598,\"name\":\"URUGUAY [598]\"},{\"phoneCode\":998,\"name\":\"UZBEKISTAN [998]\"},{\"phoneCode\":678,\"name\":\"VANUATU [678]\"},{\"phoneCode\":58,\"name\":\"VENEZUELA [58]\"},{\"phoneCode\":84,\"name\":\"VIET NAM [84]\"},{\"phoneCode\":1284,\"name\":\"VIRGIN ISLANDS (BRITISH) [1284]\"},{\"phoneCode\":1340,\"name\":\"VIRGIN ISLANDS (UNITED STATES) [1340]\"},{\"phoneCode\":681,\"name\":\"WALLIS AND FUTUNA ISLANDS [681]\"},{\"phoneCode\":967,\"name\":\"YEMEN [967]\"},{\"phoneCode\":260,\"name\":\"ZAMBIA [260]\"},{\"phoneCode\":263,\"name\":\"ZIMBABWE [263]\"}]";
    ArrayList<PhoneCode> phoneCodes = JsonParser.arrayList(phoneCodeSimulator, PhoneCode.class);
    FPTMobileCountryCode response = new FPTMobileCountryCode();
    List<FPTMobileCountryCode.Data> data = modelMapper.mapFromListConfigToListData(phoneCodes);
    response.setData(data);
    response.setErrorCode(ErrorCode.FPT.MCAB_200);
    return response;
  }

  public ExternalService.FPTTokenResponse genSimulatorToken() {
    Map<String, Object> claims = new HashMap<>();
    claims.put("partner", "WIIN");
    claims.put("client_id", "wiinvent");
    long expiresIn = System.currentTimeMillis() + 7200 * 1000;
    String accessToken = Jwts.builder()
        .setClaims(claims)
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(expiresIn))
        .signWith(SignatureAlgorithm.HS512, userPrivateKey)
        .compact();
    ExternalService.FPTTokenResponse response = new ExternalService.FPTTokenResponse();
    response.setAccessToken(accessToken);
    response.setTokenType("Basic");
    response.setExpiresIn(expiresIn);
    return response;
  }

  public String findCustomerIdByRequestId(String requestId) {
    String prefixPattern = redisPrefixKey + ":simulator:requestId:key*";

    Set<String> keys = redisTemplate.keys(prefixPattern);

    if (keys == null || keys.isEmpty()) {
      return null;
    }

    for (String key : keys) {
      String value = redisTemplate.opsForValue().get(key);
      assert value != null;
      if (value.equals(requestId)) {
        return key.substring(key.lastIndexOf(":") + 1);
      }
    }
    return null;
  }

  public String findCustomerIdByOtpId(String otpId) {
    String prefixPattern = redisPrefixKey + ":simulator:otpId:key*";
    Set<String> keys = redisTemplate.keys(prefixPattern);
    if (keys == null || keys.isEmpty()) {
      return null;
    }
    for (String key : keys) {
      String value = redisTemplate.opsForValue().get(key);
      assert value != null;
      if (value.equals(otpId)) {
        return key.substring(key.lastIndexOf(":") + 1);
      }
    }
    return null;
  }

  public String findTransactionTypeByOTP(Long customerId, String otpId) {
    String prefixPattern = redisPrefixKey + ":simulator:otpId:key:" + customerId + "*";

    Set<String> keys = redisTemplate.keys(prefixPattern);

    if (keys == null || keys.isEmpty()) {
      return null;
    }

    for (String key : keys) {
      String val = redisTemplate.opsForValue().get(key);
      assert val != null;
      if (val.equals(otpId)) {
        return key.substring(key.lastIndexOf(":") + 1);
      }
    }
    return null;
  }


  @NoArgsConstructor
  @Data
  public static class PhoneCode {
    private String name;
    private String phoneCode;
  }
}
