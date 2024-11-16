package com.wiinvent.lotusmile.domain.entity;

import com.wiinvent.lotusmile.app.response.fpt.FPTCustomerProfileResponse;
import com.wiinvent.lotusmile.domain.util.JsonParser;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(schema = "\"user\"", name = "\"user_profile\"")
@Data
@SuperBuilder
@NoArgsConstructor
public class UserProfile extends BaseEntity {

  @Id
  private Integer id;

  @Column(name = "title", length = 10)
  private String title;

  @Column(name = "name_ordering", length = 10)
  private String nameOrdering;

//  @Deprecated(forRemoval = true)
//  @Getter(AccessLevel.NONE)
//  @Column(name = "embossed_name")
//  private String embossedName;

  @Column(name = "local_full_name", length = 300)
  private String localFullName;

  @Column(name = "date_of_birth", length = 20)
  private String dateOfBirth;

//  @Column(name = "gender")
//  private String gender;

  @Column(name = "preferred_language", length = 10)
  private String preferredLanguage;

  @Column(name = "nationality", length = 3)
  private String nationality;

  @Column(name = "company_name", length = 300)
  private String companyName;

  @Column(name = "business_title", length = 300)
  private String businessTitle;

  @Column(name = "local_address", length = 300)
  private String localAddress;

  @Column(name = "address")
  @JdbcTypeCode(SqlTypes.JSON)
  private String address;

  @Column(name = "personal_document")
  @JdbcTypeCode(SqlTypes.JSON)
  private String personalDocument;

  @Column(name = "guardian_id")
  private String guardianId;

  @Column(name = "enrolment_date")
  private String enrolmentDate;

  @Column(name = "enrolment_channel")
  private String enrolmentChannel;

  @Column(name = "term_and_condition_accepted_date")
  private String termAndConditionAcceptedDate;

  @Column(name = "point_expiration_disabled")
  private Boolean pointExpirationDisabled;

  @Column(name = "child_enrolment_enabled")
  private Boolean childEnrolmentEnabled;

  @Column(name = "house_hold_join_date")
  private String houseHoldJoinDate;

  @Column(name = "house_hold_lock_date")
  private String houseHoldLockDate;

  public void updateUserProfile(FPTCustomerProfileResponse.Data fptCustomerProfileData) {
    this.title = fptCustomerProfileData.getTitle().toString();
    this.nameOrdering = fptCustomerProfileData.getNameOrdering().toString();
    this.localFullName = fptCustomerProfileData.getLocalFullName();
    this.dateOfBirth = fptCustomerProfileData.getDateOfBirth();
    this.preferredLanguage = fptCustomerProfileData.getPreferredLanguage();
    this.nationality = fptCustomerProfileData.getNationality();
    this.companyName = fptCustomerProfileData.getCompanyName();
    this.businessTitle = fptCustomerProfileData.getBusinessTitle();
    this.localAddress = fptCustomerProfileData.getLocalAddress();
    this.address = JsonParser.toJson(fptCustomerProfileData.getAddress());
    this.personalDocument = JsonParser.toJson(fptCustomerProfileData.getPersonalDocument());
    this.guardianId = String.valueOf(fptCustomerProfileData.getGuardianId());
    this.enrolmentDate = JsonParser.toJson(fptCustomerProfileData.getEnrolmentDate());
    this.termAndConditionAcceptedDate = fptCustomerProfileData.getTermAndConditionAcceptedDate();
    this.pointExpirationDisabled = fptCustomerProfileData.getPointExpirationDisabled();
    this.childEnrolmentEnabled = fptCustomerProfileData.getChildEnrolmentEnabled();
    this.houseHoldJoinDate = fptCustomerProfileData.getHouseholdJoinDate();
    this.houseHoldLockDate = fptCustomerProfileData.getHouseholdLockDate();
  }

  public static UserProfile createUserProfileByCustomerProfile(Integer userId, FPTCustomerProfileResponse.Data customerProfileResponseData) {
    return UserProfile.builder()
        .id(userId)
        .title(String.valueOf(customerProfileResponseData.getTitle()))
        .nameOrdering(String.valueOf(customerProfileResponseData.getNameOrdering()))
//        .embossedName(customerProfileResponseData.getEmbossedName())
        .localFullName(customerProfileResponseData.getLocalFullName())
        .dateOfBirth(customerProfileResponseData.getDateOfBirth())
//        .gender(String.valueOf(customerProfileResponseData.getGender()))
        .preferredLanguage(customerProfileResponseData.getPreferredLanguage())
        .nationality(customerProfileResponseData.getNationality())
        .companyName(customerProfileResponseData.getCompanyName())
        .businessTitle(customerProfileResponseData.getBusinessTitle())
        .localAddress(customerProfileResponseData.getLocalAddress())
        .address(JsonParser.toJson(customerProfileResponseData.getAddress()))
        .personalDocument(JsonParser.toJson(customerProfileResponseData.getPersonalDocument()))
        .termAndConditionAcceptedDate(customerProfileResponseData.getTermAndConditionAcceptedDate())
        .childEnrolmentEnabled(customerProfileResponseData.getChildEnrolmentEnabled())
        // TODO: waiting FPT response
//        .attributes(JsonParser.toJson(customerProfileResponseData.getAttributes()))
        .houseHoldJoinDate(customerProfileResponseData.getHouseholdJoinDate())
        .houseHoldLockDate(customerProfileResponseData.getHouseholdLockDate())
        .build();
  }

  public static UserProfile createUserProfileByEnrollment(Integer userId, FullEnrollDataDto fullEnrollDataDto) {
    return UserProfile.builder()
        .id(userId)
        .title(String.valueOf(fullEnrollDataDto.getTitle()))
        .localFullName(fullEnrollDataDto.getLocalFullName())
        .dateOfBirth(fullEnrollDataDto.getDateOfBirth())
//        .gender(fullEnrollDataDto.getGender().toString())
        .preferredLanguage(fullEnrollDataDto.getPreferredLanguage())
        .nationality(fullEnrollDataDto.getNationality())
//        .localAddress(fullEnrollDataDto.getLocalAddress())
        .address(JsonParser.toJson(fullEnrollDataDto.getAddress()))
        .build();
  }

  public void setUserProfile(FPTCustomerProfileResponse.Data customerProfileResponseData) {
    this.title = String.valueOf(customerProfileResponseData.getTitle());
    this.nameOrdering = String.valueOf(customerProfileResponseData.getNameOrdering());
//    this.embossedName = customerProfileResponseData.getEmbossedName();
    this.localFullName = customerProfileResponseData.getLocalFullName();
    this.dateOfBirth = customerProfileResponseData.getDateOfBirth();
//    this.gender = String.valueOf(customerProfileResponseData.getGender());
    this.preferredLanguage = customerProfileResponseData.getPreferredLanguage();
    this.nationality = customerProfileResponseData.getNationality();
    this.companyName = customerProfileResponseData.getCompanyName();
    this.businessTitle = customerProfileResponseData.getBusinessTitle();
    this.localAddress = customerProfileResponseData.getLocalAddress();
    this.address = JsonParser.toJson(customerProfileResponseData.getAddress());
    this.personalDocument = JsonParser.toJson(customerProfileResponseData.getPersonalDocument());
    this.termAndConditionAcceptedDate = customerProfileResponseData.getTermAndConditionAcceptedDate();
    this.childEnrolmentEnabled = customerProfileResponseData.getChildEnrolmentEnabled();
    // TODO: waiting FPT response
//    this.attributes = JsonParser.toJson(customerProfileResponseData.getAttributes());
    this.houseHoldJoinDate = customerProfileResponseData.getHouseholdJoinDate();
    this.houseHoldLockDate = customerProfileResponseData.getHouseholdLockDate();
  }

}
