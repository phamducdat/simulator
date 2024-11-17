package com.wiinvent.lotusmile.domain.annotation;

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
@Constraint(validatedBy = ExistsPhoneCode.ExistsPhoneCodeValidator.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExistsPhoneCode {

  String message() default "{wiinvent.phone.code.exists}";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  @Log4j2
  class ExistsPhoneCodeValidator implements ConstraintValidator<ExistsPhoneCode, String> {
    private final PublicAuthenticationService publicAuthenticationService;

    public ExistsPhoneCodeValidator(PublicAuthenticationService publicAuthenticationService) {
      this.publicAuthenticationService = publicAuthenticationService;
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
      return publicAuthenticationService.validatePhoneCode(value);
    }
  }
}
