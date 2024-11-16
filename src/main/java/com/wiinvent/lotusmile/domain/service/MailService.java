package com.wiinvent.lotusmile.domain.service;

import lombok.extern.log4j.Log4j2;
import net.sargue.mailgun.Configuration;
import net.sargue.mailgun.Mail;
import net.sargue.mailgun.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Log4j2
@Deprecated(forRemoval = true)
public class MailService {


  @Value("${mail.mail-server:MAILGUN}")
  private String mailServer;

  @Value("${mail.mailgun.api-key}")
  private String mailgunApiKey;

  @Value("${mail.mailgun.domain-name}")
  private String mailgunDomainName;

  @Value("${mail.mailgun.from}")
  private String mailgunFrom;

  @Autowired
  private RestTemplate restTemplate;

  @Deprecated(forRemoval = true)
  public boolean sendMailResetPassword(String to) {
    // Construct the email body
    String body = "<h1>Password Reset Notification</h1>"
        + "<p>Hi,</p>"
        + "<p>Your password has been reset to the default value.</p>"
        + "<p>Your new default password is: <strong>123456</strong></p>"
        + "<p>Please log in and change your password immediately to ensure the security of your account.</p>"
        + "<p>If you did not request this change, please contact our support team immediately.</p>"
        + "<p>Thank you!</p>";

    // Send the email
    return sendByMailgun(to, "[Lotusmile] Password Reset", body);
  }


  @Deprecated(forRemoval = true)
  protected boolean sendMailEnrollment(String to) {
    // Construct the email body
    String body = "<h1>Welcome to Lotusmile</h1>"
        + "<p>Hi,</p>"
        + "<p>We are pleased to inform you that your enrollment has been successful.</p>"
        + "<p>Your default password is: <strong>123456</strong></p>"
        + "<p>Please log in and change your password immediately for security reasons.</p>"
        + "<p>Thank you for choosing our service!</p>";

    // Send the email
    return sendByMailgun(to, "[Lotusmile] Enrollment Successful", body);
  }


  @Deprecated(forRemoval = true)
  protected boolean sendOTPMail(String to, String otp) {
    String body = "<h1>Your OTP Code</h1>"
        + "<p>Hi,</p>"
        + "<p>Your One-Time Password (OTP) is: <strong>" + otp + "</strong></p>"
        + "<p>Please use this code to complete your process. This code is valid for a limited time.</p>"
        + "<p>Thank you!</p>";
    return sendByMailgun(to, "[Lotusmile] OTP", body);
  }

  private boolean sendByMailgun(
      String to,
      String subject,
      String body) {
    try {
      Configuration configuration = new Configuration()
          .domain(mailgunDomainName).apiUrl("https://api.mailgun.net/v3")
          .apiKey(mailgunApiKey).from(mailgunDomainName, mailgunFrom);
      Response response = Mail.using(configuration).to(to)
          .subject(subject).html(body)
          .build().send();
      log.debug("Response from server: {}", response.responseMessage());
      return true;
    } catch (Exception e) {
      log.error("Error: {}", e.getMessage());
    }
    return false;
  }

}
