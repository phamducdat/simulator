package com.wiinvent.lotusmile.domain.annotation;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Arrays;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LogMethodInputs {


  @Aspect
  @Component
  @Slf4j
  class LogMethodInputsAspect {

    @Before("@annotation(LogMethodInputs)")
    public void logMethodInputs(JoinPoint joinPoint) {
      Object[] arguments = joinPoint.getArgs();
      log.debug("Method {} called with arguments: {}", joinPoint.getSignature(), Arrays.toString(arguments));
    }
  }
}
