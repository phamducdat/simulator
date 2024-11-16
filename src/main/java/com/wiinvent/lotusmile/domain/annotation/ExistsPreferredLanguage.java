package com.wiinvent.lotusmile.domain.annotation;

import com.wiinvent.lotusmile.domain.entity.Config;
import com.wiinvent.lotusmile.domain.storage.ConfigStorage;
import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Objects;

import static java.lang.annotation.ElementType.*;

@ResponseStatus(HttpStatus.NOT_FOUND)
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Constraint(validatedBy = ExistsPreferredLanguage.ExistsPreferredLanguageValidator.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExistsPreferredLanguage {

  String message() default "{wiinvent.preferred.language.exists}";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  @Log4j2
  class ExistsPreferredLanguageValidator implements ConstraintValidator<ExistsPreferredLanguage, String> {
    private final ConfigStorage configStorage;

    public ExistsPreferredLanguageValidator(ConfigStorage configStorage) {
      this.configStorage = configStorage;
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
      if (value == null) {
        return false;
      }
      return configStorage.getListPreferredLanguage()
          .stream().map(Config.LanguageCode::getCode)
          .anyMatch(code -> Objects.equals(code, value));
    }
  }
}
