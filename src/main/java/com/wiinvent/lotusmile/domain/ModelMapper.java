package com.wiinvent.lotusmile.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wiinvent.lotusmile.app.dto.FPTUpdateProfileDto;
import com.wiinvent.lotusmile.app.response.fpt.FPTCustomerProfileResponse;
import com.wiinvent.lotusmile.app.response.fpt.FPTMobileCountryCode;
import com.wiinvent.lotusmile.domain.service.SimulatorService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ModelMapper {


  CustomerInfoResponse mapFromCustomerProfileResponseData(FPTCustomerProfileResponse.Data fptCustomerProfileResponse);

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


  List<FPTMobileCountryCode.Data> mapFromListConfigToListData(List<SimulatorService.PhoneCode> phoneCodeList);

  @Mapping(target = "code", source = "phoneCode")
  @Mapping(target = "value", source = "name")
  FPTMobileCountryCode.Data mapFromConfigToData(SimulatorService.PhoneCode phoneCode);

}
