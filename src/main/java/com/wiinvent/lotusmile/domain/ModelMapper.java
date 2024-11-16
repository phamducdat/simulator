package com.wiinvent.lotusmile.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wiinvent.lotusmile.app.dto.fpt.FPTSendMessageDto;
import com.wiinvent.lotusmile.app.dto.fpt.FPTUpdateProfileDto;
import com.wiinvent.lotusmile.app.response.CustomerInfoResponse;
import com.wiinvent.lotusmile.app.response.MerchantAccountResponse;
import com.wiinvent.lotusmile.app.response.fpt.FPTCustomerProfileResponse;
import com.wiinvent.lotusmile.app.response.fpt.FPTMobileCountryCode;
import com.wiinvent.lotusmile.domain.entity.MerchantAccount;
import com.wiinvent.lotusmile.domain.entity.UserProfile;
import com.wiinvent.lotusmile.domain.service.SimulatorService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ModelMapper {

  MerchantAccountResponse toMerchantResponse(MerchantAccount merchantAccount);

  @Mapping(target = "customerId", source = "customerId")
  FPTSendMessageDto mapFromSendMessageDto(SendMessageDto sendMessageDto, String customerId);

  CustomerInfoResponse mapFromCustomerProfileResponseData(FPTCustomerProfileResponse.Data fptCustomerProfileResponse);

  @Deprecated(forRemoval = true)
  @Mapping(target = "id", ignore = true)
  UserProfile mapFromFPTUpdateProfileDto(FPTUpdateProfileDto fptUpdateProfileDto);

  // Custom method to convert Address to JSON String
  default String map(FPTUpdateProfileDto.Address address) {
    try {
      ObjectMapper objectMapper = new ObjectMapper();
      return objectMapper.writeValueAsString(address);
    } catch (JsonProcessingException e) {
      // Handle the exception according to your application's needs
      e.printStackTrace();
      return null;
    }
  }

  default String map(FPTUpdateProfileDto.PersonalDocument personalDocument) {
    try {
      ObjectMapper objectMapper = new ObjectMapper();
      return objectMapper.writeValueAsString(personalDocument);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
      return null;
    }
  }

  FPTUpdateProfileDto mapFromUpdateProfileDto(UpdateCustomerInfoDto updateCustomerInfoDto);

  @Deprecated(forRemoval = true)
  List<FPTMobileCountryCode.Data> mapFromListConfigToListData(List<SimulatorService.PhoneCode> phoneCodeList);

  @Deprecated(forRemoval = true)
  @Mapping(target = "code", source = "phoneCode")
  @Mapping(target = "value", source = "name")
  FPTMobileCountryCode.Data mapFromConfigToData(SimulatorService.PhoneCode phoneCode);

}
