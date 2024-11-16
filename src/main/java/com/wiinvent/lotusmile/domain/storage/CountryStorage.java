package com.wiinvent.lotusmile.domain.storage;

import com.wiinvent.lotusmile.app.response.fpt.FPTCountriesResponse;
import com.wiinvent.lotusmile.app.response.fpt.FPTCountryRegionResponse;
import com.wiinvent.lotusmile.app.response.fpt.FPTMobileCountryCode;
import com.wiinvent.lotusmile.domain.service.external.ExternalService;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.wiinvent.lotusmile.domain.util.cache.RemoteCache.CACHE_1DAY_DURATION;


@Component
@Log4j2
public class CountryStorage extends BaseStorage {

  @Autowired
  ExternalService externalService;

  public FPTMobileCountryCode getMobileCountryCode(@NonNull String lang, String ipAddress, String idDevice) {
    FPTMobileCountryCode fptMobileCountryCode = remoteCache.get(cacheKey.getFPTMobileCountryCode(lang), FPTMobileCountryCode.class);
    if (fptMobileCountryCode == null) {
      fptMobileCountryCode = externalService.getMobileCountryCode(lang, ipAddress, idDevice);
      if (fptMobileCountryCode != null) {
        remoteCache.put(cacheKey.getFPTMobileCountryCode(lang), fptMobileCountryCode, CACHE_1DAY_DURATION);
        return fptMobileCountryCode;
      }
    }
    return fptMobileCountryCode;
  }

  public FPTCountriesResponse getCountries(@NonNull String lang, String ipAddress, String idDevice) {
    FPTCountriesResponse fptCountriesResponse = remoteCache.get(cacheKey.genFPTCountries(lang), FPTCountriesResponse.class);
    if (fptCountriesResponse == null) {
      fptCountriesResponse = externalService.getCountries(lang, ipAddress, idDevice);
      if (fptCountriesResponse != null) {
        remoteCache.put(cacheKey.genFPTCountries(lang), fptCountriesResponse, CACHE_1DAY_DURATION);
      }
      return fptCountriesResponse;
    }
    return fptCountriesResponse;
  }

  public FPTCountryRegionResponse getCountryRegion(@NonNull String lang, String ipAddress, String idDevice, @NonNull String countryCode) {
    FPTCountryRegionResponse fptCountryRegionResponse = remoteCache.get(cacheKey.genFPTCountryRegions(lang, countryCode), FPTCountryRegionResponse.class);
    if (fptCountryRegionResponse == null) {
      fptCountryRegionResponse = externalService.getCountryRegions(lang, ipAddress, idDevice, countryCode);
      if (fptCountryRegionResponse != null) {
        remoteCache.put(cacheKey.genFPTCountryRegions(lang, countryCode), fptCountryRegionResponse, CACHE_1DAY_DURATION);
      }
      return fptCountryRegionResponse;
    }
    return fptCountryRegionResponse;
  }
}
