package com.wiinvent.lotusmile.domain.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.LocaleResolver;

import java.util.Locale;

@Service
public class LocalizationService {
  @Autowired
  private MessageSource messageSource;

  @Autowired
  private LocaleResolver localeResolver;

  @Autowired
  private HttpServletRequest request;

  public String getMessage(String key) {
    Locale locale = localeResolver.resolveLocale(request);
    return messageSource.getMessage(key, null, key, locale);
  }

}
