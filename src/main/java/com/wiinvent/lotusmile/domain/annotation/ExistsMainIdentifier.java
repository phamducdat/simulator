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

import static java.lang.annotation.ElementType.*;

@ResponseStatus(HttpStatus.NOT_FOUND)
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Constraint(validatedBy = ExistsMainIdentifier.ExistsMainIdentifierValidator.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExistsMainIdentifier {

  String message() default "{wiinvent.user.notNull}";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  class ExistsMainIdentifierValidator implements ConstraintValidator<ExistsMainIdentifier, String> {
    private final UserStorage userStorage;

    public ExistsMainIdentifierValidator(UserStorage userStorage) {
      this.userStorage = userStorage;
    }

    @Override
    public boolean isValid(String mainIdentifier, ConstraintValidatorContext context) {
      return mainIdentifier != null && userStorage.findUserByMainIdentity(mainIdentifier) != null;
    }
  }
}
