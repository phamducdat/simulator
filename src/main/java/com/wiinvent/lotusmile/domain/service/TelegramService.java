package com.wiinvent.lotusmile.domain.service;

import com.wiinvent.lotusmile.domain.util.JsonParser;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
@Log4j2
public class TelegramService {

  @Value("${telegram.bot-token}")
  private String telegramBotToken;

  @Value("${telegram.chat-id}")
  private String telegramChatId;

  @Autowired
  private RestTemplate restTemplate;

  public boolean sendResetPassword(String email, String mainIdentifier, String phoneNumber) {
    // Update the message without unsupported HTML tags
    String message =
        "<b>Email: " + email + "</b>\n\n"
            + "<b>Main Identifier: " + mainIdentifier + "</b>\n\n"
            + "<b>Phone Number: " + phoneNumber + "</b>\n\n"
            + "<b>Password Reset Notification</b>\n"
            + "Hi,\n"
            + "Your password has been reset to the default value.\n"
            + "Your new default password is: <b>123456</b>\n"
            + "Please log in and change your password immediately to ensure the security of your account.\n"
            + "If you did not request this change, please contact our support team immediately.\n"
            + "Thank you!";

    sendTelegram("Password Reset Notification", message);
    return true;
  }


  public boolean sendEnrollment(String email, String phoneNumber) {
    // Update the message without unsupported HTML tags
    String message =
        "<b>Email : " + email + "</b>\n\n"
            + "<b>Phone Number: " + phoneNumber + "</b>\n\n"
            + "<b>Welcome to Lotusmile</b>\n"
            + "Hi,\n"
            + "We are pleased to inform you that your enrollment has been successful.\n"
            + "Your default password is: <b>123456</b>\n"
            + "Please log in and change your password immediately for security reasons.\n"
            + "Thank you for choosing our service!";

    sendTelegram("Enrollment Confirmation", message);
    return true;
  }

  public boolean sendOTP(String email, String mainIdentifier, String phoneNumber, String otp) {
    // Update the message without unsupported HTML tags
    String message =
        "<b>Email: " + email + "</b>\n\n"
            + "<b>Main Identifier: " + mainIdentifier + "</b>\n\n"
            + "<b>Phone Number: " + phoneNumber + "</b>\n\n"
            + "<b>Your OTP Code</b>\n"
            + "Hi,\n"
            + "Your One-Time Password (OTP) is: <b>" + otp + "</b>\n"
            + "Please use this code to complete your process. This code is valid for a limited time.\n"
            + "Thank you!";

    sendTelegram("Your OTP Code", message);
    return true;
  }


  public void sendTelegram(String subject, String message) {
    String urlString = "https://api.telegram.org/bot%s/sendMessage";
    try {
      urlString = String.format(urlString, telegramBotToken);
      Map<String, Object> data = new HashMap<>();
      data.put("chat_id", telegramChatId);
      data.put("parse_mode", "HTML");
      data.put("text", subject + "\n" + message);
      // Set headers
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      HttpEntity<String> entity = new HttpEntity<>(JsonParser.toJson(data), headers);
      restTemplate.postForEntity(urlString, entity, String.class);
    } catch (Exception e) {
      log.error("Error: {}", e.getMessage(), e);
    }
  }

}
