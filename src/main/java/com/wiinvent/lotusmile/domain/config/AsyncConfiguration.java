package com.wiinvent.lotusmile.domain.config;

import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@Log4j2
public class AsyncConfiguration {

  @Bean
  public Executor getAsyncExecutor() {
    ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
    threadPoolTaskExecutor.setCorePoolSize(300);
    threadPoolTaskExecutor.setMaxPoolSize(300);
    threadPoolTaskExecutor.setQueueCapacity(40000);
    threadPoolTaskExecutor.setRejectedExecutionHandler((r, executor) -> {
      log.warn("Task rejected, thread pool is full and queue is also full");
      new ThreadPoolExecutor.CallerRunsPolicy();
    });
    threadPoolTaskExecutor.setTaskDecorator(new ContextCopyingDecorator());
    threadPoolTaskExecutor.setThreadNamePrefix("lotusmiles-async");
    threadPoolTaskExecutor.initialize();
    return threadPoolTaskExecutor;
  }

}
