package com.wiinvent.lotusmile.domain.entity;

import com.wiinvent.lotusmile.domain.util.Helper;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@MappedSuperclass
@SuperBuilder
@NoArgsConstructor
public abstract class BaseEntity {

  @Column(name = "created_at")
  private long createdAt;

  @Column(name = "updated_at")
  private long updatedAt;

  @PrePersist
  protected void onCreate() {
    updatedAt = createdAt = Helper.getNowMillisAtUtc();
  }

  @PreUpdate
  protected void onUpdate() {
    updatedAt = Helper.getNowMillisAtUtc();
  }
}
