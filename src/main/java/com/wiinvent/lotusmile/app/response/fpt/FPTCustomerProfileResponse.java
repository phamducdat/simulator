package com.wiinvent.lotusmile.app.response.fpt;

import com.wiinvent.lotusmile.domain.entity.User;
import com.wiinvent.lotusmile.domain.entity.UserProfile;
import com.wiinvent.lotusmile.domain.entity.types.UserState;
import com.wiinvent.lotusmile.domain.entity.types.fpt.Gender;
import com.wiinvent.lotusmile.domain.entity.types.fpt.NameOrdering;
import com.wiinvent.lotusmile.domain.entity.types.fpt.PersonalType;
import com.wiinvent.lotusmile.domain.entity.types.fpt.Title;
import com.wiinvent.lotusmile.domain.exception.ErrorCode;
import com.wiinvent.lotusmile.domain.exception.ErrorMessage;
import com.wiinvent.lotusmile.domain.util.Helper;
import com.wiinvent.lotusmile.domain.util.JsonParser;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Objects;

@Getter
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@ToString(callSuper = true)
public class FPTCustomerProfileResponse extends BaseFPTResponse {
  private Data data;

  @Deprecated(forRemoval = true)
  public static FPTCustomerProfileResponse simulatorByFaker(String customerId) {
    Data.Address address = Data.Address.builder()
        .street("street")
        .city("ha noi")
        .country("VN")
        .region("HN")
        .postalCode("10000")
        .email("email@gmail.com")
        .phoneNumber(customerId)
        .build();
    Data.PersonalDocument personalDocument = Data.PersonalDocument.builder()
        .personalId(String.valueOf(Helper.generateRandomLong(10000000)))
        .personalType(PersonalType.I)
        .personalCountryCodeIssue("HN")
        .personalIdDateIssue("2023-09-06")
        .build();

    Data data = Data.builder()
        .accountId(Math.abs(Helper.generateRandomLong(9999999999L)))
        .customerId(customerId != null ? Long.parseLong(customerId) : Math.abs(Helper.generateRandomLong(9999999999L)))
        .mainIdentifier(Math.abs(Helper.generateRandomLong(9999999999L)))
        .title(Title.Mr)
        .firstName("firstName")
        .lastName("lastName")
        .nameOrdering(NameOrdering.FG)
//        .embossedName(faker.name().firstName() + " " + faker.name().lastName())
        .localFullName("fullName")
        .dateOfBirth("2024-07-24")
        .gender(Gender.M)
        .identityCardNumber(String.valueOf(Math.abs(Helper.generateRandomLong(9999999999L))))
        .preferredLanguage("vi")
        .nationality("VN")
        .companyName("")
        .businessTitle("")
        .localAddress("localAddress")
        .address(address)
        .personalDocument(personalDocument)
        .status(UserState.A)
        .statusName("Active")
        .guardianId(Math.abs(Helper.generateRandomLong(9999999999L)))
        .enrolmentDate("2024-07-24")
        .enrolmentChannel("enrolmentChannel")
        .emailConfirmed(true)
        .termAndConditionAcceptedDate("2023-06-07")
        .pointExpirationDisabled(false)
        .childEnrolmentEnabled(true)
        .childEnrolmentDisabled(false)
        // TODO: waiting FPT response
//        .attributes("attributes")
        .householdJoinDate("2022-05-06")
        .householdLockDate("2022-05-06")
        .build();
    return FPTCustomerProfileResponse.builder()
        .errorCode(ErrorCode.FPT.MCAB_200)
        .errorMessage("Fake data")
        .data(data)
        .build();
  }

  @Deprecated(forRemoval = true)
  public static FPTCustomerProfileResponse simulatorByUserProfile(User user, UserProfile userProfile) {
    Data.Address address = JsonParser.entity(userProfile.getAddress(), Data.Address.class);
//    if (address != null) {
//      address.setEmail(user.getEmail());
//      address.setPhoneNumber(user.getPhoneNumber());
//    }
    Data.PersonalDocument personalDocument = JsonParser.entity(userProfile.getPersonalDocument(), Data.PersonalDocument.class);

    Data data = Data.builder()
        .accountId(Long.valueOf(user.getAccountId() != null ? user.getAccountId() : "-1"))
        .customerId(Long.valueOf(user.getCustomerId() != null ? user.getCustomerId() : "-1"))
        .mainIdentifier(Long.valueOf(user.getMainIdentifier() != null ? user.getMainIdentifier() : "-1"))
        .title(Title.valueOf(userProfile.getTitle()))
        .firstName(user.getFirstName())
        .lastName(user.getLastName())
        .nameOrdering(NameOrdering.valueOf(userProfile.getNameOrdering()))
        .embossedName(Objects.equals(userProfile.getNameOrdering(), NameOrdering.FG.toString()) ? user.getFirstName() + " " + user.getLastName() :
            user.getLastName() + " " + user.getFirstName())
        .localFullName(userProfile.getLocalFullName())
        .dateOfBirth(userProfile.getDateOfBirth())
        .gender(Gender.valueOf(Title.valueOf(userProfile.getTitle()).getGender().name()))
        .identityCardNumber(user.getIdentityCardNumber())
        .preferredLanguage(userProfile.getPreferredLanguage())
        .nationality(userProfile.getNationality())
        .companyName(userProfile.getCompanyName())
        .businessTitle(userProfile.getBusinessTitle())
        .localAddress(userProfile.getLocalAddress())
        .address(address)
        .personalDocument(personalDocument)
        .status(user.getState() != null ? user.getState() : UserState.A)
        .statusName("Active")
        .guardianId(Long.valueOf(userProfile.getGuardianId() != null ? userProfile.getGuardianId() : "-1"))
        .enrolmentDate("2024-07-24")
        .enrolmentChannel(userProfile.getEnrolmentChannel())
        .emailConfirmed(user.getEmailConfirmed())
        .termAndConditionAcceptedDate(userProfile.getTermAndConditionAcceptedDate())
        .pointExpirationDisabled(userProfile.getPointExpirationDisabled())
        .childEnrolmentEnabled(userProfile.getChildEnrolmentEnabled() != null && userProfile.getChildEnrolmentEnabled())
        .childEnrolmentDisabled(userProfile.getChildEnrolmentEnabled() == null || !userProfile.getChildEnrolmentEnabled())
        // TODO: waiting FPT response
//        .attributes(userProfile.getAttributes())
        .householdJoinDate(userProfile.getHouseHoldJoinDate())
        .householdLockDate(userProfile.getHouseHoldLockDate())
        .build();
    return FPTCustomerProfileResponse.builder()
        .errorCode(ErrorCode.FPT.MCAB_200)
        .errorMessage("Xử lý thành công")
        .data(data)
        .build();
  }

  @Override
  public String getErrorMessageConverted() {
    return ErrorMessage.INVALID_DATA.getMessage();
  }

  @Deprecated(forRemoval = true)
  public void mapFromEnrollment(FullEnrollDataDto fullEnrollDataDto) {
    this.data.title = fullEnrollDataDto.getTitle();
    this.data.firstName = fullEnrollDataDto.getFirstName();
    this.data.lastName = fullEnrollDataDto.getLastName();
    this.data.nameOrdering = fullEnrollDataDto.getNameOrdering();
    this.data.dateOfBirth = fullEnrollDataDto.getDateOfBirth();
//    this.data.identityCardNumber = fullEnrollDataDto.getIdentityCardNumber();
//    this.data.gender = String.valueOf(fullEnrollDataDto.getGender());
    this.data.preferredLanguage = fullEnrollDataDto.getPreferredLanguage();
    this.data.nationality = fullEnrollDataDto.getNationality();
    this.data.address.email = fullEnrollDataDto.getAddress().getEmail();
    this.data.address.phoneNumber = fullEnrollDataDto.getAddress().getPhoneNumber();
    this.data.address.city = fullEnrollDataDto.getAddress().getCity();
    this.data.address.street = fullEnrollDataDto.getAddress().getStreet();
    this.data.address.country = fullEnrollDataDto.getAddress().getCountry();
    this.data.address.region = fullEnrollDataDto.getAddress().getRegion();
    this.data.address.postalCode = fullEnrollDataDto.getAddress().getPostalCode();
    this.data.localFullName = fullEnrollDataDto.getLocalFullName();
    this.data.localAddress = fullEnrollDataDto.getLocalAddress();
  }

  @lombok.Data
  @Builder
  public static class Data {

    private Long accountId;

    private Long customerId;

    private Long mainIdentifier;

    private Title title;

    private String firstName;

    private String lastName;

    private NameOrdering nameOrdering;

    private String embossedName;

    private String localFullName;

    private String dateOfBirth;

    private Gender gender;

    private String identityCardNumber;

    private String preferredLanguage;

    private String nationality;

    private String companyName;

    private String businessTitle;

    private String localAddress;

    private Address address;

    private PersonalDocument personalDocument;

    private UserState status;

    private String statusName;

    private Long guardianId;

    private String enrolmentDate;

    private String enrolmentChannel;

    private Boolean emailConfirmed;

    private String termAndConditionAcceptedDate;

    private Boolean pointExpirationDisabled;

    private Boolean childEnrolmentDisabled;

    private Boolean childEnrolmentEnabled;

    private String attributes;

    private String householdJoinDate;

    private String householdLockDate;

//  @JsonProperty("incompleteData")
//  private Boolean incompleteData;
//
//  @JsonProperty("subscribe")
//  private Boolean subscribe;
//
//  @JsonProperty("mainPointsBalance")
//  private Long mainPointsBalance;
//
//  @JsonProperty("redemptionEnable")
//  private Boolean redemptionEnable;
//
//  @JsonProperty("partnerCode")
//  private String partnerCode;
//
//  @JsonProperty("coporate")
//  private Boolean coporate;
//
//  @JsonProperty("corporateName")
//  private String corporateName;
//
//  @JsonProperty("corporationName")
//  private String corporationName;
//
//  @JsonProperty("memberBenefit")
//  private String memberBenefit;

    @lombok.Data
    @ToString
    @Builder
    public static class Address {

      private String street;

      private String city;

      private String country;

      private String region;

      private String postalCode;

      private String email;

      private String phoneNumber;

      private String phoneCountryCode;

      private String additionalInfo;
    }

    @lombok.Data
    @ToString
    @Builder
    public static class PersonalDocument {

      private String personalId;

      private PersonalType personalType;

      private String personalCountryCodeIssue;

      private String personalIdDateIssue;
    }
  }
}
