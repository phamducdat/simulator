package com.wiinvent.lotusmile.domain.annotation;


import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LogPerformance {

  @Aspect
  @Component
  @Slf4j
  class LogPerformanceAspect {

    @Around("@annotation(LogPerformance)")
    public Object aroundAdvice(ProceedingJoinPoint joinPoint) throws Throwable {
      Object obj = null;
      long start = System.currentTimeMillis();
      try {
        obj = joinPoint.proceed();
        return obj;
      } finally {
        long end = System.currentTimeMillis();
        log.debug("Method: " + joinPoint.getSignature() + " executed in " + (end - start) + " ms");
      }
    }
  }
}
