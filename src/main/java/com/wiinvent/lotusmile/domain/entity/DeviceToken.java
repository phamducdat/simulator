package com.wiinvent.lotusmile.domain.entity;

import com.wiinvent.lotusmile.domain.entity.types.AppType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

@Entity
@Table(name = "device_token", schema = "\"user\"")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class DeviceToken {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(name = "user_id", nullable = false)
  private Integer userId;

  @Column(name = "device_token")
  private String deviceToken;

  @Column(name = "app_type")
  @Enumerated(EnumType.STRING)
  @JdbcType(PostgreSQLEnumJdbcType.class)
  private AppType appType;

  public DeviceToken(Integer userId, String deviceToken, AppType appType) {
    this.userId = userId;
    this.deviceToken = deviceToken;
    this.appType = appType;
  }
}
