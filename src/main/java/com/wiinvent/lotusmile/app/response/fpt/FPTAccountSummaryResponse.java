package com.wiinvent.lotusmile.app.response.fpt;

import com.wiinvent.lotusmile.domain.entity.types.NameOrdering;
import com.wiinvent.lotusmile.domain.entity.types.TierCode;
import com.wiinvent.lotusmile.domain.exception.ErrorCode;
import com.wiinvent.lotusmile.domain.util.JsonParser;
import jakarta.annotation.Nullable;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.wiinvent.lotusmile.domain.util.Helper.convertMobile;
import static com.wiinvent.lotusmile.domain.util.Helper.generateRandomLong;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@ToString(callSuper = true)
@Getter
public class FPTAccountSummaryResponse extends BaseFPTResponse {


  private Data data;

  @Deprecated(forRemoval = true)
  public static void fakeForSpecificUser(User user, FPTAccountSummaryResponse accountSummaryResponse) {
    if (Objects.equals(user.getPhoneNumber(), "84383883826")) {
      // register
      accountSummaryResponse.getData().setAvailableBonusMiles(0L);
      accountSummaryResponse.getData().setTotalQualifyingSegments(0D);
      accountSummaryResponse.getData().setCurrentTier("Đăng ký");
      accountSummaryResponse.getData().setCurrentTierCode(TierCode.R);
    }
    if (Objects.equals(user.getPhoneNumber(), "84988654321")) {
      accountSummaryResponse.getData().setAvailableBonusMiles(2L);
      accountSummaryResponse.getData().setTotalQualifyingSegments(3D);
      accountSummaryResponse.getData().setCurrentTier("Bạc");
      accountSummaryResponse.getData().setCurrentTierCode(TierCode.S);
    }
    if (Objects.equals(user.getPhoneNumber(), "84369512340")) {
      accountSummaryResponse.getData().setAvailableBonusMiles(20000L);
      accountSummaryResponse.getData().setTotalQualifyingSegments(20D);
      accountSummaryResponse.getData().setCurrentTier("Titan");
      accountSummaryResponse.getData().setCurrentTierCode(TierCode.T);
    }
    if (Objects.equals(user.getPhoneNumber(), "84978030258")) {
      accountSummaryResponse.getData().setAvailableBonusMiles(40000L);
      accountSummaryResponse.getData().setTotalQualifyingSegments(40D);
      accountSummaryResponse.getData().setCurrentTier("Vàng");
      accountSummaryResponse.getData().setCurrentTierCode(TierCode.G);
    }
    if (Objects.equals(user.getPhoneNumber(), "84383883827")) {
      accountSummaryResponse.getData().setAvailableBonusMiles(50000L);
      accountSummaryResponse.getData().setTotalQualifyingSegments(45D);
      accountSummaryResponse.getData().setCurrentTier("Bạch kim");
      accountSummaryResponse.getData().setCurrentTierCode(TierCode.P);
    }
    if (Objects.equals(user.getPhoneNumber(), "84383883828")) {
      accountSummaryResponse.getData().setAvailableBonusMiles(1000001L);
      accountSummaryResponse.getData().setTotalQualifyingSegments(100D);
      accountSummaryResponse.getData().setCurrentTier("Triệu dặm");
      accountSummaryResponse.getData().setCurrentTierCode(TierCode.P);
      accountSummaryResponse.getData().setMillionMiller(true);
    }


  }

  @Deprecated(forRemoval = true)
  public static FPTAccountSummaryResponse simulatorByFaker(String identifierNo, User user, UserProfile userProfile) {
    FPTCustomerProfileResponse.Data.Address address = JsonParser.entity(userProfile.getAddress(), FPTCustomerProfileResponse.Data.Address.class);
    List<Data.ExpirationPoint> expirationPoints = new ArrayList<>();
    expirationPoints.add(
        Data.ExpirationPoint.builder()
            .points(100)
            .expirationDate("2024-09-01")
            .build());
    expirationPoints.add(
        Data.ExpirationPoint.builder()
            .points(200)
            .expirationDate("2024-09-02")
            .build());
    expirationPoints.add(
        Data.ExpirationPoint.builder()
            .points(300)
            .expirationDate("2024-09-03")
            .build());
    Data data = Data.builder()
        .accountId(user != null ? Long.parseLong(user.getAccountId()) : generateRandomLong(9999999999L))
        .customerId(user != null ? Long.parseLong(user.getCustomerId()) : generateRandomLong(9999999999L))
        .dateOfBirth(userProfile.getDateOfBirth())
        .email(user != null ? user.getEmail() : "email@gmail.com")
        .mobile(convertMobile(address.getPhoneCountryCode(), address.getPhoneNumber()))
        .preferredLanguage(userProfile.getPreferredLanguage())
        .embossedName(Objects.equals(userProfile.getNameOrdering(), NameOrdering.FG.toString()) ?
            Objects.requireNonNull(user).getFirstName() + " " + user.getLastName()
            : Objects.requireNonNull(user).getLastName() + " " + user.getFirstName())
        .currentTier("Million Miler")
        .currentTierCode(TierCode.P)
        .currentTierStartDate("2024-02-02")
        .availableBonusMiles(6000L)
        .qualifyingMilesToMaintain(50000L)
        .qualifyingMilesToUpgrade(44000L)
        .qualifyingSegmentsToMaintain(18L)
        .qualifyingSegmentsToUpgrade(34L)
        .currentQualifyingPeriodStartDate("2024-03-02")
        .currentQualifyingPeriodEndDate("2024-05-05")
        .totalQualifyingMiles(6000L)
        .totalQualifyingSegments(11D)
        .totalHouseholdPointBalance(3232L)
        .totalLifetimeMiles(3232L)
        .totalMmLifetimeMiles("4344")
        .incompleteData(false)
        .enrolmentDate("2023-08-21")
        .tierMaintainEndDate("2024-03-02")
        .tierUpgradeEndDate("2024-05-05")
        .expirationPoints(expirationPoints)
        .customerStatus(String.valueOf(user.getState()))
        .emailConfirmed(true)
        .joinedHousehold(true)
        .headOfHousehold(true)
        .millionMiller(false)
        .charity(false)
        .coporate(false)
        .corporateName("")
        .build();

    FPTAccountSummaryResponse fakeData = FPTAccountSummaryResponse.builder()
        .errorCode(ErrorCode.FPT.MCAB_200)
        .errorMessage("Fake data")
        .data(data)
        .build();
    fakeForSpecificUser(user, fakeData);
    return fakeData;
  }

  @Override
  public String getErrorMessageConverted() {
    return this.getErrorCode();
  }

  @Builder
  @lombok.Data
  public static class Data {

    private Long accountId;

    private Long customerId;

    private String embossedName;

    private String dateOfBirth;

    private String email;

    private String mobile;

    private String preferredLanguage;

    private String currentTier;

    private TierCode currentTierCode;

    private String currentTierStartDate;

    private Long availableBonusMiles;

    private Long qualifyingMilesToMaintain;

    private Long qualifyingMilesToUpgrade;

    private Long qualifyingSegmentsToMaintain;

    private Long qualifyingSegmentsToUpgrade;

    private String currentQualifyingPeriodStartDate;

    private String currentQualifyingPeriodEndDate;

    private Long totalQualifyingMiles;

    private Double totalQualifyingSegments;

    private Long totalHouseholdPointBalance;

    private Long totalLifetimeMiles;

    @Nullable
    private String totalMmLifetimeMiles;

    private Boolean incompleteData;

    @Nullable
    private String tierMaintainEndDate;

    private String tierUpgradeEndDate;

    // Ngày tạo tài khoản
    private String enrolmentDate;

    @Nullable
    private String currentTierEndDate;

    private List<ExpirationPoint> expirationPoints;

    private String customerStatus;

    private String customerStatusName;

    private Boolean emailConfirmed;

    private Boolean joinedHousehold;

    @Nullable
    private Boolean headOfHousehold;

    private Boolean millionMiller;

    private Boolean charity;

    private Boolean coporate;

    @Nullable
    private String corporateName;

    @lombok.Data
    @Builder
    public static class ExpirationPoint {
      private Integer points;

      private String expirationDate;
    }
  }


}
