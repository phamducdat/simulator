package com.wiinvent.lotusmile.app.dto.fpt;

import com.wiinvent.lotusmile.app.dto.FullEnrollDataDto;
import com.wiinvent.lotusmile.domain.entity.types.fpt.NameOrdering;
import com.wiinvent.lotusmile.domain.entity.types.fpt.Title;
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

    public Address(FullEnrollDataDto.Address addressDto) {
      this.email = addressDto.getEmail();
      this.phoneNumber = addressDto.getPhoneNumber();
      this.phoneCountryCode = addressDto.getPhoneCountryCode();
      this.street = addressDto.getStreet();
      this.city = addressDto.getCity();
      this.country = addressDto.getCountry();
      this.region = addressDto.getRegion();
      this.postalCode = addressDto.getPostalCode();
    }
  }

  public FPTFullEnrollDataDto(FullEnrollDataDto dto, String partnerCode) {
    this.title = dto.getTitle();
    this.firstName = dto.getFirstName();
    this.lastName = dto.getLastName();
    this.nameOrdering = dto.getNameOrdering();
    this.dateOfBirth = dto.getDateOfBirth();
//    this.identityCardNumber = dto.getI
    this.preferredLanguage = dto.getPreferredLanguage();
    this.nationality = dto.getNationality();
    this.address = new Address(dto.getAddress());
    this.partnerCode = partnerCode;
    this.promotionCode = dto.getPromotionCode();
    this.subscribe = dto.getSubscribe();
    this.localFullName = dto.getLocalFullName();
    this.localAddress = dto.getLocalAddress();

  }
}
