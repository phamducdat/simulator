package com.wiinvent.lotusmile.app.dto;

import com.wiinvent.lotusmile.domain.entity.types.NameOrdering;
import com.wiinvent.lotusmile.domain.entity.types.PersonalType;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
public class FPTUpdateProfileDto {

//  private Title title;

//  private String firstName;

//  private String lastName;

//  // TODO: re-check documentation's name (birthDate???)
//  private String dateOfBirth;

  private String identityCardNumber;

  private NameOrdering nameOrdering;

  private String preferredLanguage;

  private String nationality;

  private Address address;

  private PersonalDocument personalDocument;

  private String companyName;

  private String businessTitle;

  private String localAddress;

//  private String localFullName;

  private Boolean subscribe;

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
