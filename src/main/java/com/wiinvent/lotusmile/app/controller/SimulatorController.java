package com.wiinvent.lotusmile.app.controller;

import com.wiinvent.lotusmile.app.dto.*;
import com.wiinvent.lotusmile.app.response.fpt.*;
import com.wiinvent.lotusmile.domain.service.SimulatorService;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "v1/customer/auth/simulator")
@Hidden
public class SimulatorController {

  @Autowired
  SimulatorService simulatorService;


  @PostMapping("sendMessage")
  public ResponseEntity<FPTSendMessageResponse> sendMessage(
      @RequestHeader(name = "lang", required = false) String lang,
      @RequestHeader(name = "ipAddress", required = false) String ipAddress,
      @RequestHeader(name = "idDevice", required = false) String idDevice,
      @RequestHeader(name = "Authorization") String authorization,
      @RequestBody @Valid FPTSendMessageDto fptSendMessageDto
  ) {
    return ResponseEntity.ok(simulatorService.sendMessage(fptSendMessageDto));
  }

  @PostMapping("customer/getForgotPasswordToken")
  public ResponseEntity<FPTForgotPasswordTokenResponse> getForgotPasswordToken(
      @RequestHeader(name = "lang", required = false) String lang,
      @RequestHeader(name = "ipAddress") String ipAddress,
//      @RequestHeader(name = "authorization") String authorization,
      @RequestBody @Valid FPTForgotPasswordTokenDto fptForgotPasswordTokenDto
  ) {
    return ResponseEntity.ok(simulatorService.getForgotPasswordToken(fptForgotPasswordTokenDto));
  }

  @PostMapping("otp/{customerId}/confirm/{otpId}/{otpValue}")
  public ResponseEntity<FPTConfirmOTPForgotPasswordResponse> confirmForgotPasswordOTP(
      @RequestHeader(name = "lang", required = false) String lang,
      @RequestHeader(name = "ipAddress") String ipAddress,
      @PathVariable Long customerId,
      @PathVariable String otpId,
      @PathVariable String otpValue) {
    return ResponseEntity.ok(simulatorService.confirmOTPForgotPasswordResponse(customerId, otpId, otpValue));
  }

  @PostMapping("customer/sendForgotPasswordLink")
  public ResponseEntity<FPTSendForgotPasswordLinkResponse> sendForgotPasswordLink(
      @RequestHeader(name = "lang", required = false) String lang,
      @RequestHeader(name = "ipAddress") String ipAddress,
      @RequestBody @Valid FPTSendForgotPasswordLinkDto linkDto
  ) {
    return ResponseEntity.ok(simulatorService.sendForgotPasswordLink(linkDto));
  }

  @PostMapping("/customer/resendForgotPassOtp/{requestId}/{otpId}")
  public ResponseEntity<FPTResendForgotPassOtpResponse> resendForgotPassOtp(
      @RequestHeader(name = "lang", required = false) String lang,
      @RequestHeader(name = "ipAddress") String ipAddress,
      @PathVariable String otpId,
      @PathVariable String requestId
  ) {
    return ResponseEntity.ok(simulatorService.resendForgotPassOtpResponse(requestId, otpId));
  }

  @GetMapping("customer/no={identifierNo}/account-summary")
  public ResponseEntity<FPTAccountSummaryResponse> getAccountSummary(
      @RequestHeader(name = "lang", required = false) String lang,
      @RequestHeader(name = "ipAddress", required = false) String ipAddress,
      @RequestHeader(name = "idDevice", required = false) String idDevice,
//      @RequestHeader(name = "authorization") String authorization,
      @PathVariable String identifierNo) {
    return ResponseEntity.ok(simulatorService.getAccountSummaryResponse(identifierNo));
  }

  @PostMapping("customer/{customerId}/changePasswordByCustomerId")
  public ResponseEntity<FPTChangePasswordResponse> changePasswordByCustomerId(
      @RequestHeader(name = "lang", required = false) String lang,
      @RequestHeader(name = "ipAddress") String ipAddress,
      @RequestHeader(name = "idDevice", required = false) String idDevice,
      @RequestHeader(name = "Authorization") String authorization,
      @RequestParam(name = "otpId") String otpId,
      @PathVariable Long customerId,
      @RequestBody @Valid FPTChangePasswordDto FPTChangePasswordDto
  ) {
    return ResponseEntity.ok(simulatorService.changePassword(customerId, otpId, FPTChangePasswordDto));
  }

  @GetMapping("customer/{customerId}/activity-history/v3")
  public ResponseEntity<FPTActivityHistoryResponse> getActivityHistory(
      @RequestHeader(name = "lang", required = false) String lang,
      @RequestHeader(name = "ipAddress", required = false) String ipAddress,
      @RequestHeader(name = "idDevice", required = false) String idDevice,
      @RequestHeader(name = "Authorization") String authorization,
      @RequestParam(required = false) String fromDate,
      @RequestParam(required = false) String toDate,
      @RequestParam(required = false) Integer firstResult,
      @RequestParam(required = false) Integer maxResults,
      @PathVariable Long customerId
  ) {
    return ResponseEntity.ok(simulatorService.getActivityHistory(customerId, fromDate, toDate, firstResult, maxResults));
  }


  @PostMapping("otp/{customerId}/request/{transactionType}")
  public ResponseEntity<FPTOTPResponse> getOTP(
      @RequestHeader(name = "lang", required = false) String lang,
      @RequestHeader(name = "ipAddress") String ipAddress,
      @RequestHeader(name = "idDevice", required = false) String idDevice,
      @RequestHeader(name = "Authorization") String authorization,
      @PathVariable Long customerId,
      @PathVariable TransactionType transactionType) {
    return ResponseEntity.ok(simulatorService.getOTP(customerId, transactionType));
  }

  @PostMapping("otp/{customerId}/resend/{otpId}")
  public ResponseEntity<FPTOTPResponse> resetOTP(
      @RequestHeader(name = "lang", required = false) String lang,
      @RequestHeader(name = "ipAddress") String ipAddress,
      @RequestHeader(name = "idDevice", required = false) String idDevice,
      @RequestHeader(name = "Authorization") String authorization,
      @PathVariable Long customerId,
      @PathVariable String otpId
  ) {
    return ResponseEntity.ok(simulatorService.resetOTP(customerId, otpId));
  }

  @GetMapping("customer/{customerId}/profile")
  public ResponseEntity<FPTCustomerProfileResponse> getCustomerProfile(
      @RequestHeader(name = "lang", required = false) String lang,
      @RequestHeader(name = "ipAddress", required = false) String ipAddress,
      @RequestHeader(name = "idDevice", required = false) String idDevice,
      @PathVariable String customerId) {
    return ResponseEntity.ok(simulatorService.getCustomerProfile(customerId));
  }

  @PutMapping("customer/{customerId}/profile")
  public ResponseEntity<FPTCustomerProfileResponse> updateCustomerProfile(
      @RequestHeader(name = "lang", required = false) String lang,
      @RequestHeader(name = "ipAddress", required = false) String ipAddress,
      @RequestHeader(name = "idDevice", required = false) String idDevice,
      @PathVariable String customerId,
      @RequestParam String otpId,
      @RequestBody FPTUpdateProfileDto FPTUpdateProfileDto
  ) {
    return ResponseEntity.ok(simulatorService.updateCustomerProfile(customerId, FPTUpdateProfileDto));
  }

  @GetMapping("promotion/customer-promotion-codes")
  public ResponseEntity<FPTPromotionCodeResponse> getPromotionCodes(
      @RequestHeader(name = "lang", required = false) String lang,
      @RequestHeader(name = "ipAddress", required = false) String ipAddress,
      @RequestParam String identifierNo
  ) {
    return ResponseEntity.ok(simulatorService.getPromotionCode(identifierNo));
  }

  @GetMapping("customer/{customerId}/avatar")
  public ResponseEntity<FPTAvatarResponse> getAvatar(
      @RequestHeader(name = "lang", required = false) String lang,
      @RequestHeader(name = "Authorization") String authorization,
      @PathVariable Long customerId
  ) {
    // TODO
    return null;
  }

  @GetMapping("/getDictionary/EXT_MOBILE_COUNTRY_CODE")
  public ResponseEntity<FPTMobileCountryCode> getMobileCountryCode(
      @RequestHeader(name = "lang", required = false) String lang,
      @RequestHeader(name = "ipAddress", required = false) String ipAddress,
      @RequestHeader(name = "idDevice", required = false) String idDevice,
      @RequestHeader(name = "Authorization") String authorization
  ) {
    return ResponseEntity.ok(simulatorService.getMobileCountryCode());
  }


  @GetMapping("getCountries")
  public ResponseEntity<FPTCountryResponse> getCountries(
      @RequestHeader(name = "lang", required = false) String lang,
      @RequestHeader(name = "ipAddress", required = false) String ipAddress,
      @RequestHeader(name = "idDevice", required = false) String idDevice,
      @RequestHeader(name = "Authorization") String authorization
  ) {
    return ResponseEntity.ok(simulatorService.getCountry());
  }

  @GetMapping("getCountryRegions")
  public ResponseEntity<FPTCountryRegionResponse> getCountryRegions(
      @RequestHeader(name = "lang", required = false) String lang,
      @RequestHeader(name = "ipAddress") String ipAddress,
      @RequestHeader(name = "idDevice", required = false) String idDevice,
      @RequestHeader(name = "Authorization") String authorization,
      @RequestParam String countryCode
  ) {
    return ResponseEntity.ok(simulatorService.getCountryRegion(countryCode));
  }


  @PostMapping("enrollment")
  public ResponseEntity<FPTFullEnrollDataResponse> enrollment(
      @RequestHeader(name = "lang", required = false) String lang,
      @RequestHeader(name = "ipAddress") String ipAddress,
      @RequestHeader(name = "idDevice", required = false) String idDevice,
      @RequestHeader(name = "Authorization") String authorization,
      @RequestBody @Valid FPTFullEnrollDataDto fullEnrollDataDto) {
    return ResponseEntity.ok(simulatorService.enrollment(fullEnrollDataDto));
  }

  @PostMapping("customer/login")
  public ResponseEntity<FPTLoginResponse> login(
      @RequestHeader(name = "lang", required = false) String lang,
      @RequestHeader(name = "ipAddress") String ipAddress,
      @RequestHeader(name = "idDevice", required = false) String idDevice,
      @RequestHeader(name = "Authorization") String authorization,
      @RequestBody @Valid FPTLoginCustomerDto FPTLoginCustomerDto) {
    return ResponseEntity.ok(simulatorService.login(FPTLoginCustomerDto));
  }

  @PostMapping("/authenticate")
  public ResponseEntity<ExternalService.FPTTokenResponse> authenticate() {
    return ResponseEntity.ok(simulatorService.genSimulatorToken());
  }

}
