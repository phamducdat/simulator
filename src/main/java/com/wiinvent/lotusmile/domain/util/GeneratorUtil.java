package com.wiinvent.lotusmile.domain.util;

import org.apache.commons.lang3.RandomStringUtils;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Random;
import java.util.UUID;

public class GeneratorUtil {
  private static final SecureRandom SECURE_RANDOM = new SecureRandom();
  private static final Random RND = new SecureRandom();

  private GeneratorUtil() {
    throw new IllegalStateException("Utils class");
  }


  public static Long genLongId() {
    CodeConfig codeConfig = new CodeConfig(10, CodeConfig.Charset.NUMBERS, null, null, "##########");
    return Long.parseLong(GeneratorUtil.generate(codeConfig));
  }

  public static String generateDebugId() {
    return UUID.randomUUID().toString().replace("-", "").toUpperCase();
  }

  public static String generateUuid() {
    return UUID.randomUUID().toString();
  }

  public static String generateVoucherCode(String prefix, int length) {
    String epochStr = String.valueOf(Instant.now().toEpochMilli());
    int epochLength = epochStr.length();

    int randomLength = length - epochLength;
    int padLeftLength;
    int padRightLength;
    if (randomLength % 2 == 0) {
      padLeftLength = randomLength / 2;
      padRightLength = padLeftLength;
    } else {
      padLeftLength = randomLength / 2;
      padRightLength = padLeftLength + 1;
    }

    String padLeft = RandomStringUtils.random(padLeftLength, 0, 0, true, true, null,
        SECURE_RANDOM).toUpperCase();
    String padRight = RandomStringUtils.random(padRightLength, 0, 0, true, true, null,
        SECURE_RANDOM).toUpperCase();

    return prefix + padLeft + epochStr + padRight;
  }

  /**
   * Generates a random code according to given config.
   *
   * @return Generated code.
   */
  public static String generate(CodeConfig config) {
    StringBuilder sb = new StringBuilder();
    char[] chars = config.getCharset().toCharArray();
    char[] pattern = config.getPattern().toCharArray();

    if (config.getPrefix() != null) {
      sb.append(config.getPrefix());
    }

    for (char c : pattern) {
      if (c == CodeConfig.PATTERN_PLACEHOLDER) {
        sb.append(chars[RND.nextInt(chars.length)]);
      } else {
        sb.append(c);
      }
    }

    if (config.getPostfix() != null) {
      sb.append(config.getPostfix());
    }

    return sb.toString();
  }

  public static void main(String[] args) {
    //

    CodeConfig codeConfig = new CodeConfig(10, CodeConfig.Charset.ALPHANUMERIC, "a", "c", "####-####-####");

    System.out.println("==========generateVoucherCode: " + generate(codeConfig));
  }

}
