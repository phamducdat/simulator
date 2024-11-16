package com.wiinvent.lotusmile.domain.annotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

@ResponseStatus(HttpStatus.NOT_FOUND)
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
public @interface PastOrPresentString {

  String message() default "{jakarta.validation.constraints.PastOrPresent.message}";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};


  class PastOrPresentStringValidator implements ConstraintValidator<PastOrPresentString, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
      return true;
    }
  }
}
