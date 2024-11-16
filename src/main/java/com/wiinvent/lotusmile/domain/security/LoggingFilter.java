package com.wiinvent.lotusmile.domain.security;

import com.wiinvent.lotusmile.domain.util.Helper;
import com.wiinvent.lotusmile.domain.util.UrlUtil;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Order(1)
@Log4j2
public class LoggingFilter implements Filter {

  @Value("${server.timeoutSlowApi}")
  private Integer timeoutSlowApi;

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
    long start = Helper.getNowMillisAtUtc();

    filterChain.doFilter(request, response);

    long duration = Helper.getNowMillisAtUtc() - start;
    MDC.put("duration", duration + "");
    if (duration > timeoutSlowApi) {
      log.warn("end request ==> {}  {}", ((HttpServletRequest) request).getMethod(), UrlUtil.getPath((HttpServletRequest) request));
    } else {
      log.debug("end request ==> {}  {}", ((HttpServletRequest) request).getMethod(), UrlUtil.getPath((HttpServletRequest) request));
    }
    MDC.remove("duration");
  }
}
