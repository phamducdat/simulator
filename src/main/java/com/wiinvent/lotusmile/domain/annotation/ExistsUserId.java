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
@Constraint(validatedBy = ExistsUserId.ExistsUserIdValidator.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExistsUserId {

  String message() default "{wiinvent.user.notNull}";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  class ExistsUserIdValidator implements ConstraintValidator<ExistsUserId, Object> {
    private final UserStorage userStorage;

    public ExistsUserIdValidator(UserStorage userStorage) {
      this.userStorage = userStorage;
    }

    @Override
    public boolean isValid(Object userId, ConstraintValidatorContext context) {
      if (userId == null) {
        return false;
      }

      int id;
      switch (userId) {
        case String stringUserId -> {
          try {
            id = Integer.parseInt(stringUserId);
          } catch (NumberFormatException e) {
            return false;
          }
        }
        case Integer integerUserId -> id = integerUserId;
        default -> {
          return false;
        }
      }

      return userStorage.findUserByUserId(id) != null;
    }
  }
}
