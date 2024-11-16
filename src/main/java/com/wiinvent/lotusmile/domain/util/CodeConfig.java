package com.wiinvent.lotusmile.domain.util;

import lombok.Getter;

import java.util.Arrays;

@Getter
public class CodeConfig {

  public static final char PATTERN_PLACEHOLDER = '#';
  private final int length;
  private final String charset;
  private final String prefix;
  private final String postfix;
  private final String pattern;

  public CodeConfig(Integer length, String charset, String prefix, String postfix, String pattern) {
    if (length == null) {
      length = 8;
    }

    if (charset == null) {
      charset = Charset.ALPHANUMERIC;
    }

    if (pattern == null) {
      char[] chars = new char[length];
      Arrays.fill(chars, PATTERN_PLACEHOLDER);
      pattern = new String(chars);
    }

    this.length = length;
    this.charset = charset;
    this.prefix = prefix;
    this.postfix = postfix;
    this.pattern = pattern;
  }

  public static CodeConfig length(int length) {
    return new CodeConfig(length, null, null, null, null);
  }

  public static CodeConfig pattern(String pattern) {
    return new CodeConfig(null, null, null, null, pattern);
  }

  public CodeConfig withCharset(String charset) {
    return new CodeConfig(length, charset, prefix, postfix, pattern);
  }

  public CodeConfig withPrefix(String prefix) {
    return new CodeConfig(length, charset, prefix, postfix, pattern);
  }

  public CodeConfig withPostfix(String postfix) {
    return new CodeConfig(length, charset, prefix, postfix, pattern);
  }

  @Override
  public String toString() {
    return "CodeConfig ["
        + "length=" + length + ", "
        + "charset=" + charset + ", "
        + "prefix=" + prefix + ", "
        + "postfix=" + postfix + ", "
        + "pattern=" + pattern + "]";
  }

  public static class Charset {
    public static final String ALPHABETIC = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final String ALPHANUMERIC = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final String NUMBERS = "0123456789";
  }
}

