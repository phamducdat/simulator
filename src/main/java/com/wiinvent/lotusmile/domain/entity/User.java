package com.wiinvent.lotusmile.domain.entity;

import com.wiinvent.lotusmile.app.dto.fpt.FPTUpdateProfileDto;
import com.wiinvent.lotusmile.app.response.fpt.FPTAccountSummaryResponse;
import com.wiinvent.lotusmile.app.response.fpt.FPTCustomerProfileResponse;
import com.wiinvent.lotusmile.domain.entity.types.UserState;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(schema = "\"user\"", name = "\"user\"")
@Getter
@SuperBuilder
@NoArgsConstructor
public class User extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(name = "account_id")
  private String accountId;

  @Column(name = "customer_id")
  private String customerId;

  @Column(name = "main_identifier")
  private String mainIdentifier;

  @Column(name = "email", length = 120)
  private String email;

  @Column(name = "identity_card_number", length = 20)
  private String identityCardNumber;

  // mobile of accountSummary
  @Column(name = "phone_number", length = 50)
  private String phoneNumber;

  @Column(name = "first_name", length = 25)
  private String firstName;

  @Column(name = "last_name", length = 25)
  private String lastName;

  @Column(name = "email_confirmed")
  private Boolean emailConfirmed;

  @Setter
  @Column(name = "is_logged")
  private Boolean isLogged;

  @Column(name = "confirmed_at")
  private Long confirmedAt;

  @Column(name = "state")
  @Enumerated(EnumType.STRING)
  @JdbcType(PostgreSQLEnumJdbcType.class)
  private UserState state;

  public void updateUser(FPTCustomerProfileResponse.Data fptCusotmerProfileData,
                         FPTAccountSummaryResponse.Data fptAccountSummaryData) {
    this.lastName = fptCusotmerProfileData.getLastName();
    this.firstName = fptCusotmerProfileData.getFirstName();
    this.identityCardNumber = fptCusotmerProfileData.getIdentityCardNumber();
    this.email = fptCusotmerProfileData.getAddress().getEmail();
    this.phoneNumber = fptAccountSummaryData.getMobile();
    this.emailConfirmed = fptAccountSummaryData.getEmailConfirmed();
    this.state = fptCusotmerProfileData.getStatus();
  }

  @Deprecated(forRemoval = true)
  public void updateUserProfileSimulator(FPTUpdateProfileDto updateProfileDto) {
    this.identityCardNumber = updateProfileDto.getIdentityCardNumber();
  }

  public static User createUserFromLoginResponse(FPTCustomerProfileResponse.Data customerProfileData, FPTAccountSummaryResponse.Data accountSummaryData) {
    return User.builder()
        .accountId(String.valueOf(customerProfileData.getAccountId()))
        .customerId(String.valueOf(customerProfileData.getCustomerId()))
        .mainIdentifier(String.valueOf(customerProfileData.getMainIdentifier()))
        .email(customerProfileData.getAddress().getEmail())
        .firstName(customerProfileData.getFirstName())
        .lastName(customerProfileData.getLastName())
        .state(customerProfileData.getStatus())
        .identityCardNumber(customerProfileData.getIdentityCardNumber())
        .phoneNumber(accountSummaryData.getMobile())
        .emailConfirmed(accountSummaryData.getEmailConfirmed())
        .isLogged(true)
        .build();
  }

  // TODO: accountId, state user?
  public static User creatUserFromEnrollment(FPTCustomerProfileResponse.Data customerProfileData,
                                             FPTAccountSummaryResponse.Data accountSummaryData) {
    return User.builder()
        .accountId(String.valueOf(accountSummaryData.getAccountId()))
        .customerId(String.valueOf(accountSummaryData.getCustomerId()))
        .mainIdentifier(String.valueOf(customerProfileData.getMainIdentifier()))
        .email(accountSummaryData.getEmail())
        .firstName(customerProfileData.getFirstName())
        .lastName(customerProfileData.getLastName())
        .phoneNumber(accountSummaryData.getMobile())
        .emailConfirmed(accountSummaryData.getEmailConfirmed())
        .isLogged(false)
        .build();
  }

}
