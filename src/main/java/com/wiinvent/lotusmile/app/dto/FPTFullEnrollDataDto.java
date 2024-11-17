package com.wiinvent.lotusmile.app.dto;

import com.wiinvent.lotusmile.domain.entity.types.NameOrdering;
import com.wiinvent.lotusmile.domain.entity.types.Title;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FPTFullEnrollDataDto {

  private Title title;

  private String firstName;
  private String lastName;
  private NameOrdering nameOrdering;
  private String dateOfBirth;
  private String identityCardNumber;
  private String preferredLanguage;
  private String nationality;
  private Address address;
  private String partnerCode;
  private String promotionCode;
  private Boolean subscribe;
  private String localFullName;
  private String localAddress;

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  public static class Address {
    private String email;
    private String phoneNumber;
    private String phoneCountryCode;
    private String street;
    private String city;
    private String country;
    private String region;
    private String postalCode;
    private String additionalInfo;
  }

}
