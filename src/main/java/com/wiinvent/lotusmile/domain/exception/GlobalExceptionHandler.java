package com.wiinvent.lotusmile.domain.exception;

import jakarta.servlet.ServletException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.lang.annotation.Annotation;
import java.util.concurrent.CompletionException;

import static com.wiinvent.lotusmile.domain.exception.ErrorMessage.INTERNAL_SERVER;

@Log4j2
@RestControllerAdvice
public class GlobalExceptionHandler {

  // Remove it if don't have to multiple languages error message
  @Autowired
  LocalizationService localizationService;

  @ExceptionHandler(value = {
      MissingRequestHeaderException.class,
      MethodArgumentTypeMismatchException.class,
      HttpMessageNotReadableException.class
  })
  protected ResponseEntity<Object> handleMissingRequestHeader(Throwable ex) {
    return ResponseEntity.badRequest().body(ExceptionResponse.builder().message(localizationService.getMessage(ErrorMessage.INVALID_DATA.getMessage())).build());
  }

  @ExceptionHandler(value = {ServletException.class})
  public ResponseEntity<ExceptionResponse> handleServletException(ServletException ex) {
    String message = ex.getMessage();
    if (ex instanceof ErrorResponse errorResponse) {
      HttpStatusCode errorCode = errorResponse.getStatusCode();
      HttpStatus status = HttpStatus.valueOf(errorCode.value());
      return new ResponseEntity<>(ExceptionResponse.builder().message(message).build(), status);
    }

    return ResponseEntity.badRequest().body(ExceptionResponse.builder().message(message).build());
  }

  @ExceptionHandler(value = {BadRequestException.class})
  public ResponseEntity<ExceptionResponse> badRequestException(Exception ex) {
    return ResponseEntity.badRequest().body(ExceptionResponse.builder().message(localizationService.getMessage(ex.getMessage())).build());
  }

  @ExceptionHandler(value = {UnAuthenticationException.class})
  public ResponseEntity<ExceptionResponse> unAuthenticationException(Exception ex) {
    return new ResponseEntity<>(ExceptionResponse.builder().message(localizationService.getMessage(ex.getMessage())).build(), HttpStatus.UNAUTHORIZED);
  }

  @ExceptionHandler(value = {ForbiddenException.class})
  public ResponseEntity<ExceptionResponse> forbiddenExceptionException(Exception ex) {
    return new ResponseEntity<>(ExceptionResponse.builder().message(localizationService.getMessage(ex.getMessage())).build(), HttpStatus.FORBIDDEN);
  }

  @ExceptionHandler(value = {ToManyRequestsException.class})
  public ResponseEntity<ExceptionResponse> toManyRequestsException(Exception ex) {
    return new ResponseEntity<>(ExceptionResponse.builder().message(localizationService.getMessage(ex.getMessage())).build(), HttpStatus.TOO_MANY_REQUESTS);
  }

  @ExceptionHandler(value = {Exception.class})
  public ResponseEntity<InternalServerResponse> internalServerException(Exception ex) {
    log.error("=====>internalServerException: ", ex);
    return ResponseEntity.internalServerError().body(InternalServerResponse
        .builder()
        .message(localizationService.getMessage(INTERNAL_SERVER.getMessage()))
        .build());
  }

  @ExceptionHandler(value = {InternalServerException.class})
  public ResponseEntity<InternalServerResponse> internalServerException(InternalServerException ex) {
    log.error("=====>internalServerException: ", ex);
    return ResponseEntity.internalServerError().body(InternalServerResponse
        .builder()
        .message(localizationService.getMessage(INTERNAL_SERVER.getMessage()))
        .build());
  }

  @ExceptionHandler(value = {CompletionException.class})
  protected ResponseEntity<ExceptionResponse> completionException(CompletionException ex) {
    log.error("==============>completionException exception = ", ex);
    if (ex.getCause() instanceof BadRequestException badRequestException) {
      return ResponseEntity.badRequest().body(ExceptionResponse.builder().message(localizationService.getMessage(badRequestException.getMessage())).build());
    }
    return ResponseEntity.internalServerError().body(ExceptionResponse.builder().message(localizationService.getMessage(INTERNAL_SERVER.getMessage())).build());
  }

  // TODO: check the message
  @ExceptionHandler(value = {MethodArgumentNotValidException.class})
  protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
    BindingResult bindingResult = ex.getBindingResult();
    FieldError firstError = bindingResult.getFieldErrors().stream().findFirst().orElse(null);
    if (firstError != null) {
      return new ResponseEntity<>(ExceptionResponse.builder()
          .message(firstError.getDefaultMessage())
          .fieldName(firstError.getField())
          .build(), HttpStatus.BAD_REQUEST);
    }
    return ResponseEntity.badRequest().body(ExceptionResponse.builder().message("Validation failed").build());
  }

  // TODO: check the message
  @ExceptionHandler(value = {ConstraintViolationException.class})
  protected ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException ex) {
    log.debug("==============>handleConstraintViolation ex = ", ex);
    ConstraintViolation<?> firstConstraintViolation = ex.getConstraintViolations().stream()
        .findFirst()
        .orElse(null);

    String message = firstConstraintViolation != null ? firstConstraintViolation.getMessage() : "Validation failed";
    HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
    if (firstConstraintViolation != null) {
      Annotation annotation = firstConstraintViolation.getConstraintDescriptor().getAnnotation();
      ResponseStatus responseStatus = annotation.annotationType().getAnnotation(ResponseStatus.class);
      httpStatus = responseStatus != null ? responseStatus.value() : HttpStatus.BAD_REQUEST;
    }

    return new ResponseEntity<>(ExceptionResponse.builder().message(message).build(), httpStatus);
  }


}
