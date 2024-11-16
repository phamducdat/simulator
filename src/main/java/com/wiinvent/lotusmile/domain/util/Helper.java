package com.wiinvent.lotusmile.domain.util;

import lombok.NonNull;
import lombok.experimental.UtilityClass;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@UtilityClass
public class Helper {

  private static final Random random = new Random();
  private static final String regexPhoneNumberVN = "(84+[3|5|7|8|9])+([0-9]{8})\b";


  public static Long getNowMillisAtUtc() {
    return System.currentTimeMillis();
  }

  public static String convertDateMMYY(@NonNull String inputDate) {
    LocalDate date = LocalDate.parse(inputDate);
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yy");
    return date.format(formatter);
  }

  public static String convertToDefaultDateTimeApp(String inputDate) {
    LocalDate date = LocalDate.parse(inputDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    return date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

  }

  public static String convertMobile(String phoneCountryCode, String phoneNumber) {
    if (phoneCountryCode != null) {
      return phoneNumber.startsWith("0") ? phoneCountryCode + phoneNumber.substring(1) : phoneNumber;
    } else
      return phoneNumber;
//    phoneNumber = phoneNumber.startsWith("0") ? phoneNumber.substring(1) : phoneNumber;
//    return phoneCountryCode + phoneNumber;
  }

  public static String checkMsisdnLogin(String msisdn) {
    if (msisdn.startsWith("0")) {
      return msisdn;
    }
    return "0" + msisdn;
  }

  public static String formatMsisdnToVietNam(String msisdn) {
    if (msisdn == null) {
      return "";
    }
    msisdn = removeAllLetterFromString(msisdn);
    if (msisdn.startsWith("0")) {
      msisdn = msisdn.substring(1);
      msisdn = "84" + msisdn;
    }
    return msisdn.replaceAll("\\s", "");
  }

  public static String removeAllLetterFromString(String string) {
    return string.replaceAll("\\D", "");
  }

  public static String getIpClient(String xForwardedFor) {
    return xForwardedFor != null ? xForwardedFor.split(",")[0].trim() : null;
  }

  public static boolean isInvalidMsisdnVietNam(String formatMsisdn) {
    Pattern pattern = Pattern.compile(regexPhoneNumberVN);
    Matcher matcher = pattern.matcher(formatMsisdn);
    return !matcher.find();
  }

  public static String splitIp(String ip) {
    if (ip == null) return "";
    String[] ips = ip.split(",");
    if (ips.length == 1) {
      ip = ips[0];
    } else if (ips.length == 2) {
      if (ips[0].startsWith("10.")) {
        ip = ips[1];
      } else {
        ip = ips[0];
      }
    } else {
      ip = "";
    }
    return ip;
  }

  public static long generateRandomLong(long bound) {
    return random.nextLong(bound);
  }
}
