package com.wiinvent.lotusmile.domain.entity;

import com.wiinvent.lotusmile.domain.entity.types.fpt.TierCode;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "config", schema = "common")
@Data
public class Config extends BaseEntity {
  @Id
  @Column(name = "id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(name = "key_config")
  private String keyConfig;

  @Column(name = "value_config")
  private String valueConfig;

  @NoArgsConstructor
  @Data
  public static class RankingLevel {
    public TierCode tierCode;
    public Long qualifyingMiles = 0L;
    public Double qualifyingSegments = 0D;
    public Boolean millionMiler;
    public Integer position;
    public Map<String, String> tier;
  }

  @NoArgsConstructor
  @Data
  public static class LanguageCode {
    private String name;
    private String code;
  }
}