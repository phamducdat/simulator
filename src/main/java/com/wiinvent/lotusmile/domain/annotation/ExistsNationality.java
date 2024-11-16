package com.wiinvent.lotusmile.domain.annotation;

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

import static java.lang.annotation.ElementType.*;

@ResponseStatus(HttpStatus.NOT_FOUND)
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Constraint(validatedBy = ExistsNationality.ExistsNationalityValidator.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExistsNationality {

  String message() default "{wiinvent.nationality.exists}";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  @Log4j2
  class ExistsNationalityValidator implements ConstraintValidator<ExistsNationality, String> {
    private final ConfigStorage configStorage;

    public ExistsNationalityValidator(ConfigStorage configStorage) {
      this.configStorage = configStorage;
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
      if (value == null) {
        return false;
      }
      log.warn("ExistsNationality not done");
      return true;
    }
  }

}
