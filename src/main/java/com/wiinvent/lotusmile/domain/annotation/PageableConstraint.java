package com.wiinvent.lotusmile.domain.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

@ResponseStatus(HttpStatus.BAD_REQUEST)
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Constraint(validatedBy = PageableConstraint.PageableValidator.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface PageableConstraint {
  String message() default "{wiinvent.page.param.invalid}";

  Class<?>[] groups() default {};

  Class<?>[] payload() default {};

  int max() default 10000;

  int min() default 0;

  class PageableValidator implements ConstraintValidator<PageableConstraint, Integer> {
    private int min;
    private int max;

    @Override
    public void initialize(PageableConstraint constraintAnnotation) {
      this.min = constraintAnnotation.min();
      this.max = constraintAnnotation.max();
    }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
      if (value == null) {
        return true;
      }
      return (value >= min && value <= max);
    }
  }
}
