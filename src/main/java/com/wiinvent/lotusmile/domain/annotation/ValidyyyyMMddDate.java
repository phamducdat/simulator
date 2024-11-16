package com.wiinvent.lotusmile.domain.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.regex.Pattern;

import static java.lang.annotation.ElementType.*;

@ResponseStatus(HttpStatus.BAD_REQUEST)
@Constraint(validatedBy = ValidyyyyMMddDate.DateValidator.class)
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidyyyyMMddDate {

  String DATE_FORMAT = "yyyy-MM-dd";

  String message() default "{wiinvent.date.valid}";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  class DateValidator implements ConstraintValidator<ValidyyyyMMddDate, String> {

    private static final Pattern DATE_PATTERN = Pattern.compile("^\\d{4}-\\d{2}-\\d{2}$");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
      if (value == null) {
        return true; // consider null as valid, use @NotNull for null checks
      }

      // Check the string matches the pattern "yyyy-MM-dd"
      if (!DATE_PATTERN.matcher(value).matches()) {
        return false;
      }

      // Validate with SimpleDateFormat to check the date is actually valid
      SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
      sdf.setLenient(false);
      try {
        sdf.parse(value);
        return true;
      } catch (ParseException e) {
        return false;
      }
    }
  }


}
