package com.wiinvent.lotusmile.domain.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(schema = "\"product\"", name = "\"merchant_info\"")
@Data
public class MerchantInfo extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(name = "location_id")
  private Integer locationId;

  @Column(name = "currency_id")
  private Integer currencyId;

  @Column(name = "name")
  private String name;

  @Column(name = "description")
  private String description;

  @Column(name = "thumbnail_img_url")
  private String thumbnailImgUrl;

  @Column(name = "cover_img_url")
  private String coverImgUrl;

  @Column(name = "msisdn_number")
  private String msisdnNumber;

  @Column(name = "email")
  private String email;

  @Column(name = "website")
  private String website;

  @Column(name = "sort")
  private Integer sort;
}
