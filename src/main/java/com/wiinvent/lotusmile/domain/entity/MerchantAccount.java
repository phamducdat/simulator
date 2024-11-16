package com.wiinvent.lotusmile.domain.entity;

import com.wiinvent.lotusmile.domain.entity.types.MerchantAccountRole;
import com.wiinvent.lotusmile.domain.entity.types.MerchantAccountState;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(schema = "\"product\"", name = "\"merchant_account\"")
@Data
public class MerchantAccount extends BaseEntity {
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(name = "merchant_id")
  private Integer merchantId;

  @Column(name = "display_name")
  private String displayName;

  @Column(name = "username")
  private String username;

  @Column(name = "password")
  private String password;

  @Column(name = "msisdn")
  private String msisdn;

  @Column(name = "email")
  private String email;

  @Column(name = "avatar_url")
  private String avatarUrl;

  @Column(name = "state")
  @Enumerated(EnumType.STRING)
  @JdbcType(PostgreSQLEnumJdbcType.class)
  private MerchantAccountState state;

  @Column(name = "role")
  @Enumerated(EnumType.STRING)
  private MerchantAccountRole role;
}
