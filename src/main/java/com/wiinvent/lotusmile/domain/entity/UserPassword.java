package com.wiinvent.lotusmile.domain.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(schema = "\"simulator\"", name = "\"user_password\"")
@Data
@SuperBuilder
@Deprecated(forRemoval = true)
@NoArgsConstructor
public class UserPassword extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(name = "user_id")
  private Integer userId;

  @Column(name = "password")
  private String password;

  @Column(name = "email")
  private String email;

  @Column(name = "phone_number")
  private String phoneNumber;

  @Column(name = "main_identifier")
  private String mainIdentifier;

  @Column(name = "customer_Id")
  private String customerId;

  public static UserPassword createFrom(User user) {
    return UserPassword.builder()
        .userId(user.getId())
        .mainIdentifier(user.getMainIdentifier())
        .phoneNumber(user.getPhoneNumber())
        .email(user.getEmail())
        .password("123456")
        .customerId(user.getCustomerId())
        .build();
  }
}
