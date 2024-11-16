package com.wiinvent.lotusmile.domain.service;

import com.wiinvent.lotusmile.app.response.fpt.FPTCountriesResponse;
import com.wiinvent.lotusmile.app.response.fpt.FPTCountryRegionResponse;
import com.wiinvent.lotusmile.domain.ModelMapper;
import com.wiinvent.lotusmile.domain.exception.BadRequestException;
import com.wiinvent.lotusmile.domain.service.external.ExternalService;
import com.wiinvent.lotusmile.domain.storage.ConfigStorage;
import com.wiinvent.lotusmile.domain.storage.CountryStorage;
import com.wiinvent.lotusmile.domain.util.JwtTokenUtil;
import com.wiinvent.lotusmile.domain.util.cache.CacheKey;
import com.wiinvent.lotusmile.domain.util.cache.RemoteCache;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static com.wiinvent.lotusmile.domain.exception.ErrorMessage.COUNTRY_CODE_INVALID;
import static com.wiinvent.lotusmile.domain.exception.ErrorMessage.COUNTRY_REGION_CODE_INVALID;

@Log4j2
@Service
abstract class BaseService {
  @Autowired
  protected JwtTokenUtil jwtTokenUtil;
  @Autowired
  RemoteCache remoteCache;
  @Autowired
  ModelMapper modelMapper;
  @Autowired
  ExternalService externalService;
  @Autowired
  CacheKey cacheKey;
  @Autowired
  ConfigStorage configStorage;
  @Autowired
  CountryStorage countryStorage;

  public void validateCountryCode(@NonNull String lang, String ipAddress, String idDevice, @NonNull String country) {
    FPTCountriesResponse countries = countryStorage.getCountries(lang, ipAddress, idDevice);
    boolean noneMatchCountry = countries.getData().parallelStream()
        .noneMatch(data -> Objects.equals(data.getCode(), country));
    if (noneMatchCountry) {
      throw new BadRequestException(COUNTRY_CODE_INVALID);
    }
  }

  public void validateCountryRegion(@NonNull String lang, String ipAddress, String idDevice, @NonNull String country, @NonNull String region) {
    FPTCountryRegionResponse countryRegion = countryStorage.getCountryRegion(lang, ipAddress, idDevice, country);
    boolean noneMatchCountryRegion = countryRegion.getData().parallelStream()
        .noneMatch(data -> Objects.equals(data.getCode(), region));
    if (noneMatchCountryRegion) {
      throw new BadRequestException(COUNTRY_REGION_CODE_INVALID);
    }
  }

}
