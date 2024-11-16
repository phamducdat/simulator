package com.wiinvent.lotusmile.domain.config;

import org.apache.logging.log4j.ThreadContext;
import org.springframework.core.task.TaskDecorator;
import org.springframework.lang.NonNull;

import java.util.Map;

public class ContextCopyingDecorator implements TaskDecorator {
  @Override
  @NonNull
  public Runnable decorate(@NonNull Runnable runnable) {
    // Capture the ThreadContext map
    Map<String, String> contextMap = ThreadContext.getContext();

    return () -> {
      try {
        // Set the context in the new thread
        ThreadContext.putAll(contextMap);
        runnable.run();
      } finally {
        // Clear the context to prevent leaks
        ThreadContext.clearAll();
      }
    };
  }
}
